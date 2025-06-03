package com.munity.pickappbook.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.munity.pickappbook.core.data.model.PickupLine
import com.munity.pickappbook.core.data.model.Tag
import com.munity.pickappbook.core.ui.components.PickupCard
import com.munity.pickappbook.util.DateUtil

@Composable
@ExperimentalMaterial3Api
fun HomeScreenLoggedIn(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    pickupLines: List<PickupLine>,
    onVoteClick: (Int, PickupLine.Vote) -> Unit,
    onStarredBtnClick: (Int) -> Unit,
    onTagClick: (Tag) -> Unit,
    modifier: Modifier = Modifier,
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
    ) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(
                items = pickupLines,
                key = { index, pickupLine ->
                    pickupLine.id
                }
            ) { index, pickupLine ->
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
}