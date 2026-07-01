package com.resq254.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq254.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun CallingScreen(label: String, number: String, onEnd: () -> Unit) {
    val context = LocalContext.current
    var connected by remember { mutableStateOf(false) }
    var secs by remember { mutableStateOf(0) }

    // Trigger the real system dialer
    LaunchedEffect(Unit) {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
        context.startActivity(intent)
        delay(2200); connected = true
    }
    LaunchedEffect(connected) { if (!connected) return@LaunchedEffect; while (true) { delay(1000); secs++ } }

    val ringProgress by rememberInfiniteTransition(label = "ring").animateFloat(0f, 1f, infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Restart), label = "rp")
    val pulseScale by rememberInfiniteTransition(label = "pulse").animateFloat(1f, 1.08f, infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = "ps")

    Column(modifier = Modifier.fillMaxSize().background(AppBg).padding(horizontal = 24.dp).padding(top = 40.dp, bottom = 52.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(if (connected) "CONNECTED" else "CALLING...", color = TextSub, fontSize = 12.sp, letterSpacing = 1.sp)
        Spacer(Modifier.weight(1f))

        Box(Modifier.size(140.dp), contentAlignment = Alignment.Center) {
            if (!connected) androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
                val c = this.center; val r = size.minDimension / 2f
                for (i in 0..2) { val ph = (ringProgress + i / 3f) % 1f
                    drawCircle(SosRed.copy(0.5f * (1f - ph)), r * (1f + ph * 1.2f), c, style = Stroke(1.5.dp.toPx())) }
            }
            Box(modifier = Modifier.size(if (connected) 120.dp else 104.dp).scale(if (connected) pulseScale else 1f).clip(CircleShape)
                .background(Brush.linearGradient(listOf(SosRed.copy(0.13f), SosRed.copy(0.27f)))).border(1.5.dp, SosRed.copy(0.33f), CircleShape),
                contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Call, null, tint = SosRed, modifier = Modifier.size(36.dp))
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(label, color = AppWhite, fontSize = 26.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp)
        Spacer(Modifier.height(4.dp))
        Text(if (connected) "%02d:%02d".format(secs / 60, secs % 60) else number, color = TextSub, fontSize = 18.sp)
        if (connected) {
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(SafeGreen.copy(0.10f))
                .border(1.dp, SafeGreen.copy(0.25f), RoundedCornerShape(20.dp)).padding(horizontal = 14.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Check, null, tint = SafeGreen, modifier = Modifier.size(12.dp))
                Text("Call connected", color = SafeGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.weight(1f))
        Box(modifier = Modifier.size(70.dp).clip(CircleShape)
            .background(Brush.radialGradient(listOf(Color(0xFFF42640), Color(0xFFAA1320)))).clickable { onEnd() },
            contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Call, "End call", tint = Color.White, modifier = Modifier.size(26.dp).rotate(135f))
        }
    }
}
