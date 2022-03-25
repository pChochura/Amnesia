package com.pointlessapps.amnesia.compose.home.ui

import android.graphics.Color
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.amnesia.R
import com.pointlessapps.amnesia.compose.home.mapper.toNoteModel
import com.pointlessapps.amnesia.domain.notes.usecase.GetAllNotesUseCase
import com.pointlessapps.amnesia.model.CategoryModel
import com.pointlessapps.amnesia.model.NoteModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat

private val CATEGORY_ALL = CategoryModel(name = "All", color = Color.parseColor("#FBCCCC"))

internal class HomeViewModel(
    getAllNotesUseCase: GetAllNotesUseCase,
    private val dateFormatter: SimpleDateFormat,
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    var state: State by mutableStateOf(State())
        private set

    init {
        val categories = listOf(
            CATEGORY_ALL,
            CategoryModel(name = "Notes", color = Color.parseColor("#CCFBD9")),
            CategoryModel(name = "Ideas", color = Color.parseColor("#D0CCFB")),
            CategoryModel(name = "Reminders", color = Color.parseColor("#FAFBCC")),
        )

        getAllNotesUseCase.prepare()
            .take(1)
            .onStart {
                state = state.copy(isLoading = true)
            }
            .onEach { notes ->
                state = state.copy(isLoading = false)
                state = state.copy(
                    categories = categories,
                    notes = notes.map { it.toNoteModel(dateFormatter) },
                )
            }
            .catch {
                println("LOG!, $it")
                state = state.copy(isLoading = false)
                eventChannel.send(Event.ShowMessage(R.string.default_error_message))
            }
            .launchIn(viewModelScope)
    }

    private fun filterNotesBySelectedCategory(): List<NoteModel> {
        if (state.selectedCategory == CATEGORY_ALL) {
            return state.notes
        }

        return state.notes.filter {
            it.categories.contains(state.selectedCategory)
        }
    }

    fun onCategorySelected(value: CategoryModel) {
        state = state.copy(
            selectedCategory = value,
        )
    }

    fun partitionNotesByPinned(): Pair<List<NoteModel>, List<NoteModel>> =
        filterNotesBySelectedCategory().partition { it.isPinned }

    internal data class State(
        val categories: List<CategoryModel> = emptyList(),
        val selectedCategory: CategoryModel = CATEGORY_ALL,
        val notes: List<NoteModel> = emptyList(),
        val isLoading: Boolean = false,
    )

    internal sealed interface Event {
        class ShowMessage(@StringRes val message: Int) : Event
    }
}
