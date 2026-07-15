package com.resq254.app.ui.user

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq254.app.ui.theme.*
import java.util.Calendar

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
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = if (hour < 12) "Good Morning" else if (hour < 17) "Good Afternoon" else "Good Evening"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(greeting, color = TextGrey, fontSize = 12.sp)
                Text("Amara Kariuki", color = TextLight, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Box {
                IconButton(onClick = onBellTap) {
                    Icon(Icons.Default.Notifications, "Notifications", tint = TextLight)
                }
                if (state.unreadCount > 0) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(RedSOS).align(Alignment.TopEnd))
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(DarkSurface).padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.LocationOn, null, tint = AccentGreen, modifier = Modifier.size(13.dp))
                Text("Westlands, Nairobi", color = TextLight, fontSize = 12.sp)
            }
            Row(
                modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(GreenSafe.copy(alpha = 0.15f)).padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.Wifi, null, tint = AccentGreen, modifier = Modifier.size(13.dp))
                Text("2,847 nearby", color = AccentGreen, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text("EMERGENCY BROADCAST", color = TextGrey, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(20.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            SOSButton(onClick = onSOSTap)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Tap to alert nearby bystanders and emergency services", color = TextGrey, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(28.dp))

        Text("QUICK CALL", color = TextGrey, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickCallButton("Police",    Icons.Default.Security,            PurplePolice, { onQuickCall("Police",    "999") },          Modifier.weight(1f))
            QuickCallButton("Ambulance", Icons.Default.Favorite,            RedSOS,       { onQuickCall("Ambulance", "0800 720 999") }, Modifier.weight(1f))
            QuickCallButton("Fire",      Icons.Default.LocalFireDepartment, OrangeAlert,  { onQuickCall("Fire",      "999") },          Modifier.weight(1f))
            QuickCallButton("Hospital",  Icons.Default.LocalHospital,       GreenSafe,    onHospitalTap,                                Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(20.dp))

        val latest = state.alerts.firstOrNull()
        if (latest != null) {
            val blink by rememberInfiniteTransition(label = "blink").animateFloat(
                1f, 0.2f, infiniteRepeatable(tween(700), RepeatMode.Reverse), label = "b"
            )
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onAlertTap() },
                colors   = CardDefaults.cardColors(containerColor = DarkSurface),
                shape    = RoundedCornerShape(10.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(RedSOS.copy(alpha = blink)))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${latest.title} - ${latest.location}", color = TextLight, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("${latest.responders} responding", color = TextGrey, fontSize = 11.sp)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = TextGrey)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SOSButton(onClick: () -> Unit) {
    val pulse by rememberInfiniteTransition(label = "sos").animateFloat(
        0.85f, 1.3f, infiniteRepeatable(tween(1200), RepeatMode.Reverse), label = "p"
    )
    Box(modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.size((140 * pulse).dp).clip(CircleShape).background(RedSOS.copy(alpha = 0.18f)))
        Box(modifier = Modifier.size((110 * pulse).dp).clip(CircleShape).background(RedSOS.copy(alpha = 0.12f)))
        Button(
            onClick = onClick,
            modifier = Modifier.size(100.dp),
            shape    = CircleShape,
            colors   = ButtonDefaults.buttonColors(containerColor = RedSOS),
            elevation= ButtonDefaults.buttonElevation(8.dp)
        ) {
            Text("SOS", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun QuickCallButton(label: String, icon: ImageVector, color: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(10.dp)).background(DarkSurface).clickable { onClick() }.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, label, tint = color, modifier = Modifier.size(18.dp))
        Text(label, color = TextGrey, fontSize = 10.sp, textAlign = TextAlign.Center)
    }
}