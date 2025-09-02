package com.munity.pickappbook.core.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feed_pl")
data class FeedPickupLineEntity(
    @PrimaryKey @ColumnInfo(name = "pl_id") val pickupLineId: String,
)
