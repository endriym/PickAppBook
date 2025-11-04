package com.munity.pickappbook.core.data.repository

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.room.withTransaction
import com.munity.pickappbook.core.data.local.database.PickAppDatabase
import com.munity.pickappbook.core.data.local.database.dao.FavoritePickupLineWithTagsDao
import com.munity.pickappbook.core.data.local.database.dao.FeedPickupLineWithTagsDao
import com.munity.pickappbook.core.data.local.database.dao.PickupLineDao
import com.munity.pickappbook.core.data.local.database.dao.PickupLineWithTagsDao
import com.munity.pickappbook.core.data.local.database.dao.PostedPickupLineWithTagsDao
import com.munity.pickappbook.core.data.local.database.dao.TagDao
import com.munity.pickappbook.core.data.local.database.entity.FavoritePickupLineEntity
import com.munity.pickappbook.core.data.local.database.entity.FeedPickupLineEntity
import com.munity.pickappbook.core.data.local.database.entity.PLTagCrossRefEntity
import com.munity.pickappbook.core.data.local.database.entity.PostedPickupLineEntity
import com.munity.pickappbook.core.data.local.datastore.PreferencesStorage
import com.munity.pickappbook.core.data.remote.ThePlaybookApi
import com.munity.pickappbook.core.data.remote.model.ErrorResponse
import com.munity.pickappbook.core.data.remote.model.GetPickupLineListRequest
import com.munity.pickappbook.core.data.remote.model.PickupLineResponse
import com.munity.pickappbook.core.model.PickupLine
import com.munity.pickappbook.core.model.Tag
import com.munity.pickappbook.core.model.User
import com.munity.pickappbook.core.ui.components.SortType
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
import kotlinx.coroutines.plus
import kotlin.io.encoding.Base64

