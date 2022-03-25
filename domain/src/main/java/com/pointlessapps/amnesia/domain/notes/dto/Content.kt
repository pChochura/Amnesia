package com.pointlessapps.amnesia.domain.notes.dto

import androidx.annotation.Keep

@Keep
data class Content(
    val text: String,
    val spanStyles: List<Span>,
    val paragraphStyles: List<Span>,
    val selectionPosition: Int,
) {

    @Keep
    data class Span(
        val start: Int,
        val end: Int,
        val tag: String,
    )
}
