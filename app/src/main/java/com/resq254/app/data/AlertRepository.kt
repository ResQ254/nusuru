package com.resq254.app.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class Alert(
    val id: String,
    val incidentId: String,
    val alertType: String,
    val status: String,
    val sentAtMs: Long
)

/**
 * Reads and updates alerts for the signed-in service provider. Alerts are created
 * server-side by the onIncidentCreated Cloud Function, always with `sent_at` as a
 * Firestore serverTimestamp — so this reads it defensively, same pattern as
 * EmergencyRepository.observeIncidents().
 */
object AlertRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private const val TAG = "AlertRepository"

    /** Streams alerts addressed to the signed-in provider, newest first. */
    fun observeMyAlerts(): Flow<List<Alert>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val registration = db.collection("alerts")
            .whereEqualTo("recipient_uid", uid)
            .orderBy("sent_at", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Alert stream error", error)
                    return@addSnapshotListener
                }
                val alerts = snapshot?.documents.orEmpty().map { doc ->
                    val sentAtMs = when (val raw = doc.get("sent_at")) {
                        is Long -> raw
                        is Number -> raw.toLong()
                        is com.google.firebase.Timestamp -> raw.toDate().time
                        else -> 0L
                    }
                    Alert(
                        id = doc.id,
                        incidentId = doc.getString("incident_id") ?: "",
                        alertType = doc.getString("alert_type") ?: "provider",
                        status = doc.getString("status") ?: "sent",
                        sentAtMs = sentAtMs
                    )
                }
                trySend(alerts)
            }
        awaitClose { registration.remove() }
    }

    /** Marks an alert acknowledged/en-route/resolved so dispatch can see response status. */
    suspend fun updateAlertStatus(alertId: String, status: String): Result<Unit> {
        return try {
            db.collection("alerts").document(alertId).update("status", status).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update alert status", e)
            Result.failure(e)
        }
    }
}