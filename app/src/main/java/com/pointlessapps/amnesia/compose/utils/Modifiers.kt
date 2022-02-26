package com.pointlessapps.amnesia.compose.utils

import androidx.compose.ui.Modifier

fun Modifier.conditional(
	predicate: () -> Boolean,
	then: Modifier.() -> Modifier
) = if (predicate()) {
	then()
} else {
	this
}
