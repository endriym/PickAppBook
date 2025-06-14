package com.munity.pickappbook.feature.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ImportContacts
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ImportContacts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.munity.pickappbook.core.ui.components.PullToRefreshLazyPickupCards

private enum class SelectedTab(val index: Int) {
    POSTS(0), FAVORITES(1)
}

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
) {
    val accountVM: AccountViewModel = viewModel(factory = AccountViewModel.Factory)
    val accountUiState by accountVM.accountUiState.collectAsState()
    var selectedTab by remember { mutableStateOf(SelectedTab.POSTS) }
    val isLoggedIn by accountVM.isLoggedIn.collectAsState()
    val currentUsername by accountVM.currentUsername.collectAsState()

    if (isLoggedIn) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 36.dp)
            ) {
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center)
                )

                IconButton(
                    onClick = accountVM::onLogoutBtnClick,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Logout",
                        tint = Color(0xffff3b3b)
                    )
                }
            }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("/images/$currentUsername.jpeg")
                    .crossfade(true)
                    .build(),
                placeholder = rememberVectorPainter(Icons.Default.AccountCircle),
                contentDescription = "Author's profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(8.dp)
                    .size(120.dp)
                    .clip(CircleShape)
            )

            Text(
                text = currentUsername ?: "No username found",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
            )

            AccountPrimaryTabRow(
                selectedTab = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) { newSelectedTab ->
                selectedTab = newSelectedTab
            }

            when (selectedTab) {
                SelectedTab.POSTS -> PullToRefreshLazyPickupCards(
                    isRefreshing = accountUiState.isPersonalRefreshing,
                    onRefresh = accountVM::onPersonalPLRefresh,
                    pickupLines = accountVM.personalPickupLines,
                    onStarredBtnClick = accountVM::onPersonalPLStarredBtnClick,
                    onVoteClick = accountVM::onPersonalPLVoteClick,
                    onTagClick = { /*TODO()*/ },
                    modifier = modifier.fillMaxSize()
                )

                SelectedTab.FAVORITES -> FavoritesScreenSection(modifier)
            }
        }
    } else {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                "You need to login.\nNavigate to the home screen through the bottom navigation bar.",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountPrimaryTabRow(
    selectedTab: SelectedTab,
    modifier: Modifier = Modifier,
    onTabSelection: (SelectedTab) -> Unit,
) {
    PrimaryTabRow(
        selectedTabIndex = selectedTab.index,
        indicator = {
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier.tabIndicatorOffset(
                    selectedTab.index,
                    matchContentSize = true
                ),
                width = 68.dp,
            )
        },
        modifier = modifier
    ) {
        Tab(
            selected = selectedTab == SelectedTab.POSTS,
            onClick = { onTabSelection(SelectedTab.POSTS) },
            modifier = Modifier.height(50.dp)
        ) {
            Icon(
                imageVector = if (selectedTab == SelectedTab.POSTS) Icons.Filled.ImportContacts else Icons.Outlined.ImportContacts,
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .padding(4.dp)
            )
        }

        Tab(
            selected = selectedTab == SelectedTab.FAVORITES,
            onClick = { onTabSelection(SelectedTab.FAVORITES) },
        ) {
            Icon(
                imageVector = if (selectedTab == SelectedTab.FAVORITES) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun FavoritesScreenSection(modifier: Modifier = Modifier) {

}
