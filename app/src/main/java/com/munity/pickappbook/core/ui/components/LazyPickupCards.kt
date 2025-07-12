package com.munity.pickappbook.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.munity.pickappbook.core.data.remote.model.PickupLineResponse
import com.munity.pickappbook.core.data.remote.model.TagResponse
import com.munity.pickappbook.util.DateUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshLazyPickupCards(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    pickupLines: List<PickupLineResponse>,
    onStarredBtnClick: (Int) -> Unit,
    onVoteClick: (Int, PickupLineResponse.Vote) -> Unit,
    onTagClick: (TagResponse) -> Unit,
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
            modifier = Modifier
        )
    }
}

@Composable
private fun LazyPickupCards(
    pickupLines: List<PickupLineResponse>,
    onStarredBtnClick: (Int) -> Unit,
    onVoteClick: (Int, PickupLineResponse.Vote) -> Unit,
    onTagClick: (TagResponse) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (pickupLines.isNotEmpty()) {
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
                    reaction = pickupLine.reaction ?: PickupLineResponse.Reaction(
                        isStarred = false,
                        vote = PickupLineResponse.Vote.NONE
                    ),
                    statistics = pickupLine.statistics,
                    onStarredBtnClick = { onStarredBtnClick(index) },
                    onVoteClick = { vote -> onVoteClick(index, vote) },
                    onTagClick = onTagClick,
                    modifier = Modifier,
                )
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
