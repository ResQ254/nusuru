package com.resq254.app.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await

data class ServiceProvider(
    val id: String,
    val name: String,
    val type: String,
    val phoneNumber: String,
    val address: String,
    val location: GeoPoint?,
    val radiusKm: Double,
    val isActive: Boolean
)

/**
 * Handles provider registration and live status updates. Providers are stored
 * in `service-providers`, keyed by their auth uid so a re-registration updates
 * the existing doc instead of creating duplicates.
 */
object ProviderRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private const val TAG = "ProviderRepository"

    /** Registers (or re-registers) the signed-in user as a service provider. */
    suspend fun registerProvider(
        name: String,
        type: String,
        phoneNumber: String,
        address: String,
        location: GeoPoint,
        radiusKm: Double
    ): Result<Unit> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("Must be signed in to register as a provider"))

        val data = hashMapOf(
            "name" to name,
            "type" to type,
            "phone_number" to phoneNumber,
            "address" to address,
            "location" to location,
            "radius_km" to radiusKm,
            "is_active" to true,
            "last_updated" to System.currentTimeMillis()
        )

        return try {
            db.collection("service-providers").document(uid).set(data).await()
            Log.d(TAG, "Provider registered: $uid")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Provider registration failed", e)
            Result.failure(e)
        }
    }

    /** Toggles availability — inactive providers are skipped by the fan-out function. */
    suspend fun setActive(isActive: Boolean): Result<Unit> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("Not signed in"))

        return try {
            db.collection("service-providers").document(uid)
                .update(mapOf("is_active" to isActive, "last_updated" to System.currentTimeMillis()))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update active status", e)
            Result.failure(e)
        }
    }

    /** Updates a provider's live location (e.g. a moving ambulance). */
    suspend fun updateLocation(location: GeoPoint): Result<Unit> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("Not signed in"))

        return try {
            db.collection("service-providers").document(uid)
                .update(mapOf("location" to location, "last_updated" to System.currentTimeMillis()))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update location", e)
            Result.failure(e)
        }
    }
}