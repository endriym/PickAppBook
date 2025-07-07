package com.munity.pickappbook.core.data.remote.model

data class CreatePickupLineRequest(
    val title: String,
    val content: String,
    val visible: Boolean,
    val starred: Boolean,
    val tagIds: List<String>,
)
