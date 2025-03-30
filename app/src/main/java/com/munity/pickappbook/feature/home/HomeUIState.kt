package com.munity.pickappbook.feature.home

data class HomeUIState(
    val usernameLogin: String = "",
    val passwordLogin: String = "",
    val usernameCreate: String = "",
    val passwordCreate: String = "",
    val isSnackbarVisible: Boolean = false,
    val snackbarMessage: String = "",
)