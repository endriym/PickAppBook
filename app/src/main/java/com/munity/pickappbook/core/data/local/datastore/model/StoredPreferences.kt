package com.munity.pickappbook.core.data.local.datastore.model

data class StoredPreferences(
    val username: String?,
    val displayName: String?,
    val password: String?,
    val accessToken: String?,
    val expiration: String?,
)
