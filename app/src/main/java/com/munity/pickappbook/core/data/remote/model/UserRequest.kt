package com.munity.pickappbook.core.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class representing the request for creating a new user.
 *
 * @param username the desired username for the new account
 * @param password the desired password for the new account
 * @param image the base 64 encoded string image for the new account
 */
@Serializable
data class UserRequest(
    val username: String,
    @SerialName("display_name") val displayName: String,
    val password: String,
    @SerialName("user_image") val image: String,
)
