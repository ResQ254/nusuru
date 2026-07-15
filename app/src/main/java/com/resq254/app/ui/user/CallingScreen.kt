package com.resq254.app.ui.user

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
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
    var seconds   by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
        context.startActivity(intent)
        delay(2000)
        connected = true
    }
    LaunchedEffect(connected) {
        if (!connected) return@LaunchedEffect
        while (true) { delay(1000); seconds++ }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val pulseState = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = ""
    )
    val pulse = pulseState.value // Extracted explicitly to prevent animation state compiler warnings

    // DYNAMIC LIGHT/DARK ADAPTATION VIA MATERIALTHEME TOKENS
    val currentBg = MaterialTheme.colorScheme.background
    val currentText = MaterialTheme.colorScheme.onBackground

    Column(
        modifier = Modifier.fillMaxSize().background(currentBg).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            if (connected) "CONNECTED" else "CALLING...",
            color = TextSecondary,
            fontSize = 12.sp,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .size((if (connected) 120 else 100).dp)
                .scale(if (!connected) pulse else 1f)
                .clip(CircleShape)
                .background(AccentRed.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Call, null, tint = AccentRed, modifier = Modifier.size(44.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(label, color = currentText, fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            if (connected) "%02d:%02d".format(seconds / 60, seconds % 60) else number,
            color = TextSecondary,
            fontSize = 16.sp
        )

        if (connected) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(SafeGreen.copy(alpha = 0.15f))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Check, null, tint = SafeGreen, modifier = Modifier.size(14.dp))
                Text("Connected", color = SafeGreen, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onEnd,
            modifier = Modifier.size(65.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
        ) {
            Icon(
                Icons.Default.Call,
                "End",
                tint = Color.White,
                modifier = Modifier.size(24.dp).rotate(135f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}