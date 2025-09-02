package com.munity.pickappbook.feature.home.loggedin

data class LoggedInHomeUIState(
    val isRefreshing: Boolean = false,
    val isBottomSheetVisible: Boolean = false,
    val canLoadNewItems: Boolean = true,
    val isLoadingNewItems: Boolean = false,
    val currentPage: Int = 0,
)
