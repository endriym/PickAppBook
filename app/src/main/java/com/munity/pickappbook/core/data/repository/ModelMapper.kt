package com.munity.pickappbook.core.data.repository

import com.munity.pickappbook.core.data.remote.ThePlaybookEndpoints
import com.munity.pickappbook.core.data.remote.model.PickupLineResponse
import com.munity.pickappbook.core.data.remote.model.TagResponse
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
        userId = this.author.id,
        username = this.author.username,
        displayName = this.author.displayName,
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

fun PickupLineResponse.Vote.asExternalModel(): PickupLine.Reaction.Vote =
    when (this) {
        PickupLineResponse.Vote.DOWNVOTE -> PickupLine.Reaction.Vote.DOWNVOTE
        PickupLineResponse.Vote.NONE -> PickupLine.Reaction.Vote.NONE
        PickupLineResponse.Vote.UPVOTE -> PickupLine.Reaction.Vote.UPVOTE
    }

fun PickupLine.Reaction.Vote.asNetworkModel(): PickupLineResponse.Vote =
    when (this) {
        PickupLine.Reaction.Vote.DOWNVOTE -> PickupLineResponse.Vote.DOWNVOTE
        PickupLine.Reaction.Vote.NONE -> PickupLineResponse.Vote.NONE
        PickupLine.Reaction.Vote.UPVOTE -> PickupLineResponse.Vote.UPVOTE
    }

fun PickupLineResponse.Reaction.asExternalModel(): PickupLine.Reaction =
    PickupLine.Reaction(isStarred = this.isStarred, vote = this.vote.asExternalModel())

fun PickupLine.Reaction.asNetworkModel(): PickupLineResponse.Reaction =
    PickupLineResponse.Reaction(isStarred = this.isStarred, vote = this.vote.asNetworkModel())

fun PickupLineResponse.asExternalModel(): PickupLine = PickupLine(
    id = this.id,
    title = this.title,
    content = this.content,
    author = User(
        id = this.userId,
        username = this.username,
        displayName = this.displayName,
        profilePictureUrl = ThePlaybookEndpoints.userImageUrl(this.username)
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
