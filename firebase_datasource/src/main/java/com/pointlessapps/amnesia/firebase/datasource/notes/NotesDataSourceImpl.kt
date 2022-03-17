package com.pointlessapps.amnesia.firebase.datasource.notes

import com.pointlessapps.amnesia.datasource.notes.NotesDataSource
import com.pointlessapps.amnesia.datasource.notes.dto.NoteDto

internal class NotesDataSourceImpl : NotesDataSource {
    override suspend fun getAllNotes(): List<NoteDto> {
        return emptyList()
    }
}
