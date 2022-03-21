package com.pointlessapps.amnesia.firebase.datasource.auth

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.pointlessapps.amnesia.datasource.auth.AuthDataSource
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
        requireNotNull(auth.currentUser).linkWithCredential(getGoogleAuthCredential()).await()
    }

    override suspend fun signInWithGoogle() {
        val options = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

//        context.startActivity(GoogleSignIn.getClient(context, options).signInIntent.apply {
//            addFlags(FLAG_ACTIVITY_NEW_TASK)
//        })
        GoogleSignIn.getClient(context, options).silentSignIn().await()
    }
}
