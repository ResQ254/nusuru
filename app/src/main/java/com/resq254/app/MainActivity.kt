package com.resq254.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.resq254.app.ui.auth.*
import com.resq254.app.ui.onboarding.OnboardingScreen
import com.resq254.app.ui.provider.SPHomeScreen
import com.resq254.app.ui.splash.SplashScreen
import com.resq254.app.ui.theme.NusuruTheme
import com.resq254.app.ui.user.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NusuruTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot()
                }
            }
        }
    }
}

@Composable
private fun AppRoot() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(
                onTimeout = {
                    navController.navigate("onboarding") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("onboarding") {
            OnboardingScreen(
                onFinished = { navController.navigate("auth_options") },
                onLoginClicked = { navController.navigate("login") }
            )
        }

        composable("auth_options") {
            AuthOptionScreen(
                onRoleSelected = { role ->
                    navController.navigate("signup/${role.name}")
                },
                onBackToLogin = { navController.navigate("login") }
            )
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("user_home") },
                onGoogleLoginClick = { },
                onForgotPasswordClicked = { navController.navigate("forgot_password") },
                onNavigateToSignUpOptions = { navController.navigate("auth_options") }
            )
        }

        composable("forgot_password") {
            ForgotPasswordScreen(
                onBackToLogin = { navController.popBackStack() },
                onResetSent = { }
            )
        }

        composable("signup/{role}") { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role")
            SignUpScreen(
                role = role,
                onSignUpSuccess = {
                    if (role == "SERVICE_PROVIDER") navController.navigate("sp_home")
                    else navController.navigate("user_home")
                },
                onGoogleSignUpClick = { },
                onBackToLogin = {
                    navController.navigate("login") {
                        popUpTo("auth_options") { inclusive = false }
                    }
                }
            )
        }

        composable("user_home") {
            var guideCat by remember { mutableStateOf("fire") }
            var feedSearch by remember { mutableStateOf("") }
            var feedFilter by remember { mutableStateOf("all") }

            val mockAlerts = listOf(
                EmergencyAlert("Structure Fire", "Tom Mboya St", 12),
                EmergencyAlert("Medical Emergency", "Kenyatta Ave", 4)
            )

            val mockFeedAlerts = listOf(
                ResqAlert("1", "Structure Fire", "fire", "active", "Tom Mboya St", 12, System.currentTimeMillis() - 180000),
                ResqAlert("2", "Medical Emergency", "medical", "responding", "Kenyatta Ave", 4, System.currentTimeMillis() - 420000),
                ResqAlert("3", "Robbery Reported", "security", "pending", "Moi Avenue", 6, System.currentTimeMillis() - 840000)
            )

            val filteredAlerts: () -> List<ResqAlert> = {
                mockFeedAlerts.filter { alert ->
                    (feedFilter == "all" || alert.type == feedFilter) &&
                            (feedSearch.isEmpty() || alert.title.contains(feedSearch, ignoreCase = true))
                }
            }

            UserShell(
                onSOSTap = { navController.navigate("broadcast") },
                onQuickCall = { label, number -> navController.navigate("calling/$label/$number") },
                onHospitalTap = { navController.navigate("hospitals") },
                onAlertTap = { navController.navigate("feed") },
                onBellTap = { navController.navigate("notifications") },
                onAlertCardTap = { id -> navController.navigate("alert_detail/$id") },
                mockAlerts = mockAlerts,
                mockFeedAlerts = mockFeedAlerts,
                filteredAlerts = filteredAlerts,
                feedSearch = feedSearch,
                feedFilter = feedFilter,
                guideCat = guideCat,
                onFeedSearchChange = { feedSearch = it },
                onFeedFilterChange = { feedFilter = it },
                onGuideCatChange = { guideCat = it }
            )
        }

        composable("broadcast") {
            val state = UiState()
            BroadcastScreen(
                state = state,
                formatTime = { s -> "%02d:%02d".format(s / 60, s % 60) },
                onCancel = { navController.popBackStack() }
            )
        }

        composable("calling/{label}/{number}") { backStackEntry ->
            val label = backStackEntry.arguments?.getString("label") ?: ""
            val number = backStackEntry.arguments?.getString("number") ?: ""
            CallingScreen(
                label = label,
                number = number,
                onEnd = { navController.popBackStack() }
            )
        }

        composable("hospitals") {
            HospitalsScreen(
                onBack = { navController.popBackStack() },
                onCall = { label, number -> navController.navigate("calling/$label/$number") }
            )
        }

        composable("notifications") {
            var notifState by remember {
                mutableStateOf(
                    NotificationUiState(
                        notifications = listOf(
                            AppNotification(1, "fire", "Structure Fire nearby", "Active fire on Tom Mboya St, 12 responding", false, "3 min ago"),
                            AppNotification(2, "medical", "Medical alert", "Emergency reported on Kenyatta Ave", true, "7 min ago")
                        )
                    )
                )
            }
            NotificationsScreen(
                state = notifState,
                onRead = { id ->
                    notifState = notifState.copy(
                        notifications = notifState.notifications.map {
                            if (it.id == id) it.copy(read = true) else it
                        }
                    )
                },
                onMarkAll = {
                    notifState = notifState.copy(
                        notifications = notifState.notifications.map { it.copy(read = true) }
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("feed") {
            var feedSearch by remember { mutableStateOf("") }
            var feedFilter by remember { mutableStateOf("all") }
            val mockAlerts = listOf(
                ResqAlert("1", "Structure Fire", "fire", "active", "Tom Mboya St", 12, System.currentTimeMillis() - 180000),
                ResqAlert("2", "Medical Emergency", "medical", "responding", "Kenyatta Ave", 4, System.currentTimeMillis() - 420000),
                ResqAlert("3", "Robbery Reported", "security", "pending", "Moi Avenue", 6, System.currentTimeMillis() - 840000),
                ResqAlert("4", "Road Accident", "medical", "resolved", "Uhuru Highway", 18, System.currentTimeMillis() - 1320000)
            )
            FeedScreen(
                state = ResqFeedUiState(feedSearch = feedSearch, feedFilter = feedFilter),
                filteredAlerts = {
                    mockAlerts.filter { alert ->
                        (feedFilter == "all" || alert.type == feedFilter) &&
                                (feedSearch.isEmpty() || alert.title.contains(feedSearch, ignoreCase = true))
                    }
                },
                onFilterChange = { feedFilter = it },
                onSearchChange = { feedSearch = it },
                onAlertTap = { navController.navigate("alert_detail/$it") }
            )
        }

        composable("alert_detail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            var isResponding by remember { mutableStateOf(false) }
            val mockAlert = Alert(
                id = id,
                title = "Structure Fire",
                type = "fire",
                status = "active",
                location = "Tom Mboya St, CBD",
                description = "A fire has broken out on the third floor of a commercial building. Thick smoke visible from street level. Building is being evacuated.",
                timestampMs = System.currentTimeMillis() - 180000,
                responders = 12
            )
            AlertDetailScreen(
                alert = mockAlert,
                isResponding = isResponding,
                onToggleResponding = { isResponding = !isResponding },
                onCall = { label, number -> navController.navigate("calling/$label/$number") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("sp_home") {
            SPHomeScreen()
        }
    }
}