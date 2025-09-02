package com.munity.pickappbook.feature.account

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ImportContacts
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ImportContacts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.munity.pickappbook.core.ui.components.PullToRefreshLazyPickupCards
import com.munity.pickappbook.core.ui.components.YouNeedToLogin
import com.munity.pickappbook.feature.pickupbottomsheet.PickupBottomSheet
import com.munity.pickappbook.feature.pickupbottomsheet.PickupBottomSheetViewModel

private enum class SelectedTab(val index: Int) {
    POSTS(0), FAVORITES(1)
}

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    userId: String?,
    onPLAuthorClick: (String) -> Unit,
) {
    if (userId != null) {
        val accountVM: AccountViewModel =
            viewModel(factory = AccountViewModel.factoryWithUsername(userId))
        val accountUiState by accountVM.accountUiState.collectAsState()
        var selectedTab by remember { mutableStateOf(SelectedTab.POSTS) }
        val isLoggedIn by accountVM.isLoggedIn.collectAsState()
        val isLoggedInUser by accountVM.isLoggedInUser.collectAsState()
        val loggedInUser by accountVM.loggedInUser.collectAsState()
        val user by accountVM.user.collectAsState()
        val postedPickupLines by accountVM.postedPickupLines.collectAsState()
        val favoritePickupLines by accountVM.favoritePickupLines.collectAsState()
//        val lazyListState = rememberLazyListState()
//        val isScrollingUp = lazyListState.isScrollingUp()

        if (isLoggedIn) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(user?.profilePictureUrl)
                                .crossfade(true)
                                .build(),
                            placeholder = rememberVectorPainter(Icons.Default.AccountCircle),
                            fallback = rememberVectorPainter(Icons.Default.AccountCircle),
                            error = rememberVectorPainter(Icons.Default.AccountCircle),
                            contentDescription = "Author's profile picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(16.dp)
                                .size(120.dp)
                                .clip(CircleShape)
                        )

                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = user?.displayName ?: "No display name found",
                                style = MaterialTheme.typography.titleLarge,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 12.dp)
                            )

                            Text(
                                text = user?.username ?: "No username found",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                            )
                        }
                    }

                    if (isLoggedInUser)
                        IconButton(
                            onClick = accountVM::onLogoutBtnClick,
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Logout",
                                tint = Color(0xffff3b3b)
                            )
                        }
                }

                if (isLoggedInUser) {
                    LoggedInAccountPrimaryTabRow(
                        selectedTab = selectedTab,
                        modifier = Modifier.fillMaxWidth()
                    ) { newSelectedTab -> selectedTab = newSelectedTab }

                    when (selectedTab) {
                        SelectedTab.POSTS -> PullToRefreshLazyPickupCards(
                            isRefreshing = accountUiState.isPostedRefreshing,
                            onRefresh = accountVM::onPostedPLRefresh,
                            pickupLines = postedPickupLines,
                            onAuthorClick = onPLAuthorClick,
                            onStarredBtnClick = accountVM::onPostedPLStarredBtnClick,
                            loggedInUsername = loggedInUser?.username,
                            onEditPLClick = accountVM::onPostedEditPLClick,
                            onDeletePLClick = accountVM::onPostedDeletePLClick,
                            onVoteClick = accountVM::onPostedPLVoteClick,
                            onTagClick = { /* TODO */ },
                            canLoadNewItems = accountUiState.canLoadNewPostedItems,
                            isLoadingNewItems = accountUiState.isLoadingNewPostedItems,
                            onLastPickupLineReached = accountVM::onLastPostedPLReached,
                            modifier = modifier.fillMaxSize(),
                        )

                        SelectedTab.FAVORITES -> PullToRefreshLazyPickupCards(
                            isRefreshing = accountUiState.isFavoriteRefreshing,
                            onRefresh = accountVM::onFavoritePLRefresh,
                            pickupLines = favoritePickupLines,
                            onAuthorClick = onPLAuthorClick,
                            onStarredBtnClick = accountVM::onFavoritePLStarredBtnClick,
                            loggedInUsername = loggedInUser?.username,
                            onEditPLClick = accountVM::onFavoriteEditPLClick,
                            onDeletePLClick = accountVM::onFavoriteDeletePLClick,
                            onVoteClick = accountVM::onFavoritePLVoteClick,
                            onTagClick = { /*TODO()*/ },
                            canLoadNewItems = accountUiState.canLoadNewFavoriteItems,
                            isLoadingNewItems = accountUiState.isLoadingNewFavoriteItems,
                            onLastPickupLineReached = accountVM::onLastFavoritePLReached,
                            modifier = modifier.fillMaxSize(),
                        )
                    }
                } else {
                    SinglePrimaryTabRow(modifier = Modifier.fillMaxWidth())

                    PullToRefreshLazyPickupCards(
                        isRefreshing = accountUiState.isPostedRefreshing,
                        onRefresh = accountVM::onPostedPLRefresh,
                        pickupLines = postedPickupLines,
                        onAuthorClick = onPLAuthorClick,
                        onStarredBtnClick = accountVM::onPostedPLStarredBtnClick,
                        loggedInUsername = loggedInUser?.username,
                        onEditPLClick = accountVM::onPostedEditPLClick,
                        onDeletePLClick = accountVM::onPostedDeletePLClick,
                        onVoteClick = accountVM::onPostedPLVoteClick,
                        onTagClick = { /* TODO */ },
                        canLoadNewItems = accountUiState.canLoadNewPostedItems,
                        isLoadingNewItems = accountUiState.isLoadingNewPostedItems,
                        onLastPickupLineReached = accountVM::onLastPostedPLReached,
                        modifier = modifier.fillMaxSize(),
                    )
                }

                if (accountUiState.isBottomSheetVisible) {
                    val pickupBottomSheetVM: PickupBottomSheetViewModel =
                        viewModel(factory = PickupBottomSheetViewModel.factory(pickupLineToEditId = accountUiState.pickupLineToEditId))
                    val pickupBottomSheetUiState by pickupBottomSheetVM.pickupBottomSheetUiState.collectAsState()

                    PickupBottomSheet(
                        onBottomSheetDismiss = accountVM::onBottomSheetDismiss,
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
                                accountVM.onBottomSheetDismiss()
                            }
                        },
                        isEditMode = true,
                        modifier = Modifier,
                    )
                }
            }
        } else {
            YouNeedToLogin(modifier = Modifier.fillMaxSize())
        }
    } else {
        YouNeedToLogin(modifier = Modifier.fillMaxSize())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoggedInAccountPrimaryTabRow(
    selectedTab: SelectedTab,
    modifier: Modifier = Modifier,
    onTabSelection: (SelectedTab) -> Unit,
) {
    PrimaryTabRow(
        selectedTabIndex = selectedTab.index,
        indicator = {
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier.tabIndicatorOffset(
                    selectedTab.index,
                    matchContentSize = true
                ),
                width = 68.dp,
            )
        },
        modifier = modifier
    ) {
        Tab(
            selected = selectedTab == SelectedTab.POSTS,
            onClick = { onTabSelection(SelectedTab.POSTS) },
            modifier = Modifier.height(50.dp)
        ) {
            Icon(
                imageVector = if (selectedTab == SelectedTab.POSTS) Icons.Filled.ImportContacts else Icons.Outlined.ImportContacts,
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .padding(4.dp)
            )
        }

        Tab(
            selected = selectedTab == SelectedTab.FAVORITES,
            onClick = { onTabSelection(SelectedTab.FAVORITES) },
        ) {
            Icon(
                imageVector = if (selectedTab == SelectedTab.FAVORITES) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .padding(4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SinglePrimaryTabRow(modifier: Modifier = Modifier) {
    PrimaryTabRow(
        selectedTabIndex = 0,
        indicator = {
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier.tabIndicatorOffset(
                    0,
                    matchContentSize = true
                ),
                width = 68.dp,
            )
        },
        modifier = modifier
    ) {
        Tab(
            selected = true,
            onClick = { },
            modifier = Modifier.height(50.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ImportContacts,
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .padding(4.dp)
            )
        }
    }
}

/**
 * Returns whether the lazy list is currently scrolling up.
 */
@Composable
private fun LazyListState.isScrollingUp(listener: (Boolean) -> Unit = {}): State<Boolean> {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }

    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }
}
