package com.pointlessapps.amnesia.compose.note.ui

import android.graphics.Color
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.amnesia.R
import com.pointlessapps.amnesia.domain.notes.dto.Category
import com.pointlessapps.amnesia.domain.notes.dto.Content
import com.pointlessapps.amnesia.domain.notes.dto.Note
import com.pointlessapps.amnesia.domain.notes.usecase.SaveNoteUseCase
import com.pointlessapps.amnesia.model.CategoryModel
import com.pointlessapps.rt_editor.model.RichTextValue
import com.pointlessapps.rt_editor.model.Style
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*
import androidx.compose.ui.graphics.Color as ComposeColor

internal class NoteViewModel(
    private val saveNoteUseCase: SaveNoteUseCase,
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

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

    fun onCategoryRemoved(value: CategoryModel) {
        state = state.copy(
            categories = state.categories.filterNot { it === value },
        )
    }

    fun onCategoryAdded(
        value: CategoryModel = CategoryModel(name = "Test category",
            color = Color.parseColor("#FBCCCC")),
    ) {
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
            }
            .catch {
                println("LOG!, $it")
                state = state.copy(isLoading = false)
                eventChannel.send(Event.ShowMessage(R.string.default_error_message))
            }
            .launchIn(viewModelScope)
    }

    private fun buildNote() = Note(
        id = UUID.randomUUID().mostSignificantBits,
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
        createdAt = Date().time,
        updatedAt = Date().time,
        categories = state.categories.map {
            Category(
                id = it.id,
                name = it.name,
                color = it.color,
            )
        },
        isPinned = false,
    )

    internal data class State(
        val title: TextFieldValue = TextFieldValue(),
        val content: RichTextValue = RichTextValue.get(),
        val categories: List<CategoryModel> = emptyList(),
        val recentColors: List<ComposeColor> = emptyList(),
        val isLoading: Boolean = false,
    )

    internal sealed interface Event {
        class ShowMessage(@StringRes val message: Int) : Event
    }
}
