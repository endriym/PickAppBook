package com.munity.pickappbook.core.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatePickupLineRequest(
    val title: String,
    val content: String,
    val visible: Boolean,
    @SerialName("tags") val tagIds: List<TagId>,
) {
    @Serializable
    data class TagId(val id: String)
}
