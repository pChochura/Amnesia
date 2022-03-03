package com.pointlessapps.rt_editor.model

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.ExperimentalUnitApi
import com.pointlessapps.rt_editor.mappers.StyleMapper
import com.pointlessapps.rt_editor.model.Style.ClearFormat
import com.pointlessapps.rt_editor.utils.*

internal typealias StyleRange<T> = AnnotatedStringBuilder.MutableRange<T>

abstract class RichTextValue {

	abstract val isUndoAvailable: Boolean
	abstract val isRedoAvailable: Boolean
	abstract val currentStyles: Set<Style>
	internal abstract val value: TextFieldValue

	abstract fun insertStyle(style: Style): RichTextValue
	abstract fun clearStyles(vararg styles: Style): RichTextValue

	abstract fun undo(): RichTextValue
	abstract fun redo(): RichTextValue

	internal abstract fun updatedValueAndStyles(value: TextFieldValue): Boolean

	companion object {
		// Indicates minimum length difference to add a new snapshot to the history stack
		internal const val MIN_LENGTH_DIFFERENCE = 10

		fun get(styleMapper: StyleMapper = StyleMapper()): RichTextValue =
			RichTextValueImpl(styleMapper)
	}
}

@OptIn(ExperimentalUnitApi::class)
internal class RichTextValueImpl(private val styleMapper: StyleMapper) : RichTextValue() {

	private val annotatedStringBuilder = AnnotatedStringBuilder()
	private var selection: TextRange = TextRange.Zero
	private var composition: TextRange? = null

	private var historyOffset: Int = 0
	private val historySnapshots = mutableListOf(
		RichTextValueSnapshot.fromAnnotatedStringBuilder(
			annotatedStringBuilder,
			selectionPosition = selection.start
		)
	)
	private val currentSnapshot: RichTextValueSnapshot?
		get() = historySnapshots.elementAtOrNull(
			historySnapshots.lastIndex - historyOffset
		)

	override val isUndoAvailable: Boolean
		get() = historySnapshots.isNotEmpty() && historyOffset < historySnapshots.lastIndex

	override val isRedoAvailable: Boolean
		get() = historyOffset > 0

	override val value: TextFieldValue
		get() = TextFieldValue(
			annotatedString = annotatedStringBuilder.toAnnotatedString(),
			selection = selection,
			composition = composition
		)

	private val currentSelection: TextRange
		get() = (composition ?: selection).coerceNotReversed()

	override val currentStyles: Set<Style>
		get() = filterCurrentStyles(annotatedStringBuilder.spanStyles)
			.map { styleMapper.fromTag(it.tag) }.toSet() +
				filterCurrentStyles(annotatedStringBuilder.paragraphStyles)
					.map { styleMapper.fromTag(it.tag) }.toSet()

	private fun clearRedoStack() {
		// If offset in the history is not 0 clear possible "redo" states
		repeat(historyOffset) {
			historySnapshots.removeLastOrNull()
		}
		historyOffset = 0
	}

	private fun updateHistoryIfNecessary() {
		currentSnapshot?.run {
			// Add a snapshot when the style is added, but not enough text was changed to be saved
			if (text != annotatedStringBuilder.text) {
				updateHistory()
			}
		}
	}

	private fun updateHistory() {
		clearRedoStack()

		historySnapshots.add(
			RichTextValueSnapshot.fromAnnotatedStringBuilder(
				annotatedStringBuilder = annotatedStringBuilder,
				selectionPosition = selection.start
			)
		)
	}

	private fun restoreFromHistory() {
		currentSnapshot?.run {
			annotatedStringBuilder.update(toAnnotatedStringBuilder(styleMapper))
			selection = TextRange(selectionPosition)
			composition = null
		}
	}

	private fun <T> filterCurrentStyles(styles: List<StyleRange<T>>) = styles.filter {
		!currentSelection.collapsed && currentSelection.intersects(TextRange(it.start, it.end))
	}

	private fun getCurrentSpanStyles(style: Style?) =
		filterCurrentStyles(annotatedStringBuilder.spanStyles)
			.filter { style == null || it.tag == style.tag() }

	private fun getCurrentParagraphStyles(style: Style?) =
		filterCurrentStyles(annotatedStringBuilder.paragraphStyles)
			.filter { style == null || it.tag == style.tag() }

	private fun <T> removeStyleFromSelection(
		styles: List<StyleRange<T>>,
		selection: TextRange = currentSelection,
	): Pair<List<StyleRange<T>>, List<StyleRange<T>>> {
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

		val changedStyles = spansToAdd.isNotEmpty() || spansToRemove.isNotEmpty() ||
				paragraphsToAdd.isNotEmpty() || paragraphsToRemove.isNotEmpty()

		if (changedStyles) {
			updateHistory()

			annotatedStringBuilder.addSpans(*spansToAdd.toTypedArray())
			annotatedStringBuilder.removeSpans(*spansToRemove.toTypedArray())
			annotatedStringBuilder.addParagraphs(*paragraphsToAdd.toTypedArray())
			annotatedStringBuilder.removeParagraphs(*paragraphsToRemove.toTypedArray())

			updateHistory()

			return this
		} else if (style == ClearFormat || (composition == null && selection.collapsed)) {
			return this
		}

		updateHistoryIfNecessary()

		val spanStyle = styleMapper.toSpanStyle(style)?.let {
			StyleRange(
				item = it,
				start = currentSelection.start,
				end = currentSelection.end,
				tag = style.tag()
			)
		}

		val paragraphStyle = styleMapper.toParagraphStyle(style)?.let {
			StyleRange(
				item = it,
				start = currentSelection.start.coerceStartOfParagraph(annotatedStringBuilder.text),
				end = currentSelection.end.coerceEndOfParagraph(annotatedStringBuilder.text),
				tag = style.tag()
			)
		}

		spanStyle?.let { annotatedStringBuilder.addSpans(it) }
		paragraphStyle?.let { annotatedStringBuilder.addParagraphs(it) }

		updateHistory()

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

	override fun undo(): RichTextValue {
		updateHistoryIfNecessary()
		historyOffset += 1
		restoreFromHistory()

		return this
	}

	override fun redo(): RichTextValue {
		historyOffset -= 1
		restoreFromHistory()

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

			currentSnapshot?.run {
				if (value.text.length - text.length >= MIN_LENGTH_DIFFERENCE) {
					updateHistory()
				} else if (value.text != text) {
					clearRedoStack()
				}
			}

			return true
		}

		return false
	}
}
