package com.pointlessapps.amnesia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.pointlessapps.amnesia.compose.home.ui.HomeScreen
import com.pointlessapps.amnesia.compose.note.ui.NoteScreen
import com.pointlessapps.amnesia.compose.ui.theme.AmnesiaTheme
import com.pointlessapps.amnesia.compose.ui.theme.Route
import dev.olshevski.navigation.reimagined.*

class MainActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		WindowCompat.setDecorFitsSystemWindows(window, false)
		super.onCreate(savedInstanceState)

		setContent {
			AmnesiaTheme {
				ProvideWindowInsets(
					windowInsetsAnimationsEnabled = true,
					consumeWindowInsets = false
				) {
					CompositionLocalProvider(
						LocalTextSelectionColors provides TextSelectionColors(
							handleColor = MaterialTheme.colors.onPrimary,
							backgroundColor = MaterialTheme.colors.onPrimary.copy(alpha = 0.4f)
						)
					) {
						Surface(
							modifier = Modifier.fillMaxSize(),
							color = MaterialTheme.colors.background
						) {
							NavHost()
						}
					}
				}
			}
		}
	}

	@OptIn(ExperimentalAnimationApi::class)
	@Composable
	private fun NavHost(
		navController: NavController<Route> = rememberNavController(
			startDestination = Route.Home
		),
	) {
		NavBackHandler(navController = navController)
		AnimatedNavHost(controller = navController) {
			when (it) {
				Route.Home -> HomeScreen(
					onNavigateToNoteClicked = {
						navController.navigate(Route.NewNote)
					}
				)
				Route.NewNote -> NoteScreen()
			}
		}
	}
}
