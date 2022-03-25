package com.pointlessapps.amnesia.domain.notes.dto

import androidx.annotation.ColorInt
import androidx.annotation.Keep

@Keep
data class Category(
    val id: Long,
    val name: String,
    @ColorInt val color: Int,
)
