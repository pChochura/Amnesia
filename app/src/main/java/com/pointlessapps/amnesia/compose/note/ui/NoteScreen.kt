package com.pointlessapps.amnesia.compose.note.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import com.pointlessapps.amnesia.LocalSnackbarHostState
import com.pointlessapps.amnesia.R
import com.pointlessapps.amnesia.compose.note.util.TEXT_SIZE_INCREMENT
import com.pointlessapps.amnesia.compose.ui.components.*
import com.pointlessapps.amnesia.compose.ui.theme.Icons
import com.pointlessapps.amnesia.compose.utils.RANDOM_UUID
import com.pointlessapps.amnesia.compose.utils.add
import com.pointlessapps.amnesia.compose.utils.decrement
import com.pointlessapps.amnesia.compose.utils.increment
import com.pointlessapps.rt_editor.model.Style
import com.pointlessapps.rt_editor.ui.RichTextEditor
import com.pointlessapps.rt_editor.ui.defaultRichTextFieldStyle
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun NoteScreen(viewModel: NoteViewModel = getViewModel()) {
    var showBottomBar by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalTextInputService.current
    val snackbarHostState = LocalSnackbarHostState.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is NoteViewModel.Event.ShowMessage ->
                    snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    AmnesiaLoader(enabled = viewModel.state.isLoading)

    AmnesiaScaffoldLayout(
        topBar = { TopBar(viewModel, isUndoRedoAvailable = showBottomBar) },
        fab = {
            AnimatedVisibility(showBottomBar, enter = fadeIn(), exit = fadeOut()) {
                BottomBar(viewModel)
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    focusRequester.requestFocus()
                    keyboardController?.showSoftwareKeyboard()
                },
        ) {
            LazyColumn(
                contentPadding = innerPadding.add(
                    top = dimensionResource(id = R.dimen.small_padding),
                    start = dimensionResource(id = R.dimen.medium_padding),
                    end = dimensionResource(id = R.dimen.medium_padding),
                ),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.medium_padding)),
            ) {
                item {
                    AmnesiaTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = viewModel.state.title,
                        onValueChange = viewModel::onTitleChanged,
                        textFieldModel = defaultAmnesiaTextFieldModel().copy(
                            textStyle = MaterialTheme.typography.h2,
                            placeholder = stringResource(id = R.string.title),
                        ),
                    )
                }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_padding)),
                    ) {
                        items(viewModel.state.categories, key = { it.id }) { category ->
                            AmnesiaChip(
                                modifier = Modifier.animateItemPlacement(),
                                text = category.name,
                                chipModel = defaultAmnesiaChipModel().copy(
                                    backgroundColor = Color(category.color),
                                ),
                                onClick = {
                                    /* TODO display popup to remove category */
                                    viewModel.onCategoryRemoved(category)
                                },
                            )
                        }
                        item(key = RANDOM_UUID) {
                            AmnesiaChip(
                                modifier = Modifier.animateItemPlacement(),
                                text = stringResource(id = R.string.add_tag),
                                chipModel = defaultAmnesiaChipModel().copy(
                                    borderColor = MaterialTheme.colors.primaryVariant,
                                ),
                                colored = false,
                                onClick = {
                                    /* TODO display popup to add category */
                                    viewModel.onCategoryAdded()
                                },
                            )
                        }
                    }
                }
                item {
                    RichTextEditor(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onFocusChanged { showBottomBar = it.isFocused },
                        value = viewModel.state.content,
                        onValueChange = viewModel::onContentChanged,
                        textFieldStyle = defaultRichTextFieldStyle().copy(
                            textStyle = MaterialTheme.typography.body1,
                            placeholder = stringResource(id = R.string.content),
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun TopBar(viewModel: NoteViewModel, isUndoRedoAvailable: Boolean) {
    ConstraintLayout(
        modifier = Modifier
            .wrapContentSize()
            .background(MaterialTheme.colors.primary.copy(alpha = 0.8f))
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
            .pointerInput(Unit) { /* no-op */ },
    ) {
        val dp16 = dimensionResource(id = R.dimen.medium_padding)
        val (menuButton, undoRedoButtons, doneButton) = createRefs()

        AmnesiaTooltipWrapper(
            modifier = Modifier.constrainAs(menuButton) {
                centerVerticallyTo(parent)
                start.linkTo(parent.start)
            },
            tooltip = stringResource(R.string.menu),
            onClick = { /*TODO*/ },
        ) {
            Icons.Menu(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.tiny_padding))
                    .size(dimensionResource(id = R.dimen.icon_size)),
                tint = MaterialTheme.colors.secondary,
            )
        }

        AnimatedVisibility(
            modifier = Modifier.constrainAs(undoRedoButtons) {
                centerVerticallyTo(parent)
                end.linkTo(doneButton.start, margin = dp16)
            },
            visible = isUndoRedoAvailable,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_padding)),
            ) {
                AmnesiaTooltipWrapper(
                    enabled = viewModel.state.content.isUndoAvailable,
                    tooltip = stringResource(R.string.undo),
                    onClick = viewModel::onUndoClicked,
                ) {
                    Icons.Undo(
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.tiny_padding))
                            .size(dimensionResource(id = R.dimen.icon_size)),
                        tint = if (viewModel.state.content.isUndoAvailable) {
                            MaterialTheme.colors.secondary
                        } else {
                            MaterialTheme.colors.primaryVariant
                        },
                    )
                }

                AmnesiaTooltipWrapper(
                    enabled = viewModel.state.content.isRedoAvailable,
                    tooltip = stringResource(R.string.redo),
                    onClick = viewModel::onRedoClicked,
                ) {
                    Icons.Redo(
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.tiny_padding))
                            .size(dimensionResource(id = R.dimen.icon_size)),
                        tint = if (viewModel.state.content.isRedoAvailable) {
                            MaterialTheme.colors.secondary
                        } else {
                            MaterialTheme.colors.primaryVariant
                        },
                    )
                }
            }
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
            onClick = viewModel::onNoteSaved,
        ) {
            Icons.Done(
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                tint = MaterialTheme.colors.onSecondary,
            )
        }
    }
}

