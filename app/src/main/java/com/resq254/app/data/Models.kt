package com.resq254.app.data

// ── Firestore document models ─────────────────────────────────
// These map 1-to-1 with your Firestore collections.
// Collection: "alerts"   → Alert
// Collection: "sos"      → SosEvent
// Collection: "users"    → UserProfile

data class Alert(
    val id          : String    = "",
    val type        : String    = "fire",       // "fire" | "medical" | "security" | "flood"
    val title       : String    = "",
    val location    : String    = "",
    val description : String    = "",
    val lat         : Double    = -1.2921,
    val lng         : Double    = 36.8219,
    val status      : String    = "active",     // "active" | "responding" | "police" | "resolved"
    val responders  : Int       = 0,
    val timestampMs : Long      = 0L,
)

data class SosEvent(
    val id          : String    = "",
    val userId      : String    = "",
    val lat         : Double    = 0.0,
    val lng         : Double    = 0.0,
    val active      : Boolean   = true,
    val timestampMs : Long      = 0L,
)

data class UserProfile(
    val uid         : String    = "",
    val name        : String    = "",
    val fcmToken    : String    = "",
    val lat         : Double    = 0.0,
    val lng         : Double    = 0.0,
)

data class Hospital(
    val name        : String,
    val area        : String,
    val distanceKm  : Float,
    val phone       : String,
    val isOpen      : Boolean   = true,
)

data class NotificationItem(
    val id          : Int,
    val type        : String,
    val title       : String,
    val body        : String,
    val minutesAgo  : Int,
    val read        : Boolean   = false,
)

// ── Static data that doesn't need Firestore ───────────────────
object StaticData {

    val hospitals = listOf(
        Hospital("Nairobi Hospital",             "Upper Hill", 1.4f, "020 2845000"),
        Hospital("MP Shah Hospital",             "Parklands",  2.1f, "020 3662000"),
        Hospital("Aga Khan University Hospital", "Parklands",  2.8f, "020 3660000"),
        Hospital("Kenyatta National Hospital",   "Upper Hill", 3.5f, "020 2726300"),
    )

    val guides = mapOf(
        "fire" to listOf(
            "Alert others"     to "Shout 'Fire!' and activate the nearest alarm. Do not wait — every second counts.",
            "Call 999"         to "State your location, building name, and which floors are involved.",
            "Stay low"         to "Crawl below smoke. Close every door behind you to slow the fire's spread.",
            "Use stairs only"  to "Elevators are off-limits during a fire. Use stairwells and keep to the walls.",
            "Assembly point"   to "Reach your designated area outside and account for everyone in your group.",
        ),
        "medical" to listOf(
            "Check the scene"    to "Ensure it is safe before approaching — don't become a second casualty.",
            "Check for response" to "Tap their shoulders firmly and shout 'Are you okay?' loudly.",
            "Call 0800 720 999"  to "Kenya Red Cross emergency line. Stay on the line with the dispatcher.",
            "Start CPR"          to "30 chest compressions followed by 2 rescue breaths. Rate: 100–120/min.",
            "Don't stop"         to "Continue until paramedics arrive and take over.",
        ),
        "security" to listOf(
            "Run if you can"         to "Leave the area immediately if doing so is genuinely safe.",
            "Hide if you can't"      to "Lock or barricade the door. Silence your phone completely.",
            "Call 999"               to "Speak quietly. Give your exact location and describe the threat.",
            "Fight as last resort"   to "Use objects to distract or disarm only if directly threatened.",
            "Wait for the all-clear" to "Only move when confirmed safe by uniformed police officers.",
        ),
        "flood" to listOf(
            "Move upward now"     to "Head to high ground or upper floors immediately — don't wait.",
            "Avoid flood water"   to "Just 15 cm of moving water can knock you off your feet.",
            "Call 0800 723 225"   to "Kenya Red Cross disaster coordination line.",
            "Signal for rescue"   to "Use a torch, mirror, or bright cloth to attract rescuers.",
            "Wait if you're safe" to "Staying put on high ground is safer than moving through water.",
        ),
    )

    fun typeColor(type: String) = when (type) {
        "fire"     -> androidx.compose.ui.graphics.Color(0xFFD97706)
        "medical"  -> androidx.compose.ui.graphics.Color(0xFFE01B2F)
        "security" -> androidx.compose.ui.graphics.Color(0xFF7C3AED)
        "flood"    -> androidx.compose.ui.graphics.Color(0xFF0EA5E9)
        else       -> androidx.compose.ui.graphics.Color(0xFFD97706)
    }

    fun statusColor(status: String) = when (status) {
        "active"     -> androidx.compose.ui.graphics.Color(0xFFE01B2F)
        "responding" -> androidx.compose.ui.graphics.Color(0xFFD97706)
        "police"     -> androidx.compose.ui.graphics.Color(0xFF7C3AED)
        "resolved"   -> androidx.compose.ui.graphics.Color(0xFF697569)
        else         -> androidx.compose.ui.graphics.Color(0xFF697569)
    }

    fun timeLabel(ms: Long): String {
        val diff = (System.currentTimeMillis() - ms) / 1000 / 60
        return if (diff < 60) "${diff}m ago" else "${diff / 60}h ago"
    }
}
