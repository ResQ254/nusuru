package com.resq254.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.resq254.app.data.Alert
import com.resq254.app.data.AlertRepository
import com.resq254.app.data.NotificationItem
import com.resq254.app.data.SosRepository
import com.resq254.app.data.StaticData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    // SOS
    val sosActive       : Boolean = false,
    val sosId           : String? = null,
    val elapsedSeconds  : Int     = 0,
    val respondersCount : Int     = 0,
    val userLat         : Double  = -1.2921,
    val userLng         : Double  = 36.8219,

    // Alerts (from Firestore)
    val alerts          : List<Alert> = emptyList(),
    val alertsLoading   : Boolean     = true,
    val feedFilter      : String      = "all",
    val feedSearch      : String      = "",

    // Alert detail
    val respondingAlerts: Set<String> = emptySet(),

    // Guide
    val guideCat        : String  = "fire",

    // Notifications (local for now)
    val notifications   : List<NotificationItem> = listOf(
        NotificationItem(1, "fire",     "Structure fire nearby",      "Tom Mboya St · 0.8 km away",      3,  false),
        NotificationItem(2, "medical",  "SOS broadcast in your area", "User near Kenyatta Ave",           9,  false),
        NotificationItem(3, "security", "Robbery alert updated",      "Moi Ave · Police en route",        16, true),
        NotificationItem(4, "fire",     "Vehicle fire resolved",      "Ronald Ngala St · Contained",      34, true),
    ),
)

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val alertRepo = AlertRepository()
    private val sosRepo   = SosRepository(app.applicationContext)

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private var timerJob    : Job? = null
    private var responderJob: Job? = null

    init {
        // Start listening to Firestore alerts immediately
        viewModelScope.launch {
            alertRepo.alertsFlow().collect { list ->
                _state.update { it.copy(alerts = list, alertsLoading = false) }
            }
        }
    }

    // ── SOS ───────────────────────────────────────────────────
    fun startSOS() {
        if (_state.value.sosActive) return
        _state.update { it.copy(sosActive = true, elapsedSeconds = 0, respondersCount = 0) }

        viewModelScope.launch {
            // Fetch GPS location, write SOS doc to Firestore
            val loc   = sosRepo.getLastLocation()
            val sosId = sosRepo.broadcastSOS()
            _state.update {
                it.copy(
                    sosId   = sosId,
                    userLat = loc?.first  ?: it.userLat,
                    userLng = loc?.second ?: it.userLng,
                )
            }
        }

        // Elapsed timer
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1_000)
                _state.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
        // Simulate growing responder count (replace with Firestore listener if you store responders)
        responderJob = viewModelScope.launch {
            var n = 0
            while (n < 68) {
                delay(850)
                n += (1..4).random()
                _state.update { it.copy(respondersCount = minOf(n, 68)) }
            }
        }
    }

    fun cancelSOS() {
        timerJob?.cancel()
        responderJob?.cancel()
        viewModelScope.launch {
            _state.value.sosId?.let { sosRepo.cancelSOS(it) }
        }
        _state.update { it.copy(sosActive = false, sosId = null, elapsedSeconds = 0, respondersCount = 0) }
    }

    // ── Feed ──────────────────────────────────────────────────
    fun setFeedFilter(f: String) = _state.update { it.copy(feedFilter = f) }
    fun setFeedSearch(q: String) = _state.update { it.copy(feedSearch = q) }

    fun filteredAlerts(): List<Alert> {
        val s = _state.value
        return s.alerts.filter { a ->
            (s.feedFilter == "all" || a.type == s.feedFilter) &&
            (s.feedSearch.isBlank() || a.title.contains(s.feedSearch, ignoreCase = true) ||
             a.location.contains(s.feedSearch, ignoreCase = true))
        }
    }

    // ── Alert responding ──────────────────────────────────────
    fun toggleResponding(alertId: String) {
        val set = _state.value.respondingAlerts.toMutableSet()
        if (alertId in set) {
            set.remove(alertId)
        } else {
            set.add(alertId)
            viewModelScope.launch { alertRepo.markResponding(alertId) }
        }
        _state.update { it.copy(respondingAlerts = set) }
    }

    // ── Guide ─────────────────────────────────────────────────
    fun setGuideCategory(cat: String) = _state.update { it.copy(guideCat = cat) }

    // ── Notifications ─────────────────────────────────────────
    fun markNotificationRead(id: Int) {
        _state.update { s ->
            s.copy(notifications = s.notifications.map { if (it.id == id) it.copy(read = true) else it })
        }
    }
    fun markAllRead() {
        _state.update { it.copy(notifications = it.notifications.map { n -> n.copy(read = true) }) }
    }

    // ── Helpers ───────────────────────────────────────────────
    fun formatElapsed(secs: Int) = "%02d:%02d".format(secs / 60, secs % 60)
}
