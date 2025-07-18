package com.munity.pickappbook.core.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class representing the user (author) in a [PickupLineResponse].
 *
 * @param id The id of the user that created the pickup line.
 * @param username The username of the user that created the pickup line.
 * @param displayName The display name of the user that created the pickup line.
 */
@Serializable
data class UserResponse(
    val id: String,
    val username: String,
    @SerialName("display_name") val displayName: String = "",
)
