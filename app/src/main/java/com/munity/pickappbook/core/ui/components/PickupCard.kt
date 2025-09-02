package com.munity.pickappbook.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.munity.pickappbook.core.model.PickupLine
import com.munity.pickappbook.core.model.Tag
import com.munity.pickappbook.util.DateUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickupCard(
    pickupLine: PickupLine,
    onAuthorImageClick: (String) -> Unit,
    onAuthorClick: (String) -> Unit,
    isPersonal: Boolean,
    onEditPLClick: (PickupLine) -> Unit,
    onDeletePLClick: (PickupLine) -> Unit,
    onStarredBtnClick: () -> Unit,
    onVoteClick: (PickupLine.Reaction.Vote) -> Unit,
    onTagClick: (Tag) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

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
                        .data(pickupLine.author.profilePictureUrl)
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
                        .clickable(onClick = { onAuthorImageClick(pickupLine.author.id) })
                )

                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = pickupLine.author.username,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.clickable(onClick = { onAuthorClick(pickupLine.author.id) })
                    )
                    Text(
                        text = DateUtil.instantToTimeAgo(pickupLine.updatedAt),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Row(modifier = Modifier.padding(end = 4.dp)) {
                IconButton(
                    onClick = onStarredBtnClick,
                ) {
                    val likedIcon =
                        if (pickupLine.reaction.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
                    val contentDescription =
                        if (pickupLine.reaction.isFavorite) "Unlike pickup line" else "Like pickup line"
                    Icon(
                        imageVector = likedIcon,
                        contentDescription = contentDescription
                    )
                }

                if (isPersonal) {
                    var expandedOptions by remember { mutableStateOf(false) }

                    Box {
                        IconButton(
                            onClick = { expandedOptions = !expandedOptions },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "See options for pickup line"
                            )
                        }

                        DropdownMenu(
                            expanded = expandedOptions,
                            onDismissRequest = { expandedOptions = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = {
                                    onEditPLClick(pickupLine)
                                    expandedOptions = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit pickup line"
                                    )
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    onDeletePLClick(pickupLine)
                                    expandedOptions = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Edit pickup line"
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = pickupLine.title,
            style = TextStyle.Default.copy(fontSize = 20.sp, fontWeight = FontWeight.W600),
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 2.dp)
        )

        Text(
            text = pickupLine.content,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 16.dp, end = 12.dp, bottom = 10.dp)
        )

        UpvoteDownvoteContainer(
            statistics = pickupLine.statistics,
            vote = pickupLine.reaction.vote,
            onVoteClick = onVoteClick,
        )

        if (pickupLine.tags != null) {
            if (pickupLine.tags.isNotEmpty() && expanded) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(pickupLine.tags) { tag ->
                        TagSuggestionChip(tag = tag)
                    }
                }
            }
        } else {
            AnimatedVisibility(expanded) {
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
private fun UpvoteDownvoteContainer(
    statistics: PickupLine.Statistics?,
    vote: PickupLine.Reaction.Vote,
    onVoteClick: (PickupLine.Reaction.Vote) -> Unit,
) {
    val isFire = vote == PickupLine.Reaction.Vote.UPVOTE
    val isCold = vote == PickupLine.Reaction.Vote.DOWNVOTE

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
                onClick = { onVoteClick(PickupLine.Reaction.Vote.UPVOTE) },
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
                onClick = { onVoteClick(PickupLine.Reaction.Vote.DOWNVOTE) },
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TagSuggestionChip(tag: Tag) {
    val tooltipState = rememberTooltipState()
    val coroutineScope = rememberCoroutineScope()

    TooltipBox(
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        tooltip = {
            RichTooltip { Text(text = tag.description) }
        },
        state = tooltipState,
    ) {
        SuggestionChip(
            onClick = {
                coroutineScope.launch {
                    tooltipState.show()
                }
            },
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
