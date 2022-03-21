package com.pointlessapps.amnesia.datasource.auth

import com.pointlessapps.amnesia.datasource.auth.dto.GoogleSignInClientDto

interface AuthDataSource {
    suspend fun singInAnonymously()

    suspend fun linkWithGoogle()

    suspend fun signInWithGoogle(): GoogleSignInClientDto
}
