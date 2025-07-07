package com.munity.pickappbook.core.data.remote.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class TokenResponse(
    val code: Int,
    @JsonNames("expire") val expiration: String,
    val token: String,
)
