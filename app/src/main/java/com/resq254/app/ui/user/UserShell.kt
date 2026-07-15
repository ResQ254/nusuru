package com.resq254.app.ui.user

data class EmergencyAlert(
    val id: String,
    val title: String,
    val location: String,
    val responders: Int
)

data class ResqAlert(
    val id: String,
    val type: String,
    val status: String,
    val title: String,
    val location: String,
    val timestampMs: Long,
    val responders: Int,
    val description: String
)

data class HomeUiState(
    val alerts: List<EmergencyAlert> = emptyList(),
    val unreadCount: Int = 0
)

data class ResqFeedUiState(
    val feedSearch: String = "",
    val feedFilter: String = "all",
    val isLoading: Boolean = false
)

data class GuideUiState(
    val guideCat: String = "fire"
)