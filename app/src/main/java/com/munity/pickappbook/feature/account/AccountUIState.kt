package com.munity.pickappbook.feature.account

data class AccountUIState(
    val isPostedRefreshing: Boolean = false,
    val canLoadNewPostedItems: Boolean = true,
    val isLoadingNewPostedItems: Boolean = false,
    val postedCurrentPage: Int = 0,

    val isFavoriteRefreshing: Boolean = false,
    val canLoadNewFavoriteItems: Boolean = true,
    val isLoadingNewFavoriteItems: Boolean = false,
    val favoriteCurrentPage: Int = 0,

    val isBottomSheetVisible: Boolean = false,
    val pickupLineToEditId: String? = null,
)
