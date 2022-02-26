package com.pointlessapps.amnesia.note.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel

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
}

data class State(
	val title: TextFieldValue = TextFieldValue(),
	val content: TextFieldValue = TextFieldValue()
)
