package com.munity.pickappbook.util

import com.munity.pickappbook.core.model.PickupLine

object PickupLineUtil {
    /**
     * The values are pairs:
     *
     * - the first component is the resulting [PickupLine.Reaction.Vote] object from the sum of
     * the pair of [PickupLine.Reaction.Vote]s used as a key;
     *
     * - the second component is a pair with the following fields: `(nSuccesses, nFailures)`.
     * These integers have to be added to the old statistics instance in order to calculate
     * the new statistics instance.
     */
    private val statisticsMapCalculator =
        mapOf<Pair<PickupLine.Reaction.Vote, PickupLine.Reaction.Vote>, Pair<PickupLine.Reaction.Vote, Pair<Int, Int>>>(
            // Upvote
            Pair(PickupLine.Reaction.Vote.NONE, PickupLine.Reaction.Vote.UPVOTE) to Pair(
                PickupLine.Reaction.Vote.UPVOTE, Pair(1, 0)
            ),
            Pair(PickupLine.Reaction.Vote.UPVOTE, PickupLine.Reaction.Vote.UPVOTE) to Pair(
                PickupLine.Reaction.Vote.NONE, Pair(-1, 0)
            ),
            Pair(PickupLine.Reaction.Vote.DOWNVOTE, PickupLine.Reaction.Vote.UPVOTE) to Pair(
                PickupLine.Reaction.Vote.UPVOTE, Pair(1, -1)
            ),

            // Downvote
            Pair(PickupLine.Reaction.Vote.NONE, PickupLine.Reaction.Vote.DOWNVOTE) to Pair(
                PickupLine.Reaction.Vote.DOWNVOTE, Pair(0, 1)
            ),
            Pair(PickupLine.Reaction.Vote.UPVOTE, PickupLine.Reaction.Vote.DOWNVOTE) to Pair(
                PickupLine.Reaction.Vote.DOWNVOTE, Pair(-1, 1)
            ),
            Pair(PickupLine.Reaction.Vote.DOWNVOTE, PickupLine.Reaction.Vote.DOWNVOTE) to Pair(
                PickupLine.Reaction.Vote.NONE, Pair(0, -1)
            ),
        )

    fun PickupLine.Reaction.Vote.plus(other: PickupLine.Reaction.Vote): Pair<PickupLine.Reaction.Vote, Pair<Int, Int>> =
        statisticsMapCalculator[Pair(this, other)]
            ?: throw IllegalArgumentException("The other vote can't be 'NONE'")
}
