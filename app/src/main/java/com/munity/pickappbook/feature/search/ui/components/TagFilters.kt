package com.munity.pickappbook.feature.search.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import com.munity.pickappbook.core.model.Tag
import com.munity.pickappbook.feature.search.FilterTitle

@Composable
fun NewTagFilter(
    tagQueryValue: String,
    onTagQueryValueChange: (String) -> Unit,
    matchingTags: List<Tag>,
    filterTags: List<Tag>,
    onFilterTagClick: (Int) -> Unit,
    isTagDropdownExpanded: Boolean,
    onDropdownItemTagClick: (Int) -> Unit,
    onTagDropdownDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var columnWidth by remember { mutableIntStateOf(0) }

    Column(modifier = modifier) {
        FilterTitle(
            text = "Tag filters:",
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp)
                .clip(CircleShape)
                .background(
                    color = TextFieldDefaults.colors().unfocusedContainerColor,
                    shape = TextFieldDefaults.shape
                )
                .padding(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = TextFieldDefaults.colors().focusedLeadingIconColor,
                modifier = Modifier.padding(start = 8.dp, end = 4.dp)
            )

            FlowRow(
                verticalArrangement = Arrangement.spacedBy((-8).dp),
                itemVerticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .onGloballyPositioned { layoutCoordinates ->
                        columnWidth = layoutCoordinates.size.width
                    }
            ) {
                filterTags.forEachIndexed { index, tag ->
                    InputChip(
                        selected = false,
                        onClick = { onFilterTagClick(index) },
                        label = { Text(text = tag.name) },
                        trailingIcon = {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        },
                        colors = InputChipDefaults.inputChipColors()
                            .copy(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }

                BasicTextField(
                    value = tagQueryValue,
                    onValueChange = onTagQueryValueChange,
                    cursorBrush = SolidColor(TextFieldDefaults.colors().cursorColor),
                    singleLine = true,
                    modifier = Modifier
                        .padding(4.dp)
                        .widthIn(min = 20.dp)
                        .weight(1f)
                )

                DropdownMenu(
                    expanded = isTagDropdownExpanded,
                    onDismissRequest = onTagDropdownDismissRequest,
                    properties = PopupProperties(focusable = false),
                    modifier = Modifier
                        .width(
                            with(LocalDensity.current) {
                                columnWidth.toDp()
                            }
                        )
                        .heightIn(max = 250.dp),
                ) {
                    matchingTags.forEachIndexed { index, tag ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = { onDropdownItemTagClick(index) })
                                .padding(8.dp)
                        ) {
                            Column {
                                Text(text = tag.name, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text = tag.description,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            if (tag in filterTags) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NewTagFilterPreview() {
    var textFieldValue: String by remember { mutableStateOf("") }
    var isExpanded: Boolean by remember { mutableStateOf(true) }

    val addedTags: SnapshotStateList<Tag> = remember {
        SnapshotStateList(3, init = {
            Tag("id$it", "Tag $it", "Tag Description $it", "User 1")
        })
    }

    val searchedTags = List<Tag>(16) {
        val id = it + 5
        Tag("id$id", "Tag $id", "Tag Description $id", "User 1")
    }
}

@Preview(showSystemUi = true)
@Composable
fun SearchBarWithSuggestions() {
    var columnWidth by remember { mutableStateOf(0) }
    var query by remember { mutableStateOf("") }
    val suggestions = listOf("Apple", "Banana", "Cherry", "Date", "Grapes", "Kiwi", "Lemon")
    val filteredSuggestions = suggestions.filter { it.contains(query, ignoreCase = true) }

    // To control the visibility of the dropdown
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(top = 80.dp, start = 12.dp, end = 12.dp)) {
        // Search bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { layoutCoordinates ->
                    columnWidth = layoutCoordinates.size.width
                }
        ) {
            TextField(
                value = query,
                onValueChange = {
                    query = it
                    expanded =
                        it.isNotEmpty() // Show dropdown only when the user types something
                },
                label = { Text("Search") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f)
            )

            // Suggestions dropdown
            DropdownMenu(
                expanded = expanded && filteredSuggestions.isNotEmpty(),
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = false),
                modifier = Modifier
                    .width(
                        with(LocalDensity.current) {
                            columnWidth.toDp()
                        }
                    )
                    .padding(top = 8.dp) // Position the dropdown just below the TextField
                    .zIndex(2f)
            ) {
                filteredSuggestions.forEach { suggestion ->
                    DropdownMenuItem(
                        onClick = {
                            query = suggestion
                            expanded = false // Dismiss the dropdown after selection
                        },
                        text = { Text(text = suggestion) }
                    )
                }
            }
        }
    }
}
