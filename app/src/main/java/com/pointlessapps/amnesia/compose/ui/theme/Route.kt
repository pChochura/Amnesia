package com.pointlessapps.amnesia.compose.ui.theme

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

internal sealed interface Route : Parcelable {
    @Parcelize
    object Login : Route

    @Parcelize
    object Home : Route

    @Parcelize
    object NewNote : Route
}
