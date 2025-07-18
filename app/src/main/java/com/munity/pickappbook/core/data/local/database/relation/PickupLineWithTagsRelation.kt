package com.munity.pickappbook.core.data.local.database.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.munity.pickappbook.core.data.local.database.entity.FavoritePLTagCrossRefEntity
import com.munity.pickappbook.core.data.local.database.entity.FeedPLTagCrossRefEntity
import com.munity.pickappbook.core.data.local.database.entity.PersonalPLTagCrossRefEntity
import com.munity.pickappbook.core.data.local.database.entity.PickupLineEntity
import com.munity.pickappbook.core.data.local.database.entity.TagEntity

sealed interface PickupLineWithTagsRelation {
    val pickupLine: PickupLineEntity
    val tags: List<TagEntity>?

    data class FeedPickupLineWithTagsRelation(
        @Embedded override val pickupLine: PickupLineEntity,

        @Relation(
            parentColumn = "pickupLineId",
            entityColumn = "tagId",
            associateBy = Junction(FeedPLTagCrossRefEntity::class),
            entity = TagEntity::class
        )
        override val tags: List<TagEntity>?,
    ) : PickupLineWithTagsRelation

    data class PersonalPickupLineWithTagsRelation(
        @Embedded override val pickupLine: PickupLineEntity,

        @Relation(
            parentColumn = "pickupLineId",
            entityColumn = "tagId",
            associateBy = Junction(PersonalPLTagCrossRefEntity::class),
            entity = TagEntity::class
        )
        override val tags: List<TagEntity>?,
    ) : PickupLineWithTagsRelation

    data class FavoritePickupLineWithTagsRelation(
        @Embedded override val pickupLine: PickupLineEntity,

        @Relation(
            parentColumn = "pickupLineId",
            entityColumn = "tagId",
            associateBy = Junction(FavoritePLTagCrossRefEntity::class),
            entity = TagEntity::class
        )
        override val tags: List<TagEntity>?,
    ) : PickupLineWithTagsRelation
}
