package com.resq254.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq254.app.ui.theme.*

@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit,
    onResetSent: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceWhite)
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(modifier = Modifier.height(16.dp))

            // Unified back button action alignment
            Row(
                modifier = Modifier
                    .clickable { onBackToLogin() }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = SafeGreen,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Back",
                    color = SafeGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (!submitted) {
                Text(
                    text = "Forgot password?",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Enter the email address linked to your account and we'll send you a reset link.",
                    color = TextSub,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SafeGreen,
                        unfocusedBorderColor = BorderColor,
                        focusedLabelColor = SafeGreen,
                        unfocusedLabelColor = TextSub,
                        focusedContainerColor = AppCardLight,
                        unfocusedContainerColor = AppCardLight
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        submitted = true
                        onResetSent()
                    },
                    enabled = email.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SafeGreen,
                        disabledContainerColor = BorderColor
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Send reset link",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

            } else {
                Spacer(modifier = Modifier.height(40.dp))

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SafeGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", color = SafeGreen, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Check your inbox",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "We sent a password reset link to $email. Check your spam folder if you don't see it.",
                    color = TextSub,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { onBackToLogin() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Back to sign in",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Didn't receive it? Resend",
                    color = AccentRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { submitted = false }
                )
            }
        }
    }
}