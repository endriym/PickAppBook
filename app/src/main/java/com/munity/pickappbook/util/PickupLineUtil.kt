package com.munity.pickappbook.util

import com.munity.pickappbook.core.data.model.PickupLine

object PickupLineUtil {
    /**
     * The values are pairs:
     *
     * - the first component is the resulting [PickupLine.Vote] object from the sum of
     * the pair of [PickupLine.Vote]s used as a key;
     *
     * - the second component is a triple with the following fields: `(nTries, nSuccesses, nFailures)`.
     * These integers have to be added to the old statistics instance in order to calculate
     * the new statistics instance.
     */
    private val statisticsMapCalculator =
        mapOf<Pair<PickupLine.Vote, PickupLine.Vote>, Pair<PickupLine.Vote, Triple<Int, Int, Int>>>(
            // Upvote
            Pair(PickupLine.Vote.NONE, PickupLine.Vote.UPVOTE) to Pair(
                PickupLine.Vote.UPVOTE, Triple(1, 1, 0)
            ),
            Pair(PickupLine.Vote.UPVOTE, PickupLine.Vote.UPVOTE) to Pair(
                PickupLine.Vote.NONE, Triple(-1, -1, 0)
            ),
            Pair(PickupLine.Vote.DOWNVOTE, PickupLine.Vote.UPVOTE) to Pair(
                PickupLine.Vote.UPVOTE, Triple(0, 1, -1)
            ),

            // Downvote
            Pair(PickupLine.Vote.NONE, PickupLine.Vote.DOWNVOTE) to Pair(
                PickupLine.Vote.DOWNVOTE, Triple(1, 0, 1)
            ),
            Pair(PickupLine.Vote.UPVOTE, PickupLine.Vote.DOWNVOTE) to Pair(
                PickupLine.Vote.DOWNVOTE, Triple(0, -1, 1)
            ),
            Pair(PickupLine.Vote.DOWNVOTE, PickupLine.Vote.DOWNVOTE) to Pair(
                PickupLine.Vote.NONE, Triple(-1, 0, -1)
            ),
        )

    fun PickupLine.Vote.plus(other: PickupLine.Vote): Pair<PickupLine.Vote, Triple<Int, Int, Int>> =
        statisticsMapCalculator[Pair(this, other)]
            ?: throw IllegalArgumentException("The other vote can't be 'NONE'")
}