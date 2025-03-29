package com.munity.pickappbook.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@Composable
fun PickAppBottomAppBar(
    onHomeButtonClick: () -> Unit,
) {
    BottomAppBar(
        actions = {
            IconButton(onClick = onHomeButtonClick) {
                Icon(imageVector = Icons.Filled.Home, contentDescription = "Home")
            }
        }
    )
}