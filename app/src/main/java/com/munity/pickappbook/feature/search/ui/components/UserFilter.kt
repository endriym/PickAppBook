package com.munity.pickappbook.feature.search.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.munity.pickappbook.core.model.User
import com.munity.pickappbook.core.ui.components.SearchUserCard
import com.munity.pickappbook.feature.search.FilterTitle

@Composable
fun UserFilter(
    userFilter: User?,
    onRemoveUserSelected: () -> Unit,
    onSearchUserBtnClick: () -> Unit,
    isUserSearcherVisible: Boolean,
    userQuery: String,
    onUserQueryChange: (String) -> Unit,
    isSearchingUsers: Boolean,
    searchedUsers: List<User>,
    onSearchedUserClick: (User) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        FilterTitle(
            text = "Search for a user in particular:",
            modifier = Modifier.padding(top = 20.dp)
        )

        if (userFilter != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.TopEnd,
                    modifier = Modifier.size(60.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(userFilter.profilePictureUrl)
                            .crossfade(true)
                            .build(),
                        placeholder = rememberVectorPainter(Icons.Default.AccountCircle),
                        contentDescription = "Author's profile picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )

                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Remove selected profile picture",
                        modifier = Modifier
                            .clip(shape = CircleShape)
                            .clickable(onClick = onRemoveUserSelected)
                    )
                }

                Text(text = userFilter.username, modifier = Modifier.padding(8.dp))
            }
        } else {
            Text(
                text = "All users",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )
        }

        TextButton(
            onClick = onSearchUserBtnClick,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(imageVector = Icons.Filled.Search, contentDescription = null)
            Text(
                text = "Search user",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
        }

        if (isUserSearcherVisible)
            SearchUserCard(
                userQuery = userQuery,
                onUserQueryChange = onUserQueryChange,
                searchedUsers = searchedUsers,
                isSearchingUsers = isSearchingUsers,
                onSearchedUserClick = onSearchedUserClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
            )
    }
}

@Preview(showBackground = true)
@Composable
private fun UserFilterPreview() {
    UserFilter(
        userFilter = User("testId", "testUsername", "testDisplayName", ""),
        onRemoveUserSelected = {},
        onSearchUserBtnClick = {},
        isUserSearcherVisible = true,
        userQuery = "test",
        onUserQueryChange = {},
        isSearchingUsers = false,
        searchedUsers = listOf(),
        onSearchedUserClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    )
}
