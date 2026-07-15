package com.resq254.app.data

data class Alert(
    val id: String,
    val type: String,
    val status: String,
    val title: String,
    val location: String,
    val timestampMs: Long,
    val responders: Int,
    val description: String,
    val latitude: Double = -1.2921,
    val longitude: Double = 36.8219,
    val otherResponders: List<OtherResponder> = emptyList()
)

data class OtherResponder(
    val name: String,
    val role: String,
    val etaMinutes: Int
)