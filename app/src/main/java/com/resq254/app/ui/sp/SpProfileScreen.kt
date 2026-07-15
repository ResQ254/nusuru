package com.resq254.app.ui.sp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.resq254.app.data.SpProfile
import com.resq254.app.ui.theme.*

@Composable
fun SpProfileScreen(
    profile: SpProfile,
    isOnline: Boolean,
    onToggleOnline: () -> Unit,
    onViewDutyLog: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentBg = MaterialTheme.colorScheme.background
    val currentSurface = MaterialTheme.colorScheme.surface
    val currentText = MaterialTheme.colorScheme.onBackground

    Column(modifier = modifier.fillMaxSize().background(currentBg).padding(16.dp)) {
        Text("Profile", color = currentText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape).background(AccentGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, tint = AccentGreen, modifier = Modifier.size(30.dp))
            }
            Column {
                Text(profile.name, color = currentText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("${profile.role} · ${profile.badgeId}", color = TextSecondary, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(currentSurface)
                .clickable { onViewDutyLog() }
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Default.History, null, tint = AccentGreen, modifier = Modifier.size(18.dp))
                Text("Duty Log", color = currentText, fontSize = 14.sp)
            }
            Icon(Icons.Default.ChevronRight, null, tint = TextSecondary)
        }
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(currentSurface)
                .clickable { onToggleOnline() }
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Default.Wifi, null, tint = if (isOnline) SafeGreen else TextSecondary, modifier = Modifier.size(18.dp))
                Text("Availability", color = currentText, fontSize = 14.sp)
            }
            Switch(
                checked = isOnline,
                onCheckedChange = { onToggleOnline() },
                colors = SwitchDefaults.colors(checkedTrackColor = SafeGreen)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ProfileStatCard("Completed", profile.completedJobs.toString(), Modifier.weight(1f), currentSurface, currentText)
            ProfileStatCard("Rating", "★ ${profile.rating}", Modifier.weight(1f), currentSurface, currentText)
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = RedSOS)
        ) {
            Icon(Icons.Default.Logout, null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text("Log Out", fontSize = 14.sp)
        }
    }
}

@Composable
private fun ProfileStatCard(label: String, value: String, modifier: Modifier = Modifier, surface: Color, text: Color) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = surface), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = text, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(label, color = TextSecondary, fontSize = 11.sp)
        }
    }
}