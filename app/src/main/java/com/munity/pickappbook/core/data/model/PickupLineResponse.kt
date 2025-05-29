package com.munity.pickappbook.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PickupLineResponse(
    val total: Int,
    val page: Int,
    @SerialName("pickup_lines") val pickupLines: List<PickupLine>,
)