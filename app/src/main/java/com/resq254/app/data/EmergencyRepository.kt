package com.resq254.app.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint

class EmergencyRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun triggerEmergency(type: String) {

        val location = GeoPoint(-1.2921, 36.8219) // TEMP

        val data = hashMapOf(
            "incident_type" to type,
            "reported_by_uid" to (auth.currentUser?.uid ?: "test_user"),
            "status" to "active",
            "address_description" to "Nairobi CBD",
            "location" to location,
            "reported_at" to System.currentTimeMillis()
        )

        db.collection("incidents")
            .add(data)
            .addOnSuccessListener { doc ->

                Log.d("INCIDENT", "✅ Created: ${doc.id}")

                // create alerts after incident
                createAlerts(doc.id, location)
            }
            .addOnFailureListener { e ->
                Log.e("INCIDENT", "❌ Failed", e)
            }
    }

    private fun createAlerts(incidentId: String, location: GeoPoint) {

        db.collection("service_providers")
            .whereEqualTo("is_active", true)
            .get()
            .addOnSuccessListener { result ->

                for (doc in result) {

                    val providerLocation = doc.getGeoPoint("location") ?: continue
                    val radius = doc.getDouble("radius_km") ?: 5.0

                    val distance = calculateDistance(location, providerLocation)

                    if (distance <= radius) {

                        val alert = hashMapOf(
                            "incident_id" to incidentId,
                            "recipient_uid" to doc.id,
                            "alert_type" to "provider",
                            "status" to "sent",
                            "sent_at" to System.currentTimeMillis()
                        )

                        db.collection("alerts")
                            .add(alert)
                            .addOnSuccessListener {
                                Log.d("ALERT", "✅ Sent to ${doc.id}")
                            }
                            .addOnFailureListener {
                                Log.e("ALERT", "❌ Failed")
                            }
                    }
                }
            }
    }

    private fun calculateDistance(a: GeoPoint, b: GeoPoint): Double {

        val R = 6371.0

        val dLat = Math.toRadians(b.latitude - a.latitude)
        val dLng = Math.toRadians(b.longitude - a.longitude)

        val lat1 = Math.toRadians(a.latitude)
        val lat2 = Math.toRadians(b.latitude)

        val aVal = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLng / 2) * Math.sin(dLng / 2) *
                Math.cos(lat1) * Math.cos(lat2)

        val c = 2 * Math.atan2(Math.sqrt(aVal), Math.sqrt(1 - aVal))

        return R * c
    }
}
