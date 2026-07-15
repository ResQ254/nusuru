
package com.resq254.app.data

// TODO(backend): replace with real model once API/DB layer is ready
data class MyNotification(
    val id: Int,
    val type: String,
    val title: String,
    val body: String,
    val time: String,
    val read: Boolean
)