package com.pointlessapps.amnesia.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

@Composable
fun Int.toDp(): Dp = Density(LocalContext.current).run {
	this@toDp.toDp()
}

@Composable
fun Float.toDp(): Dp = Density(LocalContext.current).run {
	this@toDp.toDp()
}
