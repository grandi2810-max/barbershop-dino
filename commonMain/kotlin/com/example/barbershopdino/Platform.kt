package com.example.barbershopdino

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun getCurrentDateIso(): String

expect fun openGoogleCalendarSync(dateIso: String, timeSlot: String, barberName: String, price: Double)
