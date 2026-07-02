package com.resq254.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.resq254.app.ui.auth.AuthOptionScreen
import com.resq254.app.ui.auth.LoginScreen
import com.resq254.app.ui.auth.SignUpScreen
import com.resq254.app.ui.onboarding.OnboardingScreen
import com.resq254.app.ui.splash.SplashScreen
import com.resq254.app.ui.theme.NusuruTheme


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

        // 1. Splash Screen
        composable("splash") {
            SplashScreen(
                onTimeout = {
                    navController.navigate("onboarding") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // 2. Onboarding Screen
        composable("onboarding") {
            OnboardingScreen(
                onFinished = { navController.navigate("auth_options") },
                onLoginClicked = { navController.navigate("login") }
            )
        }

        // 3. Auth Options Screen (Choose: User vs Service Provider)
        composable("auth_options") {
            AuthOptionScreen(
                onRoleSelected = { role ->
                    // role passes down as a String token matching the UserRole enum name
                    navController.navigate("signup/${role.name}")
                },
                onBackToLogin = { navController.navigate("login") }
            )
        }

        // 4. Login Screen
        composable("login") {
            LoginScreen(
                onLoginSuccess = { /* Handle dashboard navigation later */ },
                onForgotPasswordClicked = { /* Handle password reset later */ },
                onNavigateToSignUpOptions = { navController.navigate("auth_options") }
            )
        }

        // 5. Sign Up Screen (Accepts the role argument dynamically)
        composable("signup/{role}") { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role")

            SignUpScreen(
                role = role,
                onSignUpSuccess = {
                    // Handle post-signup logic later
                },
                onBackToLogin = {
                    navController.navigate("login") {
                        popUpTo("auth_options") { inclusive = false }
                    }
                }
            )
        }
    }
}