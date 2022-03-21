package com.pointlessapps.amnesia.compose.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import com.pointlessapps.amnesia.R

@Composable
internal fun AmnesiaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonModel: AmnesiaButtonModel = defaultAmnesiaButtonModel(),
) {
    Button(
        modifier = modifier,
        elevation = null,
        shape = buttonModel.shape,
        colors = buttonColors(backgroundColor = buttonModel.backgroundColor),
        contentPadding = PaddingValues(
            vertical = buttonModel.verticalPadding,
            horizontal = buttonModel.horizontalPadding,
        ),
        onClick = onClick,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.button.copy(
                color = buttonModel.textColor,
            ),
        )
    }
}

@Composable
internal fun defaultAmnesiaButtonModel() = AmnesiaButtonModel(
    backgroundColor = MaterialTheme.colors.secondary,
    textColor = MaterialTheme.colors.onSecondary,
    shape = MaterialTheme.shapes.medium,
    verticalPadding = dimensionResource(id = R.dimen.button_padding_vertical),
    horizontalPadding = dimensionResource(id = R.dimen.button_padding_horizontal),
)

internal data class AmnesiaButtonModel(
    val backgroundColor: Color,
    val textColor: Color,
    val shape: Shape,
    val verticalPadding: Dp,
    val horizontalPadding: Dp,
)
