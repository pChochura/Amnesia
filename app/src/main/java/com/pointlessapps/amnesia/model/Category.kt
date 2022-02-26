package com.pointlessapps.amnesia.model

import android.os.Parcelable
import androidx.annotation.ColorInt
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Category(
	val text: String,
	@ColorInt val color: Int,
	val id: Long = UUID.randomUUID().mostSignificantBits
) : Parcelable
