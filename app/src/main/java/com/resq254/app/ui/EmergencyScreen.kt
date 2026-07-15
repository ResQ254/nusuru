package com.resq254.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EmergencyScreen() {

    val repo = EmergencyRepository()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = {
                repo.triggerEmergency("medical")
            }
        ) {
            Text("🚨 Medical Emergency")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                repo.triggerEmergency("crime")
            }
        ) {
            Text("🚓 Report Crime")
        }
    }
}
