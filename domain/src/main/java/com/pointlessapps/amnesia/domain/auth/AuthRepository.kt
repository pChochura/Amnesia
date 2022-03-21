package com.pointlessapps.amnesia.domain.auth

import com.pointlessapps.amnesia.datasource.auth.AuthDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AuthRepository {
    fun signInAnonymously(): Flow<Unit>

    fun linkWithGoogle(): Flow<Unit>

    fun signInWithGoogle(): Flow<Unit>
}

internal class AuthRepositoryImpl(
    private val authDataSource: AuthDataSource,
) : AuthRepository {

    override fun signInAnonymously(): Flow<Unit> = flow {
        emit(authDataSource.singInAnonymously())
    }

    override fun linkWithGoogle(): Flow<Unit> = flow {
        emit(authDataSource.linkWithGoogle())
    }

    override fun signInWithGoogle(): Flow<Unit> = flow {
        emit(authDataSource.signInWithGoogle())
    }
}
