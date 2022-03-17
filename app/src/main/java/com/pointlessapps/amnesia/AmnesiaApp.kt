package com.pointlessapps.amnesia

import android.app.Application
import com.pointlessapps.amnesia.di.applicationModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AmnesiaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@AmnesiaApp)
            modules(applicationModules)
        }
    }
}
