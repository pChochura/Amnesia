package com.pointlessapps.amnesia.compose.utils

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.conditional(
    predicate: () -> Boolean,
    then: Modifier.() -> Modifier,
) = if (predicate()) {
    then()
} else {
    this
}

fun Modifier.onMove(
    onMoved: AwaitPointerEventScope.(Offset) -> Unit,
    onDown: AwaitPointerEventScope.(Offset) -> Unit = { onMoved(it) },
    onUp: AwaitPointerEventScope.(Offset) -> Unit = { onMoved(it) },
) = pointerInput(Unit) {
    forEachGesture {
        awaitPointerEventScope {
            val down = awaitFirstDown()
            onDown(down.position)
            drag(down.id) { change ->
                change.consumePositionChange()
                onMoved(change.position)
            }
            onUp(down.position)
        }
    }
}
