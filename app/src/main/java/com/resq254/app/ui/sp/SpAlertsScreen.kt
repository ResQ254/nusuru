package com.resq254.app.ui.sp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq254.app.data.Alert
import com.resq254.app.data.AppData
import com.resq254.app.data.getTypeColor
import com.resq254.app.ui.theme.*

@Composable
fun SpAlertsScreen(
    alerts: List<Alert>,
    onAlertTap: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentBg = MaterialTheme.colorScheme.background
    val currentSurface = MaterialTheme.colorScheme.surface
    val currentText = MaterialTheme.colorScheme.onBackground

    Column(modifier = modifier.fillMaxSize().background(currentBg)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Incoming Calls", color = currentText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("${alerts.size} awaiting response", color = TextSecondary, fontSize = 12.sp)
        }
        if (alerts.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                Text("No incoming calls right now", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(alerts, key = { it.id }) { alert ->
                    val typeColor = getTypeColor(alert.type)
                    val icon = when (alert.type) {
                        "fire" -> Icons.Default.LocalFireDepartment
                        "medical" -> Icons.Default.Favorite
                        "security" -> Icons.Default.Security
                        else -> Icons.Default.Water
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onAlertTap(alert.id) },
                        colors = CardDefaults.cardColors(containerColor = currentSurface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier.size(42.dp).clip(RoundedCornerShape(10.dp)).background(typeColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, null, tint = typeColor, modifier = Modifier.size(18.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(alert.title, color = currentText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(alert.location, color = TextSecondary, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(AppData.timeAgo(alert.timestampMs), color = TextSecondary, fontSize = 11.sp)
                            }
                            Icon(Icons.Default.ChevronRight, null, tint = TextSecondary)
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}