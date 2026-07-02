package com.resq254.app

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.resq254.app.data.AuthManager
import com.resq254.app.data.LocationProvider
import com.resq254.app.ui.EmergencyViewModel
import com.resq254.app.ui.FeedViewModel
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
                onLoginSuccess = { role ->
                    val destination = if (role == "service_provider") "sp_home" else "user_home"
                    navController.navigate(destination) {
                        popUpTo("login") { inclusive = true }
                    }
                },
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

            val feedViewModel: FeedViewModel = viewModel()
            val incidents by feedViewModel.incidents.collectAsState()
            val liveAlerts = feedViewModel.asAlerts(incidents)

            // Home "active near you" summary derived from the live feed (top items).
            val homeAlerts = liveAlerts.take(3).map { EmergencyAlert(it.title, it.location, it.responders) }

            val filteredAlerts: () -> List<ResqAlert> = {
                liveAlerts.filter { alert ->
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
                mockAlerts = homeAlerts,
                mockFeedAlerts = liveAlerts,
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
            val context = LocalContext.current
            val emergencyViewModel: EmergencyViewModel = viewModel()

            // Request location, then start the real broadcast (create incident + fan-out alerts).
            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { emergencyViewModel.startBroadcast(context) }

            LaunchedEffect(Unit) {
                if (LocationProvider.hasPermission(context)) {
                    emergencyViewModel.startBroadcast(context)
                } else {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

            BroadcastScreen(
                state = emergencyViewModel.uiState,
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
            val feedViewModel: FeedViewModel = viewModel()
            val incidents by feedViewModel.incidents.collectAsState()
            val liveAlerts = feedViewModel.asAlerts(incidents)
            FeedScreen(
                state = ResqFeedUiState(feedSearch = feedSearch, feedFilter = feedFilter),
                filteredAlerts = {
                    liveAlerts.filter { alert ->
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
            val feedViewModel: FeedViewModel = viewModel()
            val incidents by feedViewModel.incidents.collectAsState()
            val alert = feedViewModel.findAlert(incidents, id) ?: Alert(
                id = id,
                title = "Loading incident...",
                type = "other",
                status = "active",
                location = "Fetching location",
                description = "Loading incident details from the server.",
                timestampMs = System.currentTimeMillis(),
                responders = 0
            )
            AlertDetailScreen(
                alert = alert,
                isResponding = isResponding,
                onToggleResponding = { isResponding = !isResponding },
                onCall = { label, number -> navController.navigate("calling/$label/$number") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("sp_home") {
            SPHomeScreen(
                onLogout = {
                    AuthManager.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}