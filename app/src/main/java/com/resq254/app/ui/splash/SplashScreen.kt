package com.resq254.app.ui.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.resq254.app.ui.theme.AccentRed
import com.resq254.app.ui.theme.SurfaceWhite
import com.resq254.app.ui.theme.TextPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val ringScale by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 2.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringScale"
    )

    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringAlpha"
    )

    var wordmarkAlpha by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        delay(900)
        wordmarkAlpha = 1f
        delay(1300)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceWhite),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Box(
                modifier = Modifier.size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(60.dp)) {
                    val ringRadius = (size.minDimension / 2) * ringScale
                    drawCircle(
                        color = AccentRed.copy(alpha = ringAlpha),
                        radius = ringRadius,
                        center = Offset(size.width / 2, size.height / 2),
                        style = Stroke(width = 4f, cap = StrokeCap.Round)
                    )
                }
                Canvas(modifier = Modifier.size(60.dp)) {
                    drawCircle(
                        color = AccentRed,
                        radius = size.minDimension / 2
                    )
                }
            }

            Box(modifier = Modifier.height(14.dp))

            Text(
                text = "resq254",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(wordmarkAlpha)
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    SplashScreen(onTimeout = {})
}