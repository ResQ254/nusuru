package com.resq254.app.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq254.app.data.AppData
import com.resq254.app.ui.theme.*

@Composable
fun GuideScreen(state: GuideUiState, onCategorySet: (String) -> Unit, modifier: Modifier = Modifier) {
    val steps = AppData.guideSteps[state.guideCat] ?: emptyList()

    LazyColumn(
        modifier = modifier.fillMaxSize().background(DarkBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Emergency Guide", color = TextLight, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("What to do in an emergency", color = TextGrey, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CatButton("Fire",    Icons.Default.LocalFireDepartment, "fire",    state.guideCat, onCategorySet, Modifier.weight(1f))
                CatButton("Medical", Icons.Default.Favorite,            "medical", state.guideCat, onCategorySet, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CatButton("Security", Icons.Default.Security, "security", state.guideCat, onCategorySet, Modifier.weight(1f))
                CatButton("Flood",    Icons.Default.Water,    "flood",    state.guideCat, onCategorySet, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text("Steps to follow", color = TextGrey, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
        }
        itemsIndexed(steps) { index, step ->
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(DarkSurface).padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier.size(26.dp).clip(CircleShape).background(AccentGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${index + 1}", color = AccentGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Text(step, color = TextLight, fontSize = 13.sp, lineHeight = 18.sp, modifier = Modifier.weight(1f))
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(AccentGreen.copy(alpha = 0.08f)).padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.WifiOff, null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                Text("These guides work offline", color = AccentGreen, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun CatButton(label: String, icon: ImageVector, id: String, selected: String, onClick: (String) -> Unit, modifier: Modifier = Modifier) {
    val isSel = selected == id
    val color = when (id) {
        "fire" -> OrangeAlert
        "medical" -> RedSOS
        "security" -> PurplePolice
        else -> BlueSky
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSel) color.copy(alpha = 0.15f) else DarkSurface)
            .clickable { onClick(id) }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, label, tint = if (isSel) color else TextGrey, modifier = Modifier.size(16.dp))
        Text(label, color = if (isSel) color else TextGrey, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}