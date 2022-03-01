package com.pointlessapps.amnesia.compose.note.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.pointlessapps.amnesia.R
import com.pointlessapps.amnesia.compose.ui.theme.Icons
import com.pointlessapps.amnesia.compose.utils.AbovePositionProvider
import com.pointlessapps.amnesia.compose.utils.roundToPx

@Composable
fun TextColorPicker(
	onDismissListener: () -> Unit,
	onColorClicked: (Color) -> Unit,
) {
	Popup(
		onDismissRequest = onDismissListener,
		popupPositionProvider = AbovePositionProvider(
			IntOffset(
				x = 0,
				y = -dimensionResource(id = R.dimen.medium_padding).roundToPx()
			)
		),
		properties = PopupProperties(),
	) {
		Row(
			modifier = Modifier
				.clip(MaterialTheme.shapes.medium)
				.background(MaterialTheme.colors.secondary)
				.padding(dimensionResource(id = R.dimen.small_padding)),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_padding))
		) {
			Icons.Circle(
				modifier = Modifier
					.padding(dimensionResource(id = R.dimen.tiny_padding))
					.size(dimensionResource(id = R.dimen.icon_size))
					.clickable(
						onClick = { onColorClicked(Color.Cyan) },
						role = Role.Button,
					),
				tint = Color.Cyan
			)
			Icons.Circle(
				modifier = Modifier
					.padding(dimensionResource(id = R.dimen.tiny_padding))
					.size(dimensionResource(id = R.dimen.icon_size))
					.clickable(
						onClick = { onColorClicked(Color.Magenta) },
						role = Role.Button,
					),
				tint = Color.Magenta
			)
			Icons.Circle(
				modifier = Modifier
					.padding(dimensionResource(id = R.dimen.tiny_padding))
					.size(dimensionResource(id = R.dimen.icon_size))
					.clickable(
						onClick = { onColorClicked(Color.Red) },
						role = Role.Button,
					),
				tint = Color.Red
			)
			Icons.Plus(
				modifier = Modifier
					.padding(dimensionResource(id = R.dimen.tiny_padding))
					.size(dimensionResource(id = R.dimen.icon_size))
					.clickable(
						onClick = { },
						role = Role.Button,
					),
				tint = MaterialTheme.colors.onSecondary
			)
		}
	}
}
