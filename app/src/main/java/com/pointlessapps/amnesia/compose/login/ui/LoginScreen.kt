package com.pointlessapps.amnesia.compose.login.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.pointlessapps.amnesia.LocalSnackbarHostState
import com.pointlessapps.amnesia.R
import com.pointlessapps.amnesia.compose.ui.components.*
import com.pointlessapps.amnesia.firebase.datasource.auth.contracts.GetGoogleSignInResult
import org.koin.androidx.compose.getViewModel

private const val VERTICAL_FLIP_ROTATION = 180f

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

    AmnesiaLoader(enabled = viewModel.state.isLoading)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .rotate(VERTICAL_FLIP_ROTATION),
                alignment = Alignment.BottomCenter,
                contentScale = ContentScale.FillWidth,
                painter = painterResource(id = R.drawable.ic_background),
                contentDescription = null,
            )
            Image(
                modifier = Modifier.fillMaxWidth(),
                alignment = Alignment.BottomCenter,
                contentScale = ContentScale.FillWidth,
                painter = painterResource(id = R.drawable.ic_background),
                contentDescription = null,
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.huge_padding),
                alignment = Alignment.CenterVertically,
            ),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.big_padding)),
            ) {
                AmnesiaText(
                    text = stringResource(id = R.string.app_name),
                    textStyle = defaultAmnesiaTextStyle().copy(
                        typography = MaterialTheme.typography.h1.copy(
                            fontSize = 48.sp,
                        ),
                        textAlign = TextAlign.Center,
                    ),
                )
                AmnesiaText(
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(id = R.dimen.huge_padding),
                    ),
                    text = "An all-in-one solution for those who forgot why they downloaded this app",
                    textStyle = defaultAmnesiaTextStyle().copy(
                        typography = MaterialTheme.typography.h3,
                        textAlign = TextAlign.Center,
                    ),
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.medium_padding)),
            ) {
                AmnesiaButton(
                    text = stringResource(R.string.sign_in_with_google),
                    onClick = { viewModel.onSignInWithGoogleClicked(intentLauncher) },
                    buttonModel = defaultAmnesiaButtonModel().copy(
                        horizontalPadding = dimensionResource(id = R.dimen.medium_padding),
                        iconModel = AmnesiaButtonIconModel(
                            icon = R.drawable.ic_google,
                        ),
                    ),
                )
                AmnesiaButton(
                    text = stringResource(R.string.use_anonymously),
                    onClick = { viewModel.onSignInAnonymously() },
                    buttonModel = defaultAmnesiaButtonModel().copy(
                        backgroundColor = MaterialTheme.colors.onSecondary,
                        textColor = MaterialTheme.colors.secondary,
                        border = BorderStroke(
                            dimensionResource(id = R.dimen.border_size),
                            MaterialTheme.colors.secondary,
                        ),
                    ),
                )
            }
        }
    }
}
