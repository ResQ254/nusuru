package com.resq254.app.data


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object AuthManager {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    /**
     * Creates a Firebase Auth account and stores the user's profile in Firestore.
     *
     * @param profile extra profile fields (name, phone, role, etc.) saved under the user document.
     * @param onSuccess invoked after both the account and the profile document are created.
     * @param onError invoked with a human-readable message if any step fails.
     */
    fun signUp(
        email: String,
        password: String,
        profile: Map<String, Any?> = emptyMap(),
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.currentUser!!.uid

                val user = HashMap<String, Any?>(profile)
                user["uid"] = uid

                db.collection("users")
                    .document(uid)
                    .set(user)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "Failed to save profile")
                    }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Signup failed")
            }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "Login failed")
            }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun logout() {
        auth.signOut()
    }
}