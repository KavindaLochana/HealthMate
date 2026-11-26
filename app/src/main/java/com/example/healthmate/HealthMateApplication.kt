package com.example.healthmate

import android.app.Application
import com.example.healthmate.core.di.AppContainer

class HealthMateApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
