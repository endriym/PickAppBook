package com.munity.pickappbook.core.data.repository

import com.munity.pickappbook.core.data.local.database.entity.TagEntity
import com.munity.pickappbook.core.data.remote.model.TagResponse
import com.munity.pickappbook.core.model.Tag

/* ----------- Tag to TagResponse ----------- */
fun Tag.asNetworkModel(): TagResponse = TagResponse(
    id = this.id,
    name = this.name,
    description = this.description,
    userId = this.userId
)

/* ----------- Tag to TagEntity ----------- */
fun Tag.asEntity(): TagEntity =
    TagEntity(
        tagId = this.id,
        name = this.name,
        description = this.description,
        userId = this.userId
    )

/* ----------- TagResponse to Tag ----------- */
fun TagResponse.asExternalModel(): Tag = Tag(
    id = this.id,
    name = this.name,
    description = this.description,
    userId = this.userId
)

/* ----------- TagResponse to TagEntity ----------- */
fun TagResponse.asEntity(): TagEntity = TagEntity(
    tagId = this.id,
    name = this.name,
    description = this.description,
    userId = this.userId
)

/* ----------- TagEntity to Tag ----------- */
fun TagEntity.asExternalModel(): Tag =
    Tag(
        id = this.tagId,
        name = this.name,
        description = this.description,
        userId = this.userId
    )
