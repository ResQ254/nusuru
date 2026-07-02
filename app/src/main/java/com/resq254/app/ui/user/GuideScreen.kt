package com.resq254.app.ui.user

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq254.app.ui.theme.*

data class GuideUiState(val guideCat: String = "fire")

private data class GuideStep(val title: String, val desc: String)

private data class CategoryConfig(
    val id: String,
    val icon: ImageVector,
    val label: String
)

private object StaticGuideData {
    fun typeColor(type: String): Color = when (type.lowercase()) {
        "fire" -> AccentRed
        "medical" -> SafeGreen
        "security" -> Color(0xFF6A5ACD)
        else -> Color(0xFF2196F3)
    }

    val guides = mapOf(
        "fire" to listOf(
            GuideStep(
                "Evacuate Immediately",
                "Locate the nearest emergency exit. Do not use elevators under any circumstances."
            ),
            GuideStep(
                "Stay Low to the Ground",
                "Smoke rises. Crawl beneath toxic fumes to preserve visibility and fresh air oxygen."
            ),
            GuideStep(
                "Check Doors Safely",
                "Touch handles with the back of your hand before opening. If hot, seek an alternative route."
            )
        ),
        "medical" to listOf(
            GuideStep(
                "Assess Surroundings",
                "Ensure the immediate area is safe for both yourself and the casualty before approaching."
            ),
            GuideStep(
                "Control Heavy Bleeding",
                "Apply firm, direct pressure to the wound using a clean cloth or sterile dressing layer."
            ),
            GuideStep(
                "Keep Patient Calm",
                "Help them sit or lie down comfortably while emergency responders are actively en route."
            )
        ),
        "security" to listOf(
            GuideStep(
                "De-escalate and Retreat",
                "Prioritize personal safety. Create physical distance and avoid aggressive confrontation."
            ),
            GuideStep(
                "Secure Your Space",
                "Lock nearby entry points, silence mobile devices, and remain hidden out of sight lines."
            ),
            GuideStep(
                "Observe Details Safely",
                "Take note of physical descriptions, vehicle tags, or direction of travel if safe to do so."
            )
        ),
        "flood" to listOf(
            GuideStep(
                "Move to High Ground",
                "Evacuate low-lying structures immediately. Avoid basement areas entirely."
            ),
            GuideStep(
                "Avoid Flowing Water",
                "Do not walk or drive through moving water. As little as 6 inches can sweep you away."
            ),
            GuideStep(
                "Disconnect Power",
                "If safe to reach, shut off the main electrical breaker to prevent electrocution hazards."
            )
        )
    )
}

@Composable
fun GuideScreen(
    state: GuideUiState,
    onCategorySet: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val steps = StaticGuideData.guides[state.guideCat] ?: emptyList()
    val guideColor = StaticGuideData.typeColor(state.guideCat)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceWhite)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 20.dp)
        ) {
            Text(
                "Emergency Guide",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.3).sp
            )
            Text(
                "Step-by-step action guides",
                color = TextSub,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(Modifier.height(16.dp))

            val cats = listOf(
                CategoryConfig("fire", Icons.Default.LocalFireDepartment, "Building Fire"),
                CategoryConfig("medical", Icons.Default.Favorite, "Medical"),
                CategoryConfig("security", Icons.Default.Security, "Security"),
                CategoryConfig("flood", Icons.Default.Water, "Flash Flood")
            )

            cats.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { config ->
                        val sel = state.guideCat == config.id
                        val col = StaticGuideData.typeColor(config.id)

                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (sel) col.copy(0.12f) else AppCardLight)
                                .border(
                                    1.dp,
                                    if (sel) col.copy(0.30f) else BorderColor,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { onCategorySet(config.id) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(9.dp)
                        ) {
                            Icon(
                                config.icon,
                                config.label,
                                tint = if (sel) col else TextSub,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                config.label,
                                color = if (sel) col else TextSub,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(AppCardLight)
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
        ) {
            val blinkAlpha by rememberInfiniteTransition(label = "b").animateFloat(
                initialValue = 1f,
                targetValue = 0.1f,
                animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
                label = "ba"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(guideColor.copy(blinkAlpha))
                )
                Text(
                    "Action Protocol",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(color = BorderColor)

            steps.forEachIndexed { i, step ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 13.dp),
                    horizontalArrangement = Arrangement.spacedBy(13.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(BorderColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "${i + 1}",
                            color = TextSub,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(
                            step.title,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            step.desc,
                            color = TextSub,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
                if (i < steps.size - 1) {
                    HorizontalDivider(color = BorderColor)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AccentGreen.copy(0.05f))
                .border(1.dp, AccentGreen.copy(0.18f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(AccentGreen)
            )
            Text(
                "Offline ready. All guides work without internet.",
                color = TextSub,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }

        Spacer(Modifier.height(28.dp))
    }
}