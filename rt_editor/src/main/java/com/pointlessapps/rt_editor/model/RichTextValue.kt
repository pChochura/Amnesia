package com.pointlessapps.rt_editor.model

import androidx.compose.ui.text.ParagraphStyle
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
import com.pointlessapps.rt_editor.utils.*

internal typealias StyleRange<T> = AnnotatedStringBuilder.MutableRange<T>
internal typealias SpanRange = AnnotatedStringBuilder.MutableRange<SpanStyle>
internal typealias ParagraphRange = AnnotatedStringBuilder.MutableRange<ParagraphStyle>
internal typealias StyleList<T> = List<StyleRange<T>>

abstract class RichTextValue {

	abstract val currentStyles: Set<Style>
	internal abstract val value: TextFieldValue

	abstract fun insertStyle(style: Style): RichTextValue
	abstract fun clearStyles(vararg styles: Style): RichTextValue

	internal abstract fun updatedValueAndStyles(value: TextFieldValue): Boolean

	companion object {
		fun get(): RichTextValue = RichTextValueImpl()
	}
}

@OptIn(ExperimentalUnitApi::class)
internal class RichTextValueImpl : RichTextValue() {

	private val annotatedStringBuilder = AnnotatedStringBuilder()
	private var selection: TextRange = TextRange.Zero
	private var composition: TextRange? = null

	override val value: TextFieldValue
		get() = TextFieldValue(
			annotatedString = annotatedStringBuilder.toAnnotatedString(),
			selection = selection,
			composition = composition
		)

	private val currentSelection: TextRange
		get() = (composition ?: selection).coerceNotReversed()

	private fun <T> filterCurrentStyles(styles: StyleList<T>) = styles.filter {
		currentSelection.intersects(TextRange(it.start, it.end))
	}

	override val currentStyles: Set<Style>
		get() = filterCurrentStyles(annotatedStringBuilder.spanStyles)
			.map { Style.fromTag(it.tag) }.toSet() +
				filterCurrentStyles(annotatedStringBuilder.paragraphStyles)
					.map { Style.fromTag(it.tag) }.toSet()

	private fun getCurrentSpanStyles(style: Style?) =
		filterCurrentStyles(annotatedStringBuilder.spanStyles)
			.filter { style == null || it.tag == style.tag() }

	private fun getCurrentParagraphStyles(style: Style?) =
		filterCurrentStyles(annotatedStringBuilder.paragraphStyles)
			.filter { style == null || it.tag == style.tag() }

	private fun <T> removeStyleFromSelection(
		styles: StyleList<T>,
		selection: TextRange = currentSelection,
	): Pair<StyleList<T>, StyleList<T>> {
		if (styles.isEmpty()) {
			return Pair(emptyList(), emptyList())
		}

		val stylesToAdd = mutableListOf<StyleRange<T>>()
		val stylesToRemove = mutableListOf<StyleRange<T>>()
		val start = selection.start
		val end = selection.end
		styles.forEach {
			if (it.start <= start && it.end >= end) {
				// Split into two styles
				stylesToRemove.add(it)

				val styleBeforeSelection = it.copy(end = start)
				val styleAfterSelection = it.copy(start = end)
				if (styleBeforeSelection.start < styleBeforeSelection.end) {
					stylesToAdd.add(it.copy(end = start))
				}
				if (styleAfterSelection.start < styleAfterSelection.end) {
					stylesToAdd.add(it.copy(start = end))
				}

				return@forEach
			}

			if (it.start >= start && it.end <= end) {
				// Remove this style completely
				stylesToRemove.add(it)

				return@forEach
			}

			if (it.start >= start) {
				// Move style before the selection
				stylesToRemove.add(it)
				stylesToAdd.add(it.copy(start = end))

				return@forEach
			}

			if (it.end <= end) {
				// Move style after the selection
				stylesToRemove.add(it)
				stylesToAdd.add(it.copy(end = start))

				return@forEach
			}
		}

		return stylesToAdd to stylesToRemove
	}

	override fun insertStyle(style: Style): RichTextValue {
		val (spansToAdd, spansToRemove) = removeStyleFromSelection(
			getCurrentSpanStyles(style.takeUnless { it == ClearFormat })
		)
		val (paragraphsToAdd, paragraphsToRemove) = removeStyleFromSelection(
			getCurrentParagraphStyles(style.takeUnless { it == ClearFormat })
		)

		annotatedStringBuilder.addSpans(*spansToAdd.toTypedArray())
		annotatedStringBuilder.removeSpans(*spansToRemove.toTypedArray())
		annotatedStringBuilder.addParagraphs(*paragraphsToAdd.toTypedArray())
		annotatedStringBuilder.removeParagraphs(*paragraphsToRemove.toTypedArray())

		val changedStyles = spansToAdd.isNotEmpty() || spansToRemove.isNotEmpty() ||
				paragraphsToAdd.isNotEmpty() || paragraphsToRemove.isNotEmpty()
		if (changedStyles || (composition == null && selection.collapsed)) {
			return this
		}

		val spanStyle = when (style) {
			Bold -> SpanStyle(fontWeight = FontWeight.Bold)
			Underline -> SpanStyle(textDecoration = TextDecoration.Underline)
			Italic -> SpanStyle(fontStyle = FontStyle.Italic)
			Strikethrough -> SpanStyle(textDecoration = TextDecoration.LineThrough)
			is TextColor -> SpanStyle(color = requireNotNull(style.color))
			is TextSize -> SpanStyle(
				fontSize = TextUnit(
					requireNotNull(style.fraction),
					TextUnitType.Em
				)
			)
			else -> null
		}?.let {
			SpanRange(
				item = it,
				start = currentSelection.start,
				end = currentSelection.end,
				tag = style.tag()
			)
		}

		val paragraphStyle = when (style) {
			AlignLeft -> ParagraphStyle() // TODO()
			AlignCenter -> TODO()
			AlignRight -> TODO()
			UnorderedList -> TODO()
			OrderedList -> TODO()
			else -> null
		}?.let {
			ParagraphRange(
				item = it,
				start = currentSelection.start.coerceStartOfParagraph(annotatedStringBuilder.text),
				end = currentSelection.end.coerceEndOfParagraph(annotatedStringBuilder.text),
				tag = style.tag()
			)
		}

		spanStyle?.let { annotatedStringBuilder.addSpans(it) }
		paragraphStyle?.let { annotatedStringBuilder.addParagraphs(it) }

		return this
	}

	override fun clearStyles(vararg styles: Style): RichTextValue {
		val tags = styles.map { it.tag(simple = true) }.toSet()
		val spanStylesByType = filterCurrentStyles(annotatedStringBuilder.spanStyles)
			.filter { it.tag.startsWith(tags) }
		val paragraphStylesByType = filterCurrentStyles(annotatedStringBuilder.paragraphStyles)
			.filter { it.tag.startsWith(tags) }

		annotatedStringBuilder.removeSpans(*spanStylesByType.toTypedArray())
		annotatedStringBuilder.removeParagraphs(*paragraphStylesByType.toTypedArray())

		return this
	}

	override fun updatedValueAndStyles(value: TextFieldValue): Boolean {
		val updatedStyles = annotatedStringBuilder.updateStyles(
			previousSelection = selection,
			currentValue = value.text
		)

		if (updatedStyles || annotatedStringBuilder.text != value.text ||
			selection != value.selection || composition != value.composition
		) {
			annotatedStringBuilder.text = value.text
			selection = value.selection
			composition = value.composition

			return true
		}

		return false
	}
}
