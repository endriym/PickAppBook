package com.munity.pickappbook.core.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.munity.pickappbook.core.data.local.database.entity.PostedPickupLineEntity
import com.munity.pickappbook.core.data.local.database.relation.PickupLineWithTagsRelation
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PostedPickupLineWithTagsDao {
    @Transaction
    @Query("SELECT * FROM pickup_line WHERE pl_id IN (SELECT pl_id FROM posted_pl WHERE pl_id = :pickupLineId)")
    abstract suspend fun getPickupLineById(pickupLineId: String): PickupLineWithTagsRelation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertPickupLines(vararg pickupLines: PostedPickupLineEntity)

    @Transaction
    @Query("SELECT * FROM pickup_line WHERE pl_id in (SELECT pl_id FROM posted_pl WHERE author_id = :userId)")
    abstract fun getPickupLinesWithTags(userId: String): Flow<List<PickupLineWithTagsRelation>>

    @Query("DELETE FROM posted_pl WHERE author_id = :userId")
    protected abstract suspend fun deletePickupLineEntities(userId: String)

    @Query("DELETE FROM posted_pl")
    protected abstract suspend fun deleteAllPickupLineEntities()

    @Query(
        "DELETE FROM pickup_line WHERE pl_id NOT IN (SELECT pl_id FROM feed_pl) " +
                "AND pl_id NOT IN (SELECT pl_id FROM favorite_pl) " +
                "AND pl_id NOT IN (SELECT pl_id FROM posted_pl WHERE author_id != :userId)"
    )
    protected abstract suspend fun deletePickupLines(userId: String)

    suspend fun deleteAllPickupLines(userId: String) {
        deletePickupLines(userId)
        deletePickupLineEntities(userId)
    }
}
