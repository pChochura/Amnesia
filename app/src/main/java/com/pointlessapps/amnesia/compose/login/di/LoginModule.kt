package com.pointlessapps.amnesia.compose.login.di

import com.pointlessapps.amnesia.compose.login.ui.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val loginModule = module {
    viewModel {
        LoginViewModel(
            signInWithGoogleUseCase = get(),
        )
    }
}
