package com.pointlessapps.amnesia.compose.home.ui

import android.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.pointlessapps.amnesia.model.Category
import com.pointlessapps.amnesia.model.Note

private val CATEGORY_ALL = Category("All", Color.parseColor("#FBCCCC"))

class HomeViewModel : ViewModel() {

    var state: State by mutableStateOf(State(), policy = referentialEqualityPolicy())
        private set

    init {
        val categories = listOf(
            CATEGORY_ALL,
            Category("Notes", Color.parseColor("#CCFBD9")),
            Category("Ideas", Color.parseColor("#D0CCFB")),
            Category("Reminders", Color.parseColor("#FAFBCC")),
        )

        @Suppress("MagicNumber")
        state = state.copy(
            categories = categories,
            notes = listOf(
                Note(
                    title = "Aplikacja notatnik",
                    content = "Some stuff, some other stuff, some more stuff...",
                    createdAt = "12.02.2022",
                    updatedAt = "yesterday",
                    categories = categories.subList(1, 2).toSet(),
                    isPinned = false,
                ),
                Note(
                    title = null,
                    content = "Content with:\n" +
                        "unordered list\n" +
                        "multilevel\n" +
                        "nice\n" +
                        "\n" +
                        "ordered list\n" +
                        "multilevel\n" +
                        "nice\n" +
                        "\n" +
                        "bold, italic, underline, with different sizes and colorful",
                    createdAt = "01.02.2022",
                    updatedAt = "03.02.2022",
                    categories = categories.subList(2, 3).toSet(),
                    isPinned = false,
                ),
                Note(
                    title = null,
                    content = "Some stuff, some other stuff, some more stuff...",
                    createdAt = "12.02.2022",
                    updatedAt = "12.02.2022",
                    categories = categories.subList(1, 3).toSet(),
                    isPinned = true,
                ),
            ),
        )
    }

    fun filterNotesBySelectedCategory(): List<Note> {
        if (state.selectedCategory == CATEGORY_ALL) {
            return state.notes
        }

        return state.notes.filter {
            it.categories.contains(state.selectedCategory)
        }
    }

    fun onCategorySelected(value: Category) {
        state = state.copy(
            selectedCategory = value,
        )
    }

    fun partitionNotesByPinned(): Pair<List<Note>, List<Note>> =
        filterNotesBySelectedCategory().partition { it.isPinned }
}

data class State(
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category = CATEGORY_ALL,
    val notes: List<Note> = emptyList(),
)
