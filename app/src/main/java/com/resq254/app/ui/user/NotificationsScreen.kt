package com.resq254.app.ui.user

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq254.app.data.getTypeColor
import com.resq254.app.ui.theme.*
import com.resq254.app.viewmodel.AppState

@Composable
fun NotificationsScreen(state: AppState, onRead: (Int) -> Unit, onReadAll: () -> Unit, onBack: () -> Unit) {
    val unread = state.notifications.count { !it.read }
    Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.clickable { onBack() }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = AccentGreen, modifier = Modifier.size(18.dp))
                Text("Back", color = AccentGreen, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
            if (unread > 0) Text("Mark all read", color = TextGrey, fontSize = 12.sp, modifier = Modifier.clickable { onReadAll() })
        }
        Text("Notifications", color = TextLight, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
        if (unread > 0) Text("$unread unread", color = TextGrey, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.notifications) { n ->
                val color = getTypeColor(n.type)
                val icon = when (n.type) {
                    "fire" -> Icons.Default.LocalFireDepartment
                    "medical" -> Icons.Default.Favorite
                    "security" -> Icons.Default.Security
                    else -> Icons.Default.Water
                }
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onRead(n.id) },
                    colors = CardDefaults.cardColors(containerColor = if (n.read) DarkSurface else DarkCard),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, null, tint = color, modifier = Modifier.size(14.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    n.title, color = TextLight, fontSize = 13.sp,
                                    fontWeight = if (n.read) FontWeight.Normal else FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f)
                                )
                                if (!n.read) Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(RedSOS))
                            }
                            Text(n.body, color = TextGrey, fontSize = 12.sp)
                            Text(n.time, color = TextGrey, fontSize = 11.sp)
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}