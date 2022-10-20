package com.pointlessapps.amnesia.firebase.datasource.auth

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.pointlessapps.amnesia.datasource.auth.AuthDataSource
import com.pointlessapps.amnesia.datasource.auth.dto.GoogleSignInClientDto
import kotlinx.coroutines.tasks.await

internal class AuthDataSourceImpl(
    private val context: Context,
    private val auth: FirebaseAuth,
) : AuthDataSource {

    private suspend fun getGoogleAuthCredential(): AuthCredential {
        val idToken = auth.currentUser?.getIdToken(false)?.await()
        return GoogleAuthProvider.getCredential(idToken?.token, null)
    }

    override suspend fun singInAnonymously() {
        auth.signInAnonymously().await()
    }

    override suspend fun linkWithGoogle() {
        requireNotNull(auth.currentUser) {
            "User have to be authenticated"
        }.linkWithCredential(getGoogleAuthCredential()).await()
    }

    override suspend fun signInWithGoogle(): GoogleSignInClientDto {
        val options = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        return GoogleSignInClientDto(GoogleSignIn.getClient(context, options).signInIntent)
    }

    override fun isSignedIn(): Boolean {
        return auth.currentUser != null
    }
}
