package com.resq254.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.resq254.app.data.AuthRepository
import com.resq254.app.ui.auth.AuthOptionScreen
import com.resq254.app.ui.auth.ForgotPasswordScreen
import com.resq254.app.ui.auth.LoginScreen
import com.resq254.app.ui.auth.SignUpScreen
import com.resq254.app.ui.auth.UserRole
import com.resq254.app.ui.onboarding.OnboardingScreen
import com.resq254.app.ui.splash.SplashScreen
import com.resq254.app.ui.sp.SpShell
import com.resq254.app.ui.theme.NusuruTheme
import com.resq254.app.ui.user.UserShell
import com.resq254.app.viewmodel.MainViewModel
import com.resq254.app.viewmodel.SpViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NusuruTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ResQApp()
                }
            }
        }
    }
}

/** Navigation state machine. Manual, matching this project's existing style (no NavHost). */
private sealed class Screen {
    data object Splash : Screen()
    data object Onboarding : Screen()
    data object AuthOptions : Screen()
    data class Login(val presetRole: String? = null) : Screen()
    data class SignUp(val role: String) : Screen()
    data object ForgotPassword : Screen()
    data object UserHome : Screen()
    data object SpHome : Screen()
}

@Composable
fun ResQApp() {
    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()

    var screen by remember { mutableStateOf<Screen>(Screen.Splash) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    when (val current = screen) {
        Screen.Splash -> {
            SplashScreen(
                onTimeout = {
                    // If already signed in, skip straight past onboarding/auth into the right shell.
                    val user = authRepository.currentUser
                    if (user == null) {
                        screen = Screen.Onboarding
                    } else {
                        isLoading = true
                        scope.launch {
                            val role = authRepository.getCurrentUserRole()
                            isLoading = false
                            screen = if (role == "provider") Screen.SpHome else Screen.UserHome
                        }
                    }
                }
            )
        }

        Screen.Onboarding -> {
            OnboardingScreen(
                onFinished = { screen = Screen.AuthOptions },
                onLoginClicked = { screen = Screen.Login() }
            )
        }

        Screen.AuthOptions -> {
            AuthOptionScreen(
                onRoleSelected = { role ->
                    errorMessage = null
                    screen = Screen.SignUp(role.name)
                },
                onBackToLogin = {
                    errorMessage = null
                    screen = Screen.Login()
                }
            )
        }

        is Screen.Login -> {
            LoginScreen(
                onLogin = { email, password ->
                    isLoading = true
                    errorMessage = null
                    scope.launch {
                        val result = authRepository.signIn(email, password)
                        isLoading = false
                        result.onSuccess {
                            scope.launch {
                                val role = authRepository.getCurrentUserRole()
                                screen = if (role == "provider") Screen.SpHome else Screen.UserHome
                            }
                        }.onFailure { e ->
                            errorMessage = e.message ?: "Sign in failed. Check your details and try again."
                        }
                    }
                },
                onGoogleLoginClick = {
                    errorMessage = "Google sign-in isn't wired up yet -- use email and password."
                },
                onForgotPasswordClicked = {
                    errorMessage = null
                    screen = Screen.ForgotPassword
                },
                onNavigateToSignUpOptions = {
                    errorMessage = null
                    screen = Screen.AuthOptions
                },
                isLoading = isLoading,
                errorMessage = errorMessage
            )
        }

        is Screen.SignUp -> {
            SignUpScreen(
                role = current.role,
                onSignUp = { name, email, phone, password, serviceType, licenseNumber ->
                    isLoading = true
                    errorMessage = null
                    val backendRole = if (current.role == UserRole.SERVICE_PROVIDER.name) "provider" else "resident"
                    scope.launch {
                        val result = authRepository.signUp(
                            fullName = name,
                            email = email,
                            phone = phone,
                            password = password,
                            role = backendRole,
                            serviceType = serviceType,
                            licenseNumber = licenseNumber
                        )
                        isLoading = false
                        result.onSuccess {
                            screen = if (backendRole == "provider") Screen.SpHome else Screen.UserHome
                        }.onFailure { e ->
                            errorMessage = e.message ?: "Registration failed. Please try again."
                        }
                    }
                },
                onGoogleSignUpClick = {
                    errorMessage = "Google sign-up isn't wired up yet -- use the form below."
                },
                onBackToLogin = {
                    errorMessage = null
                    screen = Screen.Login()
                },
                isLoading = isLoading,
                errorMessage = errorMessage
            )
        }

        Screen.ForgotPassword -> {
            ForgotPasswordScreen(
                onBackToLogin = {
                    errorMessage = null
                    screen = Screen.Login()
                },
                onResetSent = { email ->
                    scope.launch { authRepository.sendPasswordReset(email) }
                }
            )
        }

        Screen.UserHome -> {
            val mainViewModel: MainViewModel = viewModel()
            UserShell(
                viewModel = mainViewModel,
                onLogout = {
                    mainViewModel.signOut()
                    screen = Screen.Login()
                }
            )
        }

        Screen.SpHome -> {
            val spViewModel: SpViewModel = viewModel()
            SpShell(
                viewModel = spViewModel,
                onCall = { _, _ -> /* dialer intent is handled inside CallingScreen/SpActiveResponseScreen */ },
                onLogout = {
                    spViewModel.signOut()
                    screen = Screen.Login()
                }
            )
        }
    }
}
