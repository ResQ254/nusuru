package com.resq254.app.ui.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import com.resq254.app.ui.theme.*

data class AlertItem(
    val type: String,
    val location: String,
    val distance: String,
    val timeAgo: String,
    val description: String,
    val isUrgent: Boolean
)

private val mockAlerts = listOf(
    AlertItem("Medical", "Westlands", "0.8 km", "2 min ago", "Person collapsed at the gate...", true),
    AlertItem("Fire", "Parklands", "2.1 km", "5 min ago", "Smoke coming from building roof...", false),
    AlertItem("Security", "CBD", "3.4 km", "8 min ago", "Robbery reported near the junction...", false)
)

@Composable
fun SPHomeScreen(onLogout: () -> Unit = {}) {
    var isAvailable by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceWhite)
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Nairobi Central", color = TextSecondary, fontSize = 11.sp)
                Text("Responder dashboard", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isAvailable) Color(0xFFE8F5E9) else Color(0xFFFCEBEB))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        if (isAvailable) "ON DUTY" else "OFF DUTY",
                        color = if (isAvailable) Color(0xFF27AE60) else AccentRed,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    "Log out",
                    color = AccentRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onLogout() }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceWhite)
                .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Availability", color = TextSecondary, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    if (isAvailable) "Available" else "Unavailable",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(22.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(if (isAvailable) Color(0xFF27AE60) else BorderColor)
                    .clickable { isAvailable = !isAvailable },
                contentAlignment = if (isAvailable) Alignment.CenterEnd else Alignment.CenterStart
            ) {
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .size(18.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "INCOMING ALERTS",
            color = TextMuted,
            fontSize = 9.sp,
            letterSpacing = 0.08.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        mockAlerts.forEach { alert ->
            SPAlertCard(alert = alert)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceWhite)
                .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            SPStatItem("3", "today", TextPrimary)
            Box(modifier = Modifier.width(1.dp).height(24.dp).background(BorderColor))
            SPStatItem("2", "responded", Color(0xFF27AE60))
            Box(modifier = Modifier.width(1.dp).height(24.dp).background(BorderColor))
            SPStatItem("1", "pending", Color(0xFFFF8F00))
        }
    }
}

@Composable
private fun SPAlertCard(alert: AlertItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (alert.isUrgent) Color(0xFFFCEBEB) else SurfaceWhite)
            .border(
                width = if (alert.isUrgent) 1.5.dp else 1.dp,
                color = if (alert.isUrgent) AccentRed else BorderColor,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "${alert.type}, ${alert.location}",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "${alert.distance} away · ${alert.timeAgo}",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
                if (!alert.isUrgent) {
                    Text(
                        "View",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (alert.isUrgent) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "\"${alert.description}\"",
                    color = TextSecondary,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentRed)
                            .clickable { }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Respond", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BorderColor)
                            .clickable { }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Decline", color = TextPrimary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SPStatItem(value: String, label: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = valueColor, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        Text(label, color = TextMuted, fontSize = 9.sp)
    }
}