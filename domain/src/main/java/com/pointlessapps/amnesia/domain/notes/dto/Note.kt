package com.pointlessapps.amnesia.domain.notes.dto

import androidx.annotation.Keep

@Keep
data class Note(
    val id: Long,
    val title: String?,
    val content: Content,
    val createdAt: Long,
    val updatedAt: Long,
    val categories: List<Category>,
    val isPinned: Boolean,
)
