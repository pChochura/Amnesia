package com.pointlessapps.amnesia.compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.pointlessapps.amnesia.R
import com.pointlessapps.amnesia.model.Note
import com.pointlessapps.dash_border.dashedBorder
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
fun AmnesiaNoteCard(
	note: Note,
	modifier: Modifier = Modifier
) {
	Column(
		modifier = modifier
			.clip(MaterialTheme.shapes.medium)
			.background(MaterialTheme.colors.primary)
			.dashedBorder(
				color = MaterialTheme.colors.secondaryVariant.copy(alpha = 0.3f),
				width = dimensionResource(id = R.dimen.border_size),
				on = dimensionResource(id = R.dimen.dash_size),
				off = dimensionResource(id = R.dimen.dash_gap_size)
			)
			.clickable {
				// TODO
			}
			.padding(dimensionResource(id = R.dimen.medium_padding)),
		verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_padding))
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(
				dimensionResource(id = R.dimen.tiny_padding)
			)
		) {
			note.categories.forEach {
				AmnesiaChip(
					text = it.text,
					chipModel = defaultAmnesiaChipModel().run {
						copy(
							backgroundColor = ComposeColor(it.color),
							textStyle = MaterialTheme.typography.subtitle1.copy(
								color = textStyle.color
							)
						)
					}
				)
			}
			if (note.categories.isNotEmpty()) {
				Text(
					text = stringResource(id = R.string.dot),
					style = MaterialTheme.typography.subtitle1.copy(
						color = MaterialTheme.colors.secondaryVariant
					)
				)
			}
			Text(
				text = note.updatedAt,
				style = MaterialTheme.typography.subtitle1.copy(
					color = MaterialTheme.colors.secondaryVariant
				)
			)
		}

		if (!note.title.isNullOrBlank()) {
			Text(
				text = note.title,
				style = MaterialTheme.typography.h1.copy(
					color = MaterialTheme.colors.onPrimary
				)
			)
		}

		Text(
			text = note.content,
			style = MaterialTheme.typography.body1.copy(
				color = MaterialTheme.colors.secondaryVariant
			)
		)
	}
}
