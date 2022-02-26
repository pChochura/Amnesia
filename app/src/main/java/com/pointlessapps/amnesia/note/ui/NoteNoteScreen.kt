package com.pointlessapps.amnesia.note.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.insets.statusBarsPadding
import com.pointlessapps.amnesia.R
import com.pointlessapps.amnesia.ui.components.AmnesiaScaffoldLayout
import com.pointlessapps.amnesia.ui.components.AmnesiaTextField
import com.pointlessapps.amnesia.ui.components.AmnesiaTooltipWrapper
import com.pointlessapps.amnesia.ui.components.defaultAMnesiaTextFieldModel
import com.pointlessapps.amnesia.ui.theme.Icons
import com.pointlessapps.amnesia.utils.add

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NoteNoteScreen() {
	var title by remember { mutableStateOf("") }
	var content by remember { mutableStateOf("") }

	val focusRequester = remember { FocusRequester() }
	val keyboardController = LocalSoftwareKeyboardController.current

	AmnesiaScaffoldLayout(
		topBar = { TopBar() },
		fab = { BottomBar() }
	) { innerPadding ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.clickable(
					interactionSource = remember { MutableInteractionSource() },
					indication = null
				) {
					focusRequester.requestFocus()
					keyboardController?.show()
				}
		) {
			LazyColumn(
				contentPadding = innerPadding.add(
					top = dimensionResource(id = R.dimen.small_padding),
					start = dimensionResource(id = R.dimen.small_padding),
					end = dimensionResource(id = R.dimen.small_padding)
				),
				verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.medium_padding))
			) {
				item {
					AmnesiaTextField(
						modifier = Modifier
							.fillMaxWidth()
							.padding(horizontal = dimensionResource(id = R.dimen.small_padding)),
						value = title,
						onValueChange = { title = it },
						textFieldModel = defaultAMnesiaTextFieldModel().copy(
							textStyle = MaterialTheme.typography.h2,
							placeholder = stringResource(id = R.string.title)
						)
					)
				}
				item {
					AmnesiaTextField(
						modifier = Modifier
							.fillMaxWidth()
							.padding(horizontal = dimensionResource(id = R.dimen.small_padding))
							.focusRequester(focusRequester),
						value = content,
						onValueChange = { content = it },
						textFieldModel = defaultAMnesiaTextFieldModel().copy(
							textStyle = MaterialTheme.typography.body1,
							placeholder = stringResource(id = R.string.content)
						)
					)
				}
			}
		}
	}
}

@Composable
private fun TopBar() {
	ConstraintLayout(
		modifier = Modifier
			.wrapContentSize()
			.background(MaterialTheme.colors.primary.copy(alpha = 0.8f))
			.statusBarsPadding()
			.fillMaxWidth()
			.padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
	) {
		val dp8 = dimensionResource(id = R.dimen.small_padding)
		val dp16 = dimensionResource(id = R.dimen.medium_padding)
		val (menuButton, undoButton, redoButton, doneButton) = createRefs()

		AmnesiaTooltipWrapper(
			modifier = Modifier.constrainAs(menuButton) {
				centerVerticallyTo(parent)
				start.linkTo(parent.start)
			},
			tooltip = stringResource(R.string.menu),
			onClick = { /*TODO*/ }
		) {
			Icons.Menu(
				modifier = Modifier
					.padding(dimensionResource(id = R.dimen.tiny_padding))
					.size(dimensionResource(id = R.dimen.icon_size)),
				tint = MaterialTheme.colors.secondary
			)
		}

		AmnesiaTooltipWrapper(
			modifier = Modifier.constrainAs(undoButton) {
				centerVerticallyTo(parent)
				end.linkTo(redoButton.start, margin = dp8)
			},
			tooltip = stringResource(R.string.undo),
			onClick = { /*TODO*/ }
		) {
			Icons.Undo(
				modifier = Modifier
					.padding(dimensionResource(id = R.dimen.tiny_padding))
					.size(dimensionResource(id = R.dimen.icon_size)),
				tint = MaterialTheme.colors.secondary
			)
		}

		AmnesiaTooltipWrapper(
			modifier = Modifier.constrainAs(redoButton) {
				centerVerticallyTo(parent)
				end.linkTo(doneButton.start, margin = dp16)
			},
			tooltip = stringResource(R.string.redo),
			onClick = { /*TODO*/ }
		) {
			Icons.Redo(
				modifier = Modifier
					.padding(dimensionResource(id = R.dimen.tiny_padding))
					.size(dimensionResource(id = R.dimen.icon_size)),
				tint = MaterialTheme.colors.secondary
			)
		}

		AmnesiaTooltipWrapper(
			modifier = Modifier
				.constrainAs(doneButton) {
					centerVerticallyTo(parent)
					end.linkTo(parent.end)
				},
			tooltip = stringResource(R.string.done),
			onClick = { /*TODO*/ }
		) {
			Box(
				modifier = Modifier
					.clip(CircleShape)
					.size(dimensionResource(id = R.dimen.icon_button_size))
					.background(MaterialTheme.colors.secondary),
				contentAlignment = Alignment.Center
			) {
				Icons.Done(
					modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
					tint = MaterialTheme.colors.onSecondary
				)
			}
		}
	}
}

@Composable
private fun BottomBar() {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(dimensionResource(id = R.dimen.medium_padding))
			.padding(bottom = dimensionResource(id = R.dimen.medium_padding))
			.navigationBarsPadding()
			.imePadding()
			.clip(MaterialTheme.shapes.medium)
			.background(MaterialTheme.colors.secondary),
		horizontalArrangement = Arrangement.SpaceEvenly,
		verticalAlignment = Alignment.CenterVertically
	) {
		IconButton(onClick = { /*TODO*/ }) {
			Icons.Bold(
				modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
				tint = MaterialTheme.colors.onSecondary
			)
		}
		IconButton(onClick = { /*TODO*/ }) {
			Icons.Underline(
				modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
				tint = MaterialTheme.colors.onSecondary
			)
		}
		IconButton(onClick = { /*TODO*/ }) {
			Icons.Italic(
				modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
				tint = MaterialTheme.colors.onSecondary
			)
		}
		IconButton(onClick = { /*TODO*/ }) {
			Icons.UnorderedList(
				modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
				tint = MaterialTheme.colors.onSecondary
			)
		}
		IconButton(onClick = { /*TODO*/ }) {
			Icons.OrderedList(
				modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
				tint = MaterialTheme.colors.onSecondary
			)
		}
		IconButton(onClick = { /*TODO*/ }) {
			Icons.TextSize(
				modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
				tint = MaterialTheme.colors.onSecondary
			)
		}
		IconButton(onClick = { /*TODO*/ }) {
			Icons.Circle(
				modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
				tint = MaterialTheme.colors.onSecondary
			)
		}
	}
}
