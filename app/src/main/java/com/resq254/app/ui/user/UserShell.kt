package com.resq254.app.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq254.app.ui.theme.AccentGreen
import com.resq254.app.ui.theme.TextMuted
import com.resq254.app.ui.theme.TextSecondary
import com.resq254.app.viewmodel.MainViewModel

private data class UserTab(val label: String, val icon: ImageVector, val route: String)
private val userTabs = listOf(
    UserTab("Home", Icons.Default.Home, "home"),
    UserTab("Feed", Icons.AutoMirrored.Filled.List, "feed"),
    UserTab("Guide", Icons.Default.MenuBook, "guide"),
    UserTab("Profile", Icons.Default.Person, "profile")
)

@Composable
fun UserShell(
    viewModel: MainViewModel,
    onLogout: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    var currentTab by remember { mutableStateOf("home") }
    var viewingAlertId by remember { mutableStateOf<String?>(null) }
    var viewingHospitals by remember { mutableStateOf(false) }
    var viewingNotifications by remember { mutableStateOf(false) }
    var viewingBroadcast by remember { mutableStateOf(false) }
    var callTarget by remember { mutableStateOf<Pair<String, String>?>(null) }

    val currentBg = MaterialTheme.colorScheme.background

    val activeCallTarget = callTarget
    if (activeCallTarget != null) {
        CallingScreen(
            label = activeCallTarget.first,
            number = activeCallTarget.second,
            onEnd = { callTarget = null }
        )
        return
    }

    if (viewingBroadcast && state.sosActive) {
        BroadcastScreen(
            state = state,
            formatTime = viewModel::formatTime,
            onCancel = {
                viewModel.cancelSOS()
                viewingBroadcast = false
            }
        )
        return
    }

    if (viewingNotifications) {
        NotificationsScreen(
            state = state,
            onRead = viewModel::markRead,
            onReadAll = viewModel::markAllRead,
            onBack = { viewingNotifications = false }
        )
        return
    }

    if (viewingHospitals) {
        HospitalsScreen(
            hospitals = state.hospitals,
            onBack = { viewingHospitals = false },
            onCall = { label, number -> callTarget = label to number }
        )
        return
    }

    val viewingId = viewingAlertId
    if (viewingId != null) {
        val alert = state.alerts.find { it.id == viewingId }
        if (alert != null) {
            AlertDetailScreen(
                alert = alert,
                responding = viewingId in state.respondingTo,
                onRespond = { viewModel.toggleResponding(viewingId) },
                onCall = { label, number -> callTarget = label to number },
                onBack = { viewingAlertId = null }
            )
        } else {
            viewingAlertId = null
        }
        return
    }

    Scaffold(
        containerColor = currentBg,
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                userTabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab.route,
                        onClick = { currentTab = tab.route },
                        icon = { Icon(tab.icon, tab.label) },
                        label = { Text(tab.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentGreen,
                            selectedTextColor = AccentGreen,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }
        }
    ) { padding ->
        when (currentTab) {
            "home" -> HomeScreen(
                state = HomeUiState(
                    alerts = state.alerts.filter { it.status == "active" }.map {
                        EmergencyAlert(it.id, it.title, it.location, it.responders)
                    },
                    unreadCount = state.notifications.count { !it.read }
                ),
                onSOSTap = {
                    viewModel.startSOS()
                    viewingBroadcast = true
                },
                onBellTap = { viewingNotifications = true },
                onQuickCall = { label, number -> callTarget = label to number },
                onHospitalTap = { viewingHospitals = true },
                onAlertTap = { state.alerts.firstOrNull()?.let { viewingAlertId = it.id } },
                modifier = Modifier.padding(padding)
            )
            "feed" -> FeedScreen(
                state = ResqFeedUiState(
                    feedSearch = state.searchQuery,
                    feedFilter = state.filterType
                ),
                filteredAlerts = {
                    viewModel.getFilteredAlerts().map {
                        ResqAlert(it.id, it.type, it.status, it.title, it.location, it.timestampMs, it.responders, it.description)
                    }
                },
                onFilterChange = viewModel::setFilter,
                onSearchChange = viewModel::setSearch,
                onAlertTap = { id -> viewingAlertId = id },
                modifier = Modifier.padding(padding)
            )
            "guide" -> GuideScreen(
                state = GuideUiState(guideCat = state.guideCategory),
                onCategorySet = viewModel::setGuide,
                modifier = Modifier.padding(padding)
            )
            "profile" -> UserProfileTab(
                email = viewModel.currentUserEmail,
                onLogout = onLogout,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun UserProfileTab(email: String?, onLogout: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = email ?: "Signed in",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp
        )
        Text(
            text = "ResQ254 resident account",
            color = TextSecondary,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(onClick = onLogout) {
            Text("Log out")
        }
    }
}

data class EmergencyAlert(
    val id: String,
    val title: String,
    val location: String,
    val responders: Int
)

data class ResqAlert(
    val id: String,
    val type: String,
    val status: String,
    val title: String,
    val location: String,
    val timestampMs: Long,
    val responders: Int,
    val description: String
)

data class HomeUiState(
    val alerts: List<EmergencyAlert> = emptyList(),
    val unreadCount: Int = 0
)

data class ResqFeedUiState(
    val feedSearch: String = "",
    val feedFilter: String = "all",
    val isLoading: Boolean = false
)

data class GuideUiState(
    val guideCat: String = "fire"
)