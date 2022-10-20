package com.pointlessapps.amnesia.domain.notes.usecase

import com.pointlessapps.amnesia.domain.notes.NotesRepository
import com.pointlessapps.amnesia.domain.notes.dto.Category
import kotlinx.coroutines.flow.Flow

class SaveCategoryUseCase(
    private val notesRepository: NotesRepository,
) {

    fun prepare(category: Category): Flow<Unit> = notesRepository.saveCategory(category)
}
