package com.resq254.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq254.app.ui.theme.*
import com.resq254.app.viewmodel.UiState
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

private data class Dot(val id: Long, val x: Float, val y: Float, val born: Long = System.currentTimeMillis())

@Composable
fun BroadcastScreen(state: UiState, formatTime: (Int) -> String, onCancel: () -> Unit) {
    var dots by remember { mutableStateOf<List<Dot>>(emptyList()) }
    var tick by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) { while (true) { delay(650); val a = Random.nextFloat() * 2f * PI.toFloat(); val r = 30f + Random.nextFloat() * 70f; dots = (dots + Dot(System.nanoTime(), 0.5f + cos(a) * r * 0.003f, 0.5f + sin(a) * r * 0.003f)).takeLast(22) } }
    LaunchedEffect(Unit) { while (true) { delay(32); tick = System.currentTimeMillis() } }
    val blinkAlpha by rememberInfiniteTransition(label = "b").animateFloat(1f, 0.1f, infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "ba")

    Box(modifier = Modifier.fillMaxSize().background(AppBg)) {
        Box(modifier = Modifier.size(360.dp).align(Alignment.TopCenter).offset(y = (-30).dp)
            .background(Brush.radialGradient(listOf(SosRed.copy(0.12f), Color.Transparent))))

        Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(SosRed.copy(0.10f))
                .border(1.dp, SosRed.copy(0.28f), RoundedCornerShape(20.dp)).padding(horizontal = 16.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(7.dp).clip(CircleShape).background(SosRed.copy(blinkAlpha)))
                Text("BROADCASTING", color = SosRed, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text("Your location is being shared", color = TextSub, fontSize = 12.sp)
            Text("%.4f° S  ·  %.4f° E".format(abs(state.userLat), state.userLng), color = Color(0xFF3A4D40), fontSize = 11.sp)
            Spacer(Modifier.height(14.dp))

            Box(modifier = Modifier.size(238.dp)) {
                Canvas(Modifier.fillMaxSize()) {
                    val c = this.center; val maxR = size.minDimension / 2f
                    for (i in 1..4) drawCircle(SosRed.copy(0.20f - i * 0.03f), maxR * (i / 4f), c, style = Stroke(1.dp.toPx()))
                    for (dot in dots) {
                        val age = (tick - dot.born).coerceAtLeast(0); val prog = (age / 1100f).coerceIn(0f, 1f)
                        if (1f - prog > 0f) drawCircle(AccentGreen.copy(1f - prog), 4.5.dp.toPx() * (1f + prog * 1.8f),
                            Offset(dot.x * size.width, dot.y * size.height))
                    }
                }
                Box(modifier = Modifier.size(52.dp).clip(CircleShape)
                    .background(Brush.radialGradient(listOf(Color(0xFFF42640), Color(0xFFAA1320)))).align(Alignment.Center),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Radio, null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
            }

            Spacer(Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(state.respondersCount.toString(), "Notified nearby", Modifier.weight(1f))
                StatCard(formatTime(state.elapsedSeconds), "Elapsed", Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
            listOf("Kenya Police Service" to VioletPurple to "Notified", "Nairobi County Ambulance" to SafeGreen to "En route").forEach { (p, s) ->
                val (name, color) = p
                Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(AppCard)
                    .border(1.dp, AppBorder, RoundedCornerShape(12.dp)).padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Text(s, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
            }
            Spacer(Modifier.weight(1f))
            OutlinedButton(onClick = onCancel, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AppBorder),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSub)) {
                Text("Cancel Broadcast", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.clip(RoundedCornerShape(16.dp)).background(AppCard).border(1.dp, AppBorder, RoundedCornerShape(16.dp)).padding(16.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = AppWhite, fontSize = 34.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(label, color = TextSub, fontSize = 11.sp)
        }
    }
}