@Composable
private fun BottomBar(viewModel: NoteViewModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.medium_padding))
            .padding(bottom = dimensionResource(id = R.dimen.medium_padding))
            .navigationBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.Center,
    ) {
        LazyRow(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colors.secondary),
            contentPadding = PaddingValues(dimensionResource(id = R.dimen.small_padding)),
            horizontalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.small_padding),
                Alignment.CenterHorizontally,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            item {
                BottomBarIcon(
                    tooltip = R.string.bold,
                    icon = R.drawable.icon_bold,
                    selected = viewModel.state.content.currentStyles.contains(Style.Bold),
                ) { viewModel.insertStyle(Style.Bold) }
            }
            item {
                BottomBarIcon(
                    tooltip = R.string.underline,
                    icon = R.drawable.icon_underline,
                    selected = viewModel.state.content.currentStyles.contains(Style.Underline),
                ) { viewModel.insertStyle(Style.Underline) }
            }
            item {
                BottomBarIcon(
                    tooltip = R.string.italic,
                    icon = R.drawable.icon_italic,
                    selected = viewModel.state.content.currentStyles.contains(Style.Italic),
                ) { viewModel.insertStyle(Style.Italic) }
            }
            item {
                BottomBarIcon(
                    tooltip = R.string.strikethrough,
                    icon = R.drawable.icon_strikethrough,
                    selected = viewModel.state.content.currentStyles.contains(Style.Strikethrough),
                ) { viewModel.insertStyle(Style.Strikethrough) }
            }
            item {
                Box {
                    var showTextSizePicker by remember { mutableStateOf(false) }
                    var currentValue by remember { mutableStateOf(Style.TextSize.DEFAULT_VALUE) }
                    BottomBarIcon(
                        tooltip = R.string.text_size,
                        icon = R.drawable.icon_text_size,
                        selected = viewModel.state.content.currentStyles
                            .filterIsInstance<Style.TextSize>().isNotEmpty(),
                    ) {
                        currentValue =
                            viewModel.state.content.currentStyles
                                .filterIsInstance<Style.TextSize>()
                                .firstOrNull()
                                ?.fraction ?: Style.TextSize.DEFAULT_VALUE
                        showTextSizePicker = true
                    }

                    if (showTextSizePicker) {
                        TextSizePicker(
                            currentValue = currentValue,
                            onDismissListener = { showTextSizePicker = false },
                            onMinusClicked = onMinusClicked@{
                                if (currentValue <= Style.TextSize.MIN_VALUE) {
                                    return@onMinusClicked
                                }

                                viewModel.clearStyles(Style.TextSize())
                                currentValue = currentValue.decrement(TEXT_SIZE_INCREMENT)
                                if (currentValue != Style.TextSize.DEFAULT_VALUE) {
                                    viewModel.insertStyle(Style.TextSize(currentValue))
                                }
                            },
                            onPlusClicked = onPlusClicked@{
                                if (currentValue >= Style.TextSize.MAX_VALUE) {
                                    return@onPlusClicked
                                }

                                viewModel.clearStyles(Style.TextSize())
                                currentValue = currentValue.increment(TEXT_SIZE_INCREMENT)
                                if (currentValue != Style.TextSize.DEFAULT_VALUE) {
                                    viewModel.insertStyle(Style.TextSize(currentValue))
                                }
                            },
                        )
                    }
                }
            }
            item {
                Box {
                    var showTextColorPicker by remember { mutableStateOf(false) }
                    BottomBarIcon(
                        tooltip = R.string.text_color,
                        icon = R.drawable.icon_circle,
                        selected = viewModel.state.content.currentStyles
                            .filterIsInstance<Style.TextColor>().isNotEmpty(),
                    ) {
                        showTextColorPicker = true
                    }

                    if (showTextColorPicker) {
                        TextColorPicker(
                            recentColors = viewModel.state.recentColors,
                            onDismissListener = { showTextColorPicker = false },
                            onColorClicked = {
                                viewModel.clearStyles(Style.TextColor(null))
                                viewModel.insertStyle(Style.TextColor(it))
                            },
                            onAddToRecents = { viewModel.updateRecentColors(it) },
                        )
                    }
                }
            }
            item {
                BottomBarIcon(
                    tooltip = R.string.align_left,
                    icon = R.drawable.icon_align_left,
                    selected = viewModel.state.content.currentStyles.contains(Style.AlignLeft),
                ) { viewModel.insertStyle(Style.AlignLeft) }
            }
            item {
                BottomBarIcon(
                    tooltip = R.string.align_center,
                    icon = R.drawable.icon_align_center,
                    selected = viewModel.state.content.currentStyles.contains(Style.AlignCenter),
                ) { viewModel.insertStyle(Style.AlignCenter) }
            }
            item {
                BottomBarIcon(
                    tooltip = R.string.align_right,
                    icon = R.drawable.icon_align_right,
                    selected = viewModel.state.content.currentStyles.contains(Style.AlignRight),
                ) { viewModel.insertStyle(Style.AlignRight) }
            }
            item {
                BottomBarIcon(
                    tooltip = R.string.clear_format,
                    icon = R.drawable.icon_format_clear,
                    selected = true,
                ) { viewModel.insertStyle(Style.ClearFormat) }
            }
        }
    }
}

@Composable
fun BottomBarIcon(
    @StringRes tooltip: Int,
    @DrawableRes icon: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    AmnesiaTooltipWrapper(
        tooltip = stringResource(tooltip),
        onClick = onClick,
        tooltipModel = defaultAmnesiaTooltipModel().copy(
            buttonRippleColor = MaterialTheme.colors.secondaryVariant,
        ),
    ) {
        Icons.Get(
            iconRes = icon,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.tiny_padding))
                .size(dimensionResource(id = R.dimen.icon_size)),
            tint = if (selected) {
                MaterialTheme.colors.primary
            } else {
                MaterialTheme.colors.secondaryVariant
            },
        )
    }
}
