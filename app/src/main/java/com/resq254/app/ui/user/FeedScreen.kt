package com.resq254.app.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq254.app.ui.theme.*

data class ResqAlert(
    val id: String,
    val title: String,
    val type: String,
    val status: String,
    val location: String,
    val responders: Int,
    val timestampMs: Long
)

data class ResqFeedUiState(
    val feedSearch: String = "",
    val feedFilter: String = "all",
    val alertsLoading: Boolean = false
)

private object FeedStaticData {
    fun typeColor(type: String): Color = when (type) {
        "fire" -> AccentRed
        "medical" -> SafeGreen
        "security" -> Color(0xFF6A5ACD)
        else -> Color(0xFF2196F3)
    }

    fun statusColor(status: String): Color = when (status.lowercase()) {
        "active", "critical" -> AccentRed
        "resolved" -> SafeGreen
        else -> TextSub
    }

    fun timeLabel(timestampMs: Long): String {
        val diff = System.currentTimeMillis() - timestampMs
        val mins = (diff / 60000).coerceAtLeast(1)
        return if (mins < 60) "${mins}m ago" else "${mins / 60}h ago"
    }
}

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

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceWhite),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 20.dp)
            ) {
                Text(
                    "Nearby Alerts",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    "Westlands · 5 km radius",
                    color = TextSub,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppCardLight)
                        .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        Icons.Default.Search,
                        null,
                        tint = TextSub,
                        modifier = Modifier.size(15.dp)
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        if (state.feedSearch.isEmpty()) {
                            Text("Search alerts...", color = TextSub, fontSize = 14.sp)
                        }
                        BasicTextField(
                            value = state.feedSearch,
                            onValueChange = onSearchChange,
                            textStyle = LocalTextStyle.current.copy(
                                color = TextPrimary,
                                fontSize = 14.sp
                            ),
                            cursorBrush = SolidColor(AccentGreen),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (state.feedSearch.isNotEmpty()) {
                        Icon(
                            Icons.Default.Close,
                            null,
                            tint = TextSub,
                            modifier = Modifier
                                .size(14.dp)
                                .clickable { onSearchChange("") }
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }

        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listOf("all", "fire", "medical", "security", "flood")) { f ->
                    val sel = state.feedFilter == f
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (sel) AccentGreen else AppCardLight)
                            .border(
                                1.dp,
                                if (sel) AccentGreen else BorderColor,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { onFilterChange(f) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = f.replaceFirstChar { it.uppercase() },
                            color = if (sel) Color.White else TextSub,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        if (state.alertsLoading) {
            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentGreen)
                }
            }
        } else if (alerts.isEmpty()) {
            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No alerts found", color = TextSub, fontSize = 13.sp)
                }
            }
        } else {
            items(alerts, key = { it.id }) { alert ->
                FeedAlertCard(
                    alert = alert,
                    onClick = { onAlertTap(alert.id) },
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 10.dp)
                )
            }
        }
    }
}

@Composable
fun FeedAlertCard(
    alert: ResqAlert,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tc = FeedStaticData.typeColor(alert.type)
    val sc = FeedStaticData.statusColor(alert.status)
    val icon = when (alert.type) {
        "fire" -> Icons.Default.LocalFireDepartment
        "medical" -> Icons.Default.Favorite
        "security" -> Icons.Default.Security
        else -> Icons.Default.Water
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppCardLight)
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(15.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(tc.copy(0.12f))
                .border(1.dp, tc.copy(0.20f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = tc, modifier = Modifier.size(16.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    alert.title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    alert.status.uppercase(),
                    color = sc,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Text(
                alert.location,
                color = TextSub,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 3.dp)
            )
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "${alert.responders} responding",
                    color = TextSub,
                    fontSize = 11.sp
                )
                Text(
                    FeedStaticData.timeLabel(alert.timestampMs),
                    color = TextSub,
                    fontSize = 11.sp
                )
            }
        }
    }
}