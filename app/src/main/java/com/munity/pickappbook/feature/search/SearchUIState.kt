package com.munity.pickappbook.feature.search

import com.munity.pickappbook.core.model.User

data class SearchUIState(
    val query: String = "",
    val isSearching: Boolean = false,
    val isSearchComplete: Boolean = false,
    val canLoadNewItems: Boolean = true,

    val isBottomSheetVisible: Boolean = false,

    val queryType: QueryType? = null,

    val isFavoriteQuery: Boolean? = false,

    val sliderValue: Float? = null,

    val isTagDropdownExpanded: Boolean = false,

    val userFilter: User? = null,
    val isUserSearcherVisible: Boolean = false,
    val userQuery: String = "",
    val isSearchingUsers: Boolean = false,
)
