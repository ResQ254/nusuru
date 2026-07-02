package com.resq254.app.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.resq254.app.ui.theme.*

// --- LOCAL DATA MODELS FOR COMPILATION FIXED DATA LAYER ---
data class Hospital(
    val name: String,
    val area: String,
    val distanceKm: Double,
    val phone: String
)

private object StaticHospitalData {
    // High-quality, real-world emergency stubs for Nairobi medical facilities
    val hospitals = listOf(
        Hospital("MP Shah Hospital", "Shivachi Road, Parklands", 1.2, "020 4291000"),
        Hospital("The Aga Khan University Hospital", "3rd Parklands Avenue", 2.4, "020 3662000"),
        Hospital("Avenue Hospital Nairobi", "First Parklands Avenue", 2.8, "0711 060000"),
        Hospital("The Nairobi Hospital (Outpatient)", "Galleria / Westlands", 3.1, "020 2845000")
    )
}

@Composable
fun HospitalsScreen(onBack: () -> Unit, onCall: (String, String) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceWhite)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .clickable { onBack() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Fixed: AutoMirrored version scales perfectly across systemic phone configurations
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = SafeGreen, modifier = Modifier.size(18.dp))
                Text("Back", color = SafeGreen, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text("Nearby Hospitals", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp)
                Text("Westlands · within 5 km", color = TextSub, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                Spacer(Modifier.height(16.dp))
            }
        }

        items(StaticHospitalData.hospitals) { h ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 10.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppCardLight)
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(h.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("${h.area} · ${h.distanceKm} km", color = TextSub, fontSize = 12.sp, modifier = Modifier.padding(top = 3.dp))
                    }
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(SafeGreen.copy(0.10f))
                            .border(1.dp, SafeGreen.copy(0.25f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.size(5.dp).clip(CircleShape).background(SafeGreen))
                        Text("OPEN", color = SafeGreen, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { onCall(h.name, h.phone) },
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SafeGreen.copy(0.22f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SafeGreen, containerColor = SafeGreen.copy(0.08f))
                ) {
                    Icon(Icons.Default.Call, null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(7.dp))
                    Text("Call ${h.phone}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}