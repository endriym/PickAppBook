package com.munity.pickappbook.core.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.munity.pickappbook.core.data.local.database.entity.PLTagCrossRefEntity
import com.munity.pickappbook.core.data.local.database.relation.PickupLineWithTagsRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface PickupLineWithTagsDao {
    @Transaction
    @Query("SELECT * FROM pickup_line WHERE pl_id = :pickupLineId")
    suspend fun getPickupLineById(pickupLineId: String): PickupLineWithTagsRelation?

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPickupLinesWithTags(vararg plTagCrossRefEntities: PLTagCrossRefEntity)

    @Transaction
    @Update
    suspend fun updatePickupLinesWithTags(vararg plTagCrossRefEntities: PLTagCrossRefEntity)

    @Transaction
    @Delete
    suspend fun deletePickupLinesWithTags(vararg plTagCrossRefEntities: PLTagCrossRefEntity)

    @Transaction
    @Query("DELETE FROM pl_tags WHERE pl_id IN (:pickupLineIds)")
    suspend fun deletePickupLinesWithTagsIdIn(pickupLineIds: List<String>)

    @Transaction
    @Query("SELECT * FROM pickup_line")
    fun getPickupLinesWithTags(): Flow<List<PickupLineWithTagsRelation>>
}
