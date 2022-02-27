package com.pointlessapps.rt_editor.model

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.pointlessapps.rt_editor.model.Style.*
import com.pointlessapps.rt_editor.utils.copy

abstract class RichTextValue {

	abstract val currentStyles: Set<Style>
	abstract val value: TextFieldValue

	abstract fun insertStyle(style: Style): RichTextValue

	internal abstract fun updateValueAndStyles(value: TextFieldValue)

	override fun toString() = value.toString()

	companion object {
		fun get(): RichTextValue = RichTextValueImpl(
			TextFieldValue(
				text = """
				Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce fermentum velit eget metus dictum, 
				vitae semper purus pharetra. Phasellus vitae mauris quis dolor euismod euismod. Aliquam facilisis, 
				lectus nec aliquam mollis, velit purus lacinia sem, quis maximus dolor tortor id velit. Vestibulum 
				ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Sed congue, neque eu 
				aliquam aliquet, nisi velit malesuada leo, sed viverra velit nisl vitae dui. Donec blandit mauris sit 
				amet sem tempor, non mollis libero interdum. Aliquam eu nibh urna. Praesent quis sapien nec neque 
				pretium pretium. Nunc erat metus, lacinia non ligula sit amet, convallis porta erat. Nullam ultrices 
				ullamcorper neque id egestas. Quisque sit amet tempus quam. Donec sodales, ante nec mattis
			""".trimIndent()
			)
		)
	}
}

@OptIn(ExperimentalUnitApi::class)
internal class RichTextValueImpl(override var value: TextFieldValue) : RichTextValue() {

	private fun set(
		annotatedString: AnnotatedString = value.annotatedString,
		selection: TextRange = value.selection,
		composition: TextRange? = value.composition
	) {
		this.value = value.copy(
			annotatedString = annotatedString,
			selection = selection,
			composition = composition
		)
	}

	override val currentStyles: Set<Style>
		get() = value.annotatedString.spanStyles.filter {
			val styleRange = TextRange(it.start, it.end)
			value.selection.intersects(styleRange)
		}.map { Style.fromTag(it.tag) }.toSet()

	private fun getCurrentRange(style: Style?): List<AnnotatedString.Range<SpanStyle>> {
		return value.annotatedString.spanStyles.filter {
			val styleRange = TextRange(it.start, it.end)
			value.selection.intersects(styleRange)
		}.filter { style == null || it.tag == style.tag() }
	}

	private fun removeStyleFromSelection(style: Style): Boolean {
		if (style != ClearFormat && getCurrentRange(style).isEmpty()) {
			return false
		}

		val start = value.composition?.start ?: value.selection.start
		val end = value.composition?.end ?: value.selection.end
		val updatedStyles = mutableListOf<AnnotatedString.Range<SpanStyle>>()
		getCurrentRange(style.takeUnless { it == ClearFormat }).forEach {
			if (it.start < start && it.end > end) {
				// Split into two styles
				val styleBeforeSelection = it.copy(end = start)
				val styleAfterSelection = it.copy(start = end)
				updatedStyles.add(styleBeforeSelection)
				updatedStyles.add(styleAfterSelection)

				return@forEach
			}

			if (it.start >= start && it.end <= end) {
				// Remove this style completely

				return@forEach
			}

			if (it.start >= start) {
				// Move style before the selection
				updatedStyles.add(it.copy(start = end))

				return@forEach
			}

			if (it.end <= end) {
				// Move style after the selection
				updatedStyles.add(it.copy(end = start))

				return@forEach
			}
		}

		// Remove the styles that intersects with the selection and add the updated ones
		val spanStyles = value.annotatedString.spanStyles.filterNot {
			val styleRange = TextRange(it.start, it.end)
			(value.composition ?: value.selection).intersects(styleRange) &&
					(style == ClearFormat || it.tag == style.tag())
		} + updatedStyles

		set(
			annotatedString = value.annotatedString.copy(
				text = value.text,
				spanStyles = spanStyles,
			)
		)

		return true
	}

