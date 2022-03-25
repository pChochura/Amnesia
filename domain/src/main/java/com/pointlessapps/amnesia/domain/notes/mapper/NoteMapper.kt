package com.pointlessapps.amnesia.domain.notes.mapper

import com.pointlessapps.amnesia.datasource.notes.dto.CategoryDto
import com.pointlessapps.amnesia.datasource.notes.dto.ContentDto
import com.pointlessapps.amnesia.datasource.notes.dto.NoteDto
import com.pointlessapps.amnesia.domain.notes.dto.Category
import com.pointlessapps.amnesia.domain.notes.dto.Content
import com.pointlessapps.amnesia.domain.notes.dto.Note

internal fun List<NoteDto>.toNotes(categories: Map<Long, Category>): List<Note> =
    map { it.toNote(categories) }

private fun NoteDto.toNote(categories: Map<Long, Category>): Note = Note(
    id = id,
    title = title,
    content = requireNotNull(content).run {
        Content(
            text = requireNotNull(text),
            spanStyles = spanStyles.map {
                Content.Span(
                    start = it.start,
                    end = it.end,
                    tag = requireNotNull(it.tag),
                )
            },
            paragraphStyles = spanStyles.map {
                Content.Span(
                    start = it.start,
                    end = it.end,
                    tag = requireNotNull(it.tag),
                )
            },
            selectionPosition = selectionPosition,
        )
    },
    createdAt = createdAt ?: System.currentTimeMillis(),
    updatedAt = updatedAt ?: System.currentTimeMillis(),
    categories = this.categories.mapNotNull { categories[it] },
    isPinned = pinned,
)

internal fun Note.toNoteDto(): NoteDto = NoteDto(
    title = title,
    content = ContentDto(
        text = content.text,
        spanStyles = content.spanStyles.map {
            ContentDto.SpanDto(
                start = it.start,
                end = it.end,
                tag = it.tag,
            )
        },
        paragraphStyles = content.spanStyles.map {
            ContentDto.SpanDto(
                start = it.start,
                end = it.end,
                tag = it.tag,
            )
        },
        selectionPosition = content.selectionPosition,
    ),
    createdAt = createdAt,
    updatedAt = updatedAt,
    categories = categories.map { it.id },
    pinned = isPinned,
)

internal fun List<CategoryDto>.toCategories(): List<Category> = map {
    Category(
        id = it.id,
        name = it.name,
        color = it.color,
    )
}
