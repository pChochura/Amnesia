package com.pointlessapps.amnesia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.imePadding
import com.google.accompanist.insets.systemBarsPadding
import com.pointlessapps.amnesia.compose.home.ui.HomeScreen
import com.pointlessapps.amnesia.compose.login.ui.LoginScreen
import com.pointlessapps.amnesia.compose.note.ui.NoteScreen
import com.pointlessapps.amnesia.compose.ui.components.AmnesiaSnackbar
import com.pointlessapps.amnesia.compose.ui.components.AmnesiaSnackbarHostState
import com.pointlessapps.amnesia.compose.ui.theme.AmnesiaTheme
import com.pointlessapps.amnesia.compose.ui.theme.Route
import dev.olshevski.navigation.reimagined.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        setContent {
            AmnesiaTheme {
                ProvideWindowInsets(
                    windowInsetsAnimationsEnabled = true,
                    consumeWindowInsets = false,
                ) {
                    val context = LocalContext.current
                    val snackbarHostState = remember { SnackbarHostState() }
                    val coroutineScope = rememberCoroutineScope()
                    val amnesiaSnackbarHostState = AmnesiaSnackbarHostState { message, duration ->
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = context.getString(message),
                                duration = duration,
                            )
                        }
                    }

                    CompositionLocalProvider(
                        LocalTextSelectionColors provides TextSelectionColors(
                            handleColor = MaterialTheme.colors.onPrimary,
                            backgroundColor = MaterialTheme.colors.onPrimary.copy(alpha = 0.4f),
                        ),
                        LocalSnackbarHostState provides amnesiaSnackbarHostState,
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colors.background,
                        ) {
                            NavHost(
                                navController = rememberNavController(
                                    startDestination = viewModel.startingRoute,
                                ),
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .systemBarsPadding()
                                    .imePadding()
                                    .padding(dimensionResource(id = R.dimen.medium_padding)),
                                contentAlignment = Alignment.BottomCenter,
                            ) {
                                SnackbarHost(hostState = snackbarHostState) {
                                    AmnesiaSnackbar(message = it.message)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    private fun NavHost(navController: NavController<Route>) {
        NavBackHandler(navController = navController)
        AnimatedNavHost(controller = navController) {
            when (it) {
                Route.Login -> LoginScreen(
                    onNavigateToHome = {
                        navController.popAll()
                        navController.navigate(Route.Home)
                    },
                )
                Route.Home -> HomeScreen(
                    onNavigateToNoteClicked = {
                        navController.navigate(Route.NewNote)
                    },
                )
                Route.NewNote -> NoteScreen()
            }
        }
    }
}

internal val LocalSnackbarHostState = compositionLocalOf<AmnesiaSnackbarHostState> {
    error("No AmnesiaSnackbarHostState")
}
