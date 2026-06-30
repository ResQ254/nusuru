package com.resq254.app.ui.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq254.app.ui.theme.AccentRed
import com.resq254.app.ui.theme.BgPage
import com.resq254.app.ui.theme.BorderColor
import com.resq254.app.ui.theme.TextPrimary
import com.resq254.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

enum class IconType { CLOCK, NETWORK, OFFLINE }

data class OnboardingPage(val title: String, val description: String, val icon: IconType)

private val pages = listOf(
    OnboardingPage("Seconds matter", "One tap sends your location, your guardians and the right responders, all at once", IconType.CLOCK),
    OnboardingPage("Help finds you", "Verified police, ambulance and fire response, matched by who is actually closest to you", IconType.NETWORK),
    OnboardingPage("No signal, no problem", "Your emergency contacts and action steps live on your phone, ready offline", IconType.OFFLINE)
)

@Composable
fun OnboardingScreen(onFinished: () -> Unit, onLoginClicked: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(BgPage)) {
        Column(modifier = Modifier.fillMaxSize()) {

            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                OnboardingPageContent(pages[page])
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                pages.indices.forEach { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .height(3.dp)
                            .width(if (isSelected) 16.dp else 6.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isSelected) AccentRed else BorderColor)
                    )
                }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)) {
                if (pagerState.currentPage < pages.size - 1) {
                    Button(
                        onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Next", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                } else {
                    Button(
                        onClick = onFinished,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TextPrimary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Get started", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                    Box(modifier = Modifier.height(8.dp))
                    Text(
                        text = "I already have an account",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OnboardingIcon(page.icon)
        Box(modifier = Modifier.height(28.dp))
        Text(page.title, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        Box(modifier = Modifier.height(6.dp))
        Text(page.description, color = TextSecondary, fontSize = 12.sp, textAlign = TextAlign.Center, lineHeight = 18.sp)
    }
}

@Composable
private fun OnboardingIcon(type: IconType) {
    Canvas(modifier = Modifier.size(140.dp)) {
        val strokeWidth = 4f
        when (type) {
            IconType.CLOCK -> {
                drawCircle(color = BorderColor, radius = size.minDimension / 2, style = Stroke(width = strokeWidth))
                val center = Offset(size.width / 2, size.height / 2)
                drawLine(AccentRed, center, Offset(center.x, center.y - size.minDimension * 0.28f), strokeWidth + 1f, StrokeCap.Round)
                drawLine(AccentRed, center, Offset(center.x + size.minDimension * 0.20f, center.y + size.minDimension * 0.10f), strokeWidth + 1f, StrokeCap.Round)
                drawCircle(color = TextPrimary, radius = 6f, center = center)
            }
            IconType.NETWORK -> {
                val center = Offset(size.width * 0.32f, size.height * 0.4f)
                val nodes = listOf(
                    Offset(size.width * 0.68f, size.height * 0.32f),
                    Offset(size.width * 0.75f, size.height * 0.62f),
                    Offset(size.width * 0.42f, size.height * 0.75f)
                )
                nodes.forEach { drawLine(AccentRed.copy(alpha = 0.4f), center, it, 2f, StrokeCap.Round) }
                drawCircle(color = AccentRed, radius = 10f, center = center)
                nodes.forEach { drawCircle(color = TextPrimary.copy(alpha = 0.18f), radius = 7f, center = it) }
            }
            IconType.OFFLINE -> {
                drawRoundRect(
                    color = BorderColor,
                    style = Stroke(width = strokeWidth),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f)
                )
                val checkStart = Offset(size.width * 0.32f, size.height * 0.52f)
                val checkMid = Offset(size.width * 0.45f, size.height * 0.65f)
                val checkEnd = Offset(size.width * 0.70f, size.height * 0.36f)
                drawLine(AccentRed, checkStart, checkMid, strokeWidth + 1f, StrokeCap.Round)
                drawLine(AccentRed, checkMid, checkEnd, strokeWidth + 1f, StrokeCap.Round)
            }
        }
    }
}

