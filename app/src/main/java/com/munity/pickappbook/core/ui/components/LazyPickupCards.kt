package com.munity.pickappbook.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.munity.pickappbook.core.model.PickupLine
import com.munity.pickappbook.core.model.Tag
import com.munity.pickappbook.util.DateUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshLazyPickupCards(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    canLoadNewItems: Boolean,
    isLoadingNewItems: Boolean,
    pickupLines: List<PickupLine>,
    onStarredBtnClick: (Int) -> Unit,
    onVoteClick: (Int, PickupLine.Reaction.Vote) -> Unit,
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
            pickupLines = pickupLines,
            onStarredBtnClick = onStarredBtnClick,
            onVoteClick = onVoteClick,
            onTagClick = onTagClick,
            isRefreshing = isRefreshing,
            canLoadNewItems = canLoadNewItems,
            isLoadingNewItems = isLoadingNewItems,
            onLastPickupLineReached = onLastPickupLineReached,
            modifier = Modifier
        )
    }
}

@Composable
private fun LazyPickupCards(
    pickupLines: List<PickupLine>,
    onStarredBtnClick: (Int) -> Unit,
    onVoteClick: (Int, PickupLine.Reaction.Vote) -> Unit,
    onTagClick: (Tag) -> Unit,
    isRefreshing: Boolean,
    canLoadNewItems: Boolean,
    isLoadingNewItems: Boolean,
    onLastPickupLineReached: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(key1 = listState.canScrollForward) {
        if (canLoadNewItems && !listState.canScrollForward && !isRefreshing) {
            onLastPickupLineReached()
        }
    }

    if (pickupLines.isNotEmpty()) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier.fillMaxSize()
        ) {
            itemsIndexed(
                items = pickupLines, key = { index, pickupLine ->
                    pickupLine.id
                }) { index, pickupLine ->

                PickupCard(
                    authorImageUrl = pickupLine.author.profilePictureUrl,
                    author = pickupLine.author.username,
                    postDate = DateUtil.instantToTimeAgo(pickupLine.updatedAt),
                    titleLine = pickupLine.title,
                    line = pickupLine.content,
                    tags = pickupLine.tags,
                    reaction = pickupLine.reaction,
                    statistics = pickupLine.statistics,
                    onStarredBtnClick = { onStarredBtnClick(index) },
                    onVoteClick = { vote -> onVoteClick(index, vote) },
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
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
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
