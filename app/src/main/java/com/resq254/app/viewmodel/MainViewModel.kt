package com.resq254.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.resq254.app.data.Alert
import com.resq254.app.data.AppData
import com.resq254.app.data.MyNotification
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppState(
    val alerts: List<Alert> = AppData.sampleAlerts,
    val isLoading: Boolean = false,
    val sosActive: Boolean = false,
    val sosId: String? = null,
    val seconds: Int = 0,
    val nearbyCount: Int = 0,
    val userLat: Double = -1.2921,
    val userLng: Double = 36.8219,
    val searchQuery: String = "",
    val filterType: String = "all",
    val guideCategory: String = "fire",
    val respondingTo: Set<String> = emptySet(),
    val notifications: List<MyNotification> = listOf(
        MyNotification(1, "fire",     "Structure fire nearby",  "Tom Mboya St, 0.8 km",    "3 min ago",  false),
        MyNotification(2, "medical",  "SOS in your area",       "Near Kenyatta Ave",        "9 min ago",  false),
        MyNotification(3, "security", "Robbery updated",        "Moi Ave, police coming",   "16 min ago", true),
        MyNotification(4, "fire",     "Vehicle fire resolved",  "Ronald Ngala St",          "34 min ago", true)
    )
)

class MainViewModel : ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var nearbyJob: Job? = null

    fun startSOS() {
        if (_state.value.sosActive) return
        _state.update { it.copy(sosActive = true, seconds = 0, nearbyCount = 0) }
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _state.update { it.copy(seconds = it.seconds + 1) }
            }
        }
        nearbyJob = viewModelScope.launch {
            var count = 0
            while (count < 60) {
                delay(900)
                count += (1..4).random()
                _state.update { it.copy(nearbyCount = minOf(count, 60)) }
            }
        }
    }

    fun cancelSOS() {
        timerJob?.cancel()
        nearbyJob?.cancel()
        _state.update { it.copy(sosActive = false, sosId = null, seconds = 0, nearbyCount = 0) }
    }

    fun setFilter(f: String) = _state.update { it.copy(filterType = f) }
    fun setSearch(q: String) = _state.update { it.copy(searchQuery = q) }
    fun setGuide(cat: String) = _state.update { it.copy(guideCategory = cat) }

    fun getFilteredAlerts(): List<Alert> {
        val s = _state.value
        return s.alerts.filter { alert ->
            val matchesType = s.filterType == "all" || alert.type == s.filterType
            val matchesSearch = s.searchQuery.isEmpty() ||
                    alert.title.contains(s.searchQuery, ignoreCase = true) ||
                    alert.location.contains(s.searchQuery, ignoreCase = true)
            matchesType && matchesSearch
        }
    }

    fun toggleResponding(alertId: String) {
        val current = _state.value.respondingTo.toMutableSet()
        if (alertId in current) current.remove(alertId) else current.add(alertId)
        _state.update { it.copy(respondingTo = current) }
    }

    fun markRead(id: Int) {
        _state.update { s ->
            s.copy(notifications = s.notifications.map {
                if (it.id == id) it.copy(read = true) else it
            })
        }
    }

    fun markAllRead() {
        _state.update { it.copy(notifications = it.notifications.map { n -> n.copy(read = true) }) }
    }

    fun formatTime(secs: Int) = "%02d:%02d".format(secs / 60, secs % 60)
}