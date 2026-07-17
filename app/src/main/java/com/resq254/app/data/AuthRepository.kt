package com.resq254.app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Wraps Firebase Auth + the "users" (and "service_providers") Firestore
 * collections. This is the single place that talks to Firebase Auth --
 * screens and ViewModels never call FirebaseAuth directly.
 */
class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    val currentUser: FirebaseUser? get() = auth.currentUser

    /** Signs in with email/password. */
    suspend fun signIn(email: String, password: String): Result<FirebaseUser> = try {
        val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
        val user = result.user ?: return Result.failure(IllegalStateException("Sign-in failed"))
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Creates a Firebase Auth account, then writes the matching profile
     * document to Firestore. role is "resident" or "provider".
     * serviceType/licenseNumber are only used when role == "provider".
     */
    suspend fun signUp(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        role: String,
        serviceType: String = "",
        licenseNumber: String = ""
    ): Result<FirebaseUser> = try {
        val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        val user = result.user ?: return Result.failure(IllegalStateException("Account creation failed"))

        val profile = hashMapOf(
            "uid" to user.uid,
            "full_name" to fullName,
            "phone_number" to phone,
            "email" to email.trim(),
            "role" to role,
            "is_guardian" to false,
            "is_verified" to false,
            "created_at" to FieldValue.serverTimestamp()
        )
        db.collection("users").document(user.uid).set(profile).await()

        if (role == "provider") {
            val providerDoc = hashMapOf(
                "name" to fullName,
                "type" to serviceType.ifBlank { "ambulance" },
                "phone_number" to phone,
                "license_number" to licenseNumber,
                "owner_uid" to user.uid,
                "is_active" to true,
                "location" to null,
                "address" to "",
                "radius_km" to 5.0
            )
            // NOTE: requires the updated firestore.rules that lets a provider
            // create their own service_providers doc (owner_uid == auth uid).
            db.collection("service_providers").document(user.uid).set(providerDoc).await()
        }

        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> = try {
        auth.sendPasswordResetEmail(email.trim()).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /** Fetches the signed-in user's role ("resident" | "provider" | "admin"), or null if no profile yet. */
    suspend fun getCurrentUserRole(): String? {
        val uid = currentUser?.uid ?: return null
        return try {
            db.collection("users").document(uid).get().await()
                .toObject(UserProfile::class.java)?.role
        } catch (e: Exception) {
            null
        }
    }

    fun signOut() = auth.signOut()
}
