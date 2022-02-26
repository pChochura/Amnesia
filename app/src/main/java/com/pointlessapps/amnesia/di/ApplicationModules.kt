package com.pointlessapps.amnesia.di

import com.pointlessapps.amnesia.home.di.homeModule
import com.pointlessapps.amnesia.note.di.noteModule

val applicationModules = listOf(
	homeModule,
	noteModule
)
