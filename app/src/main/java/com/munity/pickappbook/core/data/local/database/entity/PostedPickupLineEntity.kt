package com.munity.pickappbook.core.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.time.Instant

@Entity(tableName = "posted_pl", primaryKeys = ["pl_id", "author_id"])
data class PostedPickupLineEntity(
    @ColumnInfo(name = "pl_id") val pickupLineId: String,
    @ColumnInfo(name = "author_id") val authorId: String,
    @ColumnInfo(name = "insert_time") val insertTime: String = Instant.now().toString(),
)
