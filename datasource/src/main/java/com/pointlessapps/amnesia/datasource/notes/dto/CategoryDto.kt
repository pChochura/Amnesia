package com.pointlessapps.amnesia.datasource.notes.dto

import androidx.annotation.Keep
import java.util.*

@Keep
data class CategoryDto(
    val id: Long = UUID.randomUUID().mostSignificantBits,
    val name: String? = null,
    val color: String? = null,
)
