package com.pointlessapps.amnesia.firebase.datasource.auth.di

import com.pointlessapps.amnesia.datasource.auth.AuthDataSource
import com.pointlessapps.amnesia.firebase.datasource.auth.AuthDataSourceImpl
import org.koin.dsl.module

internal val authModule = module {
    single<AuthDataSource> {
        AuthDataSourceImpl(
            context = get(),
            auth = get(),
        )
    }
}
