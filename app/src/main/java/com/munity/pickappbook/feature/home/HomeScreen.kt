package com.munity.pickappbook.feature.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.munity.pickappbook.feature.home.loggedin.LoggedInHomeScreen
import com.munity.pickappbook.feature.home.loggedout.LoggedOutHomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    showSnackbar: suspend (String) -> Boolean,
    onAuthorClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
    val isLoggedIn by homeViewModel.isLoggedIn.collectAsState()

    LaunchedEffect(key1 = true) {
        homeViewModel.snackbarMessages.collect { message ->
            message?.let {
                showSnackbar(it)
            }
        }
    }

    if (isLoggedIn) {
        LoggedInHomeScreen(
            onAuthorClick = onAuthorClick,
            modifier = modifier,
        )
    } else {
        LoggedOutHomeScreen(
            modifier = modifier,
        )
    }
}
