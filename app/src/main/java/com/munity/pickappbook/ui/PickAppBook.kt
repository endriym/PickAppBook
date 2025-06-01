package com.munity.pickappbook.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.munity.pickappbook.core.ui.components.PickAppBottomAppBar
import com.munity.pickappbook.core.ui.theme.PickAppBookTheme
import com.munity.pickappbook.feature.account.navigation.navigateToAccount
import com.munity.pickappbook.feature.home.navigation.HomeRoute
import com.munity.pickappbook.feature.home.navigation.navigateToHome
import com.munity.pickappbook.feature.search.navigation.navigateToSearch
import com.munity.pickappbook.navigation.PickAppBookNavHost
import com.munity.pickappbook.navigation.TopLevelDestination

@Composable
fun PickAppBook() {
    PickAppBookTheme {
        val navHostController = rememberNavController()
        val snackBarHostState = remember { SnackbarHostState() }
        var selectedItem by remember { mutableStateOf(TopLevelDestination.HOME.ordinal) }

        Scaffold(
            bottomBar = {
                PickAppBottomAppBar(
                    selectedItem = selectedItem,
                    topLevelDestinationEntries = TopLevelDestination.entries,
                    onEntryClick = { selectedIndex ->
                        selectedItem = selectedIndex

                        val topLevelNavOptions = navOptions {
//                            // Pop up to the start destination of the graph to
//                            // avoid building up a large stack of destinations
//                            // on the back stack as users select items
                            popUpTo(navHostController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }

                        when (selectedIndex) {
                            TopLevelDestination.HOME.ordinal -> navHostController.navigateToHome(
                                topLevelNavOptions
                            )

                            TopLevelDestination.SEARCH.ordinal -> navHostController.navigateToSearch(
                                topLevelNavOptions
                            )

                            TopLevelDestination.ACCOUNT.ordinal -> navHostController.navigateToAccount(
                                topLevelNavOptions
                            )
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            PickAppBookNavHost(
                navHostController = navHostController,
                startDestination = HomeRoute,
                showSnackbar = { message ->
                    snackBarHostState.showSnackbar(message = message) == SnackbarResult.Dismissed
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        }
    }
}