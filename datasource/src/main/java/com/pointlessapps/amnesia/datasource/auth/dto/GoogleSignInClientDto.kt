package com.pointlessapps.amnesia.datasource.auth.dto

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

data class GoogleSignInClientDto(val signInIntent: Intent) {

    fun launch(intentLauncher: ActivityResultLauncher<Intent>) {
        intentLauncher.launch(signInIntent)
    }
}
