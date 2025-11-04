package com.munity.pickappbook.feature.search

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.munity.pickappbook.core.model.Tag
import com.munity.pickappbook.core.model.User
import com.munity.pickappbook.core.ui.components.PullToRefreshLazyPickupCards
import com.munity.pickappbook.core.ui.components.YouNeedToLogin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(onAuthorClick: (String) -> Unit, modifier: Modifier = Modifier) {
    val searchVM: SearchViewModel = viewModel(factory = SearchViewModel.Factory)
    val searchUiState: SearchUIState by searchVM.searchUiState.collectAsState()
    val isLoggedIn by searchVM.isLoggedIn.collectAsState()
    val userQuery: String by searchVM.userQuery.collectAsState()
    val tagFilterQuery: String by searchVM.tagFilterQuery.collectAsState()
    val matchingTags: List<Tag> by searchVM.matchingTags.collectAsState()

    if (isLoggedIn) {
        Column(modifier = modifier) {
            SearchBarWithFilters(
                query = searchUiState.query,
                onQueryChange = searchVM::onQueryChange,
                onTrailingClearIconClick = searchVM::onTrailingClearIconClick,
                onSearch = searchVM::onSearch,
                isSearchComplete = searchUiState.isSearchComplete,

                isFavoriteFilterOn = searchUiState.isFavoriteQuery,
                successPercentage = searchUiState.sliderValue?.toInt(),
                userFilter = searchUiState.userFilter,
                filterTags = searchVM.filterTags,
                onFilterChipClick = searchVM::onFilterChipClick,
                onTagFilterChipClick = searchVM::onTagFilterChipClick,
                onFilterBtnClick = searchVM::onFilterBtnClick,
                modifier = Modifier.fillMaxWidth(),
            )

            PullToRefreshLazyPickupCards(
                isRefreshing = searchUiState.isSearching,
                onRefresh = searchVM::onSearch,
                pickupLines = searchVM.searchedPickupLines,
                sortingVisible = false,
                onSortChipClick = { TODO() },
                onAuthorClick = { /*TODO*/ },
                onStarredBtnClick = searchVM::onPLStarredBtnClick,
                loggedInUsername = null,
                onEditPLClick = { /* TODO */ },
                onDeletePLClick = { /* TODO */ },
                onVoteClick = searchVM::onPLVoteClick,
                onTagClick = { /*TODO*/ },
                canLoadNewItems = false, //TODO,
                isLoadingNewItems = false,
                onLastPickupLineReached = {}, //TODO
                modifier = Modifier.fillMaxSize(),
            )
        }

        if (searchUiState.isBottomSheetVisible) {
            SearchBottomSheet(
                onBottomSheetDismiss = searchVM::onBottomSheetDismiss,
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),

                queryTypeValue = searchUiState.queryType,
                onQueryTypeChange = searchVM::onQueryTypeChange,

                isFavoriteQuery = searchUiState.isFavoriteQuery ?: false,
                onFavoriteQueryChange = searchVM::onIsFavoriteQueryChange,

                sliderValue = searchUiState.sliderValue ?: 0f,
                onSliderValueChange = searchVM::onSliderValueChange,

                tagQueryValue = tagFilterQuery,
                onTagQueryValueChange = searchVM::onTagFilterQueryChange,
                isTagDropdownExpanded = searchUiState.isTagDropdownExpanded,
                matchingTags = matchingTags,
                filterTags = searchVM.filterTags,
                onFilterTagClick = searchVM::onFilterTagClick,
                onDropdownItemTagClick = searchVM::onDropdownItemTagClick,
                onTagDropdownDismissRequest = searchVM::onTagDropdownDismissRequest,

                userFilter = searchUiState.userFilter,
                onRemoveUserSelected = searchVM::onRemoveUserSelected,
                onSearchUserBtnClick = searchVM::onSearchUserBtnClick,
                isUserSearcherVisible = searchUiState.isUserSearcherVisible,
                userQuery = userQuery,
                onUserQueryChange = searchVM::onUserQueryChange,
                isSearchingUsers = searchUiState.isSearchingUsers,
                searchedUsers = searchVM.searchedUsers,
                onSearchedUserClick = searchVM::onSearchedUserClick,

                onApplyFiltersBtnClick = searchVM::onApplyFiltersClick,
                modifier = Modifier,
            )
        }
    } else {
        YouNeedToLogin(modifier = Modifier.fillMaxSize())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onTrailingClearIconClick: () -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onTrailingClearIconClick) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                }
            }
        },
        colors = TextFieldDefaults.colors().copy(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        singleLine = true,
        shape = SearchBarDefaults.inputFieldShape,
        modifier = modifier.clip(SearchBarDefaults.inputFieldShape)
    )
}

