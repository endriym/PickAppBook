package com.munity.pickappbook.core.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class CreatePickupLineRequest(
    val title: String,
    val content: String,
    val visible: Boolean,
    val starred: Boolean,
    val tagIds: List<String>,
)
