package com.resq254.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.resq254.app.ui.screens.*
import com.resq254.app.viewmodel.MainViewModel

object Routes {
    const val HOME          = "home"
    const val BROADCASTING  = "broadcasting"
    const val FEED          = "feed"
    const val GUIDE         = "guide"
    const val NOTIFICATIONS = "notifications"
    const val HOSPITALS     = "hospitals"
    const val CALLING       = "calling/{label}/{num}"
    const val ALERT_DETAIL  = "alertDetail/{alertId}"

    fun calling(label: String, num: String) = "calling/${label.replace("/", "-")}/${num.replace("/", "-")}"
    fun alertDetail(id: String)             = "alertDetail/$id"
}

@Composable
fun Resq254NavGraph(
    navController: NavHostController,
    viewModel    : MainViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()

    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                state       = state,
                onSOSTap    = { viewModel.startSOS(); navController.navigate(Routes.BROADCASTING) },
                onBellTap   = { navController.navigate(Routes.NOTIFICATIONS) },
                onQuickCall = { label, num -> navController.navigate(Routes.calling(label, num)) },
                onHospitalTap = { navController.navigate(Routes.HOSPITALS) },
                onAlertTap  = {
                    val first = state.alerts.firstOrNull()
                    if (first != null) navController.navigate(Routes.alertDetail(first.id))
                },
            )
        }

        composable(Routes.BROADCASTING) {
            BroadcastScreen(
                state      = state,
                formatTime = viewModel::formatElapsed,
                onCancel   = {
                    viewModel.cancelSOS()
                    navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } }
                },
            )
        }

        composable(Routes.FEED) {
            FeedScreen(
                state          = state,
                filteredAlerts = viewModel::filteredAlerts,
                onFilterChange = viewModel::setFeedFilter,
                onSearchChange = viewModel::setFeedSearch,
                onAlertTap     = { id -> navController.navigate(Routes.alertDetail(id)) },
            )
        }

        composable(Routes.GUIDE) {
            GuideScreen(
                state         = state,
                onCategorySet = viewModel::setGuideCategory,
            )
        }

        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(
                state     = state,
                onRead    = viewModel::markNotificationRead,
                onMarkAll = viewModel::markAllRead,
                onBack    = { navController.popBackStack() },
            )
        }

        composable(Routes.HOSPITALS) {
            HospitalsScreen(
                onBack = { navController.popBackStack() },
                onCall = { label, num -> navController.navigate(Routes.calling(label, num)) },
            )
        }

        composable(
            route     = Routes.CALLING,
            arguments = listOf(
                navArgument("label") { type = NavType.StringType },
                navArgument("num")   { type = NavType.StringType },
            )
        ) { entry ->
            CallingScreen(
                label  = entry.arguments?.getString("label") ?: "",
                number = entry.arguments?.getString("num")   ?: "",
                onEnd  = { navController.popBackStack() },
            )
        }

        composable(
            route     = Routes.ALERT_DETAIL,
            arguments = listOf(navArgument("alertId") { type = NavType.StringType })
        ) { entry ->
            val alertId = entry.arguments?.getString("alertId") ?: ""
            val alert   = state.alerts.find { it.id == alertId }
            if (alert != null) {
                AlertDetailScreen(
                    alert              = alert,
                    isResponding       = alertId in state.respondingAlerts,
                    onToggleResponding = { viewModel.toggleResponding(alertId) },
                    onCall             = { label, num -> navController.navigate(Routes.calling(label, num)) },
                    onBack             = { navController.popBackStack() },
                )
            }
        }
    }
}
