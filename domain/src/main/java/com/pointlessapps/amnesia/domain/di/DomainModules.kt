package com.pointlessapps.amnesia.domain.di

import com.pointlessapps.amnesia.domain.auth.di.authModule
import com.pointlessapps.amnesia.domain.notes.di.notesModule
import com.pointlessapps.amnesia.domain.utils.di.utilsModule

val domainModules = listOf(
    notesModule,
    authModule,
    utilsModule,
)
