package com.pointlessapps.amnesia.datasource.notes.dto

import androidx.annotation.ColorInt
import androidx.annotation.Keep

@Keep
data class CategoryDto(
    val id: Long,
    val name: String,
    @ColorInt val color: Int,
)