class ThePlaybookRepository(
    parentScope: CoroutineScope,
    private val pickAppPrefsDS: PreferencesStorage,
    private val roomDatabase: PickAppDatabase,
    private val thePlaybookApi: ThePlaybookApi,
) {
    companion object {
        private const val TAG = "ThePlaybookRepository"
    }

    private val repositoryScope: CoroutineScope = run {
        val supervisorJob = SupervisorJob(parent = parentScope.coroutineContext.job)
        parentScope + Dispatchers.IO + supervisorJob
    }

    private val pickupLineDao: PickupLineDao = roomDatabase.pickupLineDao
    private val tagDao: TagDao = roomDatabase.tagDao
    private val pickupLineWithTagsDao: PickupLineWithTagsDao = roomDatabase.pickupLineWithTagsDao
    private val feedPickupLineWithTagsDao: FeedPickupLineWithTagsDao =
        roomDatabase.feedPickupLineWithTagsDao
    private val postedPickupLineWithTagsDao: PostedPickupLineWithTagsDao =
        roomDatabase.postedPickupLineWithTagsDao
    private val favoritePickupLineWithTagsDao: FavoritePickupLineWithTagsDao =
        roomDatabase.favoritePickupLineWithTagsDao

    val isLoggedIn: Flow<Boolean> =
        pickAppPrefsDS.storedPreferences.map { storedPrefs ->
            !(storedPrefs.username.isNullOrEmpty() || storedPrefs.password.isNullOrEmpty())
        }

    val loggedInUserId: Flow<String?> =
        pickAppPrefsDS.storedPreferences.map { storedPrefs ->
            storedPrefs.userId
        }

    val loggedInUsername: Flow<String?> =
        pickAppPrefsDS.storedPreferences.map { storedPrefs ->
            storedPrefs.username
        }

    val loggedInDisplayName: Flow<String?> =
        pickAppPrefsDS.storedPreferences.map { storedPrefs ->
            storedPrefs.displayName
        }

    val localFavoritePickupLines: Flow<List<PickupLine>> =
        favoritePickupLineWithTagsDao.getPickupLinesWithTags().map { pickupLinesEntities ->
            pickupLinesEntities.map { it.asExternalModel() }
        }

    val localFeedPickupLines: Flow<List<PickupLine>> =
        feedPickupLineWithTagsDao.getPickupLinesWithTags()
            .also {
                Log.d(TAG, "feedPickupLineWithTagsDao: $it")
            }
            .map { pickupLinesEntities ->
                pickupLinesEntities.map { it.asExternalModel() }
            }

    private val _searchedPickupLines: SnapshotStateList<PickupLine> =
        mutableStateListOf<PickupLine>()
    val searchedPickupLines: List<PickupLine> = _searchedPickupLines

    private val _messages = MutableSharedFlow<String?>()
    val messages: SharedFlow<String?> = _messages.asSharedFlow()

    suspend fun emitMessage(message: String?) = _messages.emit(message)

    suspend fun login(username: String, password: String): String {
        thePlaybookApi.login(username, password)
            .onSuccess { tokenInfoResponse ->
                pickAppPrefsDS.saveNewAccessToken(
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
                    userId = userInfoResponse.id,
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

    suspend fun signup(
        username: String, displayName: String, password: String, profilePicture: ByteArray,
    ): String {
        val profilePictureEncoded = Base64.encode(profilePicture)
        val result = thePlaybookApi.createUser(
            username = username,
            displayName = displayName,
            password = password,
            image = profilePictureEncoded
        )

        val message: String = when {
            result.isSuccess -> "Account successfully created for $username"

            result.isFailure ->
                result.exceptionOrNull()!!.message ?: "Something went wrong with the signup attempt"

            else -> "Something went wrong with the signup attempt"
        }

        return message
    }

    suspend fun logout() {
        pickAppPrefsDS.removeUser()
    }

    suspend fun getUsers(
        username: String? = null,
        displayName: String? = null,
        page: Int? = null,
    ): Result<List<User>> =
        thePlaybookApi.searchUsers(username = username, displayName = displayName, page = page)
            .map { userListResponse -> userListResponse.users }
            .map { userResponses ->
                userResponses.map { it.asExternalModel() }
            }

    suspend fun getUserInfoById(userId: String): Result<User> =
        thePlaybookApi.getUserInfoById(userId).map { it.asExternalModel() }

    suspend fun updatePickupLine(
        pickupLineId: String,
        newTitle: String,
        newContent: String,
        newTagIds: List<String>,
        newIsVisible: Boolean,
    ): Result<PickupLine> =
        thePlaybookApi.updatePickupLine(
            pickupLineId = pickupLineId,
            title = newTitle,
            content = newContent,
            tagIds = newTagIds,
            isVisible = newIsVisible
        ).map { it.asExternalModel() }

    suspend fun updateLocalPickupLine(pickupLine: PickupLine) {
        roomDatabase.withTransaction {
            pickupLineDao.insertPickupLines(pickupLine.asEntityModel())
            pickupLineWithTagsDao.deletePickupLinesWithTagsIdIn(listOf(pickupLine.id))
            pickupLine.tags?.forEach { tag ->
                pickupLineWithTagsDao.insertPickupLinesWithTags(
                    PLTagCrossRefEntity(
                        pickupLine.id,
                        tag.id
                    )
                )
            }
        }
    }

    suspend fun getPickupLine(pickupLineId: String): Result<PickupLine> =
        thePlaybookApi.getPickupLine(pickupLineId).map { it.asExternalModel() }

    suspend fun getLocalPickupLine(pickupLineId: String): PickupLine? =
        pickupLineWithTagsDao.getPickupLineById(pickupLineId)?.asExternalModel()

    fun getLocalPostedPickupLinesFlow(userId: String): Flow<List<PickupLine>> =
        postedPickupLineWithTagsDao.getPickupLinesWithTags(userId = userId)
            .map { pickupLinesEntities ->
                pickupLinesEntities.map { it.asExternalModel() }
            }

    suspend fun createTag(name: String, description: String): Result<Tag> {
        return thePlaybookApi.createTag(name = name, description = description)
            .map { it.asExternalModel() }
    }

    suspend fun deleteTag(tagId: String): Result<Boolean> {
        return thePlaybookApi.deleteTag(tagId = tagId)
    }

    suspend fun updateTag(newTag: Tag): Result<Tag> {
        return thePlaybookApi.updateTag(
            tagId = newTag.id,
            tagName = newTag.name,
            tagDescription = newTag.description
        ).map { it.asExternalModel() }
    }

    suspend fun getTags(): Result<List<Tag>> =
        thePlaybookApi.getTags().map { tagResponsesResult ->
            tagResponsesResult.map { it.asExternalModel() }
        }

    suspend fun createPickupLine(
        title: String, content: String, tagIds: List<String>, isVisible: Boolean,
    ): Result<String> {
        val result = thePlaybookApi.createPickupLine(
            title = title, content = content, visible = isVisible, tagIds = tagIds
        )

        return result.map { "Successfully created" }
    }

    suspend fun deletePickupLine(pickupLineId: String): Boolean {
        val result = thePlaybookApi.deletePickupLine(pickupLineId)

        result.onSuccess {
            return it
        }

        return false
    }

    suspend fun deleteLocalPickupLine(pickupLineId: String) {
        roomDatabase.withTransaction {
            pickupLineDao.deleteAllIn(listOf(pickupLineId))
            pickupLineWithTagsDao.deletePickupLinesWithTagsIdIn(listOf(pickupLineId))
        }
    }

    private suspend fun updateReaction(
        pickupLineToUpdate: PickupLine,
        newReaction: PickupLine.Reaction,
        newStatistics: PickupLine.Statistics,
    ): String {
        // Update affected pickup line in local database
        pickupLineDao.updatePickupLines(
            pickupLineToUpdate.copy(reaction = newReaction, statistics = newStatistics)
                .asEntityModel()
        )

        val result = thePlaybookApi.putReaction(
            pickupLineId = pickupLineToUpdate.id,
            newReaction = newReaction.asNetworkModel()
        )

        result.onSuccess {
            //TODO retrieve the same pickup line again
            // That is: get updated PickupLine
            return "Reactions successfully updated"
        }

        // Revert modified pickup line in local database
        pickupLineDao.updatePickupLines(pickupLineToUpdate.asEntityModel())

        return result.exceptionOrNull()!!.message ?: "Reaction not updated"
    }

    suspend fun updatePLFavoriteProperty(pickupLineId: String): String {
        // Update affected pickup line
        val pickupLineToUpdate: PickupLine? = getLocalPickupLine(pickupLineId)

        if (pickupLineToUpdate != null) {
            val oldReaction = pickupLineToUpdate.reaction.copy()
            val newReaction = oldReaction.copy(isFavorite = !oldReaction.isFavorite)

            return updateReaction(
                pickupLineToUpdate = pickupLineToUpdate,
                newReaction = newReaction,
                // Pass the same statistics when updating 'isFavorite'
                newStatistics = pickupLineToUpdate.statistics
            )
        }

        return "Reaction not updated"
    }

    suspend fun updateVote(
        pickupLineId: String,
        newVote: PickupLine.Reaction.Vote,
    ): String {
        // Update affected pickup line
        val pickupLineToUpdate: PickupLine? = getLocalPickupLine(pickupLineId)

        if (pickupLineToUpdate != null) {
            val oldReaction = pickupLineToUpdate.reaction.copy()

            val (newVote, weights) = oldReaction.vote.plus(newVote)
            val newReaction = oldReaction.copy(vote = newVote)

            return updateReaction(
                pickupLineToUpdate = pickupLineToUpdate,
                newReaction = newReaction,
                newStatistics = pickupLineToUpdate.statistics.plus(weights)
            )
        }

        return "Reaction not updated"
    }

    suspend fun getPostedPickupLines(
        userId: String,
        page: Int? = null,
    ): Pair<Int, String?> {
        var message: String? = null
        var newPickupLineReturned = 0

        val result: Result<List<PickupLineResponse>> = thePlaybookApi.getPickupLineList(
            userId = userId,
            page = page
        ).map { it.pickupLines }

        result.onSuccess { pickupLineResponses ->
            newPickupLineReturned = pickupLineResponses.size

            pickupLineResponses.forEach { pickupLineResponse ->
                roomDatabase.withTransaction {
                    pickupLineDao.insertPickupLines(pickupLineResponse.asEntity())
                    pickupLineResponse.tags?.forEach { tag ->
                        tagDao.insertTags(tag.asEntity())
                        pickupLineWithTagsDao.insertPickupLinesWithTags(
                            PLTagCrossRefEntity(
                                pickupLineResponse.id,
                                tag.id
                            )
                        )
                    }
                    postedPickupLineWithTagsDao.insertPickupLines(
                        PostedPickupLineEntity(pickupLineResponse.id, userId)
                    )
                }
            }
        }

        result.onFailure { exception ->
            message = exception.message
        }

        return Pair(newPickupLineReturned, message)
    }

    suspend fun getFavoritePickupLines(page: Int? = null): Pair<Int, String?> {
        var message: String? = null
        var newPickupLineReturned = 0

        val result: Result<List<PickupLineResponse>> =
            thePlaybookApi.getPickupLineList(starred = true, page = page).map { it.pickupLines }

        result.onSuccess { pickupLineResponses ->
            newPickupLineReturned = pickupLineResponses.size
            pickupLineResponses.forEach { pickupLineResponse ->
                roomDatabase.withTransaction {
                    pickupLineDao.insertPickupLines(pickupLineResponse.asEntity())
                    pickupLineResponse.tags?.forEach { tag ->
                        tagDao.insertTags(tag.asEntity())
                        pickupLineWithTagsDao.insertPickupLinesWithTags(
                            PLTagCrossRefEntity(
                                pickupLineResponse.id,
                                tag.id
                            )
                        )
                    }
                    favoritePickupLineWithTagsDao.insertPickupLines(
                        FavoritePickupLineEntity(pickupLineResponse.id)
                    )
                }
            }
        }

        result.onFailure { exception ->
            message = exception.message
        }

        return Pair(newPickupLineReturned, message)
    }

    suspend fun getFeedPickupLines(
        sortType: SortType? = null,
        page: Int? = null,
    ): Pair<Int, String?> {
        var message: String? = null
        var newPickupLineReturned = 0

        val result: Result<List<PickupLineResponse>> =
            thePlaybookApi.getPickupLineList(sortType = sortType?.asNetworkModel(), page = page)
                .map { it.pickupLines }

        result.onSuccess { pickupLineResponses ->
            newPickupLineReturned = pickupLineResponses.size
            pickupLineResponses.forEach { pickupLineResponse ->
                roomDatabase.withTransaction {
                    pickupLineDao.insertPickupLines(pickupLineResponse.asEntity())
                    pickupLineResponse.tags?.forEach { tag ->
                        tagDao.insertTags(tag.asEntity())
                        pickupLineWithTagsDao.insertPickupLinesWithTags(
                            PLTagCrossRefEntity(
                                pickupLineResponse.id,
                                tag.id
                            )
                        )
                    }
                    feedPickupLineWithTagsDao.insertPickupLines(
                        FeedPickupLineEntity(pickupLineResponse.id)
                    )
                }
            }
        }

        result.onFailure { exception ->
            message = exception.message
        }

        return Pair(newPickupLineReturned, message)
    }

    suspend fun searchPickupLines(
        title: String? = null,
        content: String? = null,
        starred: Boolean? = null,
        tagIds: List<String>? = null,
        isVisible: GetPickupLineListRequest.Visibility? = null,
        successPercentage: Double? = null,
        userId: String? = null,
        page: Int? = null,
    ): Result<List<PickupLine>> {
        val result: Result<List<PickupLineResponse>> = thePlaybookApi.getPickupLineList(
            title = title,
            content = content,
            starred = starred,
            tagIds = tagIds,
            isVisible = isVisible,
            successPercentage = successPercentage,
            userId = userId,
            page = page
        ).map { it.pickupLines }

        return result.map { pickupLineResponses ->
            pickupLineResponses.map { it.asExternalModel() }
        }
    }

    suspend fun cleanPostedPLs(userId: String) {
        postedPickupLineWithTagsDao.deleteAllPickupLines(userId)
    }

    suspend fun cleanFeedPLs() {
        feedPickupLineWithTagsDao.deleteAllPickupLines()
    }

    suspend fun cleanFavoritePLs() {
        favoritePickupLineWithTagsDao.deleteAllPickupLines()
    }
}
