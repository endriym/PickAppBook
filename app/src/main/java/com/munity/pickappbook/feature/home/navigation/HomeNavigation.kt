package com.munity.pickappbook.feature.home.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.munity.pickappbook.feature.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavController.navigateToHome(navOptions: NavOptions? = null) =
    navigate(route = HomeRoute, navOptions = navOptions)

fun NavGraphBuilder.homeScreen(
    showSnackbar: suspend (String) -> Boolean,
    modifier: Modifier = Modifier,
) {
    composable<HomeRoute> { navBackStackEntry ->
        HomeScreen(
            showSnackbar = showSnackbar,
            modifier = modifier,
        )
    }
}