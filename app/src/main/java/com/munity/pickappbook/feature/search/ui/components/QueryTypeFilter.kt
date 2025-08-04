package com.munity.pickappbook.feature.search.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.munity.pickappbook.feature.search.FilterTitle
import com.munity.pickappbook.feature.search.QueryType

@Composable
fun QueryTypeFilter(
    queryTypeValue: QueryType?,
    onQueryTypeChange: (QueryType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        FilterTitle(text = "Search by:", modifier = Modifier.padding(vertical = 8.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.scrollable(
                rememberScrollState(),
                orientation = Orientation.Horizontal
            )
        ) {
            FilterChip(
                selected = queryTypeValue == QueryType.CONTENT,
                onClick = { onQueryTypeChange(QueryType.CONTENT) },
                label = { Text("Content") },
                leadingIcon = if (queryTypeValue == QueryType.CONTENT) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Content query type filter",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
                modifier = Modifier.padding(end = 8.dp)
            )

            FilterChip(
                selected = queryTypeValue == QueryType.TITLE,
                onClick = { onQueryTypeChange(QueryType.TITLE) },
                label = { Text("Title") },
                leadingIcon = if (queryTypeValue == QueryType.TITLE) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Title query type filter",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
                modifier = Modifier.padding(end = 8.dp)
            )

            FilterChip(
                selected = queryTypeValue == QueryType.CONTENT_TITLE,
                onClick = { onQueryTypeChange(QueryType.CONTENT_TITLE) },
                label = { Text("Content title") },
                leadingIcon = if (queryTypeValue == QueryType.CONTENT_TITLE) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Content title query type filter",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@PreviewLightDark
@Composable
private fun QueryTypeFilterPreview() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(
            modifier = Modifier.padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 1.dp,
            tonalElevation = 1.dp,
        ) {
            QueryTypeFilter(
                queryTypeValue = QueryType.CONTENT,
                onQueryTypeChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            )
        }
    }
}