@Composable
fun SearchBarWithFilters(
    query: String,
    onQueryChange: (String) -> Unit,
    onTrailingClearIconClick: () -> Unit,
    onSearch: () -> Unit,
    isSearchComplete: Boolean,
    onFilterBtnClick: () -> Unit,
    isFavoriteFilterOn: Boolean?,
    successPercentage: Int?,
    userFilter: User?,
    filterTags: List<Tag>,
    onFilterChipClick: (FilterType) -> Unit,
    onTagFilterChipClick: (Tag) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            val simpleSearchPadding = if (isSearchComplete)
                PaddingValues(horizontal = 4.dp, vertical = 4.dp)
            else
                PaddingValues(horizontal = 8.dp, vertical = 4.dp)

            SimpleSearchBar(
                query = query,
                onQueryChange = onQueryChange,
                onTrailingClearIconClick = onTrailingClearIconClick,
                onSearch = onSearch,
                modifier = Modifier
                    .fillMaxWidth(if (isSearchComplete) 0.85f else 1f)
                    .padding(simpleSearchPadding)
            )

            if (isSearchComplete) {
                FilledTonalIconButton(
                    onClick = onFilterBtnClick,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Add filters to search"
                    )
                }
            }
        }

        if (isSearchComplete) {
            FilterBar(
                isFavoriteFilterOn = isFavoriteFilterOn,
                successPercentage = successPercentage,
                userFilter = userFilter,
                filterTags = filterTags,
                onFilterChipClick = onFilterChipClick,
                onTagFilterChipClick = onTagFilterChipClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun FilterBar(
    isFavoriteFilterOn: Boolean?,
    successPercentage: Int?,
    userFilter: User?,
    filterTags: List<Tag>,
    onFilterChipClick: (FilterType) -> Unit,
    onTagFilterChipClick: (Tag) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(start = 8.dp, end = 8.dp)
    ) {
        isFavoriteFilterOn?.let {
            FilterChip(
                selected = true,
                onClick = { onFilterChipClick(FilterType.FAVORITE) },
                leadingIcon = {
                    Icon(
                        imageVector = if (it) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                        contentDescription = "Favorite search filter"
                    )
                },
                label = { Text(text = if (it) "Only favorites" else "No favorite") },
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        }


        successPercentage?.let {
            FilterChip(
                selected = true,
                onClick = { onFilterChipClick(FilterType.SUCCESS_PERCENTAGE) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Percent,
                        contentDescription = "Success percentage search filter"
                    )
                },
                label = { Text(text = ">= $successPercentage") },
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        }

        userFilter?.let {
            FilterChip(
                selected = true,
                onClick = { onFilterChipClick(FilterType.USER) },
                leadingIcon = {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(userFilter.profilePictureUrl)
                            .crossfade(true)
                            .build(),
                        placeholder = rememberVectorPainter(Icons.Default.AccountCircle),
                        fallback = rememberVectorPainter(Icons.Default.AccountCircle),
                        error = rememberVectorPainter(Icons.Default.AccountCircle),
                        contentDescription = "Author's profile picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(25.dp)
                            .clip(CircleShape)
                    )
                },
                label = { Text(text = userFilter.username, maxLines = 1) },
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        }

        filterTags.forEach { tag ->
            FilterChip(
                selected = true,
                onClick = { onTagFilterChipClick(tag) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Sell, contentDescription = "Tag search filter")
                },
                label = { Text(text = tag.name) },
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        }
    }
}

@Preview
@Composable
private fun SearchBarWithFiltersPreview() {
//    SearchBarWithFilters(
//        query = "content",
//        onQueryChange = { },
//        onTrailingClearIconClick = {},
//        onSearch = {},
//        isSearchComplete = true,
//        onFilterBtnClick = {},
//        modifier = Modifier.fillMaxSize(),
//        isFavoriteFilterOn = ,
//        successPercentage = TODO(),
//        userFilter = TODO(),
//        filterTags = TODO(),
//        onFilterChipClick = TODO(),
//        onTagFilterChipClick = TODO()
//    )
}

@Preview
@Composable
private fun SimpleSearchBarPreview() {
    SimpleSearchBar(
        query = "content",
        onQueryChange = {},
        onTrailingClearIconClick = {},
        onSearch = {},
        modifier = Modifier.fillMaxWidth()
    )
}
