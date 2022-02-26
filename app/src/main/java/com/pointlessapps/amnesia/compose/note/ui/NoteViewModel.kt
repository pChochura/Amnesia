package com.pointlessapps.amnesia.compose.note.ui

import android.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.pointlessapps.amnesia.model.Category

class NoteViewModel : ViewModel() {

	var state: State by mutableStateOf(State())
		private set

	fun onTitleChanged(value: TextFieldValue) {
		state = state.copy(
			title = value
		)
	}

	fun onContentChanged(value: TextFieldValue) {
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
}

data class State(
	val title: TextFieldValue = TextFieldValue(),
	val content: TextFieldValue = TextFieldValue(),
	val categories: List<Category> = emptyList(),
)
