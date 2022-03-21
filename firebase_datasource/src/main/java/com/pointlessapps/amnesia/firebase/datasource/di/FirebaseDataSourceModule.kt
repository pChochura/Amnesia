package com.pointlessapps.amnesia.firebase.datasource.di

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.koin.dsl.module

internal val firebaseDataSourceModule = module {
    single { Firebase.auth }
    single { Firebase.firestore }
}
