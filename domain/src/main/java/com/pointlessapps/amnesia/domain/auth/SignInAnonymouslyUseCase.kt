package com.pointlessapps.amnesia.domain.auth

class SignInAnonymouslyUseCase(
    private val repository: AuthRepository,
) {

    fun prepare() = repository.signInAnonymously()
}
