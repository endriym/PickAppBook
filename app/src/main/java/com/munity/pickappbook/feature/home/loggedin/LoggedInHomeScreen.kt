package com.munity.pickappbook.feature.home.loggedin

import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.munity.pickappbook.core.ui.components.PullToRefreshLazyPickupCards
import com.munity.pickappbook.feature.pickupbottomsheet.PickupBottomSheet
import com.munity.pickappbook.feature.pickupbottomsheet.PickupBottomSheetViewModel

@Composable
@ExperimentalMaterial3Api
fun LoggedInHomeScreen(
    onAuthorClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val loggedInHomeVM: LoggedInHomeViewModel = viewModel(factory = LoggedInHomeViewModel.Factory)
    val loggedInHomeUIState by loggedInHomeVM.loggedInHomeUiState.collectAsState()
    val feedPickupLines by loggedInHomeVM.pickupLines.collectAsState()
//    val loggedInUsername by loggedInHomeVM.loggedInUsername.collectAsState()
    val loggedInUsername: String? = null

    Scaffold(
        floatingActionButton = { PickupLineFAB(onClick = loggedInHomeVM::onFABClick) },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(NavigationBarDefaults.windowInsets)
            .exclude(TopAppBarDefaults.windowInsets),
        modifier = modifier
    ) { innerPadding ->

        PullToRefreshLazyPickupCards(
            isRefreshing = loggedInHomeUIState.isRefreshing,
            onRefresh = loggedInHomeVM::onPullToRefresh,
            pickupLines = feedPickupLines,
            sortingVisible = true,
            sortTypeSelected = loggedInHomeUIState.sortTypeSelected,
            onSortChipClick = loggedInHomeVM::onSortChipClick,
            onAuthorClick = onAuthorClick,
            onStarredBtnClick = loggedInHomeVM::onFavoriteBtnClick,
            loggedInUsername = loggedInUsername,
            onEditPLClick = { /* TODO */ },
            onDeletePLClick = { /* TODO */ },
            onVoteClick = loggedInHomeVM::onVoteClick,
            onTagClick = {  /* TODO() */ },
            canLoadNewItems = loggedInHomeUIState.canLoadNewItems,
            isLoadingNewItems = loggedInHomeUIState.isLoadingNewItems,
            onLastPickupLineReached = loggedInHomeVM::onLastPickupLineReached,
            modifier = Modifier.padding(innerPadding),
        )

        if (loggedInHomeUIState.isBottomSheetVisible) {
            val pickupBottomSheetVM: PickupBottomSheetViewModel =
                viewModel(factory = PickupBottomSheetViewModel.factory())
            val pickupBottomSheetUiState by pickupBottomSheetVM.pickupBottomSheetUiState.collectAsState()

            PickupBottomSheet(
                onBottomSheetDismiss = loggedInHomeVM::onBottomSheetDismiss,
                titleTFValue = pickupBottomSheetUiState.pickupLineTitleCreate,
                onTitleTFChange = pickupBottomSheetVM::onTitleTFChange,
                contentTFValue = pickupBottomSheetUiState.pickupLineContentCreate,
                onContentTFChange = pickupBottomSheetVM::onContentTFChange,
                tagsToAdd = pickupBottomSheetUiState.tagsToAdd,
                onAddedTagsItemClick = pickupBottomSheetVM::onAddedTagsItemClick,
                onSearchTagBtnClick = pickupBottomSheetVM::onSearchTagBtnClick,
                onAddTagBtnClick = pickupBottomSheetVM::onAddTagBtnClick,
                tagNameTFValue = pickupBottomSheetUiState.tagNameCreate,
                onTagNameChangeValue = pickupBottomSheetVM::onTagNameChangeValue,
                tagDescriptionTFValue = pickupBottomSheetUiState.tagDescriptionCreate,
                onTagDescriptionChangeValue = pickupBottomSheetVM::onTagDescriptionChangeValue,
                onTagCreateBtnClick = pickupBottomSheetVM::onTagCreateBtnClick,
                onCancelTagCreateBtnClick = pickupBottomSheetVM::onCancelTagCreateBtnClick,
                isTagCreationLoading = pickupBottomSheetUiState.isTagCreationLoading,
                isTagCreatorVisible = pickupBottomSheetUiState.isTagCreatorVisible,
                isTagSearcherVisible = pickupBottomSheetUiState.isTagSearcherVisible,
                isSearchingTags = pickupBottomSheetUiState.isSearchingTags,
                searchedTags = pickupBottomSheetUiState.searchedTags,
                onSearchedTagChipClick = pickupBottomSheetVM::onSearchedTagChipClick,
                isPickupVisible = pickupBottomSheetUiState.pickupLineVisibilityCreate,
                onVisibilityCheckedChange = pickupBottomSheetVM::onVisibilityCheckedChange,
                onPostBtnClick = {
                    pickupBottomSheetVM.onPostBtnClick {
                        loggedInHomeVM.onBottomSheetDismiss()
                    }
                },
                isEditMode = false,
                modifier = Modifier,
            )
        }
    }
}
