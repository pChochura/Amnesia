package com.pointlessapps.amnesia.domain.auth

class SignInWithGoogleUseCase(
    private val repository: AuthRepository,
) {

    fun prepare() = repository.signInWithGoogle()
}
