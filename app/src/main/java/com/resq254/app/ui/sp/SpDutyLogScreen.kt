package com.resq254.app.ui.sp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq254.app.data.AppData
import com.resq254.app.data.CompletedJob
import com.resq254.app.data.getTypeColor
import com.resq254.app.ui.theme.*

@Composable
fun SpDutyLogScreen(jobs: List<CompletedJob>, onBack: () -> Unit) {
    val currentBg = MaterialTheme.colorScheme.background
    val currentSurface = MaterialTheme.colorScheme.surface
    val currentText = MaterialTheme.colorScheme.onBackground

    Column(modifier = Modifier.fillMaxSize().background(currentBg)) {
        Row(
            modifier = Modifier.padding(16.dp).clickable { onBack() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = AccentGreen, modifier = Modifier.size(18.dp))
            Text("Profile", color = AccentGreen, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        Text("Duty Log", color = currentText, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
        Text("${jobs.size} completed responses", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
        Spacer(modifier = Modifier.height(10.dp))

        if (jobs.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                Text("No completed jobs yet", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(jobs) { job ->
                    val typeColor = getTypeColor(job.alertType)
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = currentSurface), shape = RoundedCornerShape(12.dp)) {
                        Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(typeColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = typeColor, modifier = Modifier.size(18.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(job.alertTitle, color = currentText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(job.location, color = TextSecondary, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("Duration ${job.durationSeconds / 60}m ${job.durationSeconds % 60}s", color = TextSecondary, fontSize = 11.sp)
                                    Text(AppData.timeAgo(job.completedAtMs), color = TextSecondary, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}