package com.munity.pickappbook.core.data.local.database.entity

import androidx.room.Entity

@Entity(tableName = "favorite_pl_tags", primaryKeys = ["pickupLineId", "tagId"])
data class FavoritePLTagCrossRefEntity(
    override val pickupLineId: String,
    override val tagId: String,
) : PLTagCrossRefEntity(pickupLineId = pickupLineId, tagId = tagId)
