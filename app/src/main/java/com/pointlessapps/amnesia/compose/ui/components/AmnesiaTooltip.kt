package com.pointlessapps.amnesia.compose.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.pointlessapps.amnesia.R
import com.pointlessapps.tooltip.Tooltip

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AmnesiaTooltipWrapper(
	modifier: Modifier = Modifier,
	tooltipModel: AmnesiaTooltipModel = defaultAmnesiaTooltipModel(),
	enabled: Boolean = true,
	tooltip: String,
	onClick: () -> Unit,
	content: @Composable BoxScope.() -> Unit
) {
	Box(
		modifier = modifier,
		contentAlignment = Alignment.Center
	) {
		val showTooltip = remember { mutableStateOf(false) }
		Box(
			modifier = Modifier
				.combinedClickable(
					enabled = enabled,
					interactionSource = remember { MutableInteractionSource() },
					indication = rememberRipple(
						bounded = false,
						color = tooltipModel.buttonRippleColor
					),
					onClickLabel = tooltip,
					role = Role.Button,
					onClick = onClick,
					onLongClick = { showTooltip.value = true },
				),
			contentAlignment = Alignment.Center
		) {
			content()
		}

		Tooltip(
			expanded = showTooltip,
			backgroundColor = tooltipModel.backgroundColor,
			shape = tooltipModel.shape,
			modifier = Modifier.padding(
				vertical = tooltipModel.verticalPadding,
				horizontal = tooltipModel.horizontalPadding
			)
		) {
			Text(
				text = tooltip,
				style = tooltipModel.textStyle,
			)
		}
	}
}

@Composable
fun defaultAmnesiaTooltipModel() = AmnesiaTooltipModel(
	buttonRippleColor = Color.Unspecified,
	backgroundColor = MaterialTheme.colors.secondary,
	textStyle = MaterialTheme.typography.button.copy(
		color = MaterialTheme.colors.onSecondary
	),
	shape = MaterialTheme.shapes.medium,
	verticalPadding = dimensionResource(id = R.dimen.chip_padding_vertical),
	horizontalPadding = dimensionResource(id = R.dimen.chip_padding_horizontal)
)

data class AmnesiaTooltipModel(
	val buttonRippleColor: Color,
	val backgroundColor: Color,
	val textStyle: TextStyle,
	val shape: Shape,
	val verticalPadding: Dp,
	val horizontalPadding: Dp
)

