package com.resq254.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq254.app.ui.theme.*

enum class UserRole { USER, SERVICE_PROVIDER }

@Composable
fun AuthOptionScreen(
    onRoleSelected: (UserRole) -> Unit,
    onBackToLogin: () -> Unit
) {
    var selectedRole by remember { mutableStateOf<UserRole?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceWhite) // Fixed theme token continuity
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Join as a user or responder",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.3).sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Select your account type to get started.",
                color = TextSub, // Standardized theme color
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Option 1: Standard User (Fixed logic header mismatch)
            RoleCard(
                title = "I need emergency assistance",
                description = "Register as a citizen client to report emergencies, broadcast alerts, and reach nearby responders instantly.",
                isSelected = selectedRole == UserRole.USER,
                onClick = { selectedRole = UserRole.USER }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Option 2: Service Provider
            RoleCard(
                title = "I am a responder / provider",
                description = "Register an ambulance, police unit, medical center, or fire response team to coordinate dispatch and save lives.",
                isSelected = selectedRole == UserRole.SERVICE_PROVIDER,
                onClick = { selectedRole = UserRole.SERVICE_PROVIDER }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Continue Button
            Button(
                onClick = { selectedRole?.let { onRoleSelected(it) } },
                enabled = selectedRole != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SafeGreen, // Aligned branding token
                    disabledContainerColor = BorderColor
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Continue",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Bottom link back to login
        Text(
            text = "Already have an account? Sign In",
            color = TextSub,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .clickable { onBackToLogin() }
        )
    }
}

@Composable
fun RoleCard(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) SafeGreen else BorderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SafeGreen.copy(alpha = 0.06f) else AppCardLight
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                color = if (isSelected) SafeGreen else TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                color = TextSub,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}