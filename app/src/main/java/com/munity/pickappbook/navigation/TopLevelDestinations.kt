package com.munity.pickappbook.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.munity.pickappbook.R
import com.munity.pickappbook.feature.account.navigation.AccountRoute
import com.munity.pickappbook.feature.home.navigation.HomeRoute
import com.munity.pickappbook.feature.search.navigation.SearchRoute
import kotlin.reflect.KClass

/**
 * Type for the top level destinations in the application. Contains metadata about the destination
 * that is used in the top app bar and common navigation UI.
 *
 * @param selectedIcon The icon to be displayed in the navigation UI when this destination is
 * selected.
 * @param unselectedIcon The icon to be displayed in the navigation UI when this destination is
 * not selected.
 * @param iconTextId Text that to be displayed in the navigation UI (in the bottom app bar).
 * @param titleTextId Text that is displayed on the top app bar.
 * @param route The route to use when navigating to this destination.
 * @param baseRoute The highest ancestor of this destination. Defaults to [route], meaning that
 * there is a single destination in that section of the app (no nested destinations).
 */
enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route,
) {
    HOME(
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        iconTextId = R.string.home_icon_text,
        titleTextId = R.string.home_title_text,
        route = HomeRoute::class,
        baseRoute = HomeRoute::class,
    ),
    SEARCH(
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search,
        iconTextId = R.string.search_icon_text,
        titleTextId = R.string.search_title_text,
        route = SearchRoute::class,
        baseRoute = SearchRoute::class
    ),
    ACCOUNT(
        selectedIcon = Icons.Filled.AccountCircle,
        unselectedIcon = Icons.Outlined.AccountCircle,
        iconTextId = R.string.account_icon_text,
        titleTextId = R.string.you_title_text,
        route = AccountRoute::class,
        baseRoute = AccountRoute::class
    )
}
