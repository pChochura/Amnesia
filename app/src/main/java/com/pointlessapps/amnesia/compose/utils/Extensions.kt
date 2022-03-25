@file:Suppress("MagicNumber")

package com.pointlessapps.amnesia.compose.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import kotlin.math.roundToInt

@Composable
internal fun PaddingValues.add(
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp,
) = LocalLayoutDirection.current.let {
    PaddingValues(
        start = start + calculateStartPadding(it),
        top = top + calculateTopPadding(),
        end = end + calculateEndPadding(it),
        bottom = bottom + calculateBottomPadding(),
    )
}

@Composable
internal fun PaddingValues.left(layoutDirection: LayoutDirection = LocalLayoutDirection.current) =
    calculateStartPadding(layoutDirection)

@Composable
internal fun PaddingValues.right(layoutDirection: LayoutDirection = LocalLayoutDirection.current) =
    calculateEndPadding(layoutDirection)

@Composable
internal fun PaddingValues.width(layoutDirection: LayoutDirection = LocalLayoutDirection.current) =
    left(layoutDirection) + right(layoutDirection)

@Composable
internal fun PaddingValues.top() = calculateTopPadding()

@Composable
internal fun PaddingValues.bottom() = calculateBottomPadding()

@Composable
internal fun PaddingValues.height() = top() + bottom()

@Composable
internal fun PaddingValues.horizontal(layoutDirection: LayoutDirection = LocalLayoutDirection.current) =
    PaddingValues(
        start = left(layoutDirection),
        end = right(layoutDirection),
    )

@Composable
internal fun PaddingValues.vertical() = PaddingValues(
    top = top(),
    bottom = bottom(),
)

internal fun Float.toPercent(): String = "${(this * 100).roundToInt()}%"

@Composable
internal fun Dp.roundToPx(): Int = LocalDensity.current.run {
    this@roundToPx.roundToPx()
}

@Composable
internal fun Dp.toPx(): Float = LocalDensity.current.run {
    this@toPx.toPx()
}

internal fun Float.increment(value: Float) = ((this + value) * 10).roundToInt() / 10f

internal fun Float.decrement(value: Float) = ((this - value) * 10).roundToInt() / 10f

internal fun Float.hueToColor(saturation: Float = 1f, value: Float = 0.5f): Color = Color(
    ColorUtils.HSLToColor(floatArrayOf(this, saturation, value)),
)

internal fun Size.inset(amount: Float) = Size(width - amount, height - amount)

@Composable
internal fun Colors.foregroundColor(backgroundColor: Color) =
    if (backgroundColor.luminance() > 0.5f) {
        MaterialTheme.colors.onPrimary
    } else {
        MaterialTheme.colors.onSecondary
    }
