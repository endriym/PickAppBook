package com.munity.pickappbook.core.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.munity.pickappbook.core.data.local.database.entity.TagEntity

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(vararg tags: TagEntity)

    @Update
    suspend fun updateTags(vararg tags: TagEntity)

    @Delete
    suspend fun deleteTags(vararg tags: TagEntity)

    @Query("SELECT * FROM tag WHERE tag_id = :id")
    suspend fun getTagById(id: String): TagEntity
}
