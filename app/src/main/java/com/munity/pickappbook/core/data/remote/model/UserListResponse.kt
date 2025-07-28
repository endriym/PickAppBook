package com.munity.pickappbook.core.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserListResponse(
    @SerialName("Total") val total: Int,
    @SerialName("Page") val page: Int,
    @SerialName("Users") val users: List<UserResponse>,
)
