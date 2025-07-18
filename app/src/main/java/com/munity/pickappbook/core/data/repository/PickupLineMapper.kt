package com.munity.pickappbook.core.data.repository

import com.munity.pickappbook.core.data.local.database.entity.PickupLineEntity
import com.munity.pickappbook.core.data.local.database.relation.PickupLineWithTagsRelation
import com.munity.pickappbook.core.data.remote.ThePlaybookEndpoints
import com.munity.pickappbook.core.data.remote.model.PickupLineResponse
import com.munity.pickappbook.core.data.remote.model.UserResponse
import com.munity.pickappbook.core.model.PickupLine
import com.munity.pickappbook.core.model.User
import com.munity.pickappbook.util.DateUtil

/* ----------- PickupLine to PickupLineResponse ----------- */

fun PickupLine.asNetworkModel(): PickupLineResponse =
    PickupLineResponse(
        id = this.id,
        title = this.title,
        content = this.content,
        user = UserResponse(
            id = this.author.id,
            username = this.author.username,
            displayName = this.author.displayName
        ),
        updatedAt = this.updatedAt.toString(),
        tags = this.tags?.map { it.asNetworkModel() },
        isVisible = this.isVisible,
        statistics = this.statistics.asNetworkModel(),
        reaction = this.reaction.asNetworkModel()
    )

fun PickupLine.Statistics.asNetworkModel(): PickupLineResponse.Statistics =
    PickupLineResponse.Statistics(
        nSuccesses = this.nSuccesses,
        nFailures = this.nFailures,
        nTries = this.nTries,
        successPercentage = this.successPercentage
    )

fun PickupLine.Reaction.Vote.asNetworkModel(): PickupLineResponse.Reaction.Vote =
    when (this) {
        PickupLine.Reaction.Vote.DOWNVOTE -> PickupLineResponse.Reaction.Vote.DOWNVOTE
        PickupLine.Reaction.Vote.NONE -> PickupLineResponse.Reaction.Vote.NONE
        PickupLine.Reaction.Vote.UPVOTE -> PickupLineResponse.Reaction.Vote.UPVOTE
    }

fun PickupLine.Reaction.asNetworkModel(): PickupLineResponse.Reaction =
    PickupLineResponse.Reaction(isStarred = this.isFavorite, vote = this.vote.asNetworkModel())

/* ----------- PickupLine to PickupLineEntity  ----------- */
fun PickupLine.asEntityModel(): PickupLineEntity =
    PickupLineEntity(
        pickupLineId = this.id,
        title = this.title,
        content = this.content,
        authorId = this.author.id,
        authorUsername = this.author.username,
        authorDisplayName = this.author.displayName,
        updatedAt = this.updatedAt.toString(),
        nSuccesses = this.statistics.nSuccesses,
        nFailures = this.statistics.nFailures,
        isVisible = this.isVisible,
        isFavorite = this.reaction.isFavorite,
        vote = this.reaction.vote.toString()
    )

/* ----------- PickupLineResponse to PickupLine ----------- */

fun PickupLineResponse.Statistics.asExternalModel(): PickupLine.Statistics =
    PickupLine.Statistics(nSuccesses = this.nSuccesses, nFailures = this.nFailures)

fun PickupLineResponse.Reaction.Vote.asExternalModel(): PickupLine.Reaction.Vote =
    when (this) {
        PickupLineResponse.Reaction.Vote.DOWNVOTE -> PickupLine.Reaction.Vote.DOWNVOTE
        PickupLineResponse.Reaction.Vote.NONE -> PickupLine.Reaction.Vote.NONE
        PickupLineResponse.Reaction.Vote.UPVOTE -> PickupLine.Reaction.Vote.UPVOTE
    }

fun PickupLineResponse.Reaction.asExternalModel(): PickupLine.Reaction =
    PickupLine.Reaction(isFavorite = this.isStarred, vote = this.vote.asExternalModel())

fun PickupLineResponse.asExternalModel(): PickupLine = PickupLine(
    id = this.id,
    title = this.title,
    content = this.content,
    author = User(
        id = this.user.id,
        username = this.user.username,
        displayName = this.user.displayName,
        profilePictureUrl = ThePlaybookEndpoints.userImageUrl(this.user.username)
    ),
    updatedAt = DateUtil.iso8601ToInstant(this.updatedAt),
    tags = this.tags?.map { it.asExternalModel() },
    isVisible = this.isVisible,
    statistics = this.statistics?.asExternalModel() ?: PickupLine.Statistics.default(),
    reaction = this.reaction?.asExternalModel() ?: PickupLine.Reaction.default()
)

/* ----------- PickupLineResponse to Entity ----------- */
fun PickupLineResponse.asEntity(): PickupLineEntity =
    PickupLineEntity(
        pickupLineId = this.id,
        title = this.title,
        content = this.content,
        authorId = this.user.id,
        authorUsername = this.user.username,
        authorDisplayName = this.user.displayName,
        updatedAt = this.updatedAt,
        nSuccesses = this.statistics?.nSuccesses ?: 0,
        nFailures = this.statistics?.nFailures ?: 0,
        isVisible = this.isVisible,
        isFavorite = this.reaction?.isStarred ?: false,
        vote = this.reaction?.let { it.vote.toString() } ?: "NONE"
    )



/* ----------- PickupLineWithTagsRelation to PickupLine ----------- */
fun PickupLineWithTagsRelation.asExternalModel(): PickupLine =
    PickupLine(
        id = this.pickupLine.pickupLineId,
        title = this.pickupLine.title,
        content = this.pickupLine.content,
        author = User(
            id = this.pickupLine.authorId,
            username = this.pickupLine.authorUsername,
            displayName = this.pickupLine.authorDisplayName,
            profilePictureUrl = ThePlaybookEndpoints.userImageUrl(this.pickupLine.authorUsername)
        ),
        updatedAt = DateUtil.iso8601ToInstant(this.pickupLine.updatedAt),
        tags = this.tags?.map { it.asExternalModel() },
        isVisible = this.pickupLine.isVisible,
        statistics = PickupLine.Statistics(
            nSuccesses = this.pickupLine.nSuccesses,
            nFailures = this.pickupLine.nFailures
        ),
        reaction = PickupLine.Reaction(
            isFavorite = this.pickupLine.isFavorite,
            vote = PickupLine.Reaction.Vote.valueOf(this.pickupLine.vote)
        )
    )
