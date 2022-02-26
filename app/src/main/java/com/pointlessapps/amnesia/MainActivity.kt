package com.pointlessapps.amnesia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import com.pointlessapps.amnesia.home.ui.HomeScreen
import com.pointlessapps.amnesia.note.ui.NoteScreen
import com.pointlessapps.amnesia.theme.HOME_ROUTE
import com.pointlessapps.amnesia.theme.NEW_NOTE_ROUTE
import com.pointlessapps.amnesia.ui.theme.AmnesiaTheme

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


	@Composable
	private fun NavHost(navController: NavHostController = rememberNavController()) = NavHost(
		navController = navController,
		startDestination = HOME_ROUTE
	) {
		composable(HOME_ROUTE) {
			HomeScreen(
				onNavigateToNoteClicked = {
					navController.navigate(NEW_NOTE_ROUTE)
				}
			)
		}
		composable(NEW_NOTE_ROUTE) {
			NoteScreen()
		}
	}
}
