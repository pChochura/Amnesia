package com.pointlessapps.amnesia.compose.ui.theme

import android.os.Parcelable
import com.pointlessapps.amnesia.model.NoteModel
import kotlinx.parcelize.Parcelize

internal sealed interface Route : Parcelable {
    @Parcelize
    object Login : Route

    @Parcelize
    object Home : Route

    @Parcelize
    class Note(val note: NoteModel?) : Route
}
