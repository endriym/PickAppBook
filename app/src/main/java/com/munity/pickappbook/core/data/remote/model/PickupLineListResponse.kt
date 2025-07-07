package com.munity.pickappbook.core.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PickupLineListResponse(
    val total: Int,
    val page: Int,
    @SerialName("pickup_lines") val pickupLines: List<PickupLineResponse>,
)
