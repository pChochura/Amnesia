package com.pointlessapps.amnesia.datasource.auth

interface AuthDataSource {
    suspend fun singInAnonymously()

    suspend fun linkWithGoogle()

    suspend fun signInWithGoogle()
}
