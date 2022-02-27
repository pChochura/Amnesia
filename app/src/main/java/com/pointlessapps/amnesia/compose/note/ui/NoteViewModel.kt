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

class NoteViewModel : ViewModel() {

	var state: State by mutableStateOf(State(), policy = neverEqualPolicy())
		private set

	fun onTitleChanged(value: TextFieldValue) {
		state = state.copy(
			title = value
		)
	}

	fun onContentChanged(value: RichTextValue) {
		state = state.copy(
			content = value
		)
	}

	fun onCategoryRemoved(value: Category) {
		state = state.copy(
			categories = state.categories.filterNot { it === value }
		)
	}

	fun onCategoryAdded(value: Category = Category("Test category", Color.parseColor("#FBCCCC"))) {
		state = state.copy(
			categories = listOf(*state.categories.toTypedArray(), value)
		)
	}

	fun insertStyle(style: Style) {
		state = state.copy(
			content = state.content.insertStyle(style)
		)
	}
}

data class State(
	val title: TextFieldValue = TextFieldValue(),
	val content: RichTextValue = RichTextValue.get(),
	val categories: List<Category> = emptyList(),
)
