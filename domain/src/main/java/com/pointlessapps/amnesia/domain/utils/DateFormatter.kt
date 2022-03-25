package com.pointlessapps.amnesia.domain.utils

import java.text.SimpleDateFormat
import java.util.*

class DateFormatter(dateFormat: String) {

    private val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())

    fun toString(date: Long): String = simpleDateFormat.format(Date(date))

    fun fromString(date: String, defaultValue: Long = System.currentTimeMillis()): Long =
        runCatching { requireNotNull(simpleDateFormat.parse(date)?.time) }
            .getOrDefault(defaultValue)
}
