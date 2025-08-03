package com.munity.pickappbook.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.munity.pickappbook.core.model.Tag

@Composable
fun SearchTagCard(
    isSearchingTags: Boolean,
    searchedTags: List<Tag>,
    onSearchedTagChipClick: (Tag) -> Unit,
    addedTags: List<Tag>,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    var filteredTags by remember(key1 = searchedTags) { mutableStateOf(searchedTags) }

    Card(modifier = modifier) {
        Text(
            text = "Search tag",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 4.dp)
        )

        if (isSearchingTags)
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            )
        else {
            if (filteredTags.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(filteredTags) { tag ->
                        InputChip(
                            onClick = { onSearchedTagChipClick(tag) },
                            selected = tag in addedTags,
                            label = {
                                Text(
                                    text = tag.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            },
                            trailingIcon = {
                                if (tag in addedTags)
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        Modifier.size(InputChipDefaults.AvatarSize)
                                    )
                            },
                        )
                    }
                }
            } else {
                Text(
                    text = "No tags found",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 16.dp)
                )
            }
        }

        if (searchedTags.isNotEmpty()) {
            OutlinedTextField(
                value = query,
                onValueChange = { newQuery ->
                    query = newQuery

                    filteredTags = if (newQuery.isNotEmpty())
                        searchedTags.filter { tag ->
                            tag.name.contains(other = newQuery, ignoreCase = true) ||
                                    tag.description.contains(other = newQuery, ignoreCase = true)
                        }
                    else
                        searchedTags
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null
                    )
                },
                placeholder = { Text(text = "Search query") },
                label = { Text(text = "Search query") },
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun SearchTagCardPreview() {
    SearchTagCard(
        isSearchingTags = false,
        searchedTags = listOf<Tag>(Tag("id", "Tennis", "Sport played with a racket", "asdf")),
        onSearchedTagChipClick = {},
        addedTags = listOf<Tag>(Tag("id", "Tennis", "Sport played with a racket", "asdf")),
        modifier = Modifier.fillMaxWidth()
    )
}
