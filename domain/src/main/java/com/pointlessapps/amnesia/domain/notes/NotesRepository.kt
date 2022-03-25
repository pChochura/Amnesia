package com.pointlessapps.amnesia.domain.notes

import com.pointlessapps.amnesia.datasource.notes.StoreNotesDataSource
import com.pointlessapps.amnesia.domain.notes.dto.Category
import com.pointlessapps.amnesia.domain.notes.dto.Note
import com.pointlessapps.amnesia.domain.notes.mapper.toCategories
import com.pointlessapps.amnesia.domain.notes.mapper.toCategoryDto
import com.pointlessapps.amnesia.domain.notes.mapper.toNoteDto
import com.pointlessapps.amnesia.domain.notes.mapper.toNotes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface NotesRepository {
    fun getAllCategories(): Flow<List<Category>>

    fun saveCategory(category: Category): Flow<Unit>

    fun getAllNotes(): Flow<List<Note>>

    fun saveNote(note: Note): Flow<Unit>
}

internal class NotesRepositoryImpl(
    private val storeNotesDataSource: StoreNotesDataSource,
) : NotesRepository {

    override fun getAllCategories(): Flow<List<Category>> = flow {
        emit(storeNotesDataSource.getAllCategories().toCategories())
    }

    override fun saveCategory(category: Category): Flow<Unit> = flow {
        emit(storeNotesDataSource.saveCategory(category.toCategoryDto()))
    }

    override fun getAllNotes(): Flow<List<Note>> = flow {
        val categories = storeNotesDataSource.getAllCategories()
            .toCategories()
            .associateBy { it.id }
        emit(storeNotesDataSource.getAllNotes().toNotes(categories))
    }

    override fun saveNote(note: Note): Flow<Unit> = flow {
        emit(storeNotesDataSource.saveNote(note.toNoteDto()))
    }
}
