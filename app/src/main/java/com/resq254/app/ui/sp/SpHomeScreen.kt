package com.resq254.app.ui.sp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq254.app.data.getTypeColor
import com.resq254.app.ui.theme.*
import com.resq254.app.viewmodel.SpState
import java.util.Calendar

@Composable
fun SpHomeScreen(
    state: SpState,
    onToggleOnline: () -> Unit,
    onActiveJobTap: () -> Unit,
    onIncomingTap: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = if (hour < 12) "Good Morning" else if (hour < 17) "Good Afternoon" else "Good Evening"

    val currentBg = MaterialTheme.colorScheme.background
    val currentSurface = MaterialTheme.colorScheme.surface
    val currentText = MaterialTheme.colorScheme.onBackground

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(currentBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(greeting, color = TextSecondary, fontSize = 12.sp)
                Text(state.profile.name, color = currentText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background((if (state.isOnline) SafeGreen else TextMuted).copy(alpha = 0.15f))
                    .clickable { onToggleOnline() }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier.size(7.dp).clip(CircleShape)
                        .background(if (state.isOnline) SafeGreen else TextMuted)
                )
                Text(
                    if (state.isOnline) "Online" else "Offline",
                    color = if (state.isOnline) SafeGreen else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (state.activeJob != null) {
            val job = state.activeJob
            val typeColor = getTypeColor(job.alert.type)
            Text("ACTIVE RESPONSE", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onActiveJobTap() },
                colors = CardDefaults.cardColors(containerColor = currentSurface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(typeColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.LocalHospital, null, tint = typeColor, modifier = Modifier.size(18.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(job.alert.title, color = currentText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            Text(job.alert.location, color = TextSecondary, fontSize = 12.sp)
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = TextSecondary)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        job.status.name.replace("_", " "),
                        color = AccentGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(AccentGreen.copy(alpha = 0.12f)).padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                    if (job.alert.otherResponders.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("+${job.alert.otherResponders.size} other unit(s) on this call", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = currentSurface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CheckCircle, null, tint = SafeGreen, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No active response", color = currentText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text("You'll be notified when a call is assigned", color = TextSecondary, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(label = "Completed", value = state.profile.completedJobs.toString(), modifier = Modifier.weight(1f), surface = currentSurface, text = currentText)
            StatCard(label = "Rating", value = "★ ${state.profile.rating}", modifier = Modifier.weight(1f), surface = currentSurface, text = currentText)
            StatCard(label = "Pending", value = state.incomingAlerts.size.toString(), modifier = Modifier.weight(1f), surface = currentSurface, text = currentText)
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (state.isOnline && state.incomingAlerts.isNotEmpty()) {
            Text("INCOMING", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            state.incomingAlerts.take(3).forEach { alert ->
                val typeColor = getTypeColor(alert.type)
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { onIncomingTap(alert.id) },
                    colors = CardDefaults.cardColors(containerColor = currentSurface),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(typeColor))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(alert.title, color = currentText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text(alert.location, color = TextSecondary, fontSize = 11.sp)
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier, surface: Color, text: Color) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = surface), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(vertical = 14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(label, color = TextSecondary, fontSize = 11.sp)
        }
    }
}