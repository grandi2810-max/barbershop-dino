package com.example.barbershopdino.model

enum class UserRole {
    NONE, CLIENT, BARBER
}

enum class AppLanguage {
    IT, EN
}

enum class AppThemeMode {
    LIGHT, DARK
}

data class BarberProfile(
    val name: String = "",
    val price: Double = 20.0,
    val durationMinutes: Int = 30,
    val accessCode: String = "1234",
)

data class ClientProfile(
    val firstName: String = "",
    val lastName: String = "",
    val age: Int = 25,
    val birthDay: Int = 15,
    val birthMonth: Int = 1
)

enum class AppointmentStatus {
    CONFIRMED,
    POSTPONED_30_MIN,
    CANCELLED,
    COMPLETED
}

data class Appointment(
    val id: String,
    val dateIso: String, // format: "YYYY-MM-DD"
    val timeSlot: String, // e.g., "10:00"
    val clientId: String,
    val clientName: String,
    val clientAge: Int,
    val barberName: String,
    val price: Double,
    val durationMinutes: Int,
    val status: AppointmentStatus = AppointmentStatus.CONFIRMED,
    val cancelReason: String? = null,
    val isReviewed: Boolean = false
)

data class Review(
    val id: String,
    val appointmentId: String,
    val clientName: String,
    val rating: Float, // 0.5 to 5.0
    val comment: String,
    val dateIso: String,
    val photoUris: List<String> = emptyList()
)
