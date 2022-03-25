package com.pointlessapps.amnesia.datasource.notes.dto

import androidx.annotation.Keep
import java.util.*

@Keep
data class NoteDto(
    val id: Long = UUID.randomUUID().mostSignificantBits,
    val title: String? = null,
    val content: ContentDto? = null,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
    val categories: List<Long> = emptyList(),
    val pinned: Boolean = false,
)
