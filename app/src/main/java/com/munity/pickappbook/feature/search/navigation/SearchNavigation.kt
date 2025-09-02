package com.munity.pickappbook.feature.search.navigation

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
import com.munity.pickappbook.feature.search.SearchScreen
import kotlinx.serialization.Serializable

@Serializable
data object SearchNavHost

@Serializable
data object SearchRoute

fun NavController.navigateToSearch(navOptions: NavOptions? = null) =
    navigate(route = SearchNavHost, navOptions)

fun NavGraphBuilder.searchScreen(
    onPLAuthorClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    composable<SearchRoute> { navBackStackEntry ->
        SearchScreen(onAuthorClick = onPLAuthorClick, modifier = modifier.fillMaxSize())
    }
}

fun NavGraphBuilder.searchNavHost(
    showSnackbar: suspend (String) -> Boolean,
    modifier: Modifier = Modifier,
) {
    composable<SearchNavHost> { navHostBackStackEntry ->
        val searchNavController = rememberNavController()

        NavHost(
            navController = searchNavController,
            startDestination = SearchRoute,
            modifier = modifier
        ) {
            searchScreen(
                onPLAuthorClick = { searchNavController.navigateToAccount(authorId = it) },
                modifier = modifier
            )
            accountScreen(
                showSnackbar = showSnackbar,
                onPLAuthorClick = { searchNavController.navigateToAccount(authorId = it) },
                modifier = modifier
            )
        }
    }
}
