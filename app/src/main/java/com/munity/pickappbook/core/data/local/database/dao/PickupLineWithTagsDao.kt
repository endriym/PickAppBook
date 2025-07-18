package com.munity.pickappbook.core.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import androidx.room.Update
import com.munity.pickappbook.core.data.local.database.entity.PLTagCrossRefEntity
import com.munity.pickappbook.core.data.local.database.relation.PickupLineWithTagsRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface PickupLineWithTagsDao<T : PLTagCrossRefEntity, R : PickupLineWithTagsRelation> {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPickupLinesWithTags(vararg plTagCrossRefEntities: T)

    @Transaction
    @Update
    suspend fun updatePickupLinesWithTags(vararg plTagCrossRefEntities: T)

    @Transaction
    @Delete
    suspend fun deletePickupLinesWithTags(vararg plTagCrossRefEntities: T)

    fun getPickupLinesWithTags(): Flow<List<R>>
}
