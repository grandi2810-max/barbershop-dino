package com.example.barbershopdino

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.CalendarContract
import java.util.Calendar

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun getCurrentDateIso(): String {
    return try {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        sdf.format(java.util.Date())
    } catch (_: Exception) {
        "2025-05-15"
    }
}

var androidAppContext: Context? = null

actual fun openGoogleCalendarSync(dateIso: String, timeSlot: String, barberName: String, price: Double) {
    try {
        val ctx = androidAppContext ?: return
        val parts = dateIso.split("-")
        val year = parts.getOrNull(0)?.toIntOrNull() ?: 2025
        val month = (parts.getOrNull(1)?.toIntOrNull() ?: 5) - 1
        val day = parts.getOrNull(2)?.toIntOrNull() ?: 15

        val timeParts = timeSlot.split(":")
        val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 16
        val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0

        val startCal = Calendar.getInstance().apply {
            set(year, month, day, hour, minute, 0)
        }
        val endCal = Calendar.getInstance().apply {
            set(year, month, day, hour + 1, minute, 0)
        }

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startCal.timeInMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endCal.timeInMillis)
            putExtra(CalendarContract.Events.TITLE, "Taglio Capelli - $barberName")
            putExtra(CalendarContract.Events.DESCRIPTION, "Prenotazione BarberShop Dino (€$price)")
            putExtra(CalendarContract.Events.EVENT_LOCATION, "BarberShop Dino")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        ctx.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
