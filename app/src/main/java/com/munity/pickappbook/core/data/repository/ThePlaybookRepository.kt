package com.munity.pickappbook.core.data.repository

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.room.withTransaction
import com.munity.pickappbook.core.data.local.database.PickAppDatabase
import com.munity.pickappbook.core.data.local.database.dao.FavoritePickupLineWithTagsDao
import com.munity.pickappbook.core.data.local.database.dao.FeedPickupLineWithTagsDao
import com.munity.pickappbook.core.data.local.database.dao.PersonalPickupLineWithTagsDao
import com.munity.pickappbook.core.data.local.database.dao.PickupLineDao
import com.munity.pickappbook.core.data.local.database.dao.TagDao
import com.munity.pickappbook.core.data.local.database.entity.FavoritePLTagCrossRefEntity
import com.munity.pickappbook.core.data.local.database.entity.FeedPLTagCrossRefEntity
import com.munity.pickappbook.core.data.local.database.entity.PersonalPLTagCrossRefEntity
import com.munity.pickappbook.core.data.local.datastore.PreferencesStorage
import com.munity.pickappbook.core.data.remote.ThePlaybookApi
import com.munity.pickappbook.core.data.remote.model.ErrorResponse
import com.munity.pickappbook.core.data.remote.model.GetPickupLineListRequest
import com.munity.pickappbook.core.data.remote.model.PickupLineResponse
import com.munity.pickappbook.core.model.PickupLine
import com.munity.pickappbook.core.model.Tag
import com.munity.pickappbook.util.PickupLineUtil.plus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.job
import kotlinx.coroutines.plus
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class ThePlaybookRepository(
    parentScope: CoroutineScope,
    private val pickAppPrefsDS: PreferencesStorage,
    private val roomDatabase: PickAppDatabase,
    private val thePlaybookApi: ThePlaybookApi,
) {
    private val repositoryScope: CoroutineScope = run {
        val supervisorJob = SupervisorJob(parent = parentScope.coroutineContext.job)
        parentScope + Dispatchers.IO + supervisorJob
    }

    private val pickupLineDao: PickupLineDao = roomDatabase.pickupLineDao
    private val tagDao: TagDao = roomDatabase.tagDao
    private val feedPickupLineWithTagsDao: FeedPickupLineWithTagsDao =
        roomDatabase.feedPickupLineWithTagsDao
    private val personalPickupLineWithTagsDao: PersonalPickupLineWithTagsDao =
        roomDatabase.personalPickupLineWithTagsDao
    private val favoritePickupLineWithTagsDao: FavoritePickupLineWithTagsDao =
        roomDatabase.favoritePickupLineWithTagsDao

    val isLoggedIn: Flow<Boolean> = pickAppPrefsDS.storedPreferences.map { storedPrefs ->
        !(storedPrefs.username.isNullOrEmpty() || storedPrefs.password.isNullOrEmpty())
    }

    val currentUsername: Flow<String?> = pickAppPrefsDS.storedPreferences.map { storedPrefs ->
        storedPrefs.username
    }

    val currentDisplayName: Flow<String?> = pickAppPrefsDS.storedPreferences.map { storedPrefs ->
        storedPrefs.displayName
    }

    val personalPickupLines: Flow<List<PickupLine>> =
        personalPickupLineWithTagsDao.getPickupLinesWithTags().map { pickupLinesEntities ->
            pickupLinesEntities.map { it.asExternalModel() }
        }

    val favoritePickupLines: Flow<List<PickupLine>> =
        favoritePickupLineWithTagsDao.getPickupLinesWithTags().map { pickupLinesEntities ->
            pickupLinesEntities.map { it.asExternalModel() }
        }

    val feedPickupLines: Flow<List<PickupLine>> =
        feedPickupLineWithTagsDao.getPickupLinesWithTags().map { pickupLinesEntities ->
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
        pickAppPrefsDS.saveNewUser(username = "", displayName = "", password = "")
    }

    suspend fun updatePickupLine(pickupLine: PickupLine): Result<PickupLine> =
        thePlaybookApi.updatePickupLine(
            pickupLineId = pickupLine.id,
            title = pickupLine.title,
            content = pickupLine.content,
            tagIds = pickupLine.tags?.map { it.id },
            isVisible = pickupLine.isVisible
        ).map { it.asExternalModel() }

    suspend fun getPickupLine(pickupLineId: String): Result<PickupLine> =
        thePlaybookApi.getPickupLine(pickupLineId).map { it.asExternalModel() }

    suspend fun createTag(name: String, description: String): Result<Tag> {
        return thePlaybookApi.createTag(name = name, description = description)
            .map { it.asExternalModel() }
    }

    suspend fun deleteTag(tagId: String): Result<Boolean> {
        return thePlaybookApi.deleteTag(tagId = tagId)
    }

    suspend fun updateTag(newTag: Tag): Result<Tag> {
        return thePlaybookApi.updateTag(
            tagId = newTag.id, tagName = newTag.name, tagDescription = newTag.description
        ).map { it.asExternalModel() }
    }

    suspend fun getTags(): Result<List<Tag>> {
        return thePlaybookApi.getTags()
            .map { tagResponsesResult ->
                tagResponsesResult.map { it.asExternalModel() }
            }
    }

    suspend fun createPickupLine(
        title: String, content: String, tagIds: List<String>, isVisible: Boolean,
    ): Result<String> {
        val result = thePlaybookApi.createPickupLine(
            title = title,
            content = content,
            visible = isVisible,
            tagIds = tagIds
        )

        return result.map { "Successfully created" }
    }

//    private suspend fun refreshPickupLine(
//        pickupLineIndex: Int,
//        pickupLines: SnapshotStateList<PickupLine>,
//    ): Result<String> {
//        val result = getPickupLine(pickupLines[pickupLineIndex].id)
//
//        return when {
//            result.isSuccess -> {
//                pickupLines[pickupLineIndex] = result.getOrNull()!!
//                Result.success("PickupLine successfully updated")
//            }
//
//            else -> Result.failure(result.exceptionOrNull()!!)
//        }
//    }

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

    suspend fun updatePLFavoriteProperty(
        pickupLineIndex: Int,
        pickupLineType: PickupLineType,
    ): String {
        // Update affected pickup line
        val pickupLineToUpdate = when (pickupLineType) {
            PickupLineType.FEED -> feedPickupLines.first()[pickupLineIndex]
            PickupLineType.PERSONAL -> personalPickupLines.first()[pickupLineIndex]
            PickupLineType.FAVORITE -> favoritePickupLines.first()[pickupLineIndex]
            PickupLineType.SEARCH -> TODO()
        }

        val oldReaction = pickupLineToUpdate.reaction.copy()
        val newReaction = oldReaction.copy(isFavorite = !oldReaction.isFavorite)

        return updateReaction(
            pickupLineToUpdate = pickupLineToUpdate,
            newReaction = newReaction,
            // Pass the same statistics when updating 'isFavorite'
            newStatistics = pickupLineToUpdate.statistics
        )
    }

    suspend fun updateVote(
        pickupLineIndex: Int,
        pickupLineType: PickupLineType,
        newVote: PickupLine.Reaction.Vote,
    ): String {
        // Update affected pickup line
        val pickupLineToUpdate = when (pickupLineType) {
            PickupLineType.FEED -> feedPickupLines.first()[pickupLineIndex]
            PickupLineType.PERSONAL -> personalPickupLines.first()[pickupLineIndex]
            PickupLineType.FAVORITE -> favoritePickupLines.first()[pickupLineIndex]
            PickupLineType.SEARCH -> TODO()
        }

        val oldReaction = pickupLineToUpdate.reaction.copy()

        val (newVote, weights) = oldReaction.vote.plus(newVote)
        val newReaction = oldReaction.copy(vote = newVote)

        return updateReaction(
            pickupLineToUpdate = pickupLineToUpdate,
            newReaction = newReaction,
            newStatistics = pickupLineToUpdate.statistics.plus(weights)
        )
    }

    suspend fun getPickupLineList(
        pickupLineType: PickupLineType,
        title: String? = null,
        content: String? = null,
        starred: Boolean? = null,
        tagIds: List<String>? = null,
        isVisible: GetPickupLineListRequest.Visibility? = null,
        successPercentage: Double? = null,
        userId: String? = null,
        page: Int? = null,
    ): String? {
        var message: String? = null

        val result: Result<List<PickupLineResponse>>

        when (pickupLineType) {
            PickupLineType.FEED -> {
                result = thePlaybookApi.getPickupLineList().map { it.pickupLines }

                result.onSuccess { pickupLineResponses ->
                    pickupLineResponses.forEach { pickupLineResponse ->
                        roomDatabase.withTransaction {
                            pickupLineDao.insertPickupLines(pickupLineResponse.asEntity())
                            pickupLineResponse.tags?.forEach { tag ->
                                tagDao.insertTags(tag.asEntity())
                                feedPickupLineWithTagsDao.insertPickupLinesWithTags(
                                    FeedPLTagCrossRefEntity(
                                        pickupLineResponse.id,
                                        tag.id
                                    )
                                )
                            }
                        }
                    }
                }
            }


            PickupLineType.PERSONAL -> {
                val userInfo = thePlaybookApi.getUserInfo()
                result = thePlaybookApi.getPickupLineList(userId = userInfo.getOrNull()!!.id)
                    .map { it.pickupLines }
                result.onSuccess { pickupLineResponses ->
                    pickupLineResponses.forEach { pickupLineResponse ->
                        roomDatabase.withTransaction {
                            pickupLineDao.insertPickupLines(pickupLineResponse.asEntity())
                            pickupLineResponse.tags?.forEach { tag ->
                                tagDao.insertTags(tag.asEntity())
                                personalPickupLineWithTagsDao.insertPickupLinesWithTags(
                                    PersonalPLTagCrossRefEntity(
                                        pickupLineResponse.id,
                                        tag.id
                                    )
                                )
                            }
                        }
                    }
                }
            }

            PickupLineType.FAVORITE -> {
                result = thePlaybookApi.getPickupLineList(starred = true).map { it.pickupLines }
                result.onSuccess { pickupLineResponses ->
                    pickupLineResponses.forEach { pickupLineResponse ->
                        roomDatabase.withTransaction {
                            pickupLineDao.insertPickupLines(pickupLineResponse.asEntity())
                            pickupLineResponse.tags?.forEach { tag ->
                                tagDao.insertTags(tag.asEntity())
                                favoritePickupLineWithTagsDao.insertPickupLinesWithTags(
                                    FavoritePLTagCrossRefEntity(
                                        pickupLineResponse.id,
                                        tag.id
                                    )
                                )
                            }
                        }
                    }
                }
            }

            PickupLineType.SEARCH -> {
                result = thePlaybookApi.getPickupLineList(
                    title = title,
                    content = content,
                    starred = starred,
                    tagIds = tagIds,
                    isVisible = isVisible,
                    successPercentage = successPercentage,
                    userId = userId,
                    page = page
                ).map { it.pickupLines }

                return if (result.isSuccess) {
                    _searchedPickupLines.swapList(
                        result.getOrNull()!!.map { it.asExternalModel() })
                    null
                } else
                    result.exceptionOrNull()!!.message
            }
        }

        result.onFailure { exception ->
            message = exception.message
        }

        return message
    }

    suspend fun getPickupLineFeed(
        pickupLines: SnapshotStateList<PickupLine>,
        title: String? = null,
        content: String? = null,
        starred: Boolean? = null,
        tagIds: List<String>? = null,
        isVisible: GetPickupLineListRequest.Visibility? = null,
        successPercentage: Double? = null,
        userId: String? = null,
        page: Int? = null,
    ): String? {
        var message: String? = null

        val result = thePlaybookApi.getPickupLineList().map { it.pickupLines }

        result.onSuccess { pickupLineResponses ->
            pickupLines.swapList(pickupLineResponses.map { it.asExternalModel() })
        }.onFailure {
            message = result.exceptionOrNull()!!.message
        }

        return message
    }
}

inline fun <T> SnapshotStateList<T>.swapList(newList: List<T>) {
    clear()
    addAll(newList)
}
