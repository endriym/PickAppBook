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
    val isLoading: Boolean = false,
    val isSearchingTags: Boolean = false,
)
