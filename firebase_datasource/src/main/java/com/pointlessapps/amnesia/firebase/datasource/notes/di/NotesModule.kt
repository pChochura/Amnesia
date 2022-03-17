package com.pointlessapps.amnesia.firebase.datasource.notes.di

import com.pointlessapps.amnesia.datasource.notes.NotesDataSource
import com.pointlessapps.amnesia.firebase.datasource.notes.NotesDataSourceImpl
import org.koin.dsl.module

internal val notesModule = module {
    single<NotesDataSource> {
        NotesDataSourceImpl()
    }
}
