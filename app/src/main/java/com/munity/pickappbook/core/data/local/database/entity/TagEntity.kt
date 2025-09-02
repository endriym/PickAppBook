package com.munity.pickappbook.core.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tag")
data class TagEntity(
    @PrimaryKey @ColumnInfo(name = "tag_id") val tagId: String,
    val name: String,
    val description: String,
    val userId: String,
)
