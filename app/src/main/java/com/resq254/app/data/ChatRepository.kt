package com.resq254.app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// TODO(backend): replace with a real-time source (Firestore/WebSocket).
// Kept as a singleton so both the reporter (MainViewModel) and the
// responder (SpViewModel) observe the same thread without a server.
object ChatRepository {

    private val _threads = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    val threads: StateFlow<Map<String, List<ChatMessage>>> = _threads.asStateFlow()

    fun messagesFor(alertId: String): List<ChatMessage> =
        _threads.value[alertId] ?: emptyList()

    fun send(alertId: String, senderRole: String, text: String? = null, photoUri: String? = null) {
        val message = ChatMessage(
            id = System.currentTimeMillis().toString(),
            alertId = alertId,
            senderRole = senderRole,
            text = text,
            photoUri = photoUri
        )
        _threads.update { current ->
            current + (alertId to (current[alertId].orEmpty() + message))
        }
    }
}