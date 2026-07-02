package com.resq254.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq254.app.data.AuthManager
import com.resq254.app.ui.theme.AccentRed
import com.resq254.app.ui.theme.BgPage
import com.resq254.app.ui.theme.TextPrimary
import com.resq254.app.ui.theme.TextSecondary

@Composable
fun SignUpScreen(
    role: String?,
    onSignUpSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val isProvider = role == UserRole.SERVICE_PROVIDER.name

    // Common Form States
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Service Provider Specific States
    var serviceType by remember { mutableStateOf("") } // e.g., Ambulance, Fire, Police
    var licenseNumber by remember { mutableStateOf("") }

    // UI feedback states
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(BgPage).padding(24.dp)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = if (isProvider) "Register Responder Unit" else "Create User Account",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isProvider) "Join the emergency response network" else "Get protected with immediate emergency services",
                color = TextSecondary,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Field 1: Name / Organization Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(if (isProvider) "Organization / Agency Name" else "Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Field 2: Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Field 3: Phone
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Service Provider Fields
            if (isProvider) {
                OutlinedTextField(
                    value = serviceType,
                    onValueChange = { serviceType = it },
                    label = { Text("Service Type (Ambulance / Fire / Police)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = licenseNumber,
                    onValueChange = { licenseNumber = it },
                    label = { Text("Operating License / Badge Number") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Field 4: Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error message
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = AccentRed,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Submit Button
            Button(
                onClick = {
                    when {
                        name.isBlank() -> errorMessage = "Please enter your name"
                        email.isBlank() -> errorMessage = "Please enter your email"
                        password.length < 6 -> errorMessage = "Password must be at least 6 characters"
                        else -> {
                            errorMessage = null
                            isLoading = true

                            val profile = buildMap<String, Any?> {
                                put("name", name.trim())
                                put("email", email.trim())
                                put("phone", phone.trim())
                                put("role", if (isProvider) "service_provider" else "user")
                                if (isProvider) {
                                    put("serviceType", serviceType.trim())
                                    put("licenseNumber", licenseNumber.trim())
                                }
                            }

                            AuthManager.signUp(
                                email = email.trim(),
                                password = password,
                                profile = profile,
                                onSuccess = {
                                    isLoading = false
                                    onSignUpSuccess()
                                },
                                onError = { message ->
                                    isLoading = false
                                    errorMessage = message
                                }
                            )
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (isLoading) "Creating account..." else "Register",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Bottom link back to login
        Text(
            text = "Already have an account? Sign In",
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .clickable { onBackToLogin() }
        )
    }
}