package com.resq254.app.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resq254.app.data.Hospital
import com.resq254.app.ui.theme.*

@Composable
fun HospitalsScreen(hospitals: List<Hospital>, onBack: () -> Unit, onCall: (String, String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        Row(
            modifier = Modifier.padding(16.dp).clickable { onBack() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = AccentGreen, modifier = Modifier.size(18.dp))
            Text("Back", color = AccentGreen, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        Text("Nearby Hospitals", color = TextLight, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
        Text("Within 5 km of Westlands", color = TextGrey, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(hospitals) { h ->
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurface), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                            Column {
                                Text(h.name, color = TextLight, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text("${h.area}  ${h.distance}", color = TextGrey, fontSize = 12.sp)
                            }
                            Row(
                                modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(GreenSafe.copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(GreenSafe))
                                Text("OPEN", color = GreenSafe, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { onCall(h.name, h.phone) },
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenSafe)
                        ) {
                            Icon(Icons.Default.Call, null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Call ${h.phone}", fontSize = 13.sp)
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}