package com.munity.pickappbook.core.data.local.database.entity

sealed class PLTagCrossRefEntity(
    open val pickupLineId: String,
    open val tagId: String,
)
