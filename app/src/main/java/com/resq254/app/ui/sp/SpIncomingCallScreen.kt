package com.resq254.app.ui.sp

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq254.app.data.Alert
import com.resq254.app.data.getTypeColor
import com.resq254.app.ui.theme.RedSOS
import com.resq254.app.ui.theme.SafeGreen

@Composable
fun SpIncomingCallScreen(
    alert: Alert,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    val typeColor = getTypeColor(alert.type)
    val pulse by rememberInfiniteTransition(label = "ring").animateFloat(
        initialValue = 1f, targetValue = 1.25f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "p"
    )

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF121212)).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text("NEW EMERGENCY", color = RedSOS, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.size((130 * pulse).dp).clip(CircleShape).background(typeColor.copy(alpha = 0.15f)))
            Box(modifier = Modifier.size(90.dp).clip(CircleShape).background(typeColor), contentAlignment = Alignment.Center) {
                Icon(
                    when (alert.type) {
                        "fire" -> Icons.Default.LocalFireDepartment
                        "medical" -> Icons.Default.Favorite
                        "security" -> Icons.Default.Security
                        else -> Icons.Default.Water
                    },
                    null, tint = Color.White, modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
        Text(alert.title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(Icons.Default.LocationOn, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
            Text(alert.location, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
        }

        if (alert.otherResponders.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "${alert.otherResponders.size} other unit(s) already responding",
                color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FilledIconButton(
                    onClick = onDecline,
                    modifier = Modifier.size(64.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = RedSOS)
                ) {
                    Icon(Icons.Default.Close, "Decline", tint = Color.White, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Decline", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FilledIconButton(
                    onClick = onAccept,
                    modifier = Modifier.size(64.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = SafeGreen)
                ) {
                    Icon(Icons.Default.Check, "Accept", tint = Color.White, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Accept", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}