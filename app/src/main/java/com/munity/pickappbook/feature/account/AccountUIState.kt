package com.munity.pickappbook.feature.account

import com.munity.pickappbook.core.data.model.PickupLine

data class AccountUIState(
    val username: String = "",
    val isPersonalRefreshing: Boolean = false,
    val personalPickupLines: List<PickupLine> = listOf(),
    val isFavoriteRefreshing: Boolean = false,
    val favoritePickupLines: List<PickupLine> = listOf(),
) {
    val userJpegImageUrl: String
        get() = "/images/$username.jpeg"
}
