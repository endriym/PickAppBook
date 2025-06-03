package com.munity.pickappbook.feature.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    showSnackbar: suspend (String) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)

    val isLoggedIn by homeViewModel.isLoggedIn.collectAsState()
    val homeUiState by homeViewModel.uiState.collectAsState()

    LaunchedEffect(key1 = true) {
        homeViewModel.snackbarMessaage.collect { message ->
            message?.let {
                showSnackbar(it)
            }
        }
    }

    if (isLoggedIn) {
        HomeScreenLoggedIn(
            isRefreshing = homeUiState.isLoading,
            onRefresh = homeViewModel::onPullToRefresh,
            pickupLines = homeViewModel.pickupLines,
            onVoteClick = homeViewModel::onVoteClick,
            onStarredBtnClick = homeViewModel::onStarredBtnClick,
            onTagClick = {},
            modifier = modifier,
        )
    } else {
        HomeScreenNotLoggedIn(
            usernameLoginTFValue = homeUiState.usernameLogin,
            onUsernameLoginTFChange = homeViewModel::onUsernameLoginTFChange,
            passwordLoginTFValue = homeUiState.passwordLogin,
            onPasswordLoginTFValue = homeViewModel::onPasswordLoginTFChange,
            onLoginBtnClick = homeViewModel::onLoginBtnClick,
            usernameCreateTFValue = homeUiState.usernameCreate,
            onUsernameCreateTFValue = homeViewModel::onUsernameCreateTFChange,
            passwordCreateTFValue = homeUiState.passwordCreate,
            onPasswordCreateTFValue = homeViewModel::onPasswordCreateTFChange,
            onCreateUserBtnClick = homeViewModel::onCreateUserBtnClick,
            onImagePicked = homeViewModel::onImagePicked,
            onRemoveImagePicked = homeViewModel::onRemoveImagePicked,
            imageByteArray = homeUiState.imageByteArray,
            isLoading = homeUiState.isLoading,
            modifier = modifier,
        )
    }
}