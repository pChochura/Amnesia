package com.pointlessapps.amnesia.domain.notes.usecase

import com.pointlessapps.amnesia.domain.notes.NotesRepository
import com.pointlessapps.amnesia.domain.notes.dto.Note
import kotlinx.coroutines.flow.Flow

class GetAllNotesUseCase(
    private val notesRepository: NotesRepository,
) {

    fun prepare(): Flow<List<Note>> = notesRepository.getAllNotes()
}
