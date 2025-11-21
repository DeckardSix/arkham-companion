package com.poquets.arkham

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class ArkhamApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
                    // Initialize Timber for logging
            Timber.plant(Timber.DebugTree())
        
        // Initialize Firebase (if needed)
        // FirebaseApp.initializeApp(this)
        
        Timber.d("ArkhamApplication initialized")
    }
} 