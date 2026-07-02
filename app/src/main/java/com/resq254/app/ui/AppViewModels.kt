package com.resq254.app.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.resq254.app.data.EmergencyRepository
import com.resq254.app.data.Incident
import com.resq254.app.data.LocationProvider
import com.resq254.app.ui.user.Alert
import com.resq254.app.ui.user.ResqAlert
import com.resq254.app.ui.user.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Maps a Firestore [Incident] type onto the UI palette categories used across screens. */
private fun uiType(incidentType: String): String = when (incidentType.lowercase()) {
    "fire" -> "fire"
    "medical" -> "medical"
    "crime", "security", "robbery" -> "security"
    else -> "other"
}

private fun titleFor(incidentType: String): String = when (incidentType.lowercase()) {
    "fire" -> "Fire Reported"
    "medical" -> "Medical Emergency"
    "crime" -> "Crime Reported"
    "sos" -> "SOS Broadcast"
    else -> incidentType.replaceFirstChar { it.uppercase() }
}

private fun Incident.toResqAlert(): ResqAlert = ResqAlert(
    id = id,
    title = titleFor(type),
    type = uiType(type),
    status = status,
    location = address,
    responders = respondersCount,
    timestampMs = reportedAtMs
)

private fun Incident.toAlert(): Alert = Alert(
    id = id,
    title = titleFor(type),
    type = uiType(type),
    status = status,
    location = address,
    description = "Reported incident of type '$type'. Status: $status.",
    timestampMs = reportedAtMs,
    responders = respondersCount
)

/**
 * Streams live incidents from Firestore and exposes them to the feed, notifications and
 * alert-detail screens. Replaces the previous hardcoded mock lists in MainActivity.
 */
class FeedViewModel : ViewModel() {

    /** Single live stream of incidents; collect this in the UI to keep it active. */
    val incidents: StateFlow<List<Incident>> = EmergencyRepository.observeIncidents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Maps incidents to the feed's UI model. */
    fun asAlerts(list: List<Incident>): List<ResqAlert> = list.map { it.toResqAlert() }

    /** Looks up a single incident by id for the detail screen; null while loading or if missing. */
    fun findAlert(list: List<Incident>, id: String): Alert? =
        list.firstOrNull { it.id == id }?.toAlert()
}

/**
 * Lifecycle-aware holder for the SOS broadcast: resolves the real device location, creates the
 * incident once, and drives the elapsed timer. Survives recomposition/configuration changes.
 */
class EmergencyViewModel : ViewModel() {

    var uiState by mutableStateOf(UiState(respondersCount = 0, elapsedSeconds = 0))
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var started = false

    fun startBroadcast(context: Context, type: String = "sos") {
        if (started) return
        started = true
        viewModelScope.launch {
            val location = LocationProvider.currentLocation(context)
            uiState = uiState.copy(userLat = location.latitude, userLng = location.longitude)
            EmergencyRepository.triggerEmergency(
                type = type,
                location = location,
                onError = { message -> errorMessage = message }
            )
            while (true) {
                delay(1_000)
                uiState = uiState.copy(elapsedSeconds = uiState.elapsedSeconds + 1)
            }
        }
    }
}
