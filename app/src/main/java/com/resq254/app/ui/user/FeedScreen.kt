package com.resq254.app.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq254.app.data.getStatusColor
import com.resq254.app.data.getTypeColor
import com.resq254.app.ui.theme.*

@Composable
fun FeedScreen(
    state: ResqFeedUiState,
    filteredAlerts: () -> List<ResqAlert>,
    onFilterChange: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onAlertTap: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val alerts = filteredAlerts()

    val currentBg = MaterialTheme.colorScheme.background
    val currentSurface = MaterialTheme.colorScheme.surface
    val currentText = MaterialTheme.colorScheme.onBackground

    LazyColumn(
        modifier = modifier.fillMaxSize().background(currentBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Nearby Alerts", color = currentText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Westlands, 5 km radius", color = TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(currentSurface).padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Search, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (state.feedSearch.isEmpty()) Text("Search...", color = TextMuted, fontSize = 14.sp)
                    BasicTextField(
                        value = state.feedSearch,
                        onValueChange = onSearchChange,
                        textStyle = LocalTextStyle.current.copy(color = currentText, fontSize = 14.sp),
                        cursorBrush = SolidColor(AccentGreen),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (state.feedSearch.isNotEmpty()) {
                    Icon(Icons.Default.Close, null, tint = TextSecondary, modifier = Modifier.size(14.dp).clickable { onSearchChange("") })
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("all", "fire", "medical", "security", "flood")) { type ->
                    val selected = state.feedFilter == type
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selected) AccentGreen else currentSurface)
                            .clickable { onFilterChange(type) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            type.replaceFirstChar { it.uppercase() },
                            color = if (selected) Color.White else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
        if (state.isLoading) {
            item {
                Box(Modifier.fillMaxWidth().padding(40.dp), Alignment.Center) {
                    CircularProgressIndicator(color = AccentGreen)
                }
            }
        } else if (alerts.isEmpty()) {
            item { Text("No alerts found", color = TextSecondary, fontSize = 13.sp, modifier = Modifier.padding(20.dp)) }
        } else {
            items(alerts, key = { it.id }) { alert ->
                AlertCard(alert = alert, onClick = { onAlertTap(alert.id) })
            }
        }
    }
}

@Composable
fun AlertCard(alert: ResqAlert, onClick: () -> Unit) {
    val typeColor = getTypeColor(alert.type)
    val statusColor = getStatusColor(alert.status)
    val icon = when (alert.type) {
        "fire" -> Icons.Default.LocalFireDepartment
        "medical" -> Icons.Default.Favorite
        "security" -> Icons.Default.Security
        else -> Icons.Default.Water
    }

    val currentSurface = MaterialTheme.colorScheme.surface
    val currentText = MaterialTheme.colorScheme.onBackground

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = currentSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(typeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = typeColor, modifier = Modifier.size(18.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(alert.title, color = currentText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text(alert.status.uppercase(), color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Text(alert.location, color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("${alert.responders} responding", color = TextSecondary, fontSize = 11.sp)
                    Text(AppData.timeAgo(alert.timestampMs), color = TextSecondary, fontSize = 11.sp)
                }
            }
        }
    }
}