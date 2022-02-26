package com.pointlessapps.amnesia.note.di

import com.pointlessapps.amnesia.note.ui.NoteViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val noteModule = module {

	viewModel {
		NoteViewModel()
	}
}
