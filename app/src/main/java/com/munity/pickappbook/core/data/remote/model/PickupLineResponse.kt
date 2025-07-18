package com.munity.pickappbook.core.data.remote.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class PickupLineResponse(
    val id: String,
    val title: String,
    val content: String,
    val user: UserResponse,
    @SerialName("updated_at") val updatedAt: String,
    val tags: List<TagResponse>? = null,
    @SerialName("visible") val isVisible: Boolean,
    val statistics: Statistics? = null,
    val reaction: Reaction? = null,
) {
    @Serializable
    data class Statistics(
        @SerialName("number_of_successes") val nSuccesses: Int,
        @SerialName("number_of_failures") val nFailures: Int,
        @SerialName("number_of_tries") val nTries: Int,
        @SerialName("success_percentage") val successPercentage: Float,
    )

    @Serializable
    data class Reaction(
        @SerialName("starred") val isStarred: Boolean, val vote: Vote,
    ) {
        enum class Vote {
            DOWNVOTE, NONE, UPVOTE
        }
    }
}
