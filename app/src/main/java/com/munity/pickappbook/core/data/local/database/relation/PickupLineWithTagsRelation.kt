package com.munity.pickappbook.core.data.local.database.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.munity.pickappbook.core.data.local.database.entity.PLTagCrossRefEntity
import com.munity.pickappbook.core.data.local.database.entity.PickupLineEntity
import com.munity.pickappbook.core.data.local.database.entity.TagEntity

data class PickupLineWithTagsRelation(
    @Embedded val pickupLine: PickupLineEntity,

    @Relation(
        parentColumn = "pl_id",
        entityColumn = "tag_id",
        associateBy = Junction(PLTagCrossRefEntity::class),
        entity = TagEntity::class
    )
    val tags: List<TagEntity>?,
)
