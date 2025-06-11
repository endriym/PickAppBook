package com.munity.pickappbook.feature.home.loggedin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.munity.pickappbook.core.data.model.PickupLine
import com.munity.pickappbook.core.data.model.Tag
import com.munity.pickappbook.core.ui.components.PickupCard
import com.munity.pickappbook.util.DateUtil

@Composable
@ExperimentalMaterial3Api
fun LoggedInHomeScreen(
    modifier: Modifier = Modifier,
) {
    val loggedInHomeVM: LoggedInHomeViewModel = viewModel(factory = LoggedInHomeViewModel.Factory)
    val loggedInHomeUIState by loggedInHomeVM.loggedInHomeUiState.collectAsState()

    Scaffold(
        floatingActionButton = { PickupLineFAB(onClick = loggedInHomeVM::onFABClick) },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(NavigationBarDefaults.windowInsets)
            .exclude(TopAppBarDefaults.windowInsets),
        modifier = modifier
    ) { innerPadding ->

        PullToRefreshBox(
            isRefreshing = loggedInHomeUIState.isRefreshing,
            onRefresh = loggedInHomeVM::onPullToRefresh,
            modifier = Modifier.padding(innerPadding)
        ) {
            LazyPickupCards(
                pickupLines = loggedInHomeVM.pickupLines,
                onStarredBtnClick = loggedInHomeVM::onStarredBtnClick,
                onVoteClick = loggedInHomeVM::onVoteClick,
                onTagClick = { /*TODO*/ },
                modifier = Modifier
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
}

@Composable
fun LazyPickupCards(
    pickupLines: List<PickupLine>,
    onStarredBtnClick: (Int) -> Unit,
    onVoteClick: (Int, PickupLine.Vote) -> Unit,
    onTagClick: (Tag) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxSize()
    ) {
        itemsIndexed(
            items = pickupLines, key = { index, pickupLine ->
                pickupLine.id
            }) { index, pickupLine ->

            PickupCard(
                authorImageUrl = pickupLine.userJpegImageUrl,
                author = pickupLine.username,
                postDate = DateUtil.iso8601ToTimeAgo(pickupLine.updatedAt),
                titleLine = pickupLine.title,
                line = pickupLine.content,
                tags = pickupLine.tags,
                reactions = pickupLine.reactions,
                statistics = pickupLine.statistics,
                onStarredBtnClick = { onStarredBtnClick(index) },
                onVoteClick = { vote -> onVoteClick(index, vote) },
                onTagClick = onTagClick,
                modifier = Modifier,
            )
        }
    }
}