package com.munity.pickappbook.core.data.repository

import com.munity.pickappbook.core.data.remote.ThePlaybookEndpoints
import com.munity.pickappbook.core.data.remote.model.PickupLineResponse
import com.munity.pickappbook.core.data.remote.model.TagResponse
import com.munity.pickappbook.core.data.remote.model.UserResponse
import com.munity.pickappbook.core.model.PickupLine
import com.munity.pickappbook.core.model.Tag
import com.munity.pickappbook.core.model.User
import com.munity.pickappbook.util.DateUtil

/* ----------- PickupLine mappers ----------- */

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
        tags = this.tags?.asNetworkTagsModel(),
        isVisible = this.isVisible,
        statistics = this.statistics.asNetworkModel(),
        reaction = this.reaction.asNetworkModel()
    )

fun PickupLineResponse.Statistics.asExternalModel(): PickupLine.Statistics =
    PickupLine.Statistics(nSuccesses = this.nSuccesses, nFailures = this.nFailures)

fun PickupLine.Statistics.asNetworkModel(): PickupLineResponse.Statistics =
    PickupLineResponse.Statistics(
        nSuccesses = this.nSuccesses,
        nFailures = this.nFailures,
        nTries = this.nTries,
        successPercentage = this.successPercentage
    )

fun PickupLineResponse.Reaction.Vote.asExternalModel(): PickupLine.Reaction.Vote =
    when (this) {
        PickupLineResponse.Reaction.Vote.DOWNVOTE -> PickupLine.Reaction.Vote.DOWNVOTE
        PickupLineResponse.Reaction.Vote.NONE -> PickupLine.Reaction.Vote.NONE
        PickupLineResponse.Reaction.Vote.UPVOTE -> PickupLine.Reaction.Vote.UPVOTE
    }

fun PickupLine.Reaction.Vote.asNetworkModel(): PickupLineResponse.Reaction.Vote =
    when (this) {
        PickupLine.Reaction.Vote.DOWNVOTE -> PickupLineResponse.Reaction.Vote.DOWNVOTE
        PickupLine.Reaction.Vote.NONE -> PickupLineResponse.Reaction.Vote.NONE
        PickupLine.Reaction.Vote.UPVOTE -> PickupLineResponse.Reaction.Vote.UPVOTE
    }

fun PickupLineResponse.Reaction.asExternalModel(): PickupLine.Reaction =
    PickupLine.Reaction(isFavorite = this.isStarred, vote = this.vote.asExternalModel())

fun PickupLine.Reaction.asNetworkModel(): PickupLineResponse.Reaction =
    PickupLineResponse.Reaction(isStarred = this.isFavorite, vote = this.vote.asNetworkModel())

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
    tags = this.tags.asExternalTagsModel(),
    isVisible = this.isVisible,
    statistics = this.statistics?.asExternalModel() ?: PickupLine.Statistics.default(),
    reaction = this.reaction?.asExternalModel() ?: PickupLine.Reaction.default()
)

fun List<PickupLineResponse>?.asExternalPLsModel(): List<PickupLine>? =
    this?.map { it.asExternalModel() }

fun List<PickupLine>?.asNetworkPLsModel(): List<PickupLineResponse>? =
    this?.map { it.asNetworkModel() }

/* ----------- Tag mappers ----------- */

fun Tag.asNetworkModel(): TagResponse = TagResponse(
    id = this.id,
    name = this.name,
    description = this.description,
    userId = this.userId
)

fun TagResponse.asExternalModel(): Tag = Tag(
    id = this.id,
    name = this.name,
    description = this.description,
    userId = this.userId
)

fun List<Tag>?.asNetworkTagsModel(): List<TagResponse>? = this?.map { it.asNetworkModel() }

fun List<TagResponse>?.asExternalTagsModel(): List<Tag>? = this?.map { it.asExternalModel() }
