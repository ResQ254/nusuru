package com.resq254.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.resq254.app.data.ActiveJob
import com.resq254.app.data.Alert
import com.resq254.app.data.AppData
import com.resq254.app.data.CompletedJob
import com.resq254.app.data.SpJobStatus
import com.resq254.app.data.SpProfile
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SpState(
    val isOnline: Boolean = true,
    val incomingAlerts: List<Alert> = AppData.sampleAlerts.filter { it.status == "active" },
    val ringingAlertId: String? = null,
    val activeJob: ActiveJob? = null,
    val elapsedSeconds: Int = 0,
    val dutyLog: List<CompletedJob> = emptyList(),
    val profile: SpProfile = SpProfile(
        name = "James Otieno",
        badgeId = "RESQ-4471",
        role = "Paramedic",
        completedJobs = 128,
        rating = 4.8
    )
)

class SpViewModel : ViewModel() {

    private val _state = MutableStateFlow(SpState())
    val state: StateFlow<SpState> = _state.asStateFlow()

    private var timerJob: Job? = null

    init {
        maybeTriggerNextRing()
    }

    fun toggleOnline() {
        _state.update { it.copy(isOnline = !it.isOnline) }
        maybeTriggerNextRing()
    }

    private fun maybeTriggerNextRing() {
        _state.update { s ->
            if (s.isOnline && s.activeJob == null && s.ringingAlertId == null) {
                s.copy(ringingAlertId = s.incomingAlerts.firstOrNull()?.id)
            } else s
        }
    }

    fun declineRingingAlert() {
        val id = _state.value.ringingAlertId ?: return
        _state.update {
            it.copy(
                incomingAlerts = it.incomingAlerts.filterNot { a -> a.id == id },
                ringingAlertId = null
            )
        }
        maybeTriggerNextRing()
    }

    fun acceptRingingAlert() {
        val id = _state.value.ringingAlertId ?: return
        acceptAlert(id)
    }

    fun acceptAlert(alertId: String) {
        val current = _state.value
        val alert = current.incomingAlerts.find { it.id == alertId } ?: return

        _state.update {
            it.copy(
                incomingAlerts = it.incomingAlerts.filterNot { a -> a.id == alertId },
                ringingAlertId = if (it.ringingAlertId == alertId) null else it.ringingAlertId,
                activeJob = ActiveJob(alert = alert, status = SpJobStatus.EN_ROUTE, acceptedAtMs = System.currentTimeMillis()),
                elapsedSeconds = 0
            )
        }

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _state.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
    }

    fun advanceJobStatus() {
        val job = _state.value.activeJob ?: return
        val next = when (job.status) {
            SpJobStatus.EN_ROUTE -> SpJobStatus.ON_SCENE
            SpJobStatus.ON_SCENE -> SpJobStatus.RESOLVED
            SpJobStatus.RESOLVED -> return
        }
        _state.update { it.copy(activeJob = it.activeJob?.copy(status = next)) }
    }

    fun completeJob() {
        val job = _state.value.activeJob ?: return
        timerJob?.cancel()
        val logEntry = CompletedJob(
            alertTitle = job.alert.title,
            alertType = job.alert.type,
            location = job.alert.location,
            completedAtMs = System.currentTimeMillis(),
            durationSeconds = _state.value.elapsedSeconds
        )
        _state.update {
            it.copy(
                activeJob = null,
                elapsedSeconds = 0,
                dutyLog = listOf(logEntry) + it.dutyLog,
                profile = it.profile.copy(completedJobs = it.profile.completedJobs + 1)
            )
        }
        maybeTriggerNextRing()
    }

    fun cancelJob() {
        val job = _state.value.activeJob
        timerJob?.cancel()
        _state.update {
            it.copy(
                activeJob = null,
                elapsedSeconds = 0,
                incomingAlerts = if (job != null) listOf(job.alert) + it.incomingAlerts else it.incomingAlerts
            )
        }
        maybeTriggerNextRing()
    }

    fun formatTime(secs: Int) = "%02d:%02d".format(secs / 60, secs % 60)
}