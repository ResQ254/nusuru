package com.resq254.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.resq254.app.data.EmergencyRepository
import com.resq254.app.data.LocationProvider
import kotlinx.coroutines.launch

@Composable
fun EmergencyScreen() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    fun report(type: String) {
        scope.launch {
            val location = LocationProvider.currentLocation(context)
            EmergencyRepository.triggerEmergency(type = type, location = location)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(onClick = { report("medical") }) {
            Text("🚨 Medical Emergency")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { report("crime") }) {
            Text("🚓 Report Crime")
        }
    }
}