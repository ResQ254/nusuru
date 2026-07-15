package com.resq254.app.ui.sp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq254.app.data.ActiveJob
import com.resq254.app.data.SpJobStatus
import com.resq254.app.data.getTypeColor
import com.resq254.app.ui.theme.*
import com.resq254.app.util.openMapsNavigation

@Composable
fun SpActiveResponseScreen(
    job: ActiveJob,
    elapsedSeconds: Int,
    formatTime: (Int) -> String,
    onAdvance: () -> Unit,
    onComplete: () -> Unit,
    onCall: (String, String) -> Unit,
    onChatTap: () -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit
) {
    val currentBg = MaterialTheme.colorScheme.background
    val currentSurface = MaterialTheme.colorScheme.surface
    val currentText = MaterialTheme.colorScheme.onBackground
    val typeColor = getTypeColor(job.alert.type)
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().background(currentBg).padding(16.dp)) {
        Row(modifier = Modifier.clickable { onBack() }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = AccentGreen, modifier = Modifier.size(18.dp))
            Text("Home", color = AccentGreen, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = currentSurface), shape = RoundedCornerShape(14.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(typeColor.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.LocalHospital, null, tint = typeColor, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Text(job.alert.title, color = currentText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(job.alert.location, color = TextSecondary, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(formatTime(elapsedSeconds), color = currentText, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text("Time on call", color = TextSecondary, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text("STATUS", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(10.dp))

        StatusStep("En Route", job.status.ordinal >= SpJobStatus.EN_ROUTE.ordinal, job.status == SpJobStatus.EN_ROUTE, currentText)
        StatusStep("On Scene", job.status.ordinal >= SpJobStatus.ON_SCENE.ordinal, job.status == SpJobStatus.ON_SCENE, currentText)
        StatusStep("Resolved", job.status.ordinal >= SpJobStatus.RESOLVED.ordinal, job.status == SpJobStatus.RESOLVED, currentText)

        Spacer(modifier = Modifier.weight(1f))

        if (job.status != SpJobStatus.RESOLVED) {
            Button(
                onClick = onAdvance,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen, contentColor = MaterialTheme.colorScheme.onPrimary)
            ) {
                Text(
                    if (job.status == SpJobStatus.EN_ROUTE) "Mark Arrived On Scene" else "Mark Resolved",
                    fontWeight = FontWeight.SemiBold, fontSize = 14.sp
                )
            }
        } else {
            Button(
                onClick = onComplete,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SafeGreen, contentColor = Color.White)
            ) {
                Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Complete & Close Job", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = { openMapsNavigation(context, job.alert.latitude, job.alert.longitude, job.alert.location) },
            modifier = Modifier.fillMaxWidth().height(46.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = currentText)
        ) {
            Icon(Icons.Default.Directions, null, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(6.dp))
            Text("Navigate", fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onChatTap,
            modifier = Modifier.fillMaxWidth().height(46.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentGreen)
        ) {
            Icon(Icons.AutoMirrored.Filled.Chat, null, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(6.dp))
            Text("Chat with Reporter", fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = { onCall("Dispatch", "999") },
                modifier = Modifier.weight(1f).height(46.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = currentText)
            ) {
                Icon(Icons.Default.Call, null, modifier = Modifier.size(14.dp)); Spacer(Modifier.width(6.dp)); Text("Call Dispatch", fontSize = 13.sp)
            }
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f).height(46.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RedSOS)
            ) {
                Text("Cancel Response", fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun StatusStep(label: String, reached: Boolean, current: Boolean, textColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier.size(20.dp).clip(CircleShape).background(if (reached) AccentGreen else BorderColor),
            contentAlignment = Alignment.Center
        ) {
            if (reached) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(12.dp))
        }
        Text(label, color = if (current) AccentGreen else textColor, fontSize = 14.sp, fontWeight = if (current) FontWeight.SemiBold else FontWeight.Normal)
    }
}