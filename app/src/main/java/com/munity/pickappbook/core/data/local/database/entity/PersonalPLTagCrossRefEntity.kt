package com.munity.pickappbook.core.data.local.database.entity

import androidx.room.Entity

@Entity(tableName = "personal_pl_tags", primaryKeys = ["pickupLineId", "tagId"])
data class PersonalPLTagCrossRefEntity(
    override val pickupLineId: String,
    override val tagId: String,
) : PLTagCrossRefEntity(pickupLineId = pickupLineId, tagId = tagId)
