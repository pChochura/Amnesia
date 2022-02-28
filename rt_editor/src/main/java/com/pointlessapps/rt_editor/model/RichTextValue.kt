package com.pointlessapps.rt_editor.model

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.em
import com.pointlessapps.rt_editor.model.Style.*
import com.pointlessapps.rt_editor.utils.coerceEndOfParagraph
import com.pointlessapps.rt_editor.utils.coerceStartOfParagraph
import com.pointlessapps.rt_editor.utils.copy

internal typealias StyleRange<T> = AnnotatedString.Range<T>
internal typealias SpanRange = AnnotatedString.Range<SpanStyle>
internal typealias ParagraphRange = AnnotatedString.Range<ParagraphStyle>
internal typealias StyleList<T> = List<StyleRange<T>>
typealias SpanStyleList = List<SpanRange>
typealias ParagraphStyleList = List<ParagraphRange>

abstract class RichTextValue {

	abstract val currentStyles: Set<Style>
	abstract val value: TextFieldValue

	abstract fun insertStyle(style: Style): RichTextValue
	abstract fun clearStyle(style: Style): RichTextValue

	internal abstract fun updateValueAndStyles(value: TextFieldValue)
	internal abstract fun prepared(): TextFieldValue

	override fun toString() = value.toString()

	companion object {
		fun get(): RichTextValue = RichTextValueImpl(TextFieldValue())
	}
}

@OptIn(ExperimentalUnitApi::class)
internal class RichTextValueImpl(override var value: TextFieldValue) : RichTextValue() {

	private val currentSelection: TextRange
		get() = value.composition ?: value.selection

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

	private fun <T> filterCurrentStyles(styles: StyleList<T>) = styles.filter {
		currentSelection.intersects(TextRange(it.start, it.end))
	}

	override val currentStyles: Set<Style>
		get() = filterCurrentStyles(value.annotatedString.spanStyles)
			.map { Style.fromTag(it.tag) }.toSet()

	private fun getCurrentSpanStyles(style: Style?) =
		filterCurrentStyles(value.annotatedString.spanStyles)
			.filter { style == null || it.tag == style.tag() }

	private fun getCurrentParagraphStyles(style: Style?) =
		filterCurrentStyles(value.annotatedString.paragraphStyles)
			.filter { style == null || it.tag == style.tag() }

	private fun <T> removeStyleFromSelection(
		styles: StyleList<T>,
		onUpdatedCallback: (stylesFromSelection: StyleList<T>, updatedStyles: StyleList<T>) -> Unit
	): Boolean {
		if (styles.isEmpty()) {
			return false
		}

		var updated = false
		val start = currentSelection.start
		val end = currentSelection.end
		val updatedStyles = mutableListOf<StyleRange<T>>()
		styles.forEach {
			if (it.start <= start && it.end >= end) {
				// Split into two styles
				updated = true

				val styleBeforeSelection = it.copy(end = start)
				val styleAfterSelection = it.copy(start = end)
				if (styleBeforeSelection.start < styleBeforeSelection.end) {
					updatedStyles.add(styleBeforeSelection)
				}
				if (styleAfterSelection.start < styleAfterSelection.end) {
					updatedStyles.add(styleAfterSelection)
				}

				return@forEach
			}

			if (it.start >= start && it.end <= end) {
				// Remove this style completely
				updated = true

				return@forEach
			}

			if (it.start >= start) {
				// Move style before the selection
				updatedStyles.add(it.copy(start = end))
				updated = true

				return@forEach
			}

			if (it.end <= end) {
				// Move style after the selection
				updatedStyles.add(it.copy(end = start))
				updated = true

				return@forEach
			}
		}

		// Remove the styles that intersects with the selection and add the updated ones
		onUpdatedCallback(styles, updatedStyles)

		return updated
	}

	private fun removeStyles(
		spanStyles: SpanStyleList,
		paragraphStyles: ParagraphStyleList
	): Boolean {
		val removedSpanStyle = removeStyleFromSelection(styles = spanStyles,
			onUpdatedCallback = { stylesFromSelection, updatedStyles ->
				set(
					annotatedString = value.annotatedString.copy(
						spanStyles = value.annotatedString.spanStyles -
								stylesFromSelection + updatedStyles,
					)
				)
			}
		)
		val removedParagraphStyle = removeStyleFromSelection(styles = paragraphStyles,
			onUpdatedCallback = { stylesFromSelection, updatedStyles ->
				set(
					annotatedString = value.annotatedString.copy(
						paragraphStyles = value.annotatedString.paragraphStyles -
								stylesFromSelection + updatedStyles,
					)
				)
			}
		)

		return removedSpanStyle || removedParagraphStyle
	}

