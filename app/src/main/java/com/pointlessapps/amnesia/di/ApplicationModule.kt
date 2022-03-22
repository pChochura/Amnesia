package com.pointlessapps.amnesia.di

import com.pointlessapps.amnesia.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val applicationModule = module {
    viewModel {
        MainViewModel(
            isSignedInUseCase = get(),
        )
    }
}
