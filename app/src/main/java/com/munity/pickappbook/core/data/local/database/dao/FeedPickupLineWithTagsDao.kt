package com.munity.pickappbook.core.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.munity.pickappbook.core.data.local.database.entity.FeedPLTagCrossRefEntity
import com.munity.pickappbook.core.data.local.database.relation.PickupLineWithTagsRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedPickupLineWithTagsDao :
    PickupLineWithTagsDao<FeedPLTagCrossRefEntity, PickupLineWithTagsRelation.FeedPickupLineWithTagsRelation> {

    @Transaction
    @Query("SELECT * FROM pickup_line WHERE pickupLineId in (SELECT pickupLineId FROM feed_pl_tags)")
    override fun getPickupLinesWithTags(): Flow<List<PickupLineWithTagsRelation.FeedPickupLineWithTagsRelation>>

    @Transaction
    @Query("DELETE FROM pickup_line WHERE pickupLineId NOT IN (SELECT pickupLineId FROM personal_pl_tags) AND pickupLineId NOT IN (SELECT pickupLineId FROM favorite_pl_tags)")
    override suspend fun deletePickupLines()
}
