package com.resq254.app.data

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * All Firestore reads/writes in one place -- the Kotlin equivalent of
 * firestore/db.js, wired to the same collections: users, incidents,
 * alerts, emergency_contacts, service_providers.
 */
class FirestoreRepository {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // ── USERS ──────────────────────────────────────────────────────────

    suspend fun getUser(uid: String): UserProfile? =
        db.collection("users").document(uid).get().await().toObject(UserProfile::class.java)

    suspend fun updateUserLocation(uid: String, lat: Double, lng: Double) {
        db.collection("users").document(uid).update("location", GeoPoint(lat, lng)).await()
    }

    // ── INCIDENTS ──────────────────────────────────────────────────────

    /** Real-time stream of active incidents, newest first. */
    fun observeActiveIncidents(): Flow<List<IncidentDoc>> = callbackFlow {
        val registration = db.collection("incidents")
            .whereEqualTo("status", "active")
            .orderBy("reported_at", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val docs = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(IncidentDoc::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(docs)
            }
        awaitClose { registration.remove() }
    }

    suspend fun reportIncident(
        reportedByUid: String,
        incidentType: String,
        lat: Double,
        lng: Double,
        addressDescription: String,
        notes: String = ""
    ): String {
        val data = hashMapOf(
            "reported_by_uid" to reportedByUid,
            "incident_type" to incidentType,
            "location" to GeoPoint(lat, lng),
            "address_description" to addressDescription,
            "status" to "active",
            "reported_at" to FieldValue.serverTimestamp(),
            "resolved_at" to null,
            "notes" to notes
        )
        val ref = db.collection("incidents").add(data).await()
        return ref.id
    }

    suspend fun updateIncidentStatus(incidentId: String, status: String) {
        val updates = mutableMapOf<String, Any?>("status" to status)
        if (status == "resolved") updates["resolved_at"] = FieldValue.serverTimestamp()
        db.collection("incidents").document(incidentId).update(updates).await()
    }

    // ── EMERGENCY CONTACTS ────────────────────────────────────────────

    suspend fun getEmergencyContacts(category: String? = null): List<EmergencyContactDoc> {
        var query: Query = db.collection("emergency_contacts")
            .whereEqualTo("is_verified", true)
            .orderBy("rating", Query.Direction.DESCENDING)
        if (category != null) {
            query = db.collection("emergency_contacts")
                .whereEqualTo("is_verified", true)
                .whereEqualTo("category", category)
                .orderBy("rating", Query.Direction.DESCENDING)
        }
        return query.get().await().documents.mapNotNull { doc ->
            doc.toObject(EmergencyContactDoc::class.java)?.copy(id = doc.id)
        }
    }

    // ── SERVICE PROVIDERS ──────────────────────────────────────────────

    suspend fun getServiceProviders(type: String? = null): List<ServiceProviderDoc> {
        var query: Query = db.collection("service_providers").whereEqualTo("is_active", true)
        if (type != null) {
            query = db.collection("service_providers")
                .whereEqualTo("is_active", true)
                .whereEqualTo("type", type)
        }
        return query.get().await().documents.mapNotNull { doc ->
            doc.toObject(ServiceProviderDoc::class.java)?.copy(id = doc.id)
        }
    }

    /** Nearest hospitals to a point, closest first, mapped to the UI's Hospital model. */
    suspend fun getNearbyHospitals(lat: Double, lng: Double, maxResults: Int = 6): List<Hospital> {
        return getServiceProviders("hospital")
            .filter { it.location != null }
            .sortedBy { haversineKm(lat, lng, it.location!!.latitude, it.location.longitude) }
            .take(maxResults)
            .map { it.toHospital(lat, lng) }
    }
}
