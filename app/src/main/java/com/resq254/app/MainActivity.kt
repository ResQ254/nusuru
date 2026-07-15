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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.resq254.app.ui.sp.SpShell
import com.resq254.app.ui.theme.NusuruTheme
import com.resq254.app.ui.user.UserShell
import com.resq254.app.viewmodel.MainViewModel
import com.resq254.app.viewmodel.SpViewModel

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

private enum class AppRole { USER, SP }

@Composable
fun ResQApp() {
    var role by remember { mutableStateOf(AppRole.USER) }

    when (role) {
        AppRole.USER -> {
            val mainViewModel: MainViewModel = viewModel()
            UserShell(
                viewModel = mainViewModel,
                onSwitchToSp = { role = AppRole.SP }
            )
        }
        AppRole.SP -> {
            val spViewModel: SpViewModel = viewModel()
            SpShell(
                viewModel = spViewModel,
                onCall = { _, _ -> /* TODO: wire up real dialer intent if not already handled inside shells */ },
                onLogout = { role = AppRole.USER }
            )
        }
    }
}