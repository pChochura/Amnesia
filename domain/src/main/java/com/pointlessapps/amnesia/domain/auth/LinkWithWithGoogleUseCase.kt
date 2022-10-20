package com.pointlessapps.amnesia.domain.auth

class LinkWithWithGoogleUseCase(
    private val repository: AuthRepository,
) {

    fun prepare() = repository.linkWithGoogle()
}
