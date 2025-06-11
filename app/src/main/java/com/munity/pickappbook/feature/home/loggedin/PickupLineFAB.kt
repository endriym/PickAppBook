package com.munity.pickappbook.feature.home.loggedin

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PickupLineFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(imageVector = Icons.Filled.Create, "Create pickup line floating action button")
    }
}

@Preview
@Composable
fun PickupLineFAB_Preview() {
    PickupLineFAB(onClick = {})
}