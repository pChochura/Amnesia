package com.pointlessapps.amnesia.compose.note.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.pointlessapps.amnesia.R
import com.pointlessapps.amnesia.compose.note.util.TEXT_SIZE_INCREMENT
import com.pointlessapps.amnesia.compose.ui.components.AmnesiaTooltipWrapper
import com.pointlessapps.amnesia.compose.ui.components.defaultAmnesiaTooltipModel
import com.pointlessapps.amnesia.compose.ui.theme.Icons
import com.pointlessapps.amnesia.compose.utils.decrement
import com.pointlessapps.amnesia.compose.utils.increment
import com.pointlessapps.rt_editor.model.Style

@Composable
internal fun EditorActionsBottomBar(viewModel: NoteViewModel) {
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
                EditorActionsBottomBarIcon(
                    tooltip = R.string.bold,
                    icon = R.drawable.icon_bold,
                    selected = viewModel.state.content.currentStyles.contains(Style.Bold),
                ) { viewModel.insertStyle(Style.Bold) }
            }
            item {
                EditorActionsBottomBarIcon(
                    tooltip = R.string.underline,
                    icon = R.drawable.icon_underline,
                    selected = viewModel.state.content.currentStyles.contains(Style.Underline),
                ) { viewModel.insertStyle(Style.Underline) }
            }
            item {
                EditorActionsBottomBarIcon(
                    tooltip = R.string.italic,
                    icon = R.drawable.icon_italic,
                    selected = viewModel.state.content.currentStyles.contains(Style.Italic),
                ) { viewModel.insertStyle(Style.Italic) }
            }
            item {
                EditorActionsBottomBarIcon(
                    tooltip = R.string.strikethrough,
                    icon = R.drawable.icon_strikethrough,
                    selected = viewModel.state.content.currentStyles.contains(Style.Strikethrough),
                ) { viewModel.insertStyle(Style.Strikethrough) }
            }
            item {
                Box {
                    var showTextSizePicker by remember { mutableStateOf(false) }
                    var currentValue by remember { mutableStateOf(Style.TextSize.DEFAULT_VALUE) }
                    EditorActionsBottomBarIcon(
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
                    EditorActionsBottomBarIcon(
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
                EditorActionsBottomBarIcon(
                    tooltip = R.string.align_left,
                    icon = R.drawable.icon_align_left,
                    selected = viewModel.state.content.currentStyles.contains(Style.AlignLeft),
                ) { viewModel.insertStyle(Style.AlignLeft) }
            }
            item {
                EditorActionsBottomBarIcon(
                    tooltip = R.string.align_center,
                    icon = R.drawable.icon_align_center,
                    selected = viewModel.state.content.currentStyles.contains(Style.AlignCenter),
                ) { viewModel.insertStyle(Style.AlignCenter) }
            }
            item {
                EditorActionsBottomBarIcon(
                    tooltip = R.string.align_right,
                    icon = R.drawable.icon_align_right,
                    selected = viewModel.state.content.currentStyles.contains(Style.AlignRight),
                ) { viewModel.insertStyle(Style.AlignRight) }
            }
            item {
                EditorActionsBottomBarIcon(
                    tooltip = R.string.clear_format,
                    icon = R.drawable.icon_format_clear,
                    selected = true,
                ) { viewModel.insertStyle(Style.ClearFormat) }
            }
        }
    }
}

@Composable
private fun EditorActionsBottomBarIcon(
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
