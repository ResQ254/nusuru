package com.resq254.app.ui.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq254.app.data.StaticData
import com.resq254.app.ui.theme.*
import com.resq254.app.viewmodel.UiState

@Composable
fun GuideScreen(state: UiState, onCategorySet: (String) -> Unit) {
    val steps = StaticData.guides[state.guideCat] ?: emptyList()
    val guideColor = StaticData.typeColor(state.guideCat)

    Column(modifier = Modifier.fillMaxSize().background(AppSurface).verticalScroll(rememberScrollState())) {
        Column(modifier = Modifier.padding(horizontal = 24.dp).padding(top = 20.dp)) {
            Text("Emergency Guide", color = AppWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp)
            Text("Step-by-step action guides", color = TextSub, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
            Spacer(Modifier.height(16.dp))
            val cats = listOf("fire" to (Icons.Default.LocalFireDepartment to "Building Fire"), "medical" to (Icons.Default.Favorite to "Medical"), "security" to (Icons.Default.Security to "Security"), "flood" to (Icons.Default.Water to "Flash Flood"))
            cats.chunked(2).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { (id, pair) ->
                        val (icon, label) = pair; val sel = state.guideCat == id; val col = StaticData.typeColor(id)
                        Row(modifier = Modifier.weight(1f).clip(RoundedCornerShape(14.dp))
                            .background(if (sel) col.copy(0.12f) else AppCard).border(1.dp, if (sel) col.copy(0.30f) else AppBorder, RoundedCornerShape(14.dp))
                            .clickable { onCategorySet(id) }.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                            Icon(icon, label, tint = if (sel) col else TextSub, modifier = Modifier.size(15.dp))
                            Text(label, color = if (sel) col else TextSub, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Column(modifier = Modifier.padding(horizontal = 24.dp).clip(RoundedCornerShape(16.dp)).background(AppCard).border(1.dp, AppBorder, RoundedCornerShape(16.dp))) {
            val blinkAlpha by rememberInfiniteTransition(label = "b").animateFloat(1f, 0.1f, infiniteRepeatable(tween(2000), RepeatMode.Reverse), label = "ba")
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(guideColor.copy(blinkAlpha)))
                Text(StaticData.guides.keys.toList().let { StaticData.guides[state.guideCat]?.let { "Guide" } ?: "Guide" }, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            HorizontalDivider(color = AppBorder)
            steps.forEachIndexed { i, (title, desc) ->
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 13.dp), horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                    Box(modifier = Modifier.size(22.dp).clip(CircleShape).background(AppBorder), contentAlignment = Alignment.Center) {
                        Text("${i + 1}", color = TextSub, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text(title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(3.dp))
                        Text(desc, color = TextSub, fontSize = 12.sp, lineHeight = 17.sp)
                    }
                }
                if (i < steps.size - 1) HorizontalDivider(color = AppBorder)
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.padding(horizontal = 24.dp).clip(RoundedCornerShape(12.dp))
            .background(AccentGreen.copy(0.05f)).border(1.dp, AccentGreen.copy(0.18f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(6.dp).clip(CircleShape).background(AccentGreen))
            Text("Offline ready. All guides work without internet.", color = TextSub, fontSize = 12.sp, lineHeight = 16.sp)
        }
        Spacer(Modifier.height(28.dp))
    }
}
