package com.munity.pickappbook.feature.home.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.munity.pickappbook.feature.home.HomeScreen
import com.munity.pickappbook.feature.home.HomeViewModel
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavController.navigateToHome(navOptions: NavOptions? = null) =
    navigate(route = HomeRoute, navOptions = navOptions)

fun NavGraphBuilder.homeScreen(
    showSnackbar: suspend (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    composable<HomeRoute> { navBackStackEntry ->
        val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)

        val isLoggedIn by homeViewModel.isLoggedIn.collectAsState()
        val homeUiState by homeViewModel.uiState.collectAsState()

        HomeScreen(
            isLoggedIn = isLoggedIn,
            usernameLoginTFValue = homeUiState.usernameLogin,
            onUsernameLoginTFChange = homeViewModel::onUsernameLoginTFChange,
            passwordLoginTFValue = homeUiState.passwordLogin,
            onPasswordLoginTFChange = homeViewModel::onPasswordLoginTFChange,
            onLoginBtnClick = homeViewModel::onLoginBtnClick,
            usernameCreateTFValue = homeUiState.usernameCreate,
            onUsernameCreateTFChange = homeViewModel::onUsernameCreateTFChange,
            passwordCreateTFValue = homeUiState.passwordCreate,
            onPasswordCreateTFChange = homeViewModel::onPasswordCreateTFChange,
            onCreateUserBtnClick = homeViewModel::onCreateUserBtnClick,
            isSnackbarVisible = homeUiState.isSnackbarVisible,
            snackbarMessage = homeUiState.snackbarMessage,
            showSnackbar = showSnackbar,
            onDismissSnackBar = homeViewModel::onDismissSnackBar,
            modifier = modifier,
        )
    }
}