package com.munity.pickappbook.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.munity.pickappbook.R
import com.munity.pickappbook.core.data.model.PickupLine
import com.munity.pickappbook.core.data.model.Tag

@Composable
fun PickupCard(
    authorImageUrl: String,
    author: String,
    postDate: String,
    titleLine: String,
    line: String,
    reactions: List<PickupLine.Reaction>?,
    onStarredBtnClick: () -> Unit,
    statistics: PickupLine.Statistics?,
    onVoteClick: (PickupLine.Vote) -> Unit,
    tags: List<Tag>?,
    onTagClick: (Tag) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val isStarred = reactions?.first()?.isStarred == true
    val vote = reactions?.first()?.vote ?: PickupLine.Vote.NONE

    Card(
        modifier = modifier
            .clip(CardDefaults.shape)
            .clickable { expanded = !expanded }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 4.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(authorImageUrl)
                        .crossfade(true)
                        .build(),
                    placeholder = rememberVectorPainter(Icons.Default.AccountCircle),
                    fallback = rememberVectorPainter(Icons.Default.AccountCircle),
                    error = rememberVectorPainter(Icons.Default.AccountCircle),
                    contentDescription = "Author's profile picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                )

                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = author, style = MaterialTheme.typography.titleSmall)
                    Text(text = postDate, style = MaterialTheme.typography.bodySmall)
                }
            }

            IconButton(
                onClick =
                    onStarredBtnClick, modifier = Modifier.padding(end = 4.dp)
            ) {
                val likedIcon =
                    if (isStarred) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
                val contentDescription =
                    if (isStarred) "Unlike pickup line" else "Like pickup line"
                Icon(
                    imageVector = likedIcon,
                    contentDescription = contentDescription
                )
            }
        }

        Text(
            text = titleLine,
            style = TextStyle.Default.copy(fontSize = 20.sp, fontWeight = FontWeight.W600),
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 2.dp)
        )
        Text(
            text = line,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 16.dp, end = 12.dp, bottom = 10.dp)
        )

        UpvoteDownvoteContainer(
            statistics = statistics,
            vote = vote,
            onVoteClick = onVoteClick,
        )

        if (tags != null) {
            if (tags.isNotEmpty() && expanded) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(tags) { tag ->
                        SuggestionChip(
                            onClick = { onTagClick(tag) },
                            label = {
                                Text(
                                    text = tag.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        )
                    }
                }
            }
        } else {
            if (expanded) {
                Text(
                    text = "No tags",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp)
                )
            }
        }
    }
}

@Composable
fun UpvoteDownvoteContainer(
    statistics: PickupLine.Statistics?,
    vote: PickupLine.Vote,
    onVoteClick: (PickupLine.Vote) -> Unit,
) {
    val isFire = vote == PickupLine.Vote.UPVOTE
    val isCold = vote == PickupLine.Vote.DOWNVOTE

    Surface(
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 200.dp,
        shadowElevation = 5.dp,
        modifier = Modifier
            .padding(8.dp)
            .width(140.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Fire (upvote) button
            IconButton(
                onClick = { onVoteClick(PickupLine.Vote.UPVOTE) },
                modifier = Modifier.size(40.dp)
            ) {
                val iconId = if (isFire) R.drawable.fire_color else R.drawable.fire_light
                Image(
                    painter = painterResource(iconId),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "${statistics?.successPercentage?.toInt() ?: 0} %",
            )

            // Cold (downvote) button
            IconButton(
                onClick = { onVoteClick(PickupLine.Vote.DOWNVOTE) },
                modifier = Modifier.size(40.dp)
            ) {
                val iconId =
                    if (isCold) R.drawable.emoji_cold_color else R.drawable.emoji_cold_no_color
                Image(
                    painter = painterResource(iconId),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}