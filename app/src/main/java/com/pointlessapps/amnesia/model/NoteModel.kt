package com.pointlessapps.amnesia.model

import android.os.Parcelable
import com.pointlessapps.rt_editor.utils.RichTextValueSnapshot
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class NoteModel(
    val id: Long = UUID.randomUUID().mostSignificantBits,
    val title: String?,
    val content: RichTextValueSnapshot,
    val createdAt: String,
    val updatedAt: String,
    val categories: Set<CategoryModel>,
    val isPinned: Boolean,
) : Parcelable
