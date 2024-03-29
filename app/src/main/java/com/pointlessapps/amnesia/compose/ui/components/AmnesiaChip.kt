package com.pointlessapps.amnesia.compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.pointlessapps.amnesia.R
import com.pointlessapps.amnesia.compose.utils.conditional

@Composable
internal fun AmnesiaChip(
    text: String,
    modifier: Modifier = Modifier,
    chipModel: AmnesiaChipModel = defaultAmnesiaChipModel(),
    clickable: Boolean = true,
    onClick: () -> Unit = {},
    colored: Boolean = true,
) {
    AmnesiaText(
        text = text,
        modifier = modifier
            .clip(chipModel.shape)
            .background(
                color = if (colored) {
                    chipModel.backgroundColor
                } else {
                    Color.Transparent
                },
            )
            .conditional(!colored) {
                Modifier.border(
                    width = chipModel.borderWidth,
                    color = chipModel.borderColor,
                    shape = chipModel.shape,
                )
            }
            .clickable(enabled = clickable, onClick = onClick)
            .padding(
                vertical = chipModel.verticalPadding,
                horizontal = chipModel.horizontalPadding,
            ),
        textStyle = defaultAmnesiaTextStyle().copy(
            typography = chipModel.typography,
            textColor = MaterialTheme.colors.onPrimary,
        ),
    )
}

@Composable
internal fun defaultAmnesiaChipModel() = AmnesiaChipModel(
    borderWidth = dimensionResource(id = R.dimen.border_size),
    borderColor = colorResource(id = android.R.color.transparent),
    backgroundColor = MaterialTheme.colors.secondary,
    typography = MaterialTheme.typography.button.copy(
        color = MaterialTheme.colors.secondaryVariant,
    ),
    shape = MaterialTheme.shapes.medium,
    verticalPadding = dimensionResource(id = R.dimen.chip_padding_vertical),
    horizontalPadding = dimensionResource(id = R.dimen.chip_padding_horizontal),
)

internal data class AmnesiaChipModel(
    val borderWidth: Dp,
    val borderColor: Color,
    val backgroundColor: Color,
    val typography: TextStyle,
    val shape: Shape,
    val verticalPadding: Dp,
    val horizontalPadding: Dp,
)
