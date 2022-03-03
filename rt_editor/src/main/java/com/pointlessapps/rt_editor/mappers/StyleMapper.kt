package com.pointlessapps.rt_editor.mappers

import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.pointlessapps.rt_editor.model.Style

@OptIn(ExperimentalUnitApi::class)
open class StyleMapper {

	open fun fromTag(tag: String): Style {
		val klass = requireNotNull(Style::class.nestedClasses.find {
			tag.startsWith(requireNotNull(it.simpleName))
		})

		if (klass.objectInstance != null) {
			return requireNotNull(klass.objectInstance) as Style
		}

		return when {
			tag.startsWith(requireNotNull(Style.TextColor::class.simpleName)) ->
				Style.TextColor.fromTag(tag)
			tag.startsWith(requireNotNull(Style.TextSize::class.simpleName)) ->
				Style.TextSize.fromTag(tag)
			else -> throw IllegalArgumentException()
		}
	}

	open fun toSpanStyle(style: Style): SpanStyle? = when (style) {
		Style.Bold -> SpanStyle(fontWeight = FontWeight.Bold)
		Style.Underline -> SpanStyle(textDecoration = TextDecoration.Underline)
		Style.Italic -> SpanStyle(fontStyle = FontStyle.Italic)
		Style.Strikethrough -> SpanStyle(textDecoration = TextDecoration.LineThrough)
		is Style.TextColor -> SpanStyle(color = requireNotNull(style.color))
		is Style.TextSize -> SpanStyle(
			fontSize = TextUnit(
				requireNotNull(style.fraction),
				TextUnitType.Em
			)
		)
		else -> null
	}

	open fun toParagraphStyle(style: Style): ParagraphStyle? = when (style) {
		Style.AlignLeft -> TODO()
		Style.AlignCenter -> TODO()
		Style.AlignRight -> TODO()
		Style.UnorderedList -> TODO()
		Style.OrderedList -> TODO()
		else -> null
	}
}
