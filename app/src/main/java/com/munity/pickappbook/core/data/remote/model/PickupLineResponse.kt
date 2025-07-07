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
    @SerialName("user_id") val userId: String,
    val username: String,
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("updated_at") val updatedAt: String,
    val tags: List<TagResponse>? = null,
    @SerialName("visible") val isVisible: Boolean,
    val statistics: Statistics? = null,
    val reaction: Reaction,
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
        @SerialName("starred") val isStarred: Boolean, val vote: Vote,
    )

    enum class Vote {
        DOWNVOTE, NONE, UPVOTE,
    }

    val userJpegImageUrl: String
        get() = "/images/$username.jpeg"
}
