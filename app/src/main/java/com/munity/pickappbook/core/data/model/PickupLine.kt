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
    val tags: List<Tag>? = null,
    @SerialName("visible") val isVisible: Boolean,
    val statistics: Statistics? = null,
    val reactions: List<Reaction>? = null,
) {
    @Serializable
    data class Statistics(
        @SerialName("number_of_successes") val nSuccesses: Int,
        @SerialName("number_of_failures") val nFailures: Int,
        @SerialName("number_of_tries") val nTries: Int,
        @SerialName("success_percentage") val successPercentage: Float,
    ) {
        operator fun plus(values: Triple<Int, Int, Int>) = Statistics(
            nTries = nTries + values.first,
            nSuccesses = nSuccesses + values.second,
            nFailures = nFailures + values.third,
            successPercentage = if (nTries + values.first == 0) 0.toFloat() else (nSuccesses + values.second) / (nTries + values.first).toFloat() * 100
        )
    }

    @Serializable
    data class Reaction(
        @SerialName("starred") val isStarred: Boolean, val vote: String,
    )

    enum class Vote {
        DOWNVOTE, NONE, UPVOTE,
    }

    val userJpegImageUrl: String
        get() = "/images/$username.jpeg"
}