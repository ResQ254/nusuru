package com.resq254.app.data

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName
import com.google.firebase.Timestamp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// ─────────────────────────────────────────────────────────────────────────
// Firestore document shapes. Field names match firestore/db.js and
// firestore/seed.js exactly so this app reads/writes the same schema.
// All properties need defaults for Firestore's reflection-based mapping.
// ─────────────────────────────────────────────────────────────────────────

data class UserProfile(
    val uid: String = "",
    val full_name: String = "",
    val phone_number: String = "",
    val email: String = "",
    val role: String = "resident", // resident | provider | admin
    val is_guardian: Boolean = false,
    val is_verified: Boolean = false
)

data class IncidentDoc(
    @DocumentId val id: String = "",
    val reported_by_uid: String = "",
    val incident_type: String = "medical", // medical | accident | fire | crime | other
    val location: GeoPoint? = null,
    val address_description: String = "",
    val status: String = "active", // active | responding | resolved
    val reported_at: Timestamp? = null,
    val resolved_at: Timestamp? = null,
    val notes: String = ""
)

data class EmergencyContactDoc(
    @DocumentId val id: String = "",
    val name: String = "",
    val category: String = "", // ambulance | police | fire | relief
    val phone_number: String = "",
    val location: GeoPoint? = null,
    val address: String = "",
    @get:PropertyName("is_verified") @set:PropertyName("is_verified")
    var isVerified: Boolean = false,
    val rating: Double = 0.0
)

data class ServiceProviderDoc(
    @DocumentId val id: String = "",
    val name: String = "",
    val type: String = "", // hospital | clinic | police | ambulance | fire
    val phone_number: String = "",
    val location: GeoPoint? = null,
    val address: String = "",
    val radius_km: Double = 0.0,
    @get:PropertyName("is_active") @set:PropertyName("is_active")
    var isActive: Boolean = true,
    val owner_uid: String? = null
)

// ─────────────────────────────────────────────────────────────────────────
// Mapping helpers: Firestore docs -> the UI models the Compose screens use
// ─────────────────────────────────────────────────────────────────────────

private fun incidentTitle(type: String): String = when (type) {
    "fire" -> "Structure fire reported"
    "medical" -> "SOS - medical emergency"
    "crime" -> "Crime reported"
    "accident" -> "Accident reported"
    else -> "Emergency reported"
}

fun IncidentDoc.toAlert(): Alert = Alert(
    id = id,
    type = if (incident_type == "crime") "security" else incident_type,
    status = status,
    title = incidentTitle(incident_type),
    location = address_description,
    timestampMs = reported_at?.toDate()?.time ?: System.currentTimeMillis(),
    responders = 0,
    description = notes.ifBlank { "Reported via ResQ254." },
    latitude = location?.latitude ?: -1.2921,
    longitude = location?.longitude ?: 36.8219
)

fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
}

fun ServiceProviderDoc.toHospital(userLat: Double, userLng: Double): Hospital {
    val dist = location?.let { haversineKm(userLat, userLng, it.latitude, it.longitude) }
    return Hospital(
        name = name,
        area = address,
        distance = dist?.let { "%.1f km".format(it) } ?: "-- km",
        phone = phone_number
    )
}
