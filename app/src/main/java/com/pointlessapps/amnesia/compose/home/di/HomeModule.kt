package com.pointlessapps.amnesia.compose.home.di

import com.pointlessapps.amnesia.compose.home.ui.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val homeModule = module {
    viewModel {
        HomeViewModel(
            getAllNotesUseCase = get(),
            getAllCategoriesUseCase = get(),
            dateFormatter = get(),
        )
    }
}
