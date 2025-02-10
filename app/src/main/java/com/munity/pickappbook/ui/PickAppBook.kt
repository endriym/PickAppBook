package com.munity.pickappbook.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.munity.pickappbook.core.ui.theme.PickAppBookTheme

@Composable
fun PickAppBook() {
    PickAppBookTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        }
    }
}