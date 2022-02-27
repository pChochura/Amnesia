package com.pointlessapps.amnesia.compose.note.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.insets.statusBarsPadding
import com.pointlessapps.amnesia.R
import com.pointlessapps.amnesia.compose.ui.components.*
import com.pointlessapps.amnesia.compose.ui.theme.Icons
import com.pointlessapps.amnesia.compose.utils.RANDOM_UUID
import com.pointlessapps.amnesia.compose.utils.add
import com.pointlessapps.rt_editor.model.Style
import com.pointlessapps.rt_editor.ui.RichTextEditor
import com.pointlessapps.rt_editor.ui.defaultRTTextFieldModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteScreen(
	viewModel: NoteViewModel = getViewModel()
) {
	val focusRequester = remember { FocusRequester() }
	val keyboardController = LocalTextInputService.current

	LaunchedEffect(Unit) {
		focusRequester.requestFocus()
	}

	AmnesiaScaffoldLayout(
		topBar = { TopBar() },
		fab = { BottomBar(viewModel) }
	) { innerPadding ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.clickable(
					interactionSource = remember { MutableInteractionSource() },
					indication = null
				) {
					focusRequester.requestFocus()
					keyboardController?.showSoftwareKeyboard()
				}
		) {
			LazyColumn(
				contentPadding = innerPadding.add(
					top = dimensionResource(id = R.dimen.small_padding),
					start = dimensionResource(id = R.dimen.medium_padding),
					end = dimensionResource(id = R.dimen.medium_padding)
				),
				verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.medium_padding))
			) {
				item {
					AmnesiaTextField(
						modifier = Modifier.fillMaxWidth(),
						value = viewModel.state.title,
						onValueChange = viewModel::onTitleChanged,
						textFieldModel = defaultAmnesiaTextFieldModel().copy(
							textStyle = MaterialTheme.typography.h2,
							placeholder = stringResource(id = R.string.title)
						)
					)
				}
				item {
					LazyRow(
						horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_padding))
					) {
						items(viewModel.state.categories, key = { it.id }) { category ->
							AmnesiaChip(
								modifier = Modifier.animateItemPlacement(),
								text = category.text,
								chipModel = defaultAmnesiaChipModel().copy(
									backgroundColor = Color(category.color)
								),
								onClick = {
									/* TODO display popup to remove category */
									viewModel.onCategoryRemoved(category)
								}
							)
						}
						item(key = RANDOM_UUID) {
							AmnesiaChip(
								modifier = Modifier.animateItemPlacement(),
								text = stringResource(id = R.string.add_tag),
								chipModel = defaultAmnesiaChipModel().copy(
									borderColor = MaterialTheme.colors.primaryVariant
								),
								colored = false,
								onClick = {
									/* TODO display popup to add category */
									viewModel.onCategoryAdded()
								}
							)
						}
					}
				}
				item {
					RichTextEditor(
						modifier = Modifier
							.fillMaxWidth()
							.focusRequester(focusRequester),
						value = viewModel.state.content,
						onValueChange = viewModel::onContentChanged,
						textFieldModel = defaultRTTextFieldModel().copy(
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
			enabled = false,
			tooltip = stringResource(R.string.undo),
			onClick = { /*TODO*/ }
		) {
			Icons.Undo(
				modifier = Modifier
					.padding(dimensionResource(id = R.dimen.tiny_padding))
					.size(dimensionResource(id = R.dimen.icon_size)),
				tint = MaterialTheme.colors.primaryVariant
			)
		}

		AmnesiaTooltipWrapper(
			modifier = Modifier.constrainAs(redoButton) {
				centerVerticallyTo(parent)
				end.linkTo(doneButton.start, margin = dp16)
			},
			enabled = false,
			tooltip = stringResource(R.string.redo),
			onClick = { /*TODO*/ }
		) {
			Icons.Redo(
				modifier = Modifier
					.padding(dimensionResource(id = R.dimen.tiny_padding))
					.size(dimensionResource(id = R.dimen.icon_size)),
				tint = MaterialTheme.colors.primaryVariant
			)
		}

		AmnesiaTooltipWrapper(
			modifier = Modifier
				.clip(CircleShape)
				.size(dimensionResource(id = R.dimen.icon_button_size))
				.background(MaterialTheme.colors.secondary)
				.constrainAs(doneButton) {
					centerVerticallyTo(parent)
					end.linkTo(parent.end)
				},
			tooltip = stringResource(R.string.done),
			onClick = { /*TODO*/ }
		) {
			Icons.Done(
				modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
				tint = MaterialTheme.colors.onSecondary
			)
		}
	}
}

