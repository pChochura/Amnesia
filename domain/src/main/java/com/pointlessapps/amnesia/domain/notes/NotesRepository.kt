package com.pointlessapps.amnesia.domain.notes

import com.pointlessapps.amnesia.datasource.notes.NotesDataSource
import com.pointlessapps.amnesia.domain.notes.dto.Note
import com.pointlessapps.amnesia.domain.notes.mapper.toNotes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface NotesRepository {
    fun getAllNotes(): Flow<List<Note>>
}

internal class NotesRepositoryImpl(
    private val notesDataSource: NotesDataSource,
) : NotesRepository {

    override fun getAllNotes(): Flow<List<Note>> = flow {
        emit(notesDataSource.getAllNotes().toNotes())
    }
}
