package com.munity.pickappbook.feature.home

import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.munity.pickappbook.core.ui.components.PasswordTextField

private enum class SelectedTab(val index: Int) {
    LOGIN(0), SIGN_UP(1)
}

@OptIn(ExperimentalMaterial3Api::class)
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
    onImagePicked: (ByteArray?) -> Unit,
    imageByteArray: ByteArray?,
    isLoading: Boolean,
    onRemoveImagePicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by remember { mutableStateOf(SelectedTab.LOGIN) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = "You need to login or create a new user before unleashing your rizz.",
                modifier = Modifier.padding(16.dp)
            )
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SecondaryTabRow(
                selectedTabIndex = selectedTab.index,
                modifier = Modifier.fillMaxWidth(),
            ) {
                PickAppTab(
                    selected = selectedTab == SelectedTab.LOGIN,
                    onClick = {
                        selectedTab = SelectedTab.LOGIN
                    },
                    text = "Login"
                )

                PickAppTab(
                    selected = selectedTab == SelectedTab.SIGN_UP,
                    onClick = {
                        selectedTab = SelectedTab.SIGN_UP
                    },
                    text = "Sign up"
                )
            }

            when (selectedTab) {
                SelectedTab.LOGIN -> LoginScreen(
                    usernameLoginTFValue = usernameLoginTFValue,
                    onUsernameLoginTFChange = onUsernameLoginTFChange,
                    passwordLoginTFValue = passwordLoginTFValue,
                    onPasswordLoginTFValue = onPasswordLoginTFValue,
                    onLoginBtnClick = onLoginBtnClick,
                    isIndefIndicatorVisible = isLoading,
                    modifier = Modifier.padding(top = 16.dp),
                )

                SelectedTab.SIGN_UP -> SignUpScreen(
                    usernameCreateTFValue = usernameCreateTFValue,
                    onUsernameCreateTFValue = onUsernameCreateTFValue,
                    passwordCreateTFValue = passwordCreateTFValue,
                    onPasswordCreateTFValue = onPasswordCreateTFValue,
                    onCreateUserBtnClick = onCreateUserBtnClick,
                    onImagePicked = onImagePicked,
                    imageByteArray = imageByteArray,
                    isIndefIndicatorVisible = isLoading,
                    onRemoveImagePicked = onRemoveImagePicked,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }

            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .width(64.dp)
                        .padding(top = 20.dp),
                )
            }
        }
    }
}


@Composable
private fun LoginScreen(
    usernameLoginTFValue: String,
    onUsernameLoginTFChange: (String) -> Unit,
    passwordLoginTFValue: String,
    onPasswordLoginTFValue: (String) -> Unit,
    onLoginBtnClick: () -> Unit,
    isIndefIndicatorVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        TextField(
            value = usernameLoginTFValue,
            onValueChange = onUsernameLoginTFChange,
            placeholder = { Text(text = "Username") },
            label = { Text(text = "Username") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)
        )

        PasswordTextField(
            value = passwordLoginTFValue,
            onValueChange = onPasswordLoginTFValue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
        )

        Button(
            onClick = onLoginBtnClick,
            enabled = !isIndefIndicatorVisible,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(text = "Login")
        }
    }
}

@Composable
private fun SignUpScreen(
    usernameCreateTFValue: String,
    onUsernameCreateTFValue: (String) -> Unit,
    passwordCreateTFValue: String,
    onPasswordCreateTFValue: (String) -> Unit,
    onImagePicked: (ByteArray?) -> Unit,
    onRemoveImagePicked: () -> Unit,
    imageByteArray: ByteArray?,
    onCreateUserBtnClick: () -> Unit,
    isIndefIndicatorVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        // Registers a photo picker activity launcher in single-select mode.
        val pickMedia =
            rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    val item = context.contentResolver.openInputStream(uri)
                    onImagePicked(item?.readBytes())
                    item?.close()
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        if (imageByteArray == null) {
            Image(
                imageVector = Icons.Filled.AccountCircle,
                "Pick an image for your new account",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .clickable { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
            )
        } else {
            val bmp = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                .asImageBitmap()

            Box(contentAlignment = Alignment.TopEnd, modifier = Modifier.size(150.dp)) {
                Image(
                    bitmap = bmp,
                    contentDescription = "Profile picture picked by the user for sign-up",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .clickable { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                )
                IconButton(onClick = onRemoveImagePicked) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Remove selected profile picture"
                    )
                }
            }
        }

        Text(
            text = "Tap to load a profile picture",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(8.dp)
        )

        TextField(
            value = usernameCreateTFValue,
            onValueChange = onUsernameCreateTFValue,
            placeholder = { Text(text = "Username") },
            label = { Text(text = "Username") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)
        )

        PasswordTextField(
            value = passwordCreateTFValue,
            onValueChange = onPasswordCreateTFValue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
        )

        Button(
            onClick = onCreateUserBtnClick,
            enabled = !isIndefIndicatorVisible,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(text = "Sign up")
        }
    }
}

@Composable
private fun PickAppTab(selected: Boolean, onClick: () -> Unit, text: String) {
    Tab(
        selected = selected,
        onClick = onClick,
    ) {
        Column(
            Modifier
                .padding(10.dp)
                .height(30.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}