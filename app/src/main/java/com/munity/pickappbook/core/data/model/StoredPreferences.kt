package com.munity.pickappbook.core.data.model

data class StoredPreferences(
    val user: String?,
    val password: String?,
    val accessToken: String?,
    val expiration: String?,
)