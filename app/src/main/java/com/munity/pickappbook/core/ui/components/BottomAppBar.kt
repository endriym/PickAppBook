package com.munity.pickappbook.core.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.munity.pickappbook.navigation.TopLevelDestination
import kotlin.enums.EnumEntries

@Composable
fun PickAppBottomAppBar(
    selectedItem: Int,
    topLevelDestinationEntries: EnumEntries<TopLevelDestination>,
    onEntryClick: (Int) -> Unit,
) {
    NavigationBar {
        topLevelDestinationEntries.forEachIndexed { index, entry ->
            NavigationBarItem(
                selected = selectedItem == index,
                onClick = { onEntryClick(index) },
                icon = {
                    val imageVec =
                        if (selectedItem == index) entry.selectedIcon else entry.unselectedIcon
                    Icon(
                        imageVector = imageVec,
                        contentDescription = stringResource(entry.iconTextId)
                    )
                },
                label = { Text(text = stringResource(entry.titleTextId)) },
                alwaysShowLabel = false,
            )
        }
    }
}