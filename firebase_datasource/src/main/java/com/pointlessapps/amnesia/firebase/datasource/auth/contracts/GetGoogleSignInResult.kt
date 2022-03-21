package com.pointlessapps.amnesia.firebase.datasource.auth.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInResult

class GetGoogleSignInResult : ActivityResultContract<Intent, GoogleSignInResult?>() {

    override fun createIntent(context: Context, input: Intent) = input

    override fun parseResult(resultCode: Int, intent: Intent?): GoogleSignInResult? {
        if (resultCode != Activity.RESULT_OK || intent == null) {
            return null
        }

        return Auth.GoogleSignInApi.getSignInResultFromIntent(intent)
    }
}
