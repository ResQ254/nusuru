package com.resq254.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.resq254.app.navigation.Resq254NavGraph
import com.resq254.app.navigation.Routes
import com.resq254.app.ui.theme.*
import com.resq254.app.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* results handled gracefully in SosRepository */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestRuntimePermissions()
        setContent {
            Resq254Theme {
                Resq254App()
            }
        }
    }

    private fun requestRuntimePermissions() {
        val needed = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CALL_PHONE,
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            needed.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        val missing = needed.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) permissionsLauncher.launch(missing.toTypedArray())
    }
}

@Composable
fun Resq254App(viewModel: MainViewModel = viewModel()) {
    val navController = rememberNavController()
    val entry         by navController.currentBackStackEntryAsState()
    val currentRoute  = entry?.destination?.route

    val showNav = currentRoute in listOf(Routes.HOME, Routes.FEED, Routes.GUIDE)

    Scaffold(
        containerColor = AppBg,
        bottomBar = {
            if (showNav) {
                Resq254BottomNav(currentRoute = currentRoute) { route ->
                    navController.navigate(route) {
                        popUpTo(Routes.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState    = true
                    }
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            Resq254NavGraph(navController = navController, viewModel = viewModel)
        }
    }
}

data class NavItem(val route: String, val label: String, val icon: ImageVector)

@Composable
fun Resq254BottomNav(currentRoute: String?, onNav: (String) -> Unit) {
    val items = listOf(
        NavItem(Routes.HOME,  "Home",   Icons.Default.Home),
        NavItem(Routes.FEED,  "Alerts", Icons.Default.Notifications),
        NavItem(Routes.GUIDE, "Guide",  Icons.Default.MenuBook),
    )
    NavigationBar(containerColor = AppSurface) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick  = { onNav(item.route) },
                icon     = { Icon(item.icon, contentDescription = item.label) },
                label    = { Text(item.label, fontSize = 10.sp) },
                colors   = NavigationBarItemDefaults.colors(
                    selectedIconColor   = AccentGreen,
                    unselectedIconColor = TextSub,
                    selectedTextColor   = AccentGreen,
                    unselectedTextColor = TextSub,
                    indicatorColor      = Color(0xFF0A1F11),
                )
            )
        }
    }
}
