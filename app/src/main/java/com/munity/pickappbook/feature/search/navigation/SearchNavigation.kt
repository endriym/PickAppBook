package com.munity.pickappbook.feature.search.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object SearchRoute

fun NavController.navigateToSearch(navOptions: NavOptions? = null) =
    navigate(route = SearchRoute, navOptions)

fun NavGraphBuilder.searchScreen(
    modifier: Modifier = Modifier,
) {
    composable<SearchRoute> { navBackStackEntry ->
    }
}