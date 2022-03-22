package com.pointlessapps.amnesia.di

import com.pointlessapps.amnesia.compose.home.di.homeModule
import com.pointlessapps.amnesia.compose.login.di.loginModule
import com.pointlessapps.amnesia.compose.note.di.noteModule

val applicationModules = listOf(
    applicationModule,
    loginModule,
    homeModule,
    noteModule,
)
