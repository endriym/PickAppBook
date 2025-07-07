package com.munity.pickappbook.core.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val code: Int,
    override val message: String,
): Throwable() {
    override fun toString(): String = "Code: ${code}\nMessage: $message"
}
