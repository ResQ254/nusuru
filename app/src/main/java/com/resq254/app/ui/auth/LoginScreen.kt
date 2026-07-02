package com.resq254.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq254.app.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onForgotPasswordClicked: () -> Unit,
    onNavigateToSignUpOptions: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(SurfaceWhite).padding(24.dp)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome back",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Log in to access your rescue dashboard",
                color = TextSecondary,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Email Field
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

            // Password Field
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

            // Forgot Password Link
            Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), contentAlignment = Alignment.CenterEnd) {
                Text(
                    text = "Forgot Password?",
                    color = AccentRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onForgotPasswordClicked() }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Login Button
            Button(
                onClick = onLoginSuccess,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Sign In", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Visual "OR" Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
                Text(
                    text = "or",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Google Sign-In Button
            OutlinedButton(
                onClick = onGoogleLoginClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Continue with Google",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Bottom Navigation to Signup Options
        Text(
            text = "Don't have an account? Sign Up",
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .clickable { onNavigateToSignUpOptions() }
        )
    }
}