package com.munity.pickappbook.feature.account

import com.munity.pickappbook.core.data.remote.model.PickupLineResponse

data class AccountUIState(
    val username: String = "",
    val isPersonalRefreshing: Boolean = false,
    val personalPickupLines: List<PickupLineResponse> = listOf(),
    val isFavoriteRefreshing: Boolean = false,
    val favoritePickupLines: List<PickupLineResponse> = listOf(),
) {
    val userJpegImageUrl: String
        get() = "/images/$username.jpeg"
}
