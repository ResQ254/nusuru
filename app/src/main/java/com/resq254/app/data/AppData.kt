
package com.resq254.app.data

// TODO(backend): this whole object is mock data for frontend development.
// Replace sampleAlerts/hospitals/guideSteps with real API/DB-backed sources,
// and timeAgo() with a real relative-time formatter.
object AppData {
    val sampleAlerts: List<Alert> = listOf(
        Alert(
            id = "1", type = "fire", status = "active",
            title = "Structure fire reported", location = "Tom Mboya St, Nairobi",
            timestampMs = System.currentTimeMillis() - 3 * 60_000,
            responders = 4,
            description = "Fire reported in a commercial building. Fire department en route.",
            latitude = -1.2841, longitude = 36.8233,
            otherResponders = listOf(OtherResponder("Unit 12 - Fire", "Fire Officer", 6))
        ),
        Alert(
            id = "2", type = "medical", status = "active",
            title = "SOS - medical emergency", location = "Near Kenyatta Ave",
            timestampMs = System.currentTimeMillis() - 9 * 60_000,
            responders = 2,
            description = "Bystander reported a person collapsed. Ambulance dispatched.",
            latitude = -1.2864, longitude = 36.8172
        ),
        Alert(
            id = "3", type = "security", status = "resolved",
            title = "Robbery reported", location = "Moi Ave",
            timestampMs = System.currentTimeMillis() - 34 * 60_000,
            responders = 6,
            description = "Police responded and situation has been resolved.",
            latitude = -1.2833, longitude = 36.8256,
            otherResponders = listOf(
                OtherResponder("Unit 4 - Police", "Police Officer", 3),
                OtherResponder("Unit 9 - Police", "Police Officer", 8)
            )
        ),
        Alert(
            id = "4", type = "flood", status = "active",
            title = "Flash flooding on road", location = "Mombasa Rd",
            timestampMs = System.currentTimeMillis() - 60_000,
            responders = 1,
            description = "Heavy rain has caused flooding, road partially impassable.",
            latitude = -1.3197, longitude = 36.8506
        )
    )

    val hospitals: List<Hospital> = listOf(
        Hospital(name = "Aga Khan University Hospital", area = "Parklands", distance = "2.1 km", phone = "0703 000 000"),
        Hospital(name = "Nairobi Hospital",              area = "Upper Hill", distance = "3.4 km", phone = "0703 000 001"),
        Hospital(name = "Kenyatta National Hospital",    area = "Upper Hill", distance = "3.8 km", phone = "0703 000 002"),
        Hospital(name = "MP Shah Hospital",               area = "Parklands", distance = "2.6 km", phone = "0703 000 003")
    )

    val guideSteps: Map<String, List<String>> = mapOf(
        "fire" to listOf(
            "Alert everyone nearby and activate the fire alarm if available.",
            "Do not use elevators — use the stairs.",
            "Stay low to avoid smoke inhalation.",
            "Call the fire department once you are safe.",
            "Do not re-enter the building for any reason."
        ),
        "medical" to listOf(
            "Check if the person is responsive and breathing.",
            "Call for an ambulance immediately.",
            "If trained, begin CPR if there is no pulse.",
            "Keep the person still and warm while waiting for help.",
            "Do not give food or water to an unconscious person."
        ),
        "security" to listOf(
            "Move to a safe location away from the threat.",
            "Call the police immediately.",
            "Avoid confronting any suspects directly.",
            "Note down descriptions if it is safe to do so.",
            "Wait for police guidance before returning to the area."
        ),
        "flood" to listOf(
            "Move to higher ground immediately.",
            "Avoid walking or driving through flood water.",
            "Turn off electricity at the mains if safe to do so.",
            "Keep emergency contacts and documents in a waterproof bag.",
            "Follow official evacuation instructions."
        )
    )

    fun timeAgo(timestampMs: Long): String {
        val diffMs = System.currentTimeMillis() - timestampMs
        val minutes = diffMs / 60_000
        return when {
            minutes < 1 -> "just now"
            minutes < 60 -> "$minutes min ago"
            minutes < 1440 -> "${minutes / 60} hr ago"
            else -> "${minutes / 1440} day(s) ago"
        }
    }
}