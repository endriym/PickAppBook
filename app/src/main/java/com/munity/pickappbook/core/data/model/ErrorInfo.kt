package com.munity.pickappbook.core.data.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class ErrorInfo(
    val code: Int? = null,
    val message: String? = null,
) {
    override fun toString(): String {
        val strBuilder = StringBuilder()
        code?.also {
            strBuilder.append("Code: $it")
        }

        message?.let {
            if (strBuilder.isNotEmpty())
                strBuilder.append('\n')

            strBuilder.append("Message: $it")
        }

        return strBuilder.toString()
    }
}