@Composable
private fun BottomBar(
	viewModel: NoteViewModel
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(dimensionResource(id = R.dimen.medium_padding))
			.padding(bottom = dimensionResource(id = R.dimen.medium_padding))
			.navigationBarsPadding()
			.imePadding()
			.clip(MaterialTheme.shapes.medium)
			.background(MaterialTheme.colors.secondary)
			.padding(vertical = dimensionResource(id = R.dimen.small_padding)),
		horizontalArrangement = Arrangement.SpaceEvenly,
		verticalAlignment = Alignment.CenterVertically
	) {
		AmnesiaTooltipWrapper(
			tooltip = stringResource(R.string.bold),
			onClick = { viewModel.insertStyle(Style.Bold) }
		) {
			Icons.Bold(
				modifier = Modifier
					.padding(dimensionResource(id = R.dimen.tiny_padding))
					.size(dimensionResource(id = R.dimen.icon_size)),
				tint = MaterialTheme.colors.onSecondary
			)
		}
		AmnesiaTooltipWrapper(
			tooltip = stringResource(R.string.underline),
			onClick = { viewModel.insertStyle(Style.Underline) }
		) {
			Icons.Underline(
				modifier = Modifier
					.padding(dimensionResource(id = R.dimen.tiny_padding))
					.size(dimensionResource(id = R.dimen.icon_size)),
				tint = MaterialTheme.colors.onSecondary
			)
		}
		AmnesiaTooltipWrapper(
			tooltip = stringResource(R.string.italic),
			onClick = { viewModel.insertStyle(Style.Italic) }
		) {
			Icons.Italic(
				modifier = Modifier
					.padding(dimensionResource(id = R.dimen.tiny_padding))
					.size(dimensionResource(id = R.dimen.icon_size)),
				tint = MaterialTheme.colors.onSecondary
			)
		}
		AmnesiaTooltipWrapper(
			tooltip = stringResource(R.string.unordered_list),
			onClick = { viewModel.insertStyle(Style.UnorderedList) }
		) {
			Icons.UnorderedList(
				modifier = Modifier
					.padding(dimensionResource(id = R.dimen.tiny_padding))
					.size(dimensionResource(id = R.dimen.icon_size)),
				tint = MaterialTheme.colors.onSecondary
			)
		}
		AmnesiaTooltipWrapper(
			tooltip = stringResource(R.string.ordered_list),
			onClick = { viewModel.insertStyle(Style.OrderedList) }
		) {
			Icons.OrderedList(
				modifier = Modifier
					.padding(dimensionResource(id = R.dimen.tiny_padding))
					.size(dimensionResource(id = R.dimen.icon_size)),
				tint = MaterialTheme.colors.onSecondary
			)
		}
		AmnesiaTooltipWrapper(
			tooltip = stringResource(R.string.text_size),
			onClick = { viewModel.insertStyle(Style.TextSize(1.5f)) }
		) {
			Icons.TextSize(
				modifier = Modifier
					.padding(dimensionResource(id = R.dimen.tiny_padding))
					.size(dimensionResource(id = R.dimen.icon_size)),
				tint = MaterialTheme.colors.onSecondary
			)
		}
		AmnesiaTooltipWrapper(
			tooltip = stringResource(R.string.text_color),
			onClick = { viewModel.insertStyle(Style.TextColor(Color.Cyan)) }
		) {
			Icons.Circle(
				modifier = Modifier
					.padding(dimensionResource(id = R.dimen.tiny_padding))
					.size(dimensionResource(id = R.dimen.icon_size)),
				tint = MaterialTheme.colors.onSecondary
			)
		}
		AmnesiaTooltipWrapper(
			tooltip = stringResource(R.string.clear_format),
			onClick = { viewModel.insertStyle(Style.ClearFormat) }
		) {
			Icons.FormatClear(
				modifier = Modifier
					.padding(dimensionResource(id = R.dimen.tiny_padding))
					.size(dimensionResource(id = R.dimen.icon_size)),
				tint = MaterialTheme.colors.onSecondary
			)
		}
	}
}
