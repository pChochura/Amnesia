package com.pointlessapps.amnesia.datasource.notes.dto

import androidx.annotation.Keep

@Keep
data class ContentDto(
    val text: String? = null,
    val spanStyles: List<SpanDto> = emptyList(),
    val paragraphStyles: List<SpanDto> = emptyList(),
    val selectionPosition: Int = 0,
) {

    @Keep
    data class SpanDto(
        val start: Int = 0,
        val end: Int = 0,
        val tag: String? = null,
    )
}
