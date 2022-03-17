package com.pointlessapps.amnesia.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Note(
    val id: Long = UUID.randomUUID().mostSignificantBits,
    val title: String?,
    val content: String,
    val createdAt: String,
    val updatedAt: String,
    val categories: Set<Category>,
    val isPinned: Boolean,
) : Parcelable
