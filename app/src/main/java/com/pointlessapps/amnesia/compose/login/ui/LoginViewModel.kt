package com.pointlessapps.amnesia.compose.login.ui

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.pointlessapps.amnesia.R
import com.pointlessapps.amnesia.domain.auth.SignInAnonymouslyUseCase
import com.pointlessapps.amnesia.domain.auth.SignInWithGoogleUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal class LoginViewModel(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signInAnonymouslyUseCase: SignInAnonymouslyUseCase,
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    var state by mutableStateOf(State())
        private set

    fun onSignInWithGoogleClicked(intentLauncher: ActivityResultLauncher<Intent>) {
        signInWithGoogleUseCase.prepare()
            .take(1)
            .onStart {
                state = state.copy(
                    isLoading = true,
                )
            }
            .onEach {
                it.launch(intentLauncher)
            }
            .catch {
                eventChannel.send(Event.ShowMessage(R.string.default_error_message))
                state = state.copy(
                    isLoading = false,
                )
            }
            .launchIn(viewModelScope)
    }

    fun handleSignInWithGoogleResult(data: GoogleSignInResult?) {
        state = state.copy(
            isLoading = false,
        )
        viewModelScope.launch {
            if (data?.isSuccess == true) {
                eventChannel.send(Event.MoveToNextScreen)
            } else {
                eventChannel.send(Event.ShowMessage(R.string.default_error_message))
            }
        }
    }

    fun onSignInAnonymously() {
        signInAnonymouslyUseCase.prepare()
            .take(1)
            .onStart {
                state = state.copy(
                    isLoading = true,
                )
            }
            .onEach {
                state = state.copy(
                    isLoading = false,
                )
                eventChannel.send(Event.MoveToNextScreen)
            }
            .catch {
                eventChannel.send(Event.ShowMessage(R.string.default_error_message))
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
        class ShowMessage(@StringRes val message: Int) : Event
        object MoveToNextScreen : Event
    }
}
