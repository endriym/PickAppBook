package com.munity.pickappbook.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.munity.pickappbook.feature.account.navigation.accountScreen
import com.munity.pickappbook.feature.home.navigation.homeScreen
import com.munity.pickappbook.feature.search.navigation.searchScreen

@Composable
fun PickAppBookNavHost(
    navHostController: NavHostController,
    startDestination: Any,
    showSnackbar: suspend (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        homeScreen(showSnackbar)
        searchScreen()
        accountScreen(showSnackbar)
    }
}