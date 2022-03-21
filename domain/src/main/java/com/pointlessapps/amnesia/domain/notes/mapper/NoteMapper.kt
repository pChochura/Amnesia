package com.pointlessapps.amnesia.domain.notes.mapper

import com.pointlessapps.amnesia.datasource.notes.dto.NoteDto
import com.pointlessapps.amnesia.domain.notes.dto.Note

internal fun List<NoteDto>.toNotes(): List<Note> = map(NoteDto::toNote)

private fun NoteDto.toNote(): Note = Note(
    title = title,
    content = content,
)

internal fun Note.toNoteDto(): NoteDto = NoteDto(
    title = title,
    content = content,
)
