package com.munity.pickappbook.feature.account

import com.munity.pickappbook.core.data.remote.model.PickupLineResponse

data class AccountUIState(
    val username: String = "",

    val isPersonalRefreshing: Boolean = false,
    val canLoadNewPersonalItems: Boolean = true,
    val isLoadingNewPersonalItems: Boolean = false,
    val personalCurrentPage: Int = 0,
    val personalPickupLines: List<PickupLineResponse> = listOf(),

    val isFavoriteRefreshing: Boolean = false,
    val canLoadNewFavoriteItems: Boolean = true,
    val isLoadingNewFavoriteItems: Boolean = false,
    val favoriteCurrentPage: Int = 0,
    val favoritePickupLines: List<PickupLineResponse> = listOf(),
)
