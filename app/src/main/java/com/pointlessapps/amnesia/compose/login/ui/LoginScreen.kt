package com.pointlessapps.amnesia.compose.login.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pointlessapps.amnesia.R
import com.pointlessapps.amnesia.compose.ui.components.AmnesiaButton
import com.pointlessapps.amnesia.compose.ui.components.AudiLoader
import org.koin.androidx.compose.getViewModel

@Composable
internal fun LoginScreen(
    viewModel: LoginViewModel = getViewModel(),
    onNavigateToHome: () -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                LoginViewModel.Event.MoveToNextScreen ->
                    onNavigateToHome()
            }
        }
    }

    AudiLoader(enabled = viewModel.state.isLoading)

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AmnesiaButton(
            text = stringResource(R.string.sign_in_with_google),
            onClick = viewModel::onSignInWithGoogleClicked,
        )
    }
}
