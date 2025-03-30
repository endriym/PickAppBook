package com.munity.pickappbook.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    isLoggedIn: Boolean,
    usernameLoginTFValue: String,
    onUsernameLoginTFChange: (String) -> Unit,
    passwordLoginTFValue: String,
    onPasswordLoginTFChange: (String) -> Unit,
    onLoginBtnClick: () -> Unit,
    usernameCreateTFValue: String,
    onUsernameCreateTFChange: (String) -> Unit,
    passwordCreateTFValue: String,
    onPasswordCreateTFChange: (String) -> Unit,
    onCreateUserBtnClick: () -> Unit,
    isSnackbarVisible: Boolean,
    snackbarMessage: String,
    showSnackbar: suspend (String) -> Boolean,
    onDismissSnackBar: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(isSnackbarVisible) {
        if (isSnackbarVisible) {
            val snackBarResult = showSnackbar(snackbarMessage)
            // On SnackBar dismissed (either by timeout or by user).
            if (snackBarResult) {
                onDismissSnackBar()
            }
        }
    }

    if (isLoggedIn) {
    } else {
        HomeScreenNotLoggedIn(
            usernameLoginTFValue = usernameLoginTFValue,
            onUsernameLoginTFChange = onUsernameLoginTFChange,
            passwordLoginTFValue = passwordLoginTFValue,
            onPasswordLoginTFValue = onPasswordLoginTFChange,
            onLoginBtnClick = onLoginBtnClick,
            usernameCreateTFValue = usernameCreateTFValue,
            onUsernameCreateTFValue = onUsernameCreateTFChange,
            passwordCreateTFValue = passwordCreateTFValue,
            onPasswordCreateTFValue = onPasswordCreateTFChange,
            onCreateUserBtnClick = onCreateUserBtnClick,
            modifier = modifier
        )
    }
}

@Composable
fun HomeScreenLoggedIn(modifier: Modifier = Modifier) {

}

@Composable
fun HomeScreenNotLoggedIn(
    usernameLoginTFValue: String,
    onUsernameLoginTFChange: (String) -> Unit,
    passwordLoginTFValue: String,
    onPasswordLoginTFValue: (String) -> Unit,
    onLoginBtnClick: () -> Unit,
    usernameCreateTFValue: String,
    onUsernameCreateTFValue: (String) -> Unit,
    passwordCreateTFValue: String,
    onPasswordCreateTFValue: (String) -> Unit,
    onCreateUserBtnClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "You need to login or create a new user before unleashing your rizz.",
            modifier = Modifier.padding(4.dp)
        )

        TextField(
            value = usernameLoginTFValue,
            onValueChange = onUsernameLoginTFChange,
            placeholder = { Text(text = "Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )

        TextField(
            value = passwordLoginTFValue,
            onValueChange = onPasswordLoginTFValue,
            placeholder = { Text(text = "Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )

        Button(onClick = onLoginBtnClick) {
            Text(text = "Login")
        }

        HorizontalDivider(
            thickness = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp)
        )

        TextField(
            value = usernameCreateTFValue,
            onValueChange = onUsernameCreateTFValue,
            placeholder = { Text(text = "Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )

        TextField(
            value = passwordCreateTFValue,
            onValueChange = onPasswordCreateTFValue,
            placeholder = { Text(text = "Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )

        Button(onClick = onCreateUserBtnClick) {
            Text(text = "Login")
        }
    }
}