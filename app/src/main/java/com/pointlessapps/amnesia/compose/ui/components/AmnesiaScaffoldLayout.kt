package com.pointlessapps.amnesia.compose.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout

@Composable
internal fun AmnesiaScaffoldLayout(
    topBar: @Composable () -> Unit = {},
    fab: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    SubcomposeLayout(modifier = Modifier.imePadding()) { constraints ->
        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight

        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        layout(layoutWidth, layoutHeight) {
            val topBarPlaceables = subcompose(AmnesiaScaffoldLayoutContent.TopBar, topBar).map {
                it.measure(looseConstraints)
            }

            val topBarHeight = topBarPlaceables.maxByOrNull { it.height }?.height ?: 0

            val fabPlaceables = subcompose(AmnesiaScaffoldLayoutContent.Fab, fab)
                .mapNotNull { measurable ->
                    measurable.measure(looseConstraints).takeIf { it.height != 0 && it.width != 0 }
                }

            val fabHeight = fabPlaceables.maxByOrNull { it.height }?.height ?: 0

            val bodyContentPlaceables = subcompose(AmnesiaScaffoldLayoutContent.Content) {
                content(
                    PaddingValues(
                        top = topBarHeight.toDp(),
                        bottom = fabHeight.toDp(),
                    ),
                )
            }.map { it.measure(looseConstraints.copy(maxHeight = layoutHeight)) }

            bodyContentPlaceables.forEach { it.place(0, 0) }
            topBarPlaceables.forEach { it.place(0, 0) }
            fabPlaceables.forEach { fab ->
                fab.place(0, layoutHeight - fabHeight)
            }
        }
    }
}

private enum class AmnesiaScaffoldLayoutContent { TopBar, Content, Fab }
