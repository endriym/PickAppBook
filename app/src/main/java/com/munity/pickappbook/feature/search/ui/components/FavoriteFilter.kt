package com.munity.pickappbook.feature.search.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.munity.pickappbook.feature.search.FilterTitle

@Composable
fun FavoriteFilter(
    isFavoriteQuery: Boolean,
    onFavoriteQueryChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        FilterTitle(text = "Marked as favorite:")

        Switch(
            checked = isFavoriteQuery,
            onCheckedChange = onFavoriteQueryChange
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@PreviewLightDark
@Composable
private fun FavoriteFilterPreview() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
        ) {
            FavoriteFilter(
                isFavoriteQuery = true,
                onFavoriteQueryChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}
