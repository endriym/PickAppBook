package com.munity.pickappbook.core.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "feed_pl")
data class FeedPickupLineEntity(
    @PrimaryKey @ColumnInfo(name = "pl_id") val pickupLineId: String,
    @ColumnInfo(name = "insert_time") val insertTime: String = Instant.now().toString(),
)
