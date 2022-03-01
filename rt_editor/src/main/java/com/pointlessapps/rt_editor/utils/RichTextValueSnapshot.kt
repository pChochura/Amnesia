package com.pointlessapps.rt_editor.utils

import com.pointlessapps.rt_editor.mappers.StyleMapper
import com.pointlessapps.rt_editor.model.Style

internal class RichTextValueSnapshot private constructor(
	val text: String,
	val spanStyles: List<RichTextValueSpanSnapshot>,
	val paragraphStyles: List<RichTextValueSpanSnapshot>
) {

	fun toAnnotatedStringBuilder(): AnnotatedStringBuilder {
		val styleMapper = StyleMapper()

		val spans = this.spanStyles.map {
			val item = styleMapper.toSpanStyle(Style.fromTag(it.tag)) ?: return@map null
			AnnotatedStringBuilder.MutableRange(item, it.start, it.end, it.tag)
		}.filterNotNull()

		val paragraphs = this.paragraphStyles.map {
			val item = styleMapper.toParagraphStyle(Style.fromTag(it.tag)) ?: return@map null
			AnnotatedStringBuilder.MutableRange(item, it.start, it.end, it.tag)
		}.filterNotNull()

		return AnnotatedStringBuilder().apply {
			text = this@RichTextValueSnapshot.text
			addSpans(*spans.toTypedArray())
			addParagraphs(*paragraphs.toTypedArray())
		}
	}

	internal data class RichTextValueSpanSnapshot(
		val start: Int,
		val end: Int,
		val tag: String
	)

	companion object {
		fun fromAnnotatedStringBuilder(
			annotatedStringBuilder: AnnotatedStringBuilder
		): RichTextValueSnapshot {
			return RichTextValueSnapshot(
				text = annotatedStringBuilder.text,
				spanStyles = annotatedStringBuilder.spanStyles.map {
					RichTextValueSpanSnapshot(
						start = it.start,
						end = it.end,
						tag = it.tag
					)
				},
				paragraphStyles = annotatedStringBuilder.paragraphStyles.map {
					RichTextValueSpanSnapshot(
						start = it.start,
						end = it.end,
						tag = it.tag
					)
				}
			)
		}
	}
}
