package com.pointlessapps.amnesia.datasource.notes

import com.pointlessapps.amnesia.datasource.notes.dto.CategoryDto
import com.pointlessapps.amnesia.datasource.notes.dto.NoteDto

interface StoreNotesDataSource {
    suspend fun getAllCategories(): List<CategoryDto>

    suspend fun saveCategory(category: CategoryDto)

    suspend fun getAllNotes(): List<NoteDto>

    suspend fun saveNote(note: NoteDto)
}
