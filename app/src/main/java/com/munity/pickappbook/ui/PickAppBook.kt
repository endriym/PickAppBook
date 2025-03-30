package com.munity.pickappbook.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.munity.pickappbook.core.ui.components.PickAppBottomAppBar
import com.munity.pickappbook.core.ui.theme.PickAppBookTheme
import com.munity.pickappbook.feature.home.navigation.HomeRoute
import com.munity.pickappbook.navigation.PickAppBookNavHost

@Composable
fun PickAppBook() {
    PickAppBookTheme {
        val navHostController = rememberNavController()
        val snackBarHostState = remember { SnackbarHostState() }

        Scaffold(
            bottomBar = {
                PickAppBottomAppBar(
                    onHomeButtonClick = {},
                )
            },
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