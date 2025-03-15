package com.munity.pickappbook.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PickupLine(
    val id: String,
    val title: String,
    val content: String,
    @SerialName("user_id") val userId: String,
    val username: String,
    @SerialName("updated_at") val updatedAt: String,
) {
    enum class Visibility {
        VISIBLE, NOT_VISIBLE
    }
}