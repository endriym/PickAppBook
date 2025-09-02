package com.munity.pickappbook.core.data.local.datastore.model

data class StoredPreferences(
    val userId: String?,
    val username: String?,
    val displayName: String?,
    val password: String?,
    val accessToken: String?,
    val expiration: String?,
)
