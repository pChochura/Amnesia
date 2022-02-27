package com.pointlessapps.rt_editor.model

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color

sealed interface Style {
	object Bold : Style
	object Underline : Style
	object Italic : Style
	object UnorderedList : Style
	object OrderedList : Style
	object ClearFormat : Style
	class TextColor(val color: Color) : Style {
		override fun tag(simple: Boolean) = if (simple) {
			super.tag(simple)
		} else {
			"${javaClass.simpleName}/${color.value}"
		}

		override fun toString() = "${javaClass.simpleName}(${color.value})"

		companion object {
			fun fromTag(tag: String): TextColor {
				val value = tag.substringAfter("${TextColor::class.simpleName}/")
				return TextColor(Color(value.toLongOrNull() ?: 0))
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

	companion object {
		fun fromTag(tag: String): Style {
			val klass = requireNotNull(Style::class.sealedSubclasses.find {
				tag.startsWith(requireNotNull(it.simpleName))
			})

			if (klass.simpleName == tag) {
				return requireNotNull(klass.objectInstance)
			}

			return when {
				tag.startsWith(requireNotNull(TextColor::class.simpleName)) -> TextColor.fromTag(tag)
				tag.startsWith(requireNotNull(TextSize::class.simpleName)) -> TextSize.fromTag(tag)
				else -> throw IllegalArgumentException()
			}
		}
	}
}
