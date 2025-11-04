package com.munity.pickappbook.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.munity.pickappbook.R
import com.munity.pickappbook.core.model.PickupLine
import com.munity.pickappbook.core.model.Tag

private const val TAG = "PullToRefreshLazyPickupCards"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshLazyPickupCards(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    canLoadNewItems: Boolean,
    isLoadingNewItems: Boolean,
    listState: LazyListState = rememberLazyListState(),
    pickupLines: List<PickupLine>,
    sortingVisible: Boolean,
    sortTypeSelected: SortType? = null,
    onSortChipClick: (SortType) -> Unit = {},
    onAuthorClick: (String) -> Unit,
    loggedInUsername: String?,
    onEditPLClick: (PickupLine) -> Unit,
    onDeletePLClick: (PickupLine) -> Unit,
    onStarredBtnClick: (String) -> Unit,
    onVoteClick: (String, PickupLine.Reaction.Vote) -> Unit,
    onTagClick: (Tag) -> Unit,
    onLastPickupLineReached: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
    ) {
        LazyPickupCards(
            listState = listState,
            pickupLines = pickupLines,
            sortingVisible = sortingVisible,
            sortTypeSelected = sortTypeSelected,
            onSortChipClick = onSortChipClick,
            onAuthorClick = onAuthorClick,
            loggedInUsername = loggedInUsername,
            onEditPLClick = onEditPLClick,
            onDeletePLClick = onDeletePLClick,
            onStarredBtnClick = onStarredBtnClick,
            onVoteClick = onVoteClick,
            onTagClick = onTagClick,
            canLoadNewItems = canLoadNewItems,
            isLoadingNewItems = isLoadingNewItems,
            onLastPickupLineReached = onLastPickupLineReached,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun LazyPickupCards(
    listState: LazyListState,
    pickupLines: List<PickupLine>,
    sortingVisible: Boolean,
    sortTypeSelected: SortType? = null,
    onSortChipClick: (SortType) -> Unit,
    onAuthorClick: (String) -> Unit,
    loggedInUsername: String?,
    onEditPLClick: (PickupLine) -> Unit,
    onDeletePLClick: (PickupLine) -> Unit,
    onStarredBtnClick: (String) -> Unit,
    onVoteClick: (String, PickupLine.Reaction.Vote) -> Unit,
    onTagClick: (Tag) -> Unit,
    canLoadNewItems: Boolean,
    isLoadingNewItems: Boolean,
    onLastPickupLineReached: () -> Unit,
    modifier: Modifier = Modifier,
) {

    LaunchedEffect(key1 = listState.canScrollForward) {
        if (listState.lastScrolledForward && !listState.canScrollForward && canLoadNewItems) {
            onLastPickupLineReached()
        }
    }

    if (pickupLines.isNotEmpty()) {
        Box(modifier = modifier) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (sortingVisible) {
                    stickyHeader {
                        AnimatedVisibility(
                            visible = listState.isScrollingUp().value,
                            enter = slideInVertically(initialOffsetY = { fullHeight ->
                                -fullHeight
                            }),
                            exit = slideOutVertically(targetOffsetY = { fullHeight ->
                                -fullHeight
                            })
                        ) {
                            val screenWidthPx = LocalWindowInfo.current.containerSize.width
                            var chipsTotalWidth by remember { mutableIntStateOf(screenWidthPx + 1) }
                            val horizontalArrangement = remember(chipsTotalWidth) {
                                run {
                                    if (screenWidthPx > chipsTotalWidth)
                                        Arrangement.SpaceEvenly
                                    else
                                        Arrangement.spacedBy(4.dp)
                                }
                            }

                            Row(
                                horizontalArrangement = horizontalArrangement,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = MaterialTheme.colorScheme.surface)
                                    .horizontalScroll(rememberScrollState())
                                    .onSizeChanged { size ->
                                        chipsTotalWidth = size.width
                                    }
                            ) {
                                SortType.entries.forEach {
                                    InputChip(
                                        onClick = { onSortChipClick(it) },
                                        selected = it == sortTypeSelected,
                                        label = {
                                            Text(
                                                text = it.name,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary,
                                            )
                                        },
                                        trailingIcon = {
                                            val imageVector: ImageVector = when (it) {
                                                SortType.New -> ImageVector.vectorResource(R.drawable.new_24px)
                                                SortType.Best -> ImageVector.vectorResource(
                                                    R.drawable.crown_24px
                                                )

                                                SortType.Trending -> Icons.AutoMirrored.Outlined.TrendingUp
                                                SortType.Random -> Icons.Default.Casino
                                            }

                                            Icon(
                                                imageVector = imageVector,
                                                contentDescription = "Sorting type",
                                                Modifier.size(InputChipDefaults.AvatarSize),
                                            )
                                        },

                                        )
                                }
                            }
                        }
                    }
                }

                items(items = pickupLines, key = { it.id }) { pickupLine ->
                    PickupCard(
                        pickupLine = pickupLine,
                        onAuthorImageClick = onAuthorClick,
                        onAuthorClick = onAuthorClick,
                        isPersonal = pickupLine.author.username == loggedInUsername,
                        onEditPLClick = onEditPLClick,
                        onDeletePLClick = onDeletePLClick,
                        onStarredBtnClick = { onStarredBtnClick(pickupLine.id) },
                        onVoteClick = { vote -> onVoteClick(pickupLine.id, vote) },
                        onTagClick = onTagClick,
                        modifier = Modifier,
                    )
                }

                if (isLoadingNewItems) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .width(64.dp)
                                    .padding(16.dp),
                                color = MaterialTheme.colorScheme.secondary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        }
                    }
                }
            }


        }
    } else {
        Column(
            modifier = modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No pickup lines found",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}

enum class SortType {
    New, Best, Trending, Random
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
                listener(it)
            }
        }
    }
}
