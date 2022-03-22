package com.pointlessapps.amnesia.domain.auth

import com.pointlessapps.amnesia.datasource.auth.AuthDataSource
import com.pointlessapps.amnesia.domain.auth.mapper.toGoogleSignInClient
import com.pointlessapps.amnesia.domain.auth.model.GoogleSignInClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AuthRepository {
    fun signInAnonymously(): Flow<Unit>

    fun linkWithGoogle(): Flow<Unit>

    fun signInWithGoogle(): Flow<GoogleSignInClient>

    fun isSignedIn(): Boolean
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

    override fun signInWithGoogle(): Flow<GoogleSignInClient> = flow {
        emit(authDataSource.signInWithGoogle().toGoogleSignInClient())
    }

    override fun isSignedIn(): Boolean {
        return authDataSource.isSignedIn()
    }
}
