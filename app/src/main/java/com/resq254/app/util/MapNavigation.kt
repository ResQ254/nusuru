package com.resq254.app.util

import android.content.Context
import android.content.Intent
import android.net.Uri

fun openMapsNavigation(context: Context, latitude: Double, longitude: Double, label: String) {
    val gmmIntentUri = Uri.parse("google.navigation:q=$latitude,$longitude")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
        setPackage("com.google.android.apps.maps")
    }
    if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
    } else {
        val fallbackUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(${Uri.encode(label)})")
        context.startActivity(Intent(Intent.ACTION_VIEW, fallbackUri))
    }
}