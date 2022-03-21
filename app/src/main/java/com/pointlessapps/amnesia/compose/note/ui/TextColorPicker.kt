package com.pointlessapps.amnesia.compose.note.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.pointlessapps.amnesia.R
import com.pointlessapps.amnesia.compose.ui.theme.Icons
import com.pointlessapps.amnesia.compose.utils.*

private const val MAX_HUE_VALUE = 360
private const val HALF = 0.5f

@Composable
internal fun TextColorPicker(
    recentColors: List<Color>,
    onDismissListener: () -> Unit,
    onColorClicked: (Color) -> Unit,
    onAddToRecents: (Color) -> Unit,
) {
    var currentColor by remember { mutableStateOf(Color.Black) }
    var showColorSlider by remember { mutableStateOf(false) }

    Popup(
        onDismissRequest = onDismissListener,
        popupPositionProvider = AbovePositionProvider(
            IntOffset(
                x = 0,
                y = -dimensionResource(id = R.dimen.medium_padding).roundToPx(),
            ),
        ),
        properties = PopupProperties(),
    ) {
        Row(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colors.secondary)
                .padding(dimensionResource(id = R.dimen.small_padding)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_padding)),
        ) {
            if (showColorSlider) {
                ColorSlider {
                    currentColor = it
                    onColorClicked(it)
                }
                Icons.Done(
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.tiny_padding))
                        .size(dimensionResource(id = R.dimen.icon_size))
                        .clickable(
                            onClick = {
                                showColorSlider = false
                                onAddToRecents(currentColor)
                            },
                            role = Role.Button,
                        ),
                    tint = MaterialTheme.colors.onSecondary,
                )
            } else {
                recentColors.forEach {
                    Icons.Circle(
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.tiny_padding))
                            .size(dimensionResource(id = R.dimen.icon_size))
                            .clickable(
                                onClick = { onColorClicked(it) },
                                role = Role.Button,
                            ),
                        tint = it,
                    )
                }
                Icons.Plus(
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.tiny_padding))
                        .size(dimensionResource(id = R.dimen.icon_size))
                        .clickable(
                            onClick = { showColorSlider = true },
                            role = Role.Button,
                        ),
                    tint = MaterialTheme.colors.onSecondary,
                )
            }
        }
    }
}

@Composable
private fun ColorSlider(onValueChangeListener: (Color) -> Unit) {
    var currentColorHue by remember { mutableStateOf(0f) }
    val rainbowBrush = remember { Brush.horizontalGradient(getRainbowColors()) }
    val cornerRadius = dimensionResource(id = R.dimen.rounded_corners).toPx()
    val indicatorWidth = dimensionResource(id = R.dimen.size_10).toPx()
    val indicatorBorderWidth = dimensionResource(id = R.dimen.size_4).toPx()
    BoxWithConstraints(
        modifier = Modifier
            .width(dimensionResource(id = R.dimen.size_200))
            .height(dimensionResource(id = R.dimen.icon_button_size)),
    ) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .onMove(
                onMoved = { (x, _) ->
                    currentColorHue = (x / maxWidth.toPx() * MAX_HUE_VALUE)
                        .coerceIn(0f, MAX_HUE_VALUE.toFloat())
                },
                onUp = { onValueChangeListener(currentColorHue.hueToColor()) },
            ),
        ) {
            drawRoundRect(
                brush = rainbowBrush,
                cornerRadius = CornerRadius(cornerRadius),
            )
            drawRoundRect(
                topLeft = Offset(
                    x = (currentColorHue / MAX_HUE_VALUE * maxWidth.toPx())
                        .coerceAtMost(maxWidth.toPx() - indicatorWidth),
                    y = 0f,
                ),
                size = Size(
                    width = indicatorWidth,
                    height = maxHeight.toPx(),
                ),
                color = Color.White,
                cornerRadius = CornerRadius(cornerRadius),
            )
            drawRoundRect(
                topLeft = Offset(
                    x = (currentColorHue / MAX_HUE_VALUE * maxWidth.toPx() + indicatorBorderWidth * HALF)
                        .coerceAtMost(maxWidth.toPx() - indicatorWidth + indicatorBorderWidth * HALF),
                    y = indicatorBorderWidth * HALF,
                ),
                size = Size(
                    width = indicatorWidth,
                    height = maxHeight.toPx(),
                ).inset(indicatorBorderWidth),
                color = currentColorHue.hueToColor(),
                cornerRadius = CornerRadius(cornerRadius),
            )
        }
    }
}

@Suppress("MagicNumber")
private fun getRainbowColors() = listOf(
    Color(0xFFFF0000),
    Color(0xFFFF8000),
    Color(0xFFFFFF00),
    Color(0xFF80FF00),
    Color(0xFF00FF00),
    Color(0xFF00FF80),
    Color(0xFF00FFFF),
    Color(0xFF0080FF),
    Color(0xFF0000FF),
    Color(0xFF8000FF),
    Color(0xFFFF00FF),
    Color(0xFFFF0040),
)
