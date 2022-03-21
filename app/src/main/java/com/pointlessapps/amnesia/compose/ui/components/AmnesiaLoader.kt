package com.pointlessapps.amnesia.compose.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.zIndex
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.statusBarsPadding

private const val SCRIM_ALPHA = 0.9f

@Composable
internal fun AmnesiaLoader(enabled: Boolean) {
    val focusManager = LocalFocusManager.current
    LaunchedEffect(enabled) {
        focusManager.clearFocus(true)
    }

    AnimatedVisibility(
        modifier = Modifier.zIndex(1f),
        visible = enabled,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Surface(
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsWithImePadding(),
            color = MaterialTheme.colors.surface.copy(
                alpha = SCRIM_ALPHA,
            ),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}
