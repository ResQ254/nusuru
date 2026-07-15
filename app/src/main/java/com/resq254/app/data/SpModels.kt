package com.resq254.app.data

enum class SpJobStatus {
    EN_ROUTE, ON_SCENE, RESOLVED
}

data class ActiveJob(
    val alert: Alert,
    val status: SpJobStatus,
    val acceptedAtMs: Long
)

data class CompletedJob(
    val alertTitle: String,
    val alertType: String,
    val location: String,
    val completedAtMs: Long,
    val durationSeconds: Int
)

data class SpProfile(
    val name: String,
    val badgeId: String,
    val role: String,
    val completedJobs: Int,
    val rating: Double
)