package com.pointlessapps.amnesia.home.di

import com.pointlessapps.amnesia.home.ui.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {

	viewModel {
		HomeViewModel()
	}
}
