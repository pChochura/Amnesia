package com.pointlessapps.amnesia.domain.auth.mapper

import com.pointlessapps.amnesia.datasource.auth.dto.GoogleSignInClientDto
import com.pointlessapps.amnesia.domain.auth.model.GoogleSignInClient

internal fun GoogleSignInClientDto.toGoogleSignInClient() = GoogleSignInClient(
    signInIntent = this.signInIntent
)
