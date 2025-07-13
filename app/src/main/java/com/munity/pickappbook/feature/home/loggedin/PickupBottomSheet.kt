package com.munity.pickappbook.feature.home.loggedin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.munity.pickappbook.R
import com.munity.pickappbook.core.model.Tag
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickupBottomSheet(
    onBottomSheetDismiss: () -> Unit,
    titleTFValue: String,
    onTitleTFChange: (String) -> Unit,
    contentTFValue: String,
    onContentTFChange: (String) -> Unit,
    tagsToAdd: List<Tag>,
    onAddedTagsItemClick: (Int) -> Unit,
    onSearchTagBtnClick: () -> Unit,
    isTagSearcherVisible: Boolean,
    isSearchingTags: Boolean,
    searchedTags: List<Tag>,
    onSearchedTagChipClick: (Tag) -> Unit,
    onAddTagBtnClick: () -> Unit,
    tagNameTFValue: String,
    onTagNameChangeValue: (String) -> Unit,
    tagDescriptionTFValue: String,
    onTagDescriptionChangeValue: (String) -> Unit,
    onTagCreateBtnClick: () -> Unit,
    onCancelTagCreateBtnClick: () -> Unit,
    isTagCreatorVisible: Boolean,
    isTagCreationLoading: Boolean,
    isPickupVisible: Boolean,
    onVisibilityCheckedChange: (Boolean) -> Unit,
    onPostBtnClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onBottomSheetDismiss, sheetState = sheetState, modifier = modifier
    ) {
        Text(
            text = "Create Pickup Line",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(weight = 1f, fill = false)
        ) {
            TextField(
                value = titleTFValue,
                onValueChange = onTitleTFChange,
                placeholder = { Text(text = "Title") },
                label = { Text(text = "Title") },
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
            )

            TextField(
                value = contentTFValue,
                onValueChange = onContentTFChange,
                placeholder = { Text(text = "Content") },
                label = { Text(text = "Content") },
                maxLines = 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp),
            )

            Text(
                text = "Tags:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, top = 20.dp)
            )

            if (tagsToAdd.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy((-8).dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp)
                ) {
                    tagsToAdd.forEachIndexed { index, tag ->
                        InputChip(
                            onClick = { onAddedTagsItemClick(index) },
                            selected = true,
                            label = {
                                Text(
                                    text = tag.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remove tag",
                                    Modifier.size(InputChipDefaults.AvatarSize)
                                )
                            },
                        )
                    }
                }
            } else {
                Text(
                    text = "No tags",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp),
                )
            }

            Row {
                TextButton(
                    onClick = onSearchTagBtnClick,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = null)
                    Text(
                        text = "Search existing tag",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp),
                    )
                }

                TextButton(
                    onClick = onAddTagBtnClick,
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                    Text(
                        text = "Create tag",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp),
                    )
                }
            }

            if (isTagSearcherVisible)
                SearchTagCard(
                    isSearchingTags = isSearchingTags,
                    searchedTags = searchedTags,
                    onSearchedTagChipClick = onSearchedTagChipClick,
                    addedTags = tagsToAdd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                )

            if (isTagCreatorVisible)
                TagCreatorCard(
                    tagNameTFValue = tagNameTFValue,
                    onTagNameChangeValue = onTagNameChangeValue,
                    tagDescriptionTFValue = tagDescriptionTFValue,
                    onTagDescriptionChangeValue = onTagDescriptionChangeValue,
                    onTagCreateBtnClick = onTagCreateBtnClick,
                    onCancelTagCreateBtnClick = onCancelTagCreateBtnClick,
                    isTagCreationLoading = isTagCreationLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp, end = 12.dp),
            ) {
                val visibilityText =
                    if (isPickupVisible) stringResource(R.string.visible_to_everyone)
                    else stringResource(R.string.visible_only_to_you)

                Text(text = visibilityText, modifier = Modifier.padding(horizontal = 12.dp))

                Switch(
                    checked = isPickupVisible,
                    onCheckedChange = onVisibilityCheckedChange,
                    thumbContent = {
                        val imageVector =
                            if (isPickupVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

                        Icon(
                            imageVector = imageVector,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    })
            }
        }

        Button(
            onClick = {
                onPostBtnClick()

                scope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    if (!sheetState.isVisible)
                        onBottomSheetDismiss()
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp, end = 20.dp, bottom = 20.dp),
        ) {
            Text("Post")
        }
    }
}

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

@Composable
fun TagCreatorCard(
    tagNameTFValue: String,
    onTagNameChangeValue: (String) -> Unit,
    tagDescriptionTFValue: String,
    onTagDescriptionChangeValue: (String) -> Unit,
    onTagCreateBtnClick: () -> Unit,
    onCancelTagCreateBtnClick: () -> Unit,
    isTagCreationLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Text(
            text = "Create tag",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 4.dp)
        )

        OutlinedTextField(
            value = tagNameTFValue,
            onValueChange = onTagNameChangeValue,
            placeholder = { Text(text = "Tag name") },
            label = { Text(text = "Tag name") },
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
        )

        OutlinedTextField(
            value = tagDescriptionTFValue,
            onValueChange = onTagDescriptionChangeValue,
            placeholder = { Text(text = "Tag description") },
            label = { Text(text = "Tag description") },
            maxLines = 2,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp)
        )

        Row(
            horizontalArrangement = if (isTagCreationLoading) Arrangement.SpaceBetween else Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (isTagCreationLoading)
                CircularProgressIndicator()

            Row {
                OutlinedButton(
                    onClick = onCancelTagCreateBtnClick,
                    modifier = Modifier
                        .padding(end = 12.dp)
                ) {
                    Text(text = "Cancel")
                }

                Button(onClick = onTagCreateBtnClick) {
                    Text(text = "Create tag")
                }
            }
        }
    }
}
