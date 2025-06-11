package com.munity.pickappbook.core.data.repository

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.munity.pickappbook.core.data.local.datastore.PickAppPrefsDataSource
import com.munity.pickappbook.core.data.model.PickupLine
import com.munity.pickappbook.core.data.model.Tag
import com.munity.pickappbook.core.data.model.User
import com.munity.pickappbook.core.data.remote.ThePlaybookDataSource
import com.munity.pickappbook.util.PickupLineUtil.plus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class ThePlaybookRepository(
    parentScope: CoroutineScope,
    private val pickAppPrefsDS: PickAppPrefsDataSource,
    private val thePlaybookDS: ThePlaybookDataSource,
) {
    private val repositoryScope: CoroutineScope = run {
        val supervisorJob = SupervisorJob(parent = parentScope.coroutineContext.job)
        parentScope + Dispatchers.IO + supervisorJob
    }

    val isLoggedIn: Flow<Boolean> = pickAppPrefsDS.storedPreference.map { storedPrefs ->
        !(storedPrefs.user.isNullOrEmpty() || storedPrefs.password.isNullOrEmpty())
    }

    private val _pickupLines: SnapshotStateList<PickupLine> = mutableStateListOf<PickupLine>()
    val pickupLines: List<PickupLine> = _pickupLines

    private val _messages = MutableSharedFlow<String?>()
    val messages: SharedFlow<String?> = _messages.asSharedFlow()

    suspend fun emitMessage(message: String?) = _messages.emit(message)

    suspend fun login(username: String, password: String): String {
        val tokenInfo = thePlaybookDS.login(username, password)

        val message: String = when {
            tokenInfo.isSuccess -> {
                val tokenInfoSuccess = tokenInfo.getOrNull()!!
                pickAppPrefsDS.saveNewUser(username = username, password = password)
                pickAppPrefsDS.saveAccessToken(tokenInfoSuccess.token, tokenInfoSuccess.expiration)

                "$username successfully logged in"
            }

            tokenInfo.isFailure -> {
                tokenInfo.exceptionOrNull()!!.message ?: "Something went wrong with the login"
            }

            else -> "Something went wrong with the login"
        }

        return message
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun signup(username: String, password: String, profilePicture: ByteArray): String {
        val profilePictureEncoded = Base64.encode(profilePicture)
        val user = User(username, password, profilePictureEncoded)
        val result = thePlaybookDS.createUser(user)

        val message: String = when {
            result.isSuccess -> "Account successfully created for $username"

            result.isFailure ->
                result.exceptionOrNull()!!.message ?: "Something went wrong with the signup attempt"

            else -> "Something went wrong with the signup attempt"
        }

        return message
    }

    suspend fun updatePickupLine(pickupLine: PickupLine): Result<PickupLine> =
        thePlaybookDS.updatePickupLine(pickupLine)

    suspend fun getPickupLine(pickupLineId: String): Result<PickupLine> =
        thePlaybookDS.getPickupLine(pickupLineId)

    suspend fun createTag(name: String, description: String): Result<Tag> {
        return thePlaybookDS.createTag(name = name, description = description)
    }

    suspend fun deleteTag(tagId: String): Result<Boolean> {
        return thePlaybookDS.deleteTag(tagId = tagId)
    }

    suspend fun updateTag(newTag: Tag): Result<Tag> {
        return thePlaybookDS.updateTag(newTag = newTag)
    }

    suspend fun getTags(): Result<List<Tag>> {
        return thePlaybookDS.getTags()
    }

    suspend fun createPickupLine(
        title: String,
        content: String,
        tagIds: List<String>,
        isVisible: Boolean,
        isStarred: Boolean = false,
    ): Result<String> {
        val result = thePlaybookDS.createPickupLine(
            title = title,
            content = content,
            visible = isVisible,
            starred = isStarred,
            tagIds = tagIds
        )

        return result.map {
            "Successfully created"
        }
    }

    private suspend fun refreshPickupLine(pickupLineIndex: Int): Result<String> {
        val result = getPickupLine(_pickupLines[pickupLineIndex].id)

        return when {
            result.isSuccess -> {
                _pickupLines[pickupLineIndex] = result.getOrNull()!!
                Result.success("PickupLine successfully updated")
            }

            else -> Result.failure(result.exceptionOrNull()!!)
        }
    }

    private suspend fun updateReaction(
        pickupLineIndex: Int,
        newReaction: PickupLine.Reaction,
        oldReaction: PickupLine.Reaction,
    ): String {
        // Update affected pickup line
        _pickupLines[pickupLineIndex] =
            _pickupLines[pickupLineIndex].copy(reactions = listOf(newReaction))
        val result = thePlaybookDS.putReaction(_pickupLines[pickupLineIndex].id, newReaction)

        if (result.isSuccess) {
            // Get updated PickupLine
            repositoryScope.launch {
                refreshPickupLine(pickupLineIndex)
            }

            return "Reactions successfully updated"
        }

        // Revert modified pickup line
        _pickupLines[pickupLineIndex] =
            _pickupLines[pickupLineIndex].copy(reactions = listOf(oldReaction))

        // Get updated PickupLine
        repositoryScope.launch {
            refreshPickupLine(pickupLineIndex)
        }

        return result.exceptionOrNull()!!.message ?: "Reaction not updated"
    }

    suspend fun updateStarred(pickupLineIndex: Int): String {
        val oldReaction =
            pickupLines[pickupLineIndex].reactions?.first()?.copy() ?: PickupLine.Reaction(
                isStarred = false, vote = PickupLine.Vote.NONE.toString()
            )

        val newReaction = oldReaction.copy(isStarred = !oldReaction.isStarred)

        return updateReaction(pickupLineIndex, newReaction, oldReaction)
    }

    suspend fun updateVote(pickupLineIndex: Int, newVote: PickupLine.Vote): String {
        val oldReaction =
            _pickupLines[pickupLineIndex].reactions?.first()?.copy() ?: PickupLine.Reaction(
                isStarred = false, vote = PickupLine.Vote.NONE.toString()
            )
        val oldStatistics =
            _pickupLines[pickupLineIndex].statistics?.copy() ?: PickupLine.Statistics(
                0, 0, 0, 0.toFloat()
            )

        val (newVote, weights) = PickupLine.Vote.valueOf(oldReaction.vote).plus(newVote)
        val newReaction = oldReaction.copy(vote = newVote.toString())

        // Calculate new statistics with `weights`
        _pickupLines[pickupLineIndex] =
            _pickupLines[pickupLineIndex].copy(statistics = oldStatistics.plus(weights))

        return updateReaction(pickupLineIndex, newReaction, oldReaction)
    }

    suspend fun getPickupLineList(
        page: Int? = null,
        title: String? = null,
        tagIds: List<String>? = null,
        visibility: Boolean? = null,
        content: String? = null,
    ): String? {
        var message: String? = null

        val result = thePlaybookDS.getPickupLineList(
            page = page,
            title = title,
            tagIds = tagIds,
            isVisible = visibility,
            content = content
        ).map { it.pickupLines }

        when {
            result.isSuccess -> _pickupLines.swapList(result.getOrNull()!!)
            else -> message = result.exceptionOrNull()!!.message
        }

        return message
    }

    suspend fun getPickupLineFeed(
        page: Int? = null,
        title: String? = null,
        tagIds: List<String>? = null,
        visibility: Boolean? = null,
        content: String? = null,
    ): String? {
        var message: String? = null

        val result = thePlaybookDS.getPickupLineFeed(
            page = page,
            title = title,
            tagIds = tagIds,
            isVisible = visibility,
            content = content
        ).map { it.pickupLines }

        when {
            result.isSuccess -> _pickupLines.swapList(result.getOrNull()!!)
            else -> message = result.exceptionOrNull()!!.message
        }

        return message
    }
}

fun <T> SnapshotStateList<T>.swapList(newList: List<T>) {
    clear()
    addAll(newList)
}