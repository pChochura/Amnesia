package com.pointlessapps.amnesia.compose.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.pointlessapps.amnesia.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun AmnesiaBottomModal(
    modifier: Modifier = Modifier,
    sheetState: ModalBottomSheetState = ModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        isSkipHalfExpanded = true,
    ),
    onDismissListener: () -> Unit,
    modalContent: @Composable BoxScope.() -> Unit,
) {
    var wasShowed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(sheetState.currentValue, sheetState.isAnimationRunning) {
        if (sheetState.currentValue == ModalBottomSheetValue.Hidden && !sheetState.isAnimationRunning) {
            if (wasShowed) {
                onDismissListener()
            }
            wasShowed = true
        }
    }

    BackHandler {
        coroutineScope.launch {
            sheetState.hide()
        }
    }

    ModalBottomSheetLayout(
        modifier = Modifier
            .zIndex(1f)
            .nestedScroll(remember { object : NestedScrollConnection {} })
            .then(modifier),
        sheetState = sheetState,
        sheetElevation = 0.dp,
        sheetBackgroundColor = colorResource(id = android.R.color.transparent),
        scrimColor = colorResource(id = R.color.semi_transparent),
        sheetContent = {
            Surface(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = dimensionResource(id = R.dimen.medium_padding)),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colors.surface,
                elevation = dimensionResource(id = R.dimen.modal_elevation),
                content = {
                    Box(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .imePadding(),
                        content = modalContent,
                    )
                },
            )
        },
        content = {
            // no-op
        },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun AmnesiaBottomModal(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onDismissListener: () -> Unit,
    modalContent: @Composable BoxScope.() -> Unit,
) {
    var isShowed by remember { mutableStateOf(enabled) }
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
    LaunchedEffect(enabled) {
        if (enabled) {
            isShowed = true
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    if (isShowed) {
        AmnesiaBottomModal(
            modifier = modifier,
            sheetState = sheetState,
            onDismissListener = {
                isShowed = false
                onDismissListener()
            },
            modalContent = modalContent,
        )
    }
}
