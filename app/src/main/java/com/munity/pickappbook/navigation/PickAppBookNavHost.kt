package com.munity.pickappbook.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.munity.pickappbook.feature.account.navigation.accountNavHost
import com.munity.pickappbook.feature.home.navigation.homeNavHost
import com.munity.pickappbook.feature.search.navigation.searchNavHost

@Composable
fun PickAppBookNavHost(
    navHostController: NavHostController,
    startDestination: Any,
    loggedInUserId: String?,
    showSnackbar: suspend (String) -> Boolean,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        homeNavHost(showSnackbar = showSnackbar)
        searchNavHost(showSnackbar = showSnackbar)
        accountNavHost(showSnackbar = showSnackbar, authorId = loggedInUserId)
    }
}
