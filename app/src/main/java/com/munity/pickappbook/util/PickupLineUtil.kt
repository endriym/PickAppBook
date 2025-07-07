package com.munity.pickappbook.util

import com.munity.pickappbook.core.data.remote.model.PickupLineResponse

object PickupLineUtil {
    /**
     * The values are pairs:
     *
     * - the first component is the resulting [PickupLineResponse.Vote] object from the sum of
     * the pair of [PickupLineResponse.Vote]s used as a key;
     *
     * - the second component is a triple with the following fields: `(nTries, nSuccesses, nFailures)`.
     * These integers have to be added to the old statistics instance in order to calculate
     * the new statistics instance.
     */
    private val statisticsMapCalculator =
        mapOf<Pair<PickupLineResponse.Vote, PickupLineResponse.Vote>, Pair<PickupLineResponse.Vote, Triple<Int, Int, Int>>>(
            // Upvote
            Pair(PickupLineResponse.Vote.NONE, PickupLineResponse.Vote.UPVOTE) to Pair(
                PickupLineResponse.Vote.UPVOTE, Triple(1, 1, 0)
            ),
            Pair(PickupLineResponse.Vote.UPVOTE, PickupLineResponse.Vote.UPVOTE) to Pair(
                PickupLineResponse.Vote.NONE, Triple(-1, -1, 0)
            ),
            Pair(PickupLineResponse.Vote.DOWNVOTE, PickupLineResponse.Vote.UPVOTE) to Pair(
                PickupLineResponse.Vote.UPVOTE, Triple(0, 1, -1)
            ),

            // Downvote
            Pair(PickupLineResponse.Vote.NONE, PickupLineResponse.Vote.DOWNVOTE) to Pair(
                PickupLineResponse.Vote.DOWNVOTE, Triple(1, 0, 1)
            ),
            Pair(PickupLineResponse.Vote.UPVOTE, PickupLineResponse.Vote.DOWNVOTE) to Pair(
                PickupLineResponse.Vote.DOWNVOTE, Triple(0, -1, 1)
            ),
            Pair(PickupLineResponse.Vote.DOWNVOTE, PickupLineResponse.Vote.DOWNVOTE) to Pair(
                PickupLineResponse.Vote.NONE, Triple(-1, 0, -1)
            ),
        )

    fun PickupLineResponse.Vote.plus(other: PickupLineResponse.Vote): Pair<PickupLineResponse.Vote, Triple<Int, Int, Int>> =
        statisticsMapCalculator[Pair(this, other)]
            ?: throw IllegalArgumentException("The other vote can't be 'NONE'")
}
