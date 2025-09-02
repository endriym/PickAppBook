package com.munity.pickappbook.core.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.munity.pickappbook.core.data.local.database.entity.PickupLineEntity

@Dao
interface PickupLineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPickupLines(vararg pickupLines: PickupLineEntity)

    @Update
    suspend fun updatePickupLines(vararg pickupLines: PickupLineEntity)

    @Delete
    suspend fun deletePickupLines(vararg pickupLines: PickupLineEntity)

    @Query("DELETE FROM pickup_line WHERE pl_id IN (:pickupLineIds)")
    suspend fun deleteAllIn(pickupLineIds: List<String>)

    @Query("DELETE FROM pickup_line")
    suspend fun deleteAll()
}
