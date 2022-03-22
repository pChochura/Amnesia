package com.pointlessapps.amnesia

import androidx.lifecycle.ViewModel
import com.pointlessapps.amnesia.compose.ui.theme.Route
import com.pointlessapps.amnesia.domain.auth.IsSignedInUseCase

internal class MainViewModel(
    isSignedInUseCase: IsSignedInUseCase,
) : ViewModel() {

    val startingRoute = when (isSignedInUseCase.prepare()) {
        true -> Route.Home
        else -> Route.Login
    }
}
