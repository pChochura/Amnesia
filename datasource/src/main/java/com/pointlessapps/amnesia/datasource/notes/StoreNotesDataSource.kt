package com.pointlessapps.amnesia.datasource.notes

import com.pointlessapps.amnesia.datasource.notes.dto.NoteDto

interface StoreNotesDataSource {
    suspend fun getAllNotes(): List<NoteDto>

    suspend fun saveNote(note: NoteDto)
}
