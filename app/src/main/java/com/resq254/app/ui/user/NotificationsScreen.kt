package com.resq254.app.ui.user // Critical Fix: Standardized package tracking layout path

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.resq254.app.ui.theme.*


data class AppNotification(
    val id: Int,
    val type: String, // "fire", "medical", "security", "flood"
    val title: String,
    val description: String,
    val read: Boolean,
    val timestampLabel: String
)

data class NotificationUiState(
    val notifications: List<AppNotification> = emptyList()
)

private object StaticNotificationHelper {
    fun typeColor(type: String): Color = when (type.lowercase()) {
        "fire" -> AccentRed
        "medical" -> SafeGreen
        "security" -> Color(0xFF6A5ACD)
        else -> Color(0xFF2196F3)
    }
}

@Composable
fun NotificationsScreen(
    state: NotificationUiState,
    onRead: (Int) -> Unit,
    onMarkAll: () -> Unit,
    onBack: () -> Unit
) {
    val unread = state.notifications.count { !it.read }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceWhite)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.clickable { onBack() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = SafeGreen, modifier = Modifier.size(18.dp))
                    Text("Back", color = SafeGreen, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                if (unread > 0) {
                    Text(
                        text = "Mark all read",
                        color = TextSub,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { onMarkAll() }
                    )
                }
            }
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text("Notifications", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp)
                if (unread > 0) {
                    Text("$unread unread", color = TextSub, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                }
                Spacer(Modifier.height(16.dp))
            }
        }

        items(state.notifications) { n ->
            val tc = StaticNotificationHelper.typeColor(n.type)
            val icon = when (n.type) {
                "fire" -> Icons.Default.LocalFireDepartment
                "medical" -> Icons.Default.Favorite
                "security" -> Icons.Default.Security
                else -> Icons.Default.Water
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (n.read) AppCardLight else tc.copy(0.05f))
                    .border(1.dp, if (n.read) BorderColor else tc.copy(0.16f), RoundedCornerShape(14.dp))
                    .clickable { onRead(n.id) }
                    .padding(horizontal = 14.dp, vertical = 13.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(tc.copy(0.10f))
                        .border(1.dp, tc.copy(0.16f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = tc, modifier = Modifier.size(14.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = n.title,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = if (n.read) FontWeight.Medium else FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        if (!n.read) {
                            Box(
                                Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(AccentRed)
                            )
                        }
                    }
                    Text(n.description, color = TextSub, fontSize = 12.sp, modifier = Modifier.padding(top = 3.dp))
                    Text(n.timestampLabel, color = TextSub, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}