	private fun collapseStyles(spanStyles: List<AnnotatedString.Range<SpanStyle>>): List<AnnotatedString.Range<SpanStyle>> {
		val updatedStyles = mutableListOf<AnnotatedString.Range<SpanStyle>>()
		val startRangeMap = mutableMapOf<Int, Int>()
		val endRangeMap = mutableMapOf<Int, Int>()
		val removedIndex = mutableSetOf<Int>()

		spanStyles.forEachIndexed { index, range ->
			startRangeMap[range.start] = index
			endRangeMap[range.end] = index
		}

		spanStyles.forEachIndexed { index, range ->
			if (removedIndex.contains(index)) {
				return@forEachIndexed
			}

			var start = range.start
			var end = range.end
			if (startRangeMap.containsKey(range.end)) {
				val otherRangeIndex = requireNotNull(startRangeMap[range.end])
				if (spanStyles[otherRangeIndex].tag == range.tag) {
					end = spanStyles[otherRangeIndex].end

					// Remove collapsed values
					startRangeMap.remove(range.end)
					endRangeMap.remove(range.end)
					removedIndex.add(otherRangeIndex)
				}
			}

			if (endRangeMap.containsKey(range.start)) {
				val otherRangeIndex = requireNotNull(endRangeMap[range.start])
				if (spanStyles[otherRangeIndex].tag == range.tag) {
					start = spanStyles[otherRangeIndex].start

					// Remove collapsed values
					startRangeMap.remove(range.start)
					endRangeMap.remove(range.start)
					removedIndex.add(otherRangeIndex)
				}
			}

			updatedStyles.add(range.copy(start = start, end = end))
		}

		return updatedStyles
	}

	override fun insertStyle(style: Style): RichTextValue {
		if (removeStyleFromSelection(style)) {
			return this
		}

		if (value.composition == null && value.selection.collapsed) {
			return this
		}

		val spanStyle = when (style) {
			Bold -> SpanStyle(fontWeight = FontWeight.Bold)
			Underline -> SpanStyle(textDecoration = TextDecoration.Underline)
			Italic -> SpanStyle(fontStyle = FontStyle.Italic)
			UnorderedList -> TODO()
			OrderedList -> TODO()
			is TextColor -> SpanStyle(color = style.color)
			is TextSize -> SpanStyle(fontSize = TextUnit(style.fraction, TextUnitType.Em))
			ClearFormat -> null // Nothing to do here
		} ?: return this

		val spanStyles = value.annotatedString.spanStyles + AnnotatedString.Range(
			item = spanStyle,
			start = value.composition?.start ?: value.selection.start,
			end = value.composition?.end ?: value.selection.end,
			tag = style.tag()
		)

		set(
			annotatedString = value.annotatedString.copy(
				text = value.text,
				spanStyles = collapseStyles(spanStyles),
			)
		)

		return this
	}

	private fun updatedStyles(
		previousValue: TextFieldValue,
		currentValue: TextFieldValue
	): List<AnnotatedString.Range<SpanStyle>> {
		val lengthDifference = currentValue.text.length - previousValue.text.length
		if (lengthDifference == 0) {
			// Text was not changed at all; leave styles untouched
			return value.annotatedString.spanStyles
		}

		val prevEnd = previousValue.selection.end
		return value.annotatedString.spanStyles.map {
			val updateStart = it.start > prevEnd
			val updateEnd = it.end > prevEnd

			if (!updateStart && !updateEnd) {
				return@map it
			}

			return@map it.copy(
				start = if (updateStart) it.start + lengthDifference else it.start,
				end = if (updateEnd) it.end + lengthDifference else it.end
			)
		}
	}

	override fun updateValueAndStyles(value: TextFieldValue) {
		set(
			annotatedString = this.value.annotatedString.copy(
				text = value.text,
				spanStyles = updatedStyles(
					previousValue = this.value,
					currentValue = value
				)
			),
			selection = value.selection,
			composition = value.composition
		)
	}
}
