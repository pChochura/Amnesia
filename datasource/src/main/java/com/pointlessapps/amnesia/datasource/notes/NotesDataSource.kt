package com.pointlessapps.amnesia.datasource.notes

import com.pointlessapps.amnesia.datasource.notes.dto.NoteDto

interface NotesDataSource {
    suspend fun getAllNotes(): List<NoteDto>
}
