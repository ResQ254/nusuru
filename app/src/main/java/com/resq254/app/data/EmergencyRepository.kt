package com.resq254.app.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Plain domain model for an incident read back from Firestore, decoupled from any UI type.
 */
data class Incident(
    val id: String,
    val type: String,
    val status: String,
    val address: String,
    val reportedAtMs: Long,
    val respondersCount: Int
)

/**
 * Single source of truth for incident creation and live incident reads.
 * Responder fan-out (alert creation) is handled server-side by the
 * `onIncidentCreated` Cloud Function so it still runs even if the
 * reporting device loses connectivity right after submitting.
 */
object EmergencyRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private const val TAG = "EmergencyRepository"

    /**
     * Creates an incident at the given [location]. Alert fan-out to nearby providers
     * is triggered automatically by the onIncidentCreated Cloud Function.
     *
     * @param location the reporter's real device location (see [LocationProvider]).
     * @param onSuccess invoked with the new incident id once it is created.
     * @param onError invoked with a human-readable message if creation fails.
     */
    fun triggerEmergency(
        type: String,
        location: GeoPoint,
        addressDescription: String = "",
        onSuccess: (incidentId: String) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val data = hashMapOf(
            "incident_type" to type,
            "reported_by_uid" to (auth.currentUser?.uid ?: "anonymous"),
            "status" to "active",
            "address_description" to addressDescription,
            "location" to location,
            "responders_count" to 0,
            "reported_at" to System.currentTimeMillis()
        )

        db.collection("incidents")
            .add(data)
            .addOnSuccessListener { doc ->
                Log.d(TAG, "Incident created: ${doc.id}")
                onSuccess(doc.id)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Incident creation failed", e)
                onError(e.message ?: "Could not report the emergency")
            }
    }

    /**
     * Streams active incidents ordered by most recent, updating in real time via a snapshot listener.
     * The listener is removed automatically when the collecting coroutine is cancelled.
     */
    fun observeIncidents(): Flow<List<Incident>> = callbackFlow {
        val registration = db.collection("incidents")
            .orderBy("reported_at", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Incident stream error", error)
                    return@addSnapshotListener
                }
                val incidents = snapshot?.documents.orEmpty().map { doc ->
                    val reportedAtMs = when (val raw = doc.get("reported_at")) {
                        is Long -> raw
                        is Number -> raw.toLong()
                        is com.google.firebase.Timestamp -> raw.toDate().time
                        else -> {
                            Log.w(TAG, "Doc ${doc.id} has invalid reported_at: $raw")
                            0L
                        }
                    }
                    Incident(
                        id = doc.id,
                        type = doc.getString("incident_type") ?: "other",
                        status = doc.getString("status") ?: "active",
                        address = doc.getString("address_description") ?: "Unknown location",
                        reportedAtMs = reportedAtMs,
                        respondersCount = (doc.getLong("responders_count") ?: 0L).toInt()
                    )
                }
                trySend(incidents)
            }
        awaitClose { registration.remove() }
    }
}