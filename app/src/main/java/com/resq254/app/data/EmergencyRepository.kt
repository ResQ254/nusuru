package com.resq254.app.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class EmergencyRepository {

    private val db = FirebaseFirestore.getInstance()

    fun triggerEmergency(type: String) {

        val data = hashMapOf(
            "type" to type,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("emergencies")
            .add(data)
            .addOnSuccessListener {
                Log.d("EMERGENCY", "✅ Emergency saved")
            }
            .addOnFailureListener { e ->
                Log.e("EMERGENCY", "❌ Failed", e)
            }
    }
}