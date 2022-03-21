package com.pointlessapps.amnesia.domain.notes.usecase

import com.pointlessapps.amnesia.domain.notes.NotesRepository
import com.pointlessapps.amnesia.domain.notes.dto.Note

class SaveNoteUseCase(
    private val notesRepository: NotesRepository,
) {

    fun prepare(note: Note) = notesRepository.saveNote(note)
}
