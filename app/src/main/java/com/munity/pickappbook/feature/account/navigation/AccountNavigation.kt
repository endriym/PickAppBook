package com.munity.pickappbook.feature.account.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object AccountRoute

fun NavController.navigateToAccount(navOptions: NavOptions? = null) =
    navigate(route = AccountRoute, navOptions)

fun NavGraphBuilder.accountScreen(
    showSnackbar: suspend (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    composable<AccountRoute> { navBackStackEntry ->
    }
}