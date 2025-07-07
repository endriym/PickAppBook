package com.munity.pickappbook.core.data.repository

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.munity.pickappbook.core.data.local.datastore.PreferencesStorage
import com.munity.pickappbook.core.data.remote.ThePlaybookApi
import com.munity.pickappbook.core.data.remote.model.ErrorResponse
import com.munity.pickappbook.core.data.remote.model.PickupLineResponse
import com.munity.pickappbook.core.data.remote.model.TagResponse
import com.munity.pickappbook.core.data.remote.model.UserResponse
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
    private val pickAppPrefsDS: PreferencesStorage,
    private val thePlaybookApi: ThePlaybookApi,
) {
    private val repositoryScope: CoroutineScope = run {
        val supervisorJob = SupervisorJob(parent = parentScope.coroutineContext.job)
        parentScope + Dispatchers.IO + supervisorJob
    }

    val isLoggedIn: Flow<Boolean> = pickAppPrefsDS.storedPreferences.map { storedPrefs ->
        !(storedPrefs.username.isNullOrEmpty() || storedPrefs.password.isNullOrEmpty())
    }

    val currentUsername: Flow<String?> = pickAppPrefsDS.storedPreferences.map { storedPrefs ->
        storedPrefs.username
    }

    private val _messages = MutableSharedFlow<String?>()
    val messages: SharedFlow<String?> = _messages.asSharedFlow()

    suspend fun emitMessage(message: String?) = _messages.emit(message)

    suspend fun login(username: String, password: String): String {
        thePlaybookApi.login(username, password)
            .onSuccess { tokenInfoResponse ->
                pickAppPrefsDS.saveAccessToken(
                    newAccessToken = tokenInfoResponse.token,
                    expiration = tokenInfoResponse.expiration
                )
            }.onFailure { tokenErrorResponse ->
                return (tokenErrorResponse as ErrorResponse).toString()
            }

        // Get user info for retrieving the display name
        thePlaybookApi.getUserInfo()
            .onSuccess { userInfoResponse ->
                pickAppPrefsDS.saveNewUser(
                    username = userInfoResponse.username,
                    displayName = userInfoResponse.displayName,
                    password = password
                )
            }.onFailure { userInfoErrorResponse ->
                userInfoErrorResponse as ErrorResponse
                return "Couldn't get $username info\n\n$userInfoErrorResponse"
            }

        return "$username successfully logged in"
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun signup(
        username: String,
        displayName: String,
        password: String,
        profilePicture: ByteArray,
    ): String {
        val profilePictureEncoded = Base64.encode(profilePicture)
        val user = UserResponse(
            username, displayName, password, profilePictureEncoded,
        )
        val result = thePlaybookApi.createUser(user)

        val message: String = when {
            result.isSuccess -> "Account successfully created for $username"

            result.isFailure ->
                result.exceptionOrNull()!!.message ?: "Something went wrong with the signup attempt"

            else -> "Something went wrong with the signup attempt"
        }

        return message
    }

    suspend fun logout() {
        pickAppPrefsDS.saveNewUser(username = "", displayName = "", password = "")
    }

    suspend fun updatePickupLine(pickupLine: PickupLineResponse): Result<PickupLineResponse> =
        thePlaybookApi.updatePickupLine(pickupLine)

    suspend fun getPickupLine(pickupLineId: String): Result<PickupLineResponse> =
        thePlaybookApi.getPickupLine(pickupLineId)

    suspend fun createTag(name: String, description: String): Result<TagResponse> {
        return thePlaybookApi.createTag(name = name, description = description)
    }

    suspend fun deleteTag(tagId: String): Result<Boolean> {
        return thePlaybookApi.deleteTag(tagId = tagId)
    }

    suspend fun updateTag(newTag: TagResponse): Result<TagResponse> {
        return thePlaybookApi.updateTag(newTag = newTag)
    }

    suspend fun getTags(): Result<List<TagResponse>> {
        return thePlaybookApi.getTags()
    }

    suspend fun createPickupLine(
        title: String,
        content: String,
        tagIds: List<String>,
        isVisible: Boolean,
        isStarred: Boolean = false,
    ): Result<String> {
        val result = thePlaybookApi.createPickupLine(
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

    private suspend fun refreshPickupLine(
        pickupLineIndex: Int,
        pickupLines: SnapshotStateList<PickupLineResponse>,
    ): Result<String> {
        val result = getPickupLine(pickupLines[pickupLineIndex].id)

        return when {
            result.isSuccess -> {
                pickupLines[pickupLineIndex] = result.getOrNull()!!
                Result.success("PickupLine successfully updated")
            }

            else -> Result.failure(result.exceptionOrNull()!!)
        }
    }

    private suspend fun updateReaction(
        pickupLineIndex: Int,
        pickupLines: SnapshotStateList<PickupLineResponse>,
        newReaction: PickupLineResponse.Reaction,
        oldReaction: PickupLineResponse.Reaction,
    ): String {
        // Update affected pickup line
        pickupLines[pickupLineIndex] =
            pickupLines[pickupLineIndex].copy(reaction = newReaction)
        val result = thePlaybookApi.putReaction(pickupLines[pickupLineIndex].id, newReaction)

        if (result.isSuccess) {
            // Get updated PickupLine
            repositoryScope.launch {
                refreshPickupLine(pickupLineIndex, pickupLines)
            }

            return "Reactions successfully updated"
        }

        // Revert modified pickup line
        pickupLines[pickupLineIndex] =
            pickupLines[pickupLineIndex].copy(reaction = oldReaction)

        // Get updated PickupLine
        repositoryScope.launch {
            refreshPickupLine(pickupLineIndex, pickupLines)
        }

        return result.exceptionOrNull()!!.message ?: "Reaction not updated"
    }

    suspend fun updateStarred(
        pickupLineIndex: Int,
        pickupLines: SnapshotStateList<PickupLineResponse>,
    ): String {
        val oldReaction =
            pickupLines[pickupLineIndex].reaction.copy()

        val newReaction = oldReaction.copy(isStarred = !oldReaction.isStarred)

        return updateReaction(pickupLineIndex, pickupLines, newReaction, oldReaction)
    }

    suspend fun updateVote(
        pickupLineIndex: Int,
        pickupLines: SnapshotStateList<PickupLineResponse>,
        newVote: PickupLineResponse.Vote,
    ): String {
        val oldReaction =
            pickupLines[pickupLineIndex].reaction.copy()
        val oldStatistics =
            pickupLines[pickupLineIndex].statistics?.copy() ?: PickupLineResponse.Statistics(
                0, 0, 0, 0.toFloat()
            )

        val (newVote, weights) = oldReaction.vote.plus(newVote)
        val newReaction = oldReaction.copy(vote = newVote)

        // Calculate new statistics with `weights`
        pickupLines[pickupLineIndex] =
            pickupLines[pickupLineIndex].copy(statistics = oldStatistics.plus(weights))

        return updateReaction(pickupLineIndex, pickupLines, newReaction, oldReaction)
    }

    suspend fun getPickupLineList(
        pickupLines: SnapshotStateList<PickupLineResponse>,
        page: Int? = null,
        title: String? = null,
        tagIds: List<String>? = null,
        visibility: Boolean? = null,
        content: String? = null,
    ): String? {
        var message: String? = null

        val result = thePlaybookApi.getPickupLineList().map { it.pickupLines }

        when {
            result.isSuccess -> pickupLines.swapList(result.getOrNull()!!)
            else -> message = result.exceptionOrNull()!!.message
        }

        return message
    }

    suspend fun getPickupLineFeed(
        pickupLines: SnapshotStateList<PickupLineResponse>,
        page: Int? = null,
        title: String? = null,
        tagIds: List<String>? = null,
        visibility: Boolean? = null,
        content: String? = null,
    ): String? {
        var message: String? = null

        val result = thePlaybookApi.getPickupLineList().map { it.pickupLines }

        when {
            result.isSuccess -> pickupLines.swapList(result.getOrNull()!!)
            else -> message = result.exceptionOrNull()!!.message
        }

        return message
    }
}

fun <T> SnapshotStateList<T>.swapList(newList: List<T>) {
    clear()
    addAll(newList)
}