	private fun <T> collapseStyles(spanStyles: StyleList<T>): StyleList<T> {
		val updatedStyles = mutableListOf<StyleRange<T>>()
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
		val removedStyles = removeStyles(
			getCurrentSpanStyles(style.takeUnless { it == ClearFormat }),
			getCurrentParagraphStyles(style.takeUnless { it == ClearFormat })
		)

		if (removedStyles || (value.composition == null && value.selection.collapsed)) {
			return this
		}

		val spanStyle = when (style) {
			Bold -> SpanStyle(fontWeight = FontWeight.Bold)
			Underline -> SpanStyle(textDecoration = TextDecoration.Underline)
			Italic -> SpanStyle(fontStyle = FontStyle.Italic)
			is TextColor -> SpanStyle(color = style.color)
			is TextSize -> SpanStyle(
				fontSize = TextUnit(
					requireNotNull(style.fraction),
					TextUnitType.Em
				)
			)
			else -> null
		}

		val paragraphStyle = when (style) {
			UnorderedList -> ParagraphStyle(textIndent = TextIndent(restLine = 1.em))
			OrderedList -> TODO()
			else -> null
		}

		val spanStyles = when {
			spanStyle != null -> value.annotatedString.spanStyles + SpanRange(
				item = spanStyle,
				start = currentSelection.start,
				end = currentSelection.end,
				tag = style.tag()
			)
			else -> value.annotatedString.spanStyles
		}

		val paragraphStyles = when {
			paragraphStyle != null -> value.annotatedString.paragraphStyles + ParagraphRange(
				item = paragraphStyle,
				start = currentSelection.start.coerceStartOfParagraph(value.text),
				end = currentSelection.end.coerceEndOfParagraph(value.text),
				tag = style.tag()
			)
			else -> value.annotatedString.paragraphStyles
		}

		println("LOG!, added: $spanStyle, $paragraphStyle")

		set(
			annotatedString = value.annotatedString.copy(
				text = value.text,
				spanStyles = collapseStyles(spanStyles),
				paragraphStyles = collapseStyles(paragraphStyles)
			)
		)

		return this
	}

	override fun clearStyle(style: Style): RichTextValue {
		val spanStylesByType = filterCurrentStyles(value.annotatedString.spanStyles)
			.filter { it.tag.startsWith(style.tag(simple = true)) }
		val paragraphStylesByType = filterCurrentStyles(value.annotatedString.paragraphStyles)
			.filter { it.tag.startsWith(style.tag(simple = true)) }

		removeStyles(spanStylesByType, paragraphStylesByType)

		return this
	}

	private fun updatedStyles(
		previousValue: TextFieldValue,
		currentValue: TextFieldValue
	): SpanStyleList {
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
		var updatedValueText = value.text
		val unorderedListParagraphStyles = this.value.annotatedString.paragraphStyles.filter {
			it.tag == UnorderedList.tag()
		}

		// FIXME: whole section, lol
		val updatedParagraphStyles = mutableListOf<ParagraphRange>()
		if (unorderedListParagraphStyles.isNotEmpty()) {
			unorderedListParagraphStyles.forEach {
				val correspondingText = value.text.substring(it.start, it.end)
				val lines = correspondingText.split(System.lineSeparator())
				val updatedLines = lines.map { line ->
					return@map if (line.startsWith(UnorderedList.BULLET_CHARACTER)) {
						line.substringAfter(UnorderedList.BULLET_CHARACTER)
					} else {
						line
					}
				}

				updatedValueText = value.text.substring(0, it.start) + updatedLines.joinToString(
					System.lineSeparator()
				) + value.text.substring(it.end)

				updatedParagraphStyles.add(
					it.copy(end = it.end - updatedLines.size * UnorderedList.BULLET_CHARACTER.length)
				)
			}
		}

		set(
			annotatedString = this.value.annotatedString.copy(
				text = updatedValueText,
				spanStyles = updatedStyles(
					previousValue = this.value,
					currentValue = value.copy(
						text = updatedValueText
					)
				),
				paragraphStyles = value.annotatedString.paragraphStyles -
						unorderedListParagraphStyles + updatedParagraphStyles
			),
			selection = value.selection,
			composition = value.composition
		)
	}

	override fun prepared(): TextFieldValue {
		val unorderedListParagraphStyles = value.annotatedString.paragraphStyles.filter {
			it.tag == UnorderedList.tag()
		}

		if (unorderedListParagraphStyles.isEmpty()) {
			return value
		}

		// FIXME: whole section, lol
		val updatedParagraphStyles = mutableListOf<ParagraphRange>()
		var updatedValueText = value.text
		unorderedListParagraphStyles.forEach {
			val correspondingText = value.text.substring(it.start, it.end + 1)
			val lines = correspondingText.split(System.lineSeparator())
			val updatedLines = lines.map { line ->
				"${UnorderedList.BULLET_CHARACTER}$line"
			}

			updatedValueText = value.text.substring(0, it.start) + updatedLines.joinToString(
				System.lineSeparator()
			) + value.text.substring(it.end + 1)

			updatedParagraphStyles.add(
				it.copy(end = it.end + updatedLines.size * UnorderedList.BULLET_CHARACTER.length)
			)
		}

		set(
			annotatedString = value.annotatedString.copy(
				text = updatedValueText,
				paragraphStyles = value.annotatedString.paragraphStyles -
						unorderedListParagraphStyles + updatedParagraphStyles
			)
		)

		return value
	}
}
