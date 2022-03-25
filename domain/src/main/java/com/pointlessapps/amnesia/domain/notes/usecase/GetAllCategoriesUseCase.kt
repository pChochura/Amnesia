package com.pointlessapps.amnesia.domain.notes.usecase

import com.pointlessapps.amnesia.domain.notes.NotesRepository
import com.pointlessapps.amnesia.domain.notes.dto.Category
import kotlinx.coroutines.flow.Flow

class GetAllCategoriesUseCase(
    private val notesRepository: NotesRepository,
) {

    fun prepare(): Flow<List<Category>> = notesRepository.getAllCategories()
}
