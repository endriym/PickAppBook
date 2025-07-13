package com.munity.pickappbook.core.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdatePickupLineRequest(
    val title: String,
    val content: String,
    val tags: List<TagId>?,
    @SerialName("visible") val isVisible: Boolean,
)
