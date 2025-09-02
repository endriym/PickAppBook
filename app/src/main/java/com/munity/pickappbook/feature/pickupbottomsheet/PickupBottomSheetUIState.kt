package com.munity.pickappbook.feature.pickupbottomsheet

import com.munity.pickappbook.core.model.Tag

data class PickupBottomSheetUIState(
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
    val isSearchingTags: Boolean = false,
)
