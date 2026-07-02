package com.resq254.app.ui.user

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq254.app.ui.theme.*
import java.util.Calendar

data class EmergencyAlert(
    val title: String,
    val location: String,
    val responders: Int
)

data class NotificationItem(
    val id: String,
    val read: Boolean
)

data class HomeUiState(
    val notifications: List<NotificationItem> = emptyList(),
    val alerts: List<EmergencyAlert> = emptyList()
)

@Composable
fun HomeScreen(
    state: HomeUiState,
    onSOSTap: () -> Unit,
    onBellTap: () -> Unit,
    onQuickCall: (String, String) -> Unit,
    onHospitalTap: () -> Unit,
    onAlertTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "GOOD MORNING"
        in 12..16 -> "GOOD AFTERNOON"
        else -> "GOOD EVENING"
    }
    val unread = state.notifications.count { !it.read }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceWhite)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    greeting,
                    color = TextSub,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    "Amara Kariuki",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                )
            }
            Box {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AppCardLight)
                        .border(1.dp, BorderColor, CircleShape)
                        .clickable { onBellTap() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        null,
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                if (unread > 0) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(AccentRed)
                            .align(Alignment.TopEnd)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AppCardLight)
                    .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(SafeGreen)
                )
                Icon(
                    Icons.Default.LocationOn,
                    null,
                    tint = TextPrimary,
                    modifier = Modifier.size(11.dp)
                )
                Text(
                    "Westlands, Nairobi",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(SafeGreen.copy(0.08f))
                    .border(1.dp, SafeGreen.copy(0.22f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    Icons.Default.Wifi,
                    null,
                    tint = SafeGreen,
                    modifier = Modifier.size(11.dp)
                )
                Text(
                    "2,847 nearby",
                    color = SafeGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "EMERGENCY BROADCAST",
                color = TextSub,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp
            )
            Spacer(Modifier.height(28.dp))
            SosButton(onClick = onSOSTap)
            Spacer(Modifier.height(20.dp))
            Text(
                "Instantly alerts nearby bystanders\nand emergency services",
                color = TextSub,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                "QUICK CALL",
                color = TextSub,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuickCallTile(
                    "Police",
                    Icons.Default.Security,
                    Color(0xFF6A5ACD),
                    { onQuickCall("Police", "999") },
                    Modifier.weight(1f)
                )
                QuickCallTile(
                    "Ambulance",
                    Icons.Default.Favorite,
                    AccentRed,
                    { onQuickCall("Ambulance", "0800 720 999") },
                    Modifier.weight(1f)
                )
                QuickCallTile(
                    "Fire",
                    Icons.Default.LocalFireDepartment,
                    Color(0xFFFF9800),
                    { onQuickCall("Fire", "999") },
                    Modifier.weight(1f)
                )
                QuickCallTile(
                    "Hospital",
                    Icons.Default.LocalHospital,
                    SafeGreen,
                    onHospitalTap,
                    Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        val topAlert = state.alerts.firstOrNull()
        if (topAlert != null) {
            val blinkAlpha by rememberInfiniteTransition(label = "blink").animateFloat(
                initialValue = 1f,
                targetValue = 0.1f,
                animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
                label = "a"
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(AccentRed.copy(0.07f), Color.Transparent)
                        )
                    )
                    .border(1.dp, AccentRed.copy(0.18f), RoundedCornerShape(14.dp))
                    .clickable { onAlertTap() }
                    .padding(13.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(AccentRed.copy(alpha = blinkAlpha))
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "${topAlert.title} · ${topAlert.location}",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "${topAlert.responders} responding",
                            color = TextSub,
                            fontSize = 11.sp
                        )
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        null,
                        tint = TextSub,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))
    }
}

@Composable
fun SosButton(onClick: () -> Unit) {
    val progress by rememberInfiniteTransition(label = "sos").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(2150, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "p"
    )
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.94f else 1f, label = "s")

    Box(Modifier.size(188.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val c = this.center
            val r = size.minDimension / 2f
            for (i in 0..2) {
                val ph = (progress + i / 3f) % 1f
                drawCircle(
                    color = Color(0xFFE01B2F).copy(0.45f * (1f - ph)),
                    radius = r * (0.85f + 1.75f * ph),
                    center = c,
                    style = Stroke(1.5.dp.toPx())
                )
            }
        }
        Box(
            modifier = Modifier
                .size(158.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFFF42640), Color(0xFFAA1320))
                    )
                )
                .clickable {
                    pressed = false
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "SOS",
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp
                )
                Text(
                    "TAP TO BROADCAST",
                    color = Color.White.copy(0.55f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp
                )
            }
        }
    }
}

@Composable
fun QuickCallTile(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(AppCardLight)
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, label, tint = color, modifier = Modifier.size(17.dp))
        Text(label, color = TextSub, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}