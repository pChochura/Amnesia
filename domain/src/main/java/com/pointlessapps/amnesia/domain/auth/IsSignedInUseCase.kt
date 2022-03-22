package com.pointlessapps.amnesia.domain.auth

class IsSignedInUseCase(
    private val repository: AuthRepository,
) {

    fun prepare() = repository.isSignedIn()
}
