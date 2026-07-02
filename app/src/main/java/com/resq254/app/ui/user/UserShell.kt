package com.resq254.app.ui.user

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.resq254.app.ui.theme.AccentRed
import com.resq254.app.ui.theme.TextMuted
import com.resq254.app.ui.theme.SurfaceWhite

private data class BottomTab(val label: String, val icon: ImageVector, val route: String)

private val tabs = listOf(
    BottomTab("Home", Icons.Default.Home, "home"),
    BottomTab("Alerts", Icons.Default.Notifications, "feed"),
    BottomTab("Guide", Icons.Default.Book, "guide")
)

@Composable
fun UserShell(
    onSOSTap: () -> Unit,
    onQuickCall: (String, String) -> Unit,
    onHospitalTap: () -> Unit,
    onAlertTap: () -> Unit,
    onBellTap: () -> Unit,
    onAlertCardTap: (String) -> Unit,
    mockAlerts: List<EmergencyAlert>,
    mockFeedAlerts: List<ResqAlert>,
    filteredAlerts: () -> List<ResqAlert>,
    feedSearch: String,
    feedFilter: String,
    guideCat: String,
    onFeedSearchChange: (String) -> Unit,
    onFeedFilterChange: (String) -> Unit,
    onGuideCatChange: (String) -> Unit
) {
    var currentTab by remember { mutableStateOf("home") }

    Scaffold(
        containerColor = SurfaceWhite,
        bottomBar = {
            NavigationBar(containerColor = SurfaceWhite) {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab.route,
                        onClick = { currentTab = tab.route },
                        icon = { Icon(tab.icon, tab.label) },
                        label = { Text(tab.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentRed,
                            selectedTextColor = AccentRed,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = SurfaceWhite
                        )
                    )
                }
            }
        }
    ) { padding ->
        when (currentTab) {
            "home" -> HomeScreen(
                state = HomeUiState(alerts = mockAlerts),
                onSOSTap = onSOSTap,
                onBellTap = onBellTap,
                onQuickCall = onQuickCall,
                onHospitalTap = onHospitalTap,
                onAlertTap = onAlertTap,
                modifier = Modifier.padding(padding)
            )
            "feed" -> FeedScreen(
                state = ResqFeedUiState(feedSearch = feedSearch, feedFilter = feedFilter),
                filteredAlerts = filteredAlerts,
                onFilterChange = onFeedFilterChange,
                onSearchChange = onFeedSearchChange,
                onAlertTap = { onAlertCardTap(it) },
                modifier = Modifier.padding(padding)
            )
            "guide" -> GuideScreen(
                state = GuideUiState(guideCat = guideCat),
                onCategorySet = onGuideCatChange,
                modifier = Modifier.padding(padding)
            )
        }
    }
}