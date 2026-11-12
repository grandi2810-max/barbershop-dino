package com.example.barbershopdino

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun getCurrentDateIso(): String {
    return "2025-05-15"
}

actual fun openGoogleCalendarSync(dateIso: String, timeSlot: String, barberName: String, price: Double) {
    // iOS Calendar Sync fallback
}
