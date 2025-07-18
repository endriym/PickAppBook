package com.munity.pickappbook.core.model

import java.time.Instant

data class PickupLine(
    val id: String,
    val title: String,
    val content: String,
    val author: User,
    val updatedAt: Instant,
    val tags: List<Tag>? = null,
    val isVisible: Boolean,
    val statistics: Statistics,
    val reaction: Reaction,
) {
    data class Statistics(
        val nSuccesses: Int,
        val nFailures: Int,
    ) {
        companion object {
            fun default(): Statistics = Statistics(0, 0)
        }

        val nTries: Int
            get() = nSuccesses + nFailures

        val successPercentage: Float
            get() = if (nTries == 0) 0f else (nSuccesses / nTries.toFloat()) * 100

        operator fun plus(values: Pair<Int, Int>) = Statistics(
            nSuccesses = nSuccesses + values.first,
            nFailures = nFailures + values.second,
        )
    }

    data class Reaction(
        val isFavorite: Boolean, val vote: Vote,
    ) {
        companion object {
            fun default(): Reaction = Reaction(false, Vote.NONE)
        }

        enum class Vote {
            DOWNVOTE, NONE, UPVOTE,
        }
    }
}
