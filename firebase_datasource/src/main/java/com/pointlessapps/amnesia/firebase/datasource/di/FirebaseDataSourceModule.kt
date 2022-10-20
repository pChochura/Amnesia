package com.pointlessapps.amnesia.firebase.datasource.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.koin.dsl.module

internal val firebaseDataSourceModule = module {
    single { FirebaseAuth.getInstance() }
    single { Firebase.firestore }
}
