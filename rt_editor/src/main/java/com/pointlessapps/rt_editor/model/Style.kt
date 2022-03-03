package com.pointlessapps.rt_editor.model

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color

interface Style {

	object ClearFormat : Style

	object OrderedList : Style
	object UnorderedList : Style
	object AlignLeft : Style
	object AlignCenter : Style
	object AlignRight : Style

	object Bold : Style
	object Underline : Style
	object Italic : Style
	object Strikethrough : Style

	class TextColor(val color: Color? = null) : Style {
		override fun tag(simple: Boolean) = if (simple) {
			super.tag(simple)
		} else {
			"${javaClass.simpleName}/${color?.value}"
		}

		override fun toString() = "${javaClass.simpleName}(${color?.value})"

		companion object {
			fun fromTag(tag: String): TextColor {
				val value = tag.substringAfter("${TextColor::class.simpleName}/")
				return TextColor(Color(value.toULongOrNull() ?: 0UL))
			}
		}
	}

	class TextSize(@FloatRange(from = MIN_VALUE, to = MAX_VALUE) fraction: Float? = null) : Style {
		@FloatRange(from = MIN_VALUE, to = MAX_VALUE)
		var fraction: Float? = fraction?.coerceIn(
			minimumValue = MIN_VALUE.toFloat(),
			maximumValue = MAX_VALUE.toFloat()
		)

		override fun tag(simple: Boolean) = if (simple) {
			super.tag(simple)
		} else {
			"${javaClass.simpleName}/${fraction}"
		}

		override fun toString() = "${javaClass.simpleName}($fraction)"

		companion object {
			const val DEFAULT_VALUE = 1f
			const val MIN_VALUE = 0.5
			const val MAX_VALUE = 2.0
			const val INCREMENT = 0.1f

			fun fromTag(tag: String): TextSize {
				val value = tag.substringAfter("${TextSize::class.simpleName}/")
				return TextSize(value.toFloatOrNull() ?: 1f)
			}
		}
	}

	fun tag(simple: Boolean = false): String = javaClass.simpleName
}
