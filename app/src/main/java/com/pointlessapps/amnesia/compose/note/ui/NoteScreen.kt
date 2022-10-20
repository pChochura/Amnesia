package com.pointlessapps.amnesia.compose.note.ui

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
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout
import com.pointlessapps.amnesia.LocalSnackbarHostState
import com.pointlessapps.amnesia.R
import com.pointlessapps.amnesia.compose.ui.components.*
import com.pointlessapps.amnesia.compose.ui.theme.Icons
import com.pointlessapps.amnesia.compose.utils.RANDOM_UUID
import com.pointlessapps.amnesia.compose.utils.add
import com.pointlessapps.amnesia.compose.utils.foregroundColor
import com.pointlessapps.amnesia.model.CategoryModel
import com.pointlessapps.amnesia.model.NoteModel
import com.pointlessapps.rt_editor.ui.RichTextEditor
import com.pointlessapps.rt_editor.ui.defaultRichTextFieldStyle
import org.koin.androidx.compose.getViewModel

private const val SPACER_ALPHA = 0.5f

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun NoteScreen(
    viewModel: NoteViewModel = getViewModel(),
    note: NoteModel?,
    onNavigateToHome: () -> Unit,
) {
    var showChooseCategoryModal by remember { mutableStateOf(false) }
    var isContentFocused by remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalTextInputService.current
    val snackbarHostState = LocalSnackbarHostState.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        viewModel.setNote(note)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                NoteViewModel.Event.NavigateToHome -> onNavigateToHome()
                is NoteViewModel.Event.ShowMessage ->
                    snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    AmnesiaLoader(enabled = viewModel.state.isLoading)

    CategoryModal(
        enabled = showChooseCategoryModal,
        isLoading = viewModel.state.isLoadingCategories,
        categories = viewModel.allCategories,
        onDismissListener = { showChooseCategoryModal = false },
        onCategoryAdded = { color, name -> viewModel.onCategoryAdded(color, name) },
        onCategorySelected = {
            showChooseCategoryModal = false
            viewModel.onCategorySelected(it)
        }
    )

    AmnesiaScaffoldLayout(
        topBar = { TopBar(viewModel, isUndoRedoAvailable = isContentFocused) },
        fab = {
            AnimatedVisibility(isContentFocused, enter = fadeIn(), exit = fadeOut()) {
                EditorActionsBottomBar(viewModel)
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
                                chipModel = defaultAmnesiaChipModel().run {
                                    copy(
                                        backgroundColor = Color(category.color),
                                        typography = typography.copy(
                                            color = MaterialTheme.colors.foregroundColor(
                                                Color(category.color),
                                            ),
                                        ),
                                    )
                                },
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
                                    viewModel.refreshAllCategories()
                                    showChooseCategoryModal = true
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
                            .onFocusChanged { isContentFocused = it.isFocused },
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
private fun CategoryModal(
    enabled: Boolean,
    isLoading: Boolean,
    categories: List<CategoryModel>,
    onDismissListener: () -> Unit,
    onCategoryAdded: (Color, String) -> Unit,
    onCategorySelected: (CategoryModel) -> Unit,
) {
    AmnesiaBottomModal(enabled = enabled, onDismissListener = onDismissListener) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_padding)),
            contentPadding = PaddingValues(vertical = dimensionResource(id = R.dimen.medium_padding))
        ) {
            item {
                AmnesiaText(
                    text = stringResource(id = R.string.tags),
                    textStyle = defaultAmnesiaTextStyle().copy(
                        typography = MaterialTheme.typography.h2,
                        textColor = MaterialTheme.colors.onPrimary,
                        textAlign = TextAlign.Center,
                    ),
                )
            }
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = MaterialTheme.colors.surface),
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colors.onPrimary,
                        )
                    }
                }
            } else {
                items(categories) { category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCategorySelected(category) }
                            .padding(
                                vertical = dimensionResource(id = R.dimen.small_padding),
                                horizontal = dimensionResource(id = R.dimen.medium_padding),
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            space = dimensionResource(id = R.dimen.medium_padding),
                        ),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(dimensionResource(id = R.dimen.icon_size))
                                .clip(MaterialTheme.shapes.small)
                                .background(color = Color(category.color)),
                        )
                        AmnesiaText(
                            text = category.name,
                            textStyle = defaultAmnesiaTextStyle().copy(
                                typography = MaterialTheme.typography.body1,
                                textColor = MaterialTheme.colors.onPrimary,
                            ),
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colors.secondaryVariant.copy(
                                    alpha = SPACER_ALPHA,
                                ),
                            ),
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = dimensionResource(id = R.dimen.small_padding),
                            horizontal = dimensionResource(id = R.dimen.medium_padding),
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        space = dimensionResource(id = R.dimen.medium_padding),
                    ),
                ) {
                    Icons.Plus(
                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                        tint = MaterialTheme.colors.onPrimary,
                    )
                    AmnesiaText(
                        text = stringResource(id = R.string.add_tag),
                        textStyle = defaultAmnesiaTextStyle().copy(
                            typography = MaterialTheme.typography.body1,
                            textColor = MaterialTheme.colors.onPrimary,
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
