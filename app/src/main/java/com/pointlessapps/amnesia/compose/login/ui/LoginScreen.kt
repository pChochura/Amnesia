package com.pointlessapps.amnesia.compose.login.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pointlessapps.amnesia.LocalSnackbarHostState
import com.pointlessapps.amnesia.R
import com.pointlessapps.amnesia.compose.ui.components.AmnesiaButton
import com.pointlessapps.amnesia.compose.ui.components.AudiLoader
import com.pointlessapps.amnesia.firebase.datasource.auth.contracts.GetGoogleSignInResult
import org.koin.androidx.compose.getViewModel

@Composable
internal fun LoginScreen(
    viewModel: LoginViewModel = getViewModel(),
    onNavigateToHome: () -> Unit,
) {
    val snackbarHostState = LocalSnackbarHostState.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                LoginViewModel.Event.MoveToNextScreen ->
                    onNavigateToHome()
                is LoginViewModel.Event.ShowMessage ->
                    snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    val intentLauncher = rememberLauncherForActivityResult(
        contract = GetGoogleSignInResult(),
        onResult = viewModel::handleSignInWithGoogleResult,
    )

    AudiLoader(enabled = viewModel.state.isLoading)

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AmnesiaButton(
            text = stringResource(R.string.sign_in_with_google),
            onClick = { viewModel.onSignInWithGoogleClicked(intentLauncher) },
        )
    }
}
