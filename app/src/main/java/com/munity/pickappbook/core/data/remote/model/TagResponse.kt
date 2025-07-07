package com.munity.pickappbook.core.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TagResponse(
    val id: String,
    val name: String,
    val description: String,
    @SerialName("user_id") val userId: String,
)
