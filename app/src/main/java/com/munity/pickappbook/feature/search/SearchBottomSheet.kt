package com.munity.pickappbook.feature.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.munity.pickappbook.core.model.Tag
import com.munity.pickappbook.core.model.User
import com.munity.pickappbook.feature.search.ui.components.FavoriteFilter
import com.munity.pickappbook.feature.search.ui.components.NewTagFilter
import com.munity.pickappbook.feature.search.ui.components.QueryTypeFilter
import com.munity.pickappbook.feature.search.ui.components.SuccessPercentageFilter
import com.munity.pickappbook.feature.search.ui.components.UserFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBottomSheet(
    onBottomSheetDismiss: () -> Unit,
    sheetState: SheetState,

    queryTypeValue: QueryType?,
    onQueryTypeChange: (QueryType) -> Unit,

    isFavoriteQuery: Boolean,
    onFavoriteQueryChange: (Boolean) -> Unit,

    sliderValue: Float,
    onSliderValueChange: (Float) -> Unit,

    tagQueryValue: String,
    onTagQueryValueChange: (String) -> Unit,
    matchingTags: List<Tag>,
    filterTags: List<Tag>,
    onFilterTagClick: (Int) -> Unit,
    isTagDropdownExpanded: Boolean,
    onDropdownItemTagClick: (Int) -> Unit,
    onTagDropdownDismissRequest: () -> Unit,

    userFilter: User?,
    onRemoveUserSelected: () -> Unit,
    onSearchUserBtnClick: () -> Unit,
    isUserSearcherVisible: Boolean,
    userQuery: String,
    onUserQueryChange: (String) -> Unit,
    isSearchingUsers: Boolean,
    searchedUsers: List<User>,
    onSearchedUserClick: (User) -> Unit,

    onApplyFiltersBtnClick: () -> Unit,

    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onBottomSheetDismiss, sheetState = sheetState, modifier = modifier
    ) {
        Text(
            text = "Advanced search",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(weight = 1f, fill = false)
        ) {
            FilterSurface {
                QueryTypeFilter(
                    queryTypeValue = queryTypeValue,
                    onQueryTypeChange = onQueryTypeChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                )
            }

            FilterSurface {
                FavoriteFilter(
                    isFavoriteQuery = isFavoriteQuery,
                    onFavoriteQueryChange = onFavoriteQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            FilterSurface {
                SuccessPercentageFilter(
                    sliderValue = sliderValue,
                    onSliderValueChange = onSliderValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 12.dp)
                )
            }

            FilterSurface {
                NewTagFilter(
                    tagQueryValue = tagQueryValue,
                    onTagQueryValueChange = onTagQueryValueChange,
                    matchingTags = matchingTags,
                    filterTags = filterTags,
                    onFilterTagClick = onFilterTagClick,
                    isTagDropdownExpanded = isTagDropdownExpanded,
                    onDropdownItemTagClick = onDropdownItemTagClick,
                    onTagDropdownDismissRequest = onTagDropdownDismissRequest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                )

            }

            FilterSurface {
                UserFilter(
                    userFilter = userFilter,
                    onRemoveUserSelected = onRemoveUserSelected,
                    onSearchUserBtnClick = onSearchUserBtnClick,
                    isUserSearcherVisible = isUserSearcherVisible,
                    userQuery = userQuery,
                    onUserQueryChange = onUserQueryChange,
                    isSearchingUsers = isSearchingUsers,
                    searchedUsers = searchedUsers,
                    onSearchedUserClick = onSearchedUserClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                )
            }

            Button(
                onClick = onApplyFiltersBtnClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(text = "Apply filters")
            }
        }
    }
}

@Composable
private fun FilterSurface(content: @Composable (() -> Unit)) {
    Surface(
        modifier = Modifier.padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
    ) {
        content()
    }
}

@Composable
fun FilterTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontSize = 16.sp,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun SearchBottomSheetPreview() {
    var sliderValue by remember { mutableStateOf(0f) }
    var tagQueryValue by remember { mutableStateOf("tag") }
    var isTagDropdownExpanded by remember { mutableStateOf(false) }
    val matchingTags = List(3) {
        Tag(
            id = "id#$it",
            name = "tagName#$it",
            description = "tagDescription#$it",
            userId = "userId"
        )
    }

    val filterTags = List(3) {
        val id = it + 4
        Tag(
            id = "id#$id",
            name = "tagName#$id",
            description = "tagDescription#$id",
            userId = "userId"
        )
    }

    val userFilter = User("userId", "usernameTest", "userDisplayName", "")

    MaterialTheme(colorScheme = darkColorScheme()) {
        SearchBottomSheet(
            onBottomSheetDismiss = {},
            sheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Expanded),

            queryTypeValue = QueryType.TITLE,
            onQueryTypeChange = { },

            isFavoriteQuery = true,
            onFavoriteQueryChange = {},

            sliderValue = sliderValue,
            onSliderValueChange = { sliderValue = it },

            tagQueryValue = tagQueryValue,
            onTagQueryValueChange = { tagQueryValue = it },
            isTagDropdownExpanded = isTagDropdownExpanded,
            matchingTags = matchingTags,
            filterTags = filterTags,
            onFilterTagClick = {},
            onDropdownItemTagClick = {},
            onTagDropdownDismissRequest = { isTagDropdownExpanded = false },

            userFilter = userFilter,
            onRemoveUserSelected = {},
            onSearchUserBtnClick = {},
            isUserSearcherVisible = false,
            userQuery = "user",
            onUserQueryChange = {},
            isSearchingUsers = false,
            searchedUsers = listOf(),
            onSearchedUserClick = {},

            onApplyFiltersBtnClick = {},
            modifier = Modifier,
        )
    }
}
