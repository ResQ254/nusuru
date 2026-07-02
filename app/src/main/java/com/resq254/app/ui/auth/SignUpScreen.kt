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
import com.resq254.app.ui.theme.*

@Composable
fun SignUpScreen(
    role: String?,
    onSignUpSuccess: () -> Unit,
    onGoogleSignUpClick: () -> Unit,
    onBackToLogin: () -> Unit
) {
    // Uses UserRole declared in AuthOptionScreen.kt
    val isProvider = role == UserRole.SERVICE_PROVIDER.name

    // Common Form States
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Service Provider Specific States
    var serviceType by remember { mutableStateOf("") }
    var licenseNumber by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(SurfaceWhite).padding(24.dp)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(24.dp))

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
            Spacer(modifier = Modifier.height(28.dp))

            // Google Sign-Up Action
            OutlinedButton(
                onClick = onGoogleSignUpClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Text(
                    text = if (isProvider) "Sign up organization with Google" else "Continue with Google",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Visual "OR" Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
                Text(
                    text = "or register via form",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Field 1: Name / Organization Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(if (isProvider) "Organization / Agency Name" else "Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SafeGreen,
                    unfocusedBorderColor = BorderColor,
                    focusedLabelColor = SafeGreen,
                    unfocusedLabelColor = TextSecondary,
                    focusedContainerColor = AppCardLight,
                    unfocusedContainerColor = AppCardLight
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Field 2: Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SafeGreen,
                    unfocusedBorderColor = BorderColor,
                    focusedLabelColor = SafeGreen,
                    unfocusedLabelColor = TextSecondary,
                    focusedContainerColor = AppCardLight,
                    unfocusedContainerColor = AppCardLight
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Field 3: Phone
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SafeGreen,
                    unfocusedBorderColor = BorderColor,
                    focusedLabelColor = SafeGreen,
                    unfocusedLabelColor = TextSecondary,
                    focusedContainerColor = AppCardLight,
                    unfocusedContainerColor = AppCardLight
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Service Provider Fields
            if (isProvider) {
                OutlinedTextField(
                    value = serviceType,
                    onValueChange = { serviceType = it },
                    label = { Text("Service Type (Ambulance / Fire / Police)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SafeGreen,
                        unfocusedBorderColor = BorderColor,
                        focusedLabelColor = SafeGreen,
                        unfocusedLabelColor = TextSecondary,
                        focusedContainerColor = AppCardLight,
                        unfocusedContainerColor = AppCardLight
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = licenseNumber,
                    onValueChange = { licenseNumber = it },
                    label = { Text("Operating License / Badge Number") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SafeGreen,
                        unfocusedBorderColor = BorderColor,
                        focusedLabelColor = SafeGreen,
                        unfocusedLabelColor = TextSecondary,
                        focusedContainerColor = AppCardLight,
                        unfocusedContainerColor = AppCardLight
                    )
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
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SafeGreen,
                    unfocusedBorderColor = BorderColor,
                    focusedLabelColor = SafeGreen,
                    unfocusedLabelColor = TextSecondary,
                    focusedContainerColor = AppCardLight,
                    unfocusedContainerColor = AppCardLight
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Submit Button
            Button(
                onClick = onSignUpSuccess,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Register", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(80.dp))
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