package com.munity.pickappbook.core.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.munity.pickappbook.core.data.local.database.entity.FavoritePickupLineEntity
import com.munity.pickappbook.core.data.local.database.relation.PickupLineWithTagsRelation
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FavoritePickupLineWithTagsDao {
    @Transaction
    @Query("SELECT * FROM pickup_line WHERE pl_id IN (SELECT pl_id FROM favorite_pl WHERE pl_id = :pickupLineId)")
    abstract suspend fun getPickupLineById(pickupLineId: String): PickupLineWithTagsRelation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertPickupLines(vararg pickupLines: FavoritePickupLineEntity)

    @Transaction
    @Query("SELECT * FROM pickup_line WHERE pl_id in (SELECT pl_id FROM favorite_pl)")
    abstract fun getPickupLinesWithTags(): Flow<List<PickupLineWithTagsRelation>>

    @Query("DELETE FROM favorite_pl")
    protected abstract suspend fun deletePickupLineEntities()

    @Query(
        "DELETE FROM pickup_line " +
                "WHERE pl_id NOT IN (SELECT pl_id FROM feed_pl) " +
                "AND pl_id NOT IN (SELECT pl_id FROM posted_pl)"
    )
    protected abstract suspend fun deletePickupLines()

    suspend fun deleteAllPickupLines() {
        deletePickupLines()
        deletePickupLineEntities()
    }
}
