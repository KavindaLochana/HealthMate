package com.example.healthmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.healthmate.core.di.appViewModelFactory
import com.example.healthmate.feature.app.AppViewModel
import com.example.healthmate.feature.app.HealthMateApp


class MainActivity : ComponentActivity() {
    private val container by lazy { (application as HealthMateApplication).container }
    private val viewModelFactory by lazy { appViewModelFactory(container) }
    private val appViewModel: AppViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthMateApp(appViewModel = appViewModel, viewModelFactory = viewModelFactory)
        }
    }

    override fun onStop() {
        super.onStop()
        // Rotation also stops the activity; only re-lock when the app really leaves the foreground.
        if (!isChangingConfigurations) appViewModel.onAppBackgrounded()
    }
}