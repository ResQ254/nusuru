
package com.resq254.app.data

import androidx.compose.ui.graphics.Color
import com.resq254.app.ui.theme.RedSOS
import com.resq254.app.ui.theme.OrangeAlert
import com.resq254.app.ui.theme.PurplePolice
import com.resq254.app.ui.theme.BlueSky
import com.resq254.app.ui.theme.SafeGreen

// TODO(backend): purely presentational — safe to leave as-is even after
// the real data layer is wired in, since these just map type/status strings to colors.
fun getTypeColor(type: String): Color = when (type) {
    "fire" -> OrangeAlert
    "medical" -> RedSOS
    "security" -> PurplePolice
    "flood" -> BlueSky
    else -> BlueSky
}

fun getStatusColor(status: String): Color = when (status) {
    "active" -> RedSOS
    "resolved" -> SafeGreen
    else -> SafeGreen
}