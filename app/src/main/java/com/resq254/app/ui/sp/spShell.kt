package com.resq254.app.ui.sp

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.resq254.app.data.ChatRepository
import com.resq254.app.ui.chat.ChatScreen
import com.resq254.app.ui.theme.AccentGreen
import com.resq254.app.ui.theme.TextMuted
import com.resq254.app.viewmodel.SpViewModel

private data class SpTab(val label: String, val icon: ImageVector, val route: String)
private val spTabs = listOf(
    SpTab("Home", Icons.Default.Home, "sp_home"),
    SpTab("Alerts", Icons.Default.Notifications, "sp_alerts"),
    SpTab("Profile", Icons.Default.Person, "sp_profile")
)

@Composable
fun SpShell(
    viewModel: SpViewModel,
    onCall: (String, String) -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val chatThreads by ChatRepository.threads.collectAsState()

    var currentTab by remember { mutableStateOf("sp_home") }
    var viewingAlertId by remember { mutableStateOf<String?>(null) }
    var viewingActiveJob by remember { mutableStateOf(false) }
    var viewingChat by remember { mutableStateOf(false) }
    var viewingDutyLog by remember { mutableStateOf(false) }

    val currentBg = MaterialTheme.colorScheme.background
    val activeJob = state.activeJob
    val ringingAlert = state.incomingAlerts.find { it.id == state.ringingAlertId }

    // Full-screen incoming call takes priority over everything else
    if (ringingAlert != null) {
        SpIncomingCallScreen(
            alert = ringingAlert,
            onAccept = {
                viewModel.acceptRingingAlert()
                viewingActiveJob = true
            },
            onDecline = viewModel::declineRingingAlert
        )
        return
    }

    Scaffold(
        containerColor = currentBg,
        bottomBar = {
            if (viewingAlertId == null && !viewingActiveJob && !viewingChat && !viewingDutyLog) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    spTabs.forEach { tab ->
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
        }
    ) { padding ->
        when {
            viewingDutyLog -> {
                SpDutyLogScreen(
                    jobs = state.dutyLog,
                    onBack = { viewingDutyLog = false }
                )
            }
            viewingChat && activeJob != null -> {
                ChatScreen(
                    alertTitle = activeJob.alert.title,
                    myRole = "responder",
                    messages = chatThreads[activeJob.alert.id].orEmpty(),
                    onSendText = { text -> ChatRepository.send(activeJob.alert.id, "responder", text = text) },
                    onSendPhoto = { uri -> ChatRepository.send(activeJob.alert.id, "responder", photoUri = uri.toString()) },
                    onBack = { viewingChat = false }
                )
            }
            viewingActiveJob && activeJob != null -> {
                SpActiveResponseScreen(
                    job = activeJob,
                    elapsedSeconds = state.elapsedSeconds,
                    formatTime = viewModel::formatTime,
                    onAdvance = viewModel::advanceJobStatus,
                    onComplete = {
                        viewModel.completeJob()
                        viewingActiveJob = false
                    },
                    onCall = onCall,
                    onChatTap = { viewingChat = true },
                    onCancel = {
                        viewModel.cancelJob()
                        viewingActiveJob = false
                    },
                    onBack = { viewingActiveJob = false }
                )
            }
            viewingAlertId != null -> {
                val alert = state.incomingAlerts.find { it.id == viewingAlertId }
                if (alert != null) {
                    SpAlertDetailScreen(
                        alert = alert,
                        onAccept = {
                            viewModel.acceptAlert(alert.id)
                            viewingAlertId = null
                            viewingActiveJob = true
                        },
                        onCall = onCall,
                        onBack = { viewingAlertId = null }
                    )
                } else {
                    viewingAlertId = null
                }
            }
            else -> when (currentTab) {
                "sp_home" -> SpHomeScreen(
                    state = state,
                    onToggleOnline = viewModel::toggleOnline,
                    onActiveJobTap = { viewingActiveJob = true },
                    onIncomingTap = { viewingAlertId = it },
                    modifier = Modifier.padding(padding)
                )
                "sp_alerts" -> SpAlertsScreen(
                    alerts = state.incomingAlerts,
                    onAlertTap = { viewingAlertId = it },
                    modifier = Modifier.padding(padding)
                )
                "sp_profile" -> SpProfileScreen(
                    profile = state.profile,
                    isOnline = state.isOnline,
                    onToggleOnline = viewModel::toggleOnline,
                    onViewDutyLog = { viewingDutyLog = true },
                    onLogout = onLogout,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}