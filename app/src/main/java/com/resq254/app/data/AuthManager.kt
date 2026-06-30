package com.resq254.app.data


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object AuthManager {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun signUp(email: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                val uid = auth.currentUser!!.uid

                val user = hashMapOf(
                    "uid" to uid,
                    "role" to "resident"
                )

                db.collection("users")
                    .document(uid)
                    .set(user)
            }
            .addOnFailureListener {
                println("Signup failed: ${it.message}")
            }
    }

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnFailureListener {
                println("Login failed: ${it.message}")
            }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun logout() {
        auth.signOut()
    }
}