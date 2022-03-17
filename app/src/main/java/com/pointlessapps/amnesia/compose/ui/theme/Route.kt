package com.pointlessapps.amnesia.compose.ui.theme

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface Route : Parcelable {
    @Parcelize
    object Home : Route

    @Parcelize
    object NewNote : Route
}
