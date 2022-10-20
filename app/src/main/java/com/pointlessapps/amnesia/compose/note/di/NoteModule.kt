package com.pointlessapps.amnesia.compose.note.di

import com.pointlessapps.amnesia.compose.note.ui.NoteViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val noteModule = module {

    viewModel {
        NoteViewModel(
            getAllCategoriesUseCase = get(),
            saveNoteUseCase = get(),
            saveCategoryUseCase = get(),
            dateFormatter = get(),
        )
    }
}
