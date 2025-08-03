package com.munity.pickappbook.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TagCreatorCard(
    tagNameTFValue: String,
    onTagNameChangeValue: (String) -> Unit,
    tagDescriptionTFValue: String,
    onTagDescriptionChangeValue: (String) -> Unit,
    onTagCreateBtnClick: () -> Unit,
    onCancelTagCreateBtnClick: () -> Unit,
    isTagCreationLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Text(
            text = "Create tag",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 4.dp)
        )

        OutlinedTextField(
            value = tagNameTFValue,
            onValueChange = onTagNameChangeValue,
            placeholder = { Text(text = "Tag name") },
            label = { Text(text = "Tag name") },
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
        )

        OutlinedTextField(
            value = tagDescriptionTFValue,
            onValueChange = onTagDescriptionChangeValue,
            placeholder = { Text(text = "Tag description") },
            label = { Text(text = "Tag description") },
            maxLines = 2,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp)
        )

        Row(
            horizontalArrangement = if (isTagCreationLoading) Arrangement.SpaceBetween else Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (isTagCreationLoading)
                CircularProgressIndicator()

            Row {
                OutlinedButton(
                    onClick = onCancelTagCreateBtnClick,
                    modifier = Modifier
                        .padding(end = 12.dp)
                ) {
                    Text(text = "Cancel")
                }

                Button(onClick = onTagCreateBtnClick) {
                    Text(text = "Create tag")
                }
            }
        }
    }
}
