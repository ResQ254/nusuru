package com.resq254.app.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await

/**
 * Thin wrapper around FusedLocationProviderClient that returns the current device
 * location as a Firestore [GeoPoint], or null when permission is missing / location
 * is unavailable. Nairobi CBD is used only as a last-resort fallback.
 */
object LocationProvider {

    private val fallback = GeoPoint(-1.2921, 36.8219)

    fun hasPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    /**
     * Returns the current location, falling back to the last known location and then
     * to Nairobi CBD if nothing is available. Must be called only after checking
     * [hasPermission]; suppresses SecurityException defensively.
     */
    suspend fun currentLocation(context: Context): GeoPoint {
        if (!hasPermission(context)) return fallback
        return try {
            val client = LocationServices.getFusedLocationProviderClient(context)
            val location = client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
                ?: client.lastLocation.await()
            location?.let { GeoPoint(it.latitude, it.longitude) } ?: fallback
        } catch (e: SecurityException) {
            fallback
        }
    }
}
