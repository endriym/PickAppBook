package com.munity.pickappbook.core.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pickup_line")
class PickupLineEntity(
    @PrimaryKey val pickupLineId: String,
    val title: String,
    val content: String,
    @ColumnInfo(name = "author_id") val authorId: String,
    @ColumnInfo(name = "author_username") val authorUsername: String,
    @ColumnInfo(name = "author_display_name") val authorDisplayName: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String,
    @ColumnInfo(name = "n_successes") val nSuccesses: Int,
    @ColumnInfo(name = "n_failures") val nFailures: Int,
    @ColumnInfo(name = "is_visible") val isVisible: Boolean,
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean,
    val vote: String,
)
