package com.resq254.app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AlertRepository {

    private val db by lazy { FirebaseFirestore.getInstance() }

    // ── Real-time stream of alerts from Firestore ─────────────
    // Maps to your "alerts" collection, ordered by timestamp descending.
    // Each document must have fields matching the Alert data class.
    fun alertsFlow(): Flow<List<Alert>> = callbackFlow {
        val listener = db.collection("alerts")
            .orderBy("timestampMs", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Don't close — Firestore will retry automatically
                    return@addSnapshotListener
                }
                val alerts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject<Alert>()?.copy(id = doc.id)
                } ?: emptyList()
                trySend(alerts)
            }
        awaitClose { listener.remove() }
    }

    // ── Single fetch by id ────────────────────────────────────
    suspend fun getAlert(id: String): Alert? {
        return try {
            val doc = db.collection("alerts").document(id).get().await()
            doc.toObject<Alert>()?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    // ── Increment responders on an alert ─────────────────────
    suspend fun markResponding(alertId: String) {
        try {
            db.collection("alerts").document(alertId)
                .update("responders", com.google.firebase.firestore.FieldValue.increment(1))
                .await()
        } catch (_: Exception) {}
    }
}
