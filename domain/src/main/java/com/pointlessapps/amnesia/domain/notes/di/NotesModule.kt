package com.pointlessapps.amnesia.domain.notes.di

import com.pointlessapps.amnesia.domain.notes.NotesRepository
import com.pointlessapps.amnesia.domain.notes.NotesRepositoryImpl
import com.pointlessapps.amnesia.domain.notes.usecase.GetAllNotesUseCase
import org.koin.dsl.module

internal val notesModule = module {
    factory {
        GetAllNotesUseCase(
            notesRepository = get(),
        )
    }

    single<NotesRepository> {
        NotesRepositoryImpl(
            notesDataSource = get(),
        )
    }
}
