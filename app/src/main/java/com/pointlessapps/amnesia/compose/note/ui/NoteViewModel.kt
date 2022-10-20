package com.pointlessapps.amnesia.compose.note.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.amnesia.R
import com.pointlessapps.amnesia.compose.home.mapper.toCategoryModel
import com.pointlessapps.amnesia.domain.notes.dto.Category
import com.pointlessapps.amnesia.domain.notes.dto.Content
import com.pointlessapps.amnesia.domain.notes.dto.Note
import com.pointlessapps.amnesia.domain.notes.usecase.GetAllCategoriesUseCase
import com.pointlessapps.amnesia.domain.notes.usecase.SaveCategoryUseCase
import com.pointlessapps.amnesia.domain.notes.usecase.SaveNoteUseCase
import com.pointlessapps.amnesia.domain.utils.DateFormatter
import com.pointlessapps.amnesia.model.CategoryModel
import com.pointlessapps.amnesia.model.NoteModel
import com.pointlessapps.rt_editor.model.RichTextValue
import com.pointlessapps.rt_editor.model.Style
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*
import androidx.compose.ui.graphics.Color as ComposeColor

internal class NoteViewModel(
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val saveCategoryUseCase: SaveCategoryUseCase,
    private val dateFormatter: DateFormatter,
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    var allCategories by mutableStateOf(emptyList<CategoryModel>())
        private set

    var state by mutableStateOf(State())
        private set

    fun refreshAllCategories() {
        getAllCategoriesUseCase.prepare()
            .take(1)
            .onStart {
                state = state.copy(isLoadingCategories = true)
            }
            .onEach {
                state = state.copy(isLoadingCategories = false)
                allCategories = it.map(Category::toCategoryModel)
            }
            .catch {
                state = state.copy(isLoadingCategories = false)
                eventChannel.send(Event.ShowMessage(R.string.default_error_message))
            }
            .launchIn(viewModelScope)
    }

    fun setNote(note: NoteModel?) {
        if (note == null) {
            return
        }

        state = state.copy(
            id = note.id,
            title = TextFieldValue(note.title.orEmpty()),
            content = RichTextValue.fromSnapshot(note.content),
            categories = note.categories.toList(),
            createdAt = dateFormatter.fromString(note.createdAt),
            isPinned = note.isPinned,
        )
    }

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

    fun onCategoryRemoved(value: CategoryModel) {
        state = state.copy(
            categories = state.categories.filterNot { it === value },
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun onCategoryAdded(color: ComposeColor, name: String) {
        saveCategoryUseCase
            .prepare(buildCategory(color, name))
            .take(1)
            .onStart {
                state = state.copy(isLoadingCategories = true)
            }
            .flatMapLatest {
                getAllCategoriesUseCase.prepare()
            }
            .onEach {
                state = state.copy(isLoadingCategories = false)
                allCategories = it.map(Category::toCategoryModel)
            }
            .catch {
                state = state.copy(isLoadingCategories = false)
                eventChannel.send(Event.ShowMessage(R.string.default_error_message))
            }
            .launchIn(viewModelScope)
    }

    fun onCategorySelected(value: CategoryModel) {
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

    fun onNoteSaved() {
        saveNoteUseCase
            .prepare(buildNote())
            .take(1)
            .onStart {
                state = state.copy(isLoading = true)
            }
            .onEach {
                state = state.copy(isLoading = false)
                eventChannel.send(Event.NavigateToHome)
            }
            .catch {
                state = state.copy(isLoading = false)
                eventChannel.send(Event.ShowMessage(R.string.default_error_message))
            }
            .launchIn(viewModelScope)
    }

    private fun buildNote() = Note(
        id = state.id,
        title = state.title.text,
        content = state.content.getLastSnapshot().run {
            Content(
                text = text,
                spanStyles = spanStyles.map {
                    Content.Span(it.start, it.end, it.tag)
                },
                paragraphStyles = paragraphStyles.map {
                    Content.Span(it.start, it.end, it.tag)
                },
                selectionPosition = selectionPosition,
            )
        },
        updatedAt = System.currentTimeMillis(),
        createdAt = state.createdAt,
        categories = state.categories.map {
            Category(
                id = it.id,
                name = it.name,
                color = it.color,
            )
        },
        isPinned = state.isPinned,
    )

    private fun buildCategory(color: ComposeColor, name: String) = Category(
        id = UUID.randomUUID().mostSignificantBits,
        name = name,
        color = color.value,
    )

    internal data class State(
        val id: Long = UUID.randomUUID().mostSignificantBits,
        val createdAt: Long = System.currentTimeMillis(),
        val isPinned: Boolean = false,
        val title: TextFieldValue = TextFieldValue(),
        val content: RichTextValue = RichTextValue.get(),
        val categories: List<CategoryModel> = emptyList(),
        val recentColors: List<ComposeColor> = emptyList(),
        val isLoading: Boolean = false,
        val isLoadingCategories: Boolean = false,
    )

    internal sealed interface Event {
        class ShowMessage(@StringRes val message: Int) : Event
        object NavigateToHome : Event
    }
}
