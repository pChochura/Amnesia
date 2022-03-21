package com.pointlessapps.amnesia.domain.auth.model

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

data class GoogleSignInClient(private val signInIntent: Intent) {

    fun launch(intentLauncher: ActivityResultLauncher<Intent>) {
        intentLauncher.launch(signInIntent)
    }
}
