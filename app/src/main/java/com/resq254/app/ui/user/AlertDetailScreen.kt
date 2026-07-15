package com.resq254.app.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Fixed deprecation warning
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
import com.resq254.app.ui.theme.*

// =====================================================================
// PLACEHOLDER STUBS
// Delete these once you copy over your friend's actual data package files!
// =====================================================================
data class Alert(
    val type: String,
    val status: String,
    val title: String,
    val location: String,
    val timestampMs: Long,
    val responders: Int,
    val description: String
)

object AppData {
    fun timeAgo(timestampMs: Long): String = "2 hours ago"
}

fun getTypeColor(type: String): Color = when(type) {
    "fire" -> Color(0xFFE24B4A)
    "medical" -> Color(0xFF4CAF50)
    "security" -> Color(0xFF2196F3)
    else -> Color(0xFF00BCD4)
}

fun getStatusColor(status: String): Color = Color(0xFFFF9800)
// =====================================================================

@Composable
fun AlertDetailScreen(alert: Alert, responding: Boolean, onRespond: () -> Unit, onCall: (String, String) -> Unit, onBack: () -> Unit) {
    val typeColor   = getTypeColor(alert.type)
    val statusColor = getStatusColor(alert.status)
    val icon = when (alert.type) { "fire" -> Icons.Default.LocalFireDepartment; "medical" -> Icons.Default.Favorite; "security" -> Icons.Default.Security; else -> Icons.Default.Water }

    // DYNAMIC LIGHT/DARK ADAPTATION VIA MATERIALTHEME TOKENS
    val currentBg = MaterialTheme.colorScheme.background
    val currentSurface = MaterialTheme.colorScheme.surface
    val currentText = MaterialTheme.colorScheme.onBackground

    Column(modifier = Modifier.fillMaxSize().background(currentBg).verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.padding(16.dp).clickable { onBack() }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = AccentGreen, modifier = Modifier.size(18.dp))
            Text("Alerts", color = AccentGreen, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = currentSurface), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(typeColor.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                            Icon(icon, null, tint = typeColor, modifier = Modifier.size(22.dp))
                        }
                        Column {
                            Text(alert.title, color = currentText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.LocationOn, null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                                Text(alert.location, color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(alert.status.uppercase(), color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(statusColor.copy(alpha = 0.12f)).padding(horizontal = 8.dp, vertical = 3.dp))
                        Text(AppData.timeAgo(alert.timestampMs), color = TextSecondary, fontSize = 10.sp, modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(BorderColor).padding(horizontal = 8.dp, vertical = 3.dp))
                        Text("${alert.responders} responding", color = TextSecondary, fontSize = 10.sp, modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(BorderColor).padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = currentSurface), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("DETAILS", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(alert.description.ifEmpty { "No additional details." }, color = currentText, fontSize = 13.sp, lineHeight = 19.sp)
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Button(onClick = onRespond, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (responding) SafeGreen.copy(alpha = 0.2f) else SafeGreen, contentColor = if (responding) SafeGreen else MaterialTheme.colorScheme.onPrimary)) {
                Icon(if (responding) Icons.Default.Check else Icons.Default.Navigation, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (responding) "You are responding" else "I am responding", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = { onCall("Emergency", "999") }, modifier = Modifier.weight(1f).height(46.dp), shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = currentText)) {
                    Icon(Icons.Default.Call, null, modifier = Modifier.size(14.dp)); Spacer(Modifier.width(6.dp)); Text("Call 999", fontSize = 13.sp)
                }
                OutlinedButton(onClick = {}, modifier = Modifier.weight(1f).height(46.dp), shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = currentText)) {
                    Icon(Icons.Default.Share, null, modifier = Modifier.size(14.dp)); Spacer(Modifier.width(6.dp)); Text("Share", fontSize = 13.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}