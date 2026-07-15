package com.resq254.app.data

data class ChatMessage(
    val id: String,
    val alertId: String,
    val senderRole: String,   // "reporter" | "responder"
    val text: String? = null,
    val photoUri: String? = null,
    val timestampMs: Long = System.currentTimeMillis()
)