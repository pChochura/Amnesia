package com.pointlessapps.rt_editor.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle

internal fun AnnotatedString.copy(
	text: String = this.text,
	spanStyles: List<AnnotatedString.Range<SpanStyle>> = this.spanStyles,
	paragraphStyles: List<AnnotatedString.Range<ParagraphStyle>> = this.paragraphStyles
) = AnnotatedString(
	text = text,
	spanStyles = spanStyles,
	paragraphStyles = paragraphStyles
)
