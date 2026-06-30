package com.resq254.app.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

object AlertRepository {

    private val db = FirebaseFirestore.getInstance()

    fun listenToAlerts(uid: String): ListenerRegistration {

        return db.collection("alerts")
            .whereEqualTo("recipient_uid", uid)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    Log.e("ALERTS", "Error: ${error.message}")
                    return@addSnapshotListener
                }

                val alerts = snapshot?.documents?.map { it.data }

                Log.d("ALERTS", alerts.toString())
            }
    }

    fun respondToAlert(alertId: String) {
        db.collection("alerts")
            .document(alertId)
            .update(
                mapOf(
                    "status" to "responded"
                )
            )
    }
}
