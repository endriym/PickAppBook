package com.munity.pickappbook.feature.search.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.munity.pickappbook.feature.search.FilterTitle
import kotlin.math.roundToInt

@Composable
fun SuccessPercentageFilter(
    sliderValue: Float,
    onSliderValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        FilterTitle(
            text = "Success percentage at least: ${sliderValue.roundToInt()}%",
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Slider(
            value = sliderValue,
            onValueChange = onSliderValueChange,
            valueRange = 0f..100f,
            steps = 9,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SuccessPercentageFilterPreview() {
    SuccessPercentageFilter(
        sliderValue = 50f,
        onSliderValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp)
    )
}
