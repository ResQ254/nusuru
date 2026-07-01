package com.resq254.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq254.app.data.Alert
import com.resq254.app.data.StaticData
import com.resq254.app.ui.theme.*

@Composable
fun AlertDetailScreen(alert: Alert, isResponding: Boolean, onToggleResponding: () -> Unit, onCall: (String, String) -> Unit, onBack: () -> Unit) {
    val color = StaticData.typeColor(alert.type)
    val statusColor = StaticData.statusColor(alert.status)
    val icon = when (alert.type) { "fire" -> Icons.Default.LocalFireDepartment; "medical" -> Icons.Default.Favorite; "security" -> Icons.Default.Security; else -> Icons.Default.Water }

    Column(modifier = Modifier.fillMaxSize().background(AppSurface).verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp).clickable { onBack() }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Default.ArrowBack, "Back", tint = AccentGreen, modifier = Modifier.size(18.dp))
            Text("Alerts", color = AccentGreen, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        Column(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 28.dp)) {
            // Header card
            Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(color.copy(0.05f)).border(1.dp, color.copy(0.16f), RoundedCornerShape(16.dp)).padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(13.dp)).background(color.copy(0.10f)).border(1.dp, color.copy(0.19f), RoundedCornerShape(13.dp)), contentAlignment = Alignment.Center) {
                        Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Text(alert.title, color = AppWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.2).sp)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 3.dp)) {
                            Icon(Icons.Default.LocationOn, null, tint = TextSub, modifier = Modifier.size(11.dp))
                            Text(alert.location, color = TextSub, fontSize = 12.sp)
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(alert.status.uppercase() to statusColor, StaticData.timeLabel(alert.timestampMs) to TextSub, "${alert.responders} responding" to TextSub).forEach { (t, c) ->
                        Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(c.copy(0.10f)).border(1.dp, c.copy(0.19f), RoundedCornerShape(20.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                            Text(t, color = c, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.4.sp)
                        }
                    }
                }
            }
            Spacer(Modifier.height(14.dp))

            // Map
            val ringProgress by rememberInfiniteTransition(label = "map").animateFloat(0f, 1f, infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart), label = "rp")
            Box(modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFF0A0D0C)).border(1.dp, AppBorder, RoundedCornerShape(14.dp))) {
                Canvas(Modifier.fillMaxSize()) {
                    val w = size.width; val h = size.height; val step = 22.dp.toPx()
                    var x = 0f; while (x < w) { drawLine(Color.White.copy(0.022f), Offset(x, 0f), Offset(x, h)); x += step }
                    var y = 0f; while (y < h) { drawLine(Color.White.copy(0.022f), Offset(0f, y), Offset(w, y)); y += step }
                    drawRect(Color.White.copy(0.035f), topLeft = Offset(0f, h * 0.44f), size = androidx.compose.ui.geometry.Size(w, 7.dp.toPx()))
                    drawRect(Color.White.copy(0.035f), topLeft = Offset(w * 0.37f, 0f), size = androidx.compose.ui.geometry.Size(6.dp.toPx(), h))
                    val inc = Offset(w * 0.37f, h * 0.44f)
                    for (i in 0..1) { val ph = (ringProgress + i / 2f) % 1f; drawCircle(color.copy(1f - ph), 8.dp.toPx() + ph * 18.dp.toPx(), inc, style = Stroke(1.5.dp.toPx())) }
                    drawCircle(color, 6.5.dp.toPx(), inc)
                    drawCircle(AccentGreen, 5.dp.toPx(), Offset(w * 0.63f, h * 0.60f))
                }
                Text("INCIDENT", color = TextSub, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.6.sp, modifier = Modifier.align(Alignment.TopStart).padding(12.dp))
                Text("MY LOCATION", color = TextSub, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.6.sp, modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp))
            }
            Spacer(Modifier.height(14.dp))

            // Description
            Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(AppCard).border(1.dp, AppBorder, RoundedCornerShape(14.dp)).padding(16.dp)) {
                Text("INCIDENT DETAILS", color = TextSub, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                Spacer(Modifier.height(8.dp))
                Text(alert.description, color = TextPrimary, fontSize = 13.sp, lineHeight = 19.sp)
            }
            Spacer(Modifier.height(14.dp))

            // Actions
            Button(onClick = onToggleResponding, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isResponding) SafeGreen.copy(0.12f) else SafeGreen, contentColor = if (isResponding) SafeGreen else Color.Black)) {
                Icon(if (isResponding) Icons.Default.Check else Icons.Default.Navigation, null, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(8.dp))
                Text(if (isResponding) "Marked as responding" else "I'm responding", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = { onCall("Emergency", "999") }, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppBorder), colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)) {
                    Icon(Icons.Default.Call, null, modifier = Modifier.size(15.dp)); Spacer(Modifier.width(7.dp)); Text("Call 999", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                OutlinedButton(onClick = {}, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppBorder), colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)) {
                    Icon(Icons.Default.Share, null, modifier = Modifier.size(15.dp)); Spacer(Modifier.width(7.dp)); Text("Share", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
