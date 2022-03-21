package com.pointlessapps.amnesia.firebase.datasource.di

import com.pointlessapps.amnesia.firebase.datasource.auth.di.authModule
import com.pointlessapps.amnesia.firebase.datasource.notes.di.notesModule

val firebaseDataSourceModules = listOf(
    firebaseDataSourceModule,
    notesModule,
    authModule,
)
