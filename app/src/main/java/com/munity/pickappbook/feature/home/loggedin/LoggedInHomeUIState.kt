package com.munity.pickappbook.feature.home.loggedin

import com.munity.pickappbook.core.model.Tag

data class LoggedInHomeUIState(
    val isRefreshing: Boolean = false,
    val isBottomSheetVisible: Boolean = false,
    val pickupLineTitleCreate: String = "",
    val pickupLineContentCreate: String = "",
    val tagsToAdd: List<Tag> = listOf(),
    val tagNameCreate: String = "",
    val tagDescriptionCreate: String = "",
    val isTagSearcherVisible: Boolean = false,
    val isTagCreatorVisible: Boolean = false,
    val isTagCreationLoading: Boolean = false,
    val searchedTags: List<Tag> = listOf(),
    val pickupLineVisibilityCreate: Boolean = true,
    val isPostCreationLoading: Boolean = false,
    val canLoadNewItems: Boolean = true,
    val isLoadingNewItems: Boolean = false,
    val currentPage: Int = 0,
    val isSearchingTags: Boolean = false,
)
