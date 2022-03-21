package com.pointlessapps.amnesia.compose.ui.components

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

@Composable
internal fun AmnesiaText(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: AmnesiaTextStyle = defaultAmnesiaTextStyle(),
) = AmnesiaText(
    annotatedString = AnnotatedString(text),
    modifier = modifier,
    textStyle = textStyle,
)

@Composable
internal fun AmnesiaText(
    annotatedString: AnnotatedString,
    modifier: Modifier = Modifier,
    textStyle: AmnesiaTextStyle = defaultAmnesiaTextStyle(),
) {
    Text(
        modifier = modifier,
        text = annotatedString,
        style = textStyle.typography.copy(
            color = textStyle.textColor,
            textAlign = textStyle.textAlign,
        ),
    )
}

@Composable
internal fun defaultAmnesiaTextStyle() = AmnesiaTextStyle(
    textColor = MaterialTheme.colors.onPrimary,
    typography = MaterialTheme.typography.body1,
    textAlign = TextAlign.Start,
)

internal data class AmnesiaTextStyle(
    val textColor: Color,
    val textAlign: TextAlign,
    val typography: TextStyle,
)
