package com.example.healthmate

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
//import com.example.healthmate.ui.navigation.HealthMateApp
import com.example.healthmate.core.ui.theme.HealthMateTheme
import com.example.healthmate.feature.dashboard.DashboardScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthMateTheme {
//                HealthMateApp()
                DashboardScreen()
            }
        }
    }
}