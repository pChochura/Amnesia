package com.pointlessapps.amnesia.compose.login.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.amnesia.domain.auth.SignInWithGoogleUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

internal class LoginViewModel(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    var state by mutableStateOf(State())
        private set

    fun onSignInWithGoogleClicked() {
        signInWithGoogleUseCase.prepare()
            .take(1)
            .onStart {
                state = state.copy(
                    isLoading = true,
                )
            }
            .onEach {
                eventChannel.send(Event.MoveToNextScreen)
                state = state.copy(
                    isLoading = false,
                )
            }
            .catch {
                println("LOG!, $it")
                state = state.copy(
                    isLoading = false,
                )
            }
            .launchIn(viewModelScope)
    }

    internal data class State(
        val isLoading: Boolean = false,
    )

    internal sealed interface Event {
        object MoveToNextScreen : Event
    }
}
