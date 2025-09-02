package com.munity.pickappbook.feature.account.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.munity.pickappbook.feature.account.AccountScreen
import kotlinx.serialization.Serializable

@Serializable
data object AccountNavHost

@Serializable
data class AccountRoute(val authorId: String? = null)

fun NavController.navigateToAccount(authorId: String? = null, navOptions: NavOptions? = null) =
    navigate(route = AccountRoute(authorId), navOptions)

fun NavController.navigateToAccountNavHost(navOptions: NavOptions) =
    navigate(route = AccountNavHost, navOptions)

fun NavGraphBuilder.accountScreen(
    showSnackbar: suspend (String) -> Boolean,
    onPLAuthorClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    composable<AccountRoute> { navBackStackEntry ->
        val accountRoute: AccountRoute = navBackStackEntry.toRoute()

        AccountScreen(
            modifier = modifier,
            userId = accountRoute.authorId,
            onPLAuthorClick = onPLAuthorClick
        )
    }
}

fun NavGraphBuilder.accountNavHost(
    showSnackbar: suspend (String) -> Boolean,
    authorId: String? = null,
    modifier: Modifier = Modifier,
) {
    composable<AccountNavHost> { navHostBackStackEntry ->
        val accountNavController = rememberNavController()

        NavHost(
            navController = accountNavController,
            startDestination = AccountRoute(authorId),
            modifier = modifier
        ) {
            accountScreen(
                showSnackbar = showSnackbar,
                onPLAuthorClick = { accountNavController.navigateToAccount(authorId = it) },
                modifier = modifier
            )
        }
    }
}
