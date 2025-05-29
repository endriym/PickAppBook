package com.munity.pickappbook.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PickupLine(
    val id: String,
    val title: String,
    val content: String,
    @SerialName("user_id") val userId: String,
    val username: String,
    @SerialName("updated_at") val updatedAt: String,
    val tags: List<Tag>,
    @SerialName("visible") val isVisible: Boolean,
    val statistics: Statistics,
    val reactions: List<Reaction>,
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
        @SerialName("starred") val isStarred: Boolean, val vote: String
    )

    enum class Vote {
        NONE, DOWNVOTE, UPVOTE
    }

    val userJpegImageUrl: String
        get() = "/$username.jpeg"
}