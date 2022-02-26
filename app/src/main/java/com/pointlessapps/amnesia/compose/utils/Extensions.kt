package com.pointlessapps.amnesia.compose.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun PaddingValues.add(
	start: Dp = 0.dp,
	top: Dp = 0.dp,
	end: Dp = 0.dp,
	bottom: Dp = 0.dp
) = LocalLayoutDirection.current.let {
	PaddingValues(
		start = start + calculateStartPadding(it),
		top = top + calculateTopPadding(),
		end = end + calculateEndPadding(it),
		bottom = bottom + calculateBottomPadding()
	)
}

@Composable
fun PaddingValues.left(layoutDirection: LayoutDirection = LocalLayoutDirection.current) =
	calculateStartPadding(layoutDirection)

@Composable
fun PaddingValues.right(layoutDirection: LayoutDirection = LocalLayoutDirection.current) =
	calculateEndPadding(layoutDirection)

@Composable
fun PaddingValues.width(layoutDirection: LayoutDirection = LocalLayoutDirection.current) =
	left(layoutDirection) + right(layoutDirection)

@Composable
fun PaddingValues.top() = calculateTopPadding()

@Composable
fun PaddingValues.bottom() = calculateBottomPadding()

@Composable
fun PaddingValues.height() = top() + bottom()

@Composable
fun PaddingValues.horizontal(layoutDirection: LayoutDirection = LocalLayoutDirection.current) =
	PaddingValues(
		start = left(layoutDirection),
		end = right(layoutDirection)
	)

@Composable
fun PaddingValues.vertical() = PaddingValues(
	top = top(),
	bottom = bottom()
)
