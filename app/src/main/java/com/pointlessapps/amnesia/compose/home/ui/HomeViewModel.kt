package com.pointlessapps.amnesia.compose.home.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.amnesia.R
import com.pointlessapps.amnesia.compose.home.mapper.toCategoryModel
import com.pointlessapps.amnesia.compose.home.mapper.toNoteModel
import com.pointlessapps.amnesia.domain.notes.dto.Category
import com.pointlessapps.amnesia.domain.notes.usecase.GetAllCategoriesUseCase
import com.pointlessapps.amnesia.domain.notes.usecase.GetAllNotesUseCase
import com.pointlessapps.amnesia.domain.utils.DateFormatter
import com.pointlessapps.amnesia.model.CategoryModel
import com.pointlessapps.amnesia.model.NoteModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

internal class HomeViewModel(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val dateFormatter: DateFormatter,
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    var state: State by mutableStateOf(State())
        private set

    fun refreshNotes() {
        combine(
            getAllCategoriesUseCase.prepare(),
            getAllNotesUseCase.prepare(),
        ) { categories, notes ->
            categories to notes
        }.take(1)
            .onStart {
                state = state.copy(isLoading = true)
            }
            .onEach { (categories, notes) ->
                state = state.copy(
                    isLoading = false,
                    categories = categories.map(Category::toCategoryModel),
                    notes = notes.map { it.toNoteModel(dateFormatter) },
                )
            }
            .catch {
                state = state.copy(isLoading = false)
                eventChannel.send(Event.ShowMessage(R.string.default_error_message))
            }
            .launchIn(viewModelScope)
    }

    private fun filterNotesBySelectedCategory(): List<NoteModel> {
        if (state.selectedCategory == null) {
            return state.notes
        }

        return state.notes.filter {
            it.categories.contains(state.selectedCategory)
        }
    }

    fun onCategorySelected(value: CategoryModel?) {
        state = state.copy(
            selectedCategory = value,
        )
    }

    fun partitionNotesByPinned(): Pair<List<NoteModel>, List<NoteModel>> =
        filterNotesBySelectedCategory().partition { it.isPinned }

    internal data class State(
        val categories: List<CategoryModel> = emptyList(),
        val selectedCategory: CategoryModel? = null,
        val notes: List<NoteModel> = emptyList(),
        val isLoading: Boolean = true,
    )

    internal sealed interface Event {
        class ShowMessage(@StringRes val message: Int) : Event
    }
}
