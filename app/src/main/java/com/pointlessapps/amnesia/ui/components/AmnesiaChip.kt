package com.pointlessapps.amnesia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.pointlessapps.amnesia.R

@Composable
fun AmnesiaChip(
	text: String,
	modifier: Modifier = Modifier,
	chipModel: AmnesiaChipModel = defaultAmnesiaChipModel(),
	clickable: Boolean = true,
	onClick: () -> Unit = {},
	colored: Boolean = true
) {
	Text(
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
			.clickable(enabled = clickable, onClick = onClick)
			.padding(
				vertical = chipModel.verticalPadding,
				horizontal = chipModel.horizontalPadding
			),
		style = chipModel.textStyle
	)
}

@Composable
fun defaultAmnesiaChipModel() = AmnesiaChipModel(
	backgroundColor = MaterialTheme.colors.secondary,
	textStyle = MaterialTheme.typography.button.copy(
		color = MaterialTheme.colors.secondaryVariant
	),
	shape = MaterialTheme.shapes.medium,
	verticalPadding = dimensionResource(id = R.dimen.chip_padding_vertical),
	horizontalPadding = dimensionResource(id = R.dimen.chip_padding_horizontal)
)

data class AmnesiaChipModel(
	val backgroundColor: Color,
	val textStyle: TextStyle,
	val shape: Shape,
	val verticalPadding: Dp,
	val horizontalPadding: Dp
)
