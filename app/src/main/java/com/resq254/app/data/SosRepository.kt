package com.resq254.app.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class SosRepository(private val context: Context) {

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val fusedLocation = LocationServices.getFusedLocationProviderClient(context)

    // ── Broadcast SOS to Firestore ────────────────────────────
    // Creates a document in "sos" collection.
    // Your Firestore rules + Cloud Functions can fan-out FCM
    // to nearby users from there.
    suspend fun broadcastSOS(): String? {
        val loc = getLastLocation()
        val uid = auth.currentUser?.uid ?: "anonymous"

        val event = hashMapOf(
            "userId"      to uid,
            "lat"         to (loc?.first ?: -1.2921),
            "lng"         to (loc?.second ?: 36.8219),
            "active"      to true,
            "timestampMs" to System.currentTimeMillis(),
        )

        return try {
            val ref = db.collection("sos").add(event).await()
            ref.id
        } catch (e: Exception) {
            null
        }
    }

    // ── Cancel an active SOS ──────────────────────────────────
    suspend fun cancelSOS(sosId: String) {
        try {
            db.collection("sos").document(sosId)
                .update("active", false)
                .await()
        } catch (_: Exception) {}
    }

    // ── Fetch last known GPS location ─────────────────────────
    suspend fun getLastLocation(): Pair<Double, Double>? {
        val hasFine   = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!hasFine && !hasCoarse) return null

        return suspendCancellableCoroutine { cont ->
            fusedLocation.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { loc ->
                    cont.resume(if (loc != null) Pair(loc.latitude, loc.longitude) else null)
                }
                .addOnFailureListener { cont.resume(null) }
        }
    }
}
