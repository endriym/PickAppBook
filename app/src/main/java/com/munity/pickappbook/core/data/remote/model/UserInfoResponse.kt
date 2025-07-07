package com.munity.pickappbook.core.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoResponse(
    val id: String,
    @SerialName("display_name") val displayName: String,
    val username: String,
)
