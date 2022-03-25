package com.pointlessapps.amnesia.compose.home.mapper

import com.pointlessapps.amnesia.domain.notes.dto.Category
import com.pointlessapps.amnesia.domain.notes.dto.Content
import com.pointlessapps.amnesia.domain.notes.dto.Note
import com.pointlessapps.amnesia.model.CategoryModel
import com.pointlessapps.amnesia.model.NoteModel
import com.pointlessapps.rt_editor.utils.RichTextValueSnapshot
import java.text.SimpleDateFormat
import java.util.*

internal fun Note.toNoteModel(dateFormatter: SimpleDateFormat) = NoteModel(
    id = id,
    title = title,
    content = content.toRichTextValueSnapshot(),
    createdAt = dateFormatter.format(Date(createdAt)),
    updatedAt = dateFormatter.format(Date(updatedAt)),
    categories = categories.map(Category::toCategoryModel).toSet(),
    isPinned = isPinned,
)

private fun Category.toCategoryModel() = CategoryModel(
    id = id,
    name = name,
    color = color,
)

private fun Content.toRichTextValueSnapshot() = RichTextValueSnapshot(
    text = text,
    spanStyles = spanStyles.map(Content.Span::toRichTextValueSpanSnapshot),
    paragraphStyles = paragraphStyles.map(Content.Span::toRichTextValueSpanSnapshot),
    selectionPosition = selectionPosition,
)

private fun Content.Span.toRichTextValueSpanSnapshot() =
    RichTextValueSnapshot.RichTextValueSpanSnapshot(
        start = start,
        end = end,
        tag = tag,
    )
