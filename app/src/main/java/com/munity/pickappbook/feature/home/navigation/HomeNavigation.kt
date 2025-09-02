package com.munity.pickappbook.feature.home.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.munity.pickappbook.feature.account.navigation.accountScreen
import com.munity.pickappbook.feature.account.navigation.navigateToAccount
import com.munity.pickappbook.feature.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeNavHost

@Serializable
data object HomeRoute

fun NavController.navigateToHome(navOptions: NavOptions? = null) =
    navigate(route = HomeNavHost, navOptions = navOptions)

fun NavGraphBuilder.homeScreen(
    showSnackbar: suspend (String) -> Boolean,
    onPLAuthorClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    composable<HomeRoute> { navBackStackEntry ->
        HomeScreen(
            showSnackbar = showSnackbar,
            onAuthorClick = onPLAuthorClick,
            modifier = modifier.fillMaxSize(),
        )
    }
}

fun NavGraphBuilder.homeNavHost(
    showSnackbar: suspend (String) -> Boolean,
    modifier: Modifier = Modifier,
) {
    composable<HomeNavHost> { navBackStackEntry ->
        val homeNavController = rememberNavController()

        NavHost(
            navController = homeNavController,
            startDestination = HomeRoute,
            modifier = modifier
        ) {
            homeScreen(
                showSnackbar = showSnackbar,
                onPLAuthorClick = { homeNavController.navigateToAccount(authorId = it) },
                modifier = modifier
            )
            accountScreen(
                showSnackbar = showSnackbar,
                onPLAuthorClick = { homeNavController.navigateToAccount(authorId = it) },
                modifier = modifier
            )
        }
    }
}
