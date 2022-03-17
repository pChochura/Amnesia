package com.pointlessapps.amnesia.di

import com.pointlessapps.amnesia.compose.home.di.homeModule
import com.pointlessapps.amnesia.compose.note.di.noteModule

val applicationModules = listOf(
    homeModule,
    noteModule
)
