package com.resq254.app.ui.user

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq254.app.ui.theme.*
import com.resq254.app.viewmodel.AppState
import kotlin.math.abs

@Composable
fun BroadcastScreen(state: AppState, formatTime: (Int) -> String, onCancel: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    // Explicitly casting the animation value state to float values to fix the type mismatch
    val pulseState = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "p"
    )
    val pulse = pulseState.value

    val blinkState = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "b"
    )
    val blink = blinkState.value

    // DYNAMIC LIGHT/DARK ADAPTATION VIA MATERIALTHEME TOKENS
    val currentBg = MaterialTheme.colorScheme.background
    val currentSurface = MaterialTheme.colorScheme.surface
    val currentText = MaterialTheme.colorScheme.onBackground

    Column(
        modifier = Modifier.fillMaxSize().background(currentBg).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(RedSOS.copy(alpha = 0.15f)).padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(RedSOS.copy(alpha = blink)))
            Text("BROADCASTING", color = RedSOS, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Your location is being shared", color = TextSecondary, fontSize = 13.sp)
        Text("%.4f S  %.4f E".format(abs(state.userLat), state.userLng), color = TextSecondary.copy(alpha = 0.5f), fontSize = 11.sp)

        Spacer(modifier = Modifier.height(28.dp))

        Box(modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.size((150 * pulse).dp).clip(CircleShape).background(RedSOS.copy(alpha = 0.07f)))
            Box(modifier = Modifier.size((110 * pulse).dp).clip(CircleShape).background(RedSOS.copy(alpha = 0.12f)))
            Box(modifier = Modifier.size(70.dp).clip(CircleShape).background(RedSOS), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Radio, null, tint = Color.White, modifier = Modifier.size(30.dp))
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = currentSurface), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.nearbyCount.toString(), color = currentText, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Text("Notified nearby", color = TextSecondary, fontSize = 11.sp)
                }
            }
            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = currentSurface), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(formatTime(state.seconds), color = currentText, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Text("Elapsed", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = currentSurface), shape = RoundedCornerShape(12.dp)) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Kenya Police Service",     color = currentText,   fontSize = 13.sp)
                    Text("Notified",                 color = PurplePolice, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                HorizontalDivider(color = BorderColor)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Nairobi County Ambulance", color = currentText, fontSize = 13.sp)
                    Text("En route",                 color = SafeGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape    = RoundedCornerShape(12.dp),
            colors   = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
            border   = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
        ) {
            Text("Cancel Broadcast", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}