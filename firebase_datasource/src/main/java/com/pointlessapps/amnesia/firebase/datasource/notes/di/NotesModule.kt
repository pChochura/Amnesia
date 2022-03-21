package com.pointlessapps.amnesia.firebase.datasource.notes.di

import com.pointlessapps.amnesia.datasource.notes.StoreNotesDataSource
import com.pointlessapps.amnesia.firebase.datasource.notes.StoreNotesDataSourceImpl
import org.koin.dsl.module

internal val notesModule = module {
    single<StoreNotesDataSource> {
        StoreNotesDataSourceImpl(
            firestore = get(),
            auth = get(),
        )
    }
}
