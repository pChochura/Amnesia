package com.pointlessapps.amnesia.domain.auth.di

import com.pointlessapps.amnesia.domain.auth.*
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

    factory {
        SignInAnonymouslyUseCase(
            repository = get(),
        )
    }

    factory {
        IsSignedInUseCase(
            repository = get(),
        )
    }

    factory {
        LinkWithWithGoogleUseCase(
            repository = get(),
        )
    }
}
