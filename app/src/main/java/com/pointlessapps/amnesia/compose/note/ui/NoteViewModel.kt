package com.pointlessapps.amnesia.compose.note.ui

import android.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.pointlessapps.amnesia.model.Category
import com.pointlessapps.rt_editor.model.RichTextValue
import com.pointlessapps.rt_editor.model.Style
import androidx.compose.ui.graphics.Color as ComposeColor

class NoteViewModel : ViewModel() {

    var state by mutableStateOf(State(), policy = neverEqualPolicy())
        private set

    fun onTitleChanged(value: TextFieldValue) {
        state = state.copy(
            title = value,
        )
    }

    fun onContentChanged(value: RichTextValue) {
        state = state.copy(
            content = value,
        )
    }

    fun onCategoryRemoved(value: Category) {
        state = state.copy(
            categories = state.categories.filterNot { it === value },
        )
    }

    fun onCategoryAdded(value: Category = Category("Test category", Color.parseColor("#FBCCCC"))) {
        state = state.copy(
            categories = buildList {
                addAll(state.categories)
                add(value)
            },
        )
    }

    fun insertStyle(style: Style) {
        state = state.copy(
            content = state.content.insertStyle(style),
        )
    }

    fun clearStyles(vararg style: Style) {
        state = state.copy(
            content = state.content.clearStyles(*style),
        )
    }

    fun onUndoClicked() {
        state = state.copy(
            content = state.content.undo(),
        )
    }

    fun onRedoClicked() {
        state = state.copy(
            content = state.content.redo(),
        )
    }

    fun updateRecentColors(color: ComposeColor) {
        state = state.copy(
            recentColors = buildList {
                add(color)
                addAll(state.recentColors.take(2))
            },
        )
    }
}

data class State(
    val title: TextFieldValue = TextFieldValue(),
    val content: RichTextValue = RichTextValue.get(),
    val categories: List<Category> = emptyList(),
    val recentColors: List<ComposeColor> = emptyList(),
)
