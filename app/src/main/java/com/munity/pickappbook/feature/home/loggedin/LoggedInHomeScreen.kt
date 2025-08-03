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

@Composable
@ExperimentalMaterial3Api
fun LoggedInHomeScreen(
    modifier: Modifier = Modifier,
) {
    val loggedInHomeVM: LoggedInHomeViewModel = viewModel(factory = LoggedInHomeViewModel.Factory)
    val loggedInHomeUIState by loggedInHomeVM.loggedInHomeUiState.collectAsState()
    val feedPickupLines by loggedInHomeVM.pickupLines.collectAsState()

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
            onStarredBtnClick = loggedInHomeVM::onFavoriteBtnClick,
            onVoteClick = loggedInHomeVM::onVoteClick,
            onTagClick = {  /* TODO() */ },
            canLoadNewItems = loggedInHomeUIState.canLoadNewItems,
            isLoadingNewItems = loggedInHomeUIState.isLoadingNewItems,
            onLastPickupLineReached = loggedInHomeVM::onLastPickupLineReached,
            modifier = Modifier.padding(innerPadding)
        )

        if (loggedInHomeUIState.isBottomSheetVisible) {
            PickupBottomSheet(
                onBottomSheetDismiss = loggedInHomeVM::onBottomSheetDismiss,
                titleTFValue = loggedInHomeUIState.pickupLineTitleCreate,
                onTitleTFChange = loggedInHomeVM::onTitleTFChange,
                contentTFValue = loggedInHomeUIState.pickupLineContentCreate,
                onContentTFChange = loggedInHomeVM::onContentTFChange,
                tagsToAdd = loggedInHomeUIState.tagsToAdd,
                onAddedTagsItemClick = loggedInHomeVM::onAddedTagsItemClick,
                onSearchTagBtnClick = loggedInHomeVM::onSearchTagBtnClick,
                onAddTagBtnClick = loggedInHomeVM::onAddTagBtnClick,
                tagNameTFValue = loggedInHomeUIState.tagNameCreate,
                onTagNameChangeValue = loggedInHomeVM::onTagNameChangeValue,
                tagDescriptionTFValue = loggedInHomeUIState.tagDescriptionCreate,
                onTagDescriptionChangeValue = loggedInHomeVM::onTagDescriptionChangeValue,
                onTagCreateBtnClick = loggedInHomeVM::onTagCreateBtnClick,
                onCancelTagCreateBtnClick = loggedInHomeVM::onCancelTagCreateBtnClick,
                isTagCreationLoading = loggedInHomeUIState.isTagCreationLoading,
                isTagCreatorVisible = loggedInHomeUIState.isTagCreatorVisible,
                isTagSearcherVisible = loggedInHomeUIState.isTagSearcherVisible,
                isSearchingTags = loggedInHomeUIState.isSearchingTags,
                searchedTags = loggedInHomeUIState.searchedTags,
                onSearchedTagChipClick = loggedInHomeVM::onSearchedTagChipClick,
                isPickupVisible = loggedInHomeUIState.pickupLineVisibilityCreate,
                onVisibilityCheckedChange = loggedInHomeVM::onVisibilityCheckedChange,
                onPostBtnClick = loggedInHomeVM::onPostBtnClick,
                modifier = Modifier,
            )
        }
    }
}
