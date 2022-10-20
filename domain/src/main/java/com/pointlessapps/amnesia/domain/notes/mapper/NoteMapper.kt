package com.pointlessapps.amnesia.domain.notes.mapper

import com.pointlessapps.amnesia.datasource.notes.dto.CategoryDto
import com.pointlessapps.amnesia.datasource.notes.dto.ContentDto
import com.pointlessapps.amnesia.datasource.notes.dto.NoteDto
import com.pointlessapps.amnesia.domain.notes.dto.Category
import com.pointlessapps.amnesia.domain.notes.dto.Content
import com.pointlessapps.amnesia.domain.notes.dto.Note

internal fun List<NoteDto>.toNotes(categories: Map<Long, Category>): List<Note> = map { note ->
    Note(
        id = note.id,
        title = note.title,
        content = requireNotNull(note.content) { "Content cannot be empty" }.run {
            Content(
                text = requireNotNull(text) { "Content text cannot be empty" },
                spanStyles = spanStyles.map {
                    Content.Span(
                        start = it.start,
                        end = it.end,
                        tag = requireNotNull(it.tag) { "Content span tag cannot be empty" },
                    )
                },
                paragraphStyles = paragraphStyles.map {
                    Content.Span(
                        start = it.start,
                        end = it.end,
                        tag = requireNotNull(it.tag) { "Content paragraph tag cannot be empty" },
                    )
                },
                selectionPosition = selectionPosition,
            )
        },
        createdAt = note.createdAt ?: System.currentTimeMillis(),
        updatedAt = note.updatedAt ?: System.currentTimeMillis(),
        categories = note.categories.mapNotNull { categories[it] },
        isPinned = note.pinned,
    )
}

internal fun Note.toNoteDto(): NoteDto = NoteDto(
    id = id,
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
        paragraphStyles = content.paragraphStyles.map {
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
        name = requireNotNull(it.name) { "Category name cannot be empty" },
        color = requireNotNull(it.color) { "Category color cannot be empty" }.toULong(),
    )
}

internal fun Category.toCategoryDto(): CategoryDto = CategoryDto(
    id = id,
    name = name,
    color = color.toString(),
)
