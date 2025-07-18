package com.munity.pickappbook.core.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tag")
data class TagEntity(
    @PrimaryKey val tagId: String,
    val name: String,
    val description: String,
    val userId: String,
)
