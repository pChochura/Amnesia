package com.pointlessapps.amnesia.model

import android.os.Parcelable
import androidx.annotation.ColorInt
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class CategoryModel(
    val id: Long = UUID.randomUUID().mostSignificantBits,
    val name: String,
    @ColorInt val color: ULong,
) : Parcelable
