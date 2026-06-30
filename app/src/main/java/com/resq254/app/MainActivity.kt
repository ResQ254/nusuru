package com.resq254.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import com.resq254.app.data.AuthManager
import com.resq254.app.data.AlertRepository
import com.resq254.app.ui.EmergencyScreen
import com.resq254.app.ui.LoginScreen
import com.resq254.app.ui.theme.NusuruTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            NusuruTheme {

                // ✅ Check if user is logged in
                val uid = AuthManager.getCurrentUserId()

                if (uid == null) {
                    // ❌ No user → show login
                    LoginScreen()
                } else {

                    // ✅ Start listening to alerts
                    LaunchedEffect(Unit) {
                        AlertRepository.listenToAlerts(uid)
                    }

                    // ✅ Show main app
                    EmergencyScreen()
                }
            }
        }
    }
}
