package com.munity.pickappbook.core.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_pl")
data class FavoritePickupLineEntity(
    @PrimaryKey @ColumnInfo(name = "pl_id") val pickupLineId: String,
)
