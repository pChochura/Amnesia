package com.pointlessapps.amnesia.domain.notes.di

import com.pointlessapps.amnesia.domain.notes.NotesRepository
import com.pointlessapps.amnesia.domain.notes.NotesRepositoryImpl
import com.pointlessapps.amnesia.domain.notes.usecase.GetAllCategoriesUseCase
import com.pointlessapps.amnesia.domain.notes.usecase.GetAllNotesUseCase
import com.pointlessapps.amnesia.domain.notes.usecase.SaveNoteUseCase
import org.koin.dsl.module

internal val notesModule = module {
    factory {
        GetAllNotesUseCase(
            notesRepository = get(),
        )
    }
    factory {
        SaveNoteUseCase(
            notesRepository = get(),
        )
    }
    factory {
        GetAllCategoriesUseCase(
            notesRepository = get(),
        )
    }

    single<NotesRepository> {
        NotesRepositoryImpl(
            storeNotesDataSource = get(),
        )
    }
}
