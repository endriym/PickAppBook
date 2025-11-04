package com.munity.pickappbook.feature.home.loggedin

import com.munity.pickappbook.core.ui.components.SortType

data class LoggedInHomeUIState(
    val isRefreshing: Boolean = false,
    val isBottomSheetVisible: Boolean = false,
    val canLoadNewItems: Boolean = true,
    val isLoadingNewItems: Boolean = false,
    val currentPage: Int? = null,
    val sortTypeSelected: SortType? = null,
)
