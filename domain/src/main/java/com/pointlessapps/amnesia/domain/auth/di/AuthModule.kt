package com.pointlessapps.amnesia.domain.auth.di

import com.pointlessapps.amnesia.domain.auth.AuthRepository
import com.pointlessapps.amnesia.domain.auth.AuthRepositoryImpl
import com.pointlessapps.amnesia.domain.auth.SignInWithGoogleUseCase
import org.koin.dsl.module

internal val authModule = module {
    single<AuthRepository> {
        AuthRepositoryImpl(
            authDataSource = get(),
        )
    }

    factory {
        SignInWithGoogleUseCase(
            repository = get(),
        )
    }
}
