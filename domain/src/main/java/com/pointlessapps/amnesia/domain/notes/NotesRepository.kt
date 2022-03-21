package com.pointlessapps.amnesia.domain.notes

import com.pointlessapps.amnesia.datasource.notes.StoreNotesDataSource
import com.pointlessapps.amnesia.domain.notes.dto.Note
import com.pointlessapps.amnesia.domain.notes.mapper.toNoteDto
import com.pointlessapps.amnesia.domain.notes.mapper.toNotes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface NotesRepository {
    fun getAllNotes(): Flow<List<Note>>

    fun saveNote(note: Note): Flow<Unit>
}

internal class NotesRepositoryImpl(
    private val storeNotesDataSource: StoreNotesDataSource,
) : NotesRepository {

    override fun getAllNotes(): Flow<List<Note>> = flow {
        emit(storeNotesDataSource.getAllNotes().toNotes())
    }

    override fun saveNote(note: Note): Flow<Unit> = flow {
        emit(storeNotesDataSource.saveNote(note.toNoteDto()))
    }
}
