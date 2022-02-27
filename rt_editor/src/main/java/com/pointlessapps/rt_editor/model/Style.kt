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
		override fun tag() = "${javaClass.simpleName}/${color.value}"

		companion object {
			fun fromTag(tag: String): TextColor {
				val value = tag.substringAfter("${TextColor::class.simpleName}/")
				return TextColor(Color(value.toLongOrNull() ?: 0))
			}
		}
	}

	class TextSize(@FloatRange(from = 1.0, to = 2.0) val fraction: Float) : Style {
		override fun tag() = "${javaClass.simpleName}/${fraction}"

		companion object {
			fun fromTag(tag: String): TextSize {
				val value = tag.substringAfter("${TextColor::class.simpleName}/")
				return TextSize(value.toFloatOrNull() ?: 1f)
			}
		}
	}

	fun tag(): String = javaClass.simpleName

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
