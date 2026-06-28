package com.resq254.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.resq254.app.ui.EmergencyScreen
import com.resq254.app.ui.theme.NusuruTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NusuruTheme {
                EmergencyScreen()
            }
        }
    }
}