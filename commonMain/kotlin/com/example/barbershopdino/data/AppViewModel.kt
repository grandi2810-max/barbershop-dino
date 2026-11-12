package com.example.barbershopdino.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.barbershopdino.model.*

class AppViewModel : ViewModel() {

    var currentRole by mutableStateOf(UserRole.NONE)
    var currentLanguage by mutableStateOf(AppLanguage.IT)
    var currentThemeMode by mutableStateOf(AppThemeMode.DARK)

    var barberProfile by mutableStateOf(
        BarberProfile(name = "Dino Barber", price = 22.0, durationMinutes = 30)
    )

    var clientProfile by mutableStateOf(
        ClientProfile(firstName = "Marco", lastName = "Rossi", age = 28)
    )

    // Calendar state
    var selectedDateIso by mutableStateOf(todayIso())

    // List of appointments
    val appointments = mutableStateListOf<Appointment>()

    // List of reviews
    val reviews = mutableStateListOf<Review>()

    // Barber unavailability list
    val unavailableDates = mutableStateListOf<String>()

    // Notification states
    var showBarber30MinPrompt by mutableStateOf(false)
    var promptAppointment by mutableStateOf<Appointment?>(null)

    var showClientPostponedAlert by mutableStateOf(false)
    var clientAlertAppointment by mutableStateOf<Appointment?>(null)

    var showPriceUpdateClientNotification by mutableStateOf(false)
    var updatedPriceAmount by mutableStateOf(20.0)

    var showReviewDialog by mutableStateOf(false)
    var reviewAppointment by mutableStateOf<Appointment?>(null)

    init {
        // Pre-populate sample appointments & reviews for demonstration
        val today = todayIso() // "2025-05-15" (Orario Ordinario: 16:00 - 19:00)
        val tomorrow = dayOffsetIso(1) // "2025-05-16"
        val sundayHoliday = "2025-05-18" // (Orario Festivo: 14:00 - 17:00)

        appointments.addAll(
            listOf(
                Appointment(
                    id = "app_1",
                    dateIso = today,
                    timeSlot = "16:00",
                    clientId = "Marco_Rossi",
                    clientName = "Marco Rossi",
                    clientAge = 28,
                    barberName = "Dino Barber",
                    price = 22.0,
                    durationMinutes = 30,
                    status = AppointmentStatus.CONFIRMED
                ),
                Appointment(
                    id = "app_2",
                    dateIso = today,
                    timeSlot = "17:00",
                    clientId = "Luca_Bianchi",
                    clientName = "Luca Bianchi",
                    clientAge = 34,
                    barberName = "Dino Barber",
                    price = 22.0,
                    durationMinutes = 30,
                    status = AppointmentStatus.CONFIRMED
                ),
                Appointment(
                    id = "app_3",
                    dateIso = tomorrow,
                    timeSlot = "16:30",
                    clientId = "Andrea_Verdi",
                    clientName = "Andrea Verdi",
                    clientAge = 22,
                    barberName = "Dino Barber",
                    price = 22.0,
                    durationMinutes = 30,
                    status = AppointmentStatus.CONFIRMED
                ),
                Appointment(
                    id = "app_4",
                    dateIso = sundayHoliday,
                    timeSlot = "14:30",
                    clientId = "Giuseppe_Neri",
                    clientName = "Giuseppe Neri",
                    clientAge = 40,
                    barberName = "Dino Barber",
                    price = 22.0,
                    durationMinutes = 30,
                    status = AppointmentStatus.CONFIRMED
                )
            )
        )

        reviews.addAll(
            listOf(
                Review(
                    id = "rev_1",
                    appointmentId = "app_0",
                    clientName = "Giovanni M.",
                    rating = 5.0f,
                    comment = "Taglio perfetto! Dino è un vero professionista.",
                    dateIso = dayOffsetIso(-2)
                ),
                Review(
                    id = "rev_2",
                    appointmentId = "app_00",
                    clientName = "Alessandro P.",
                    rating = 4.5f,
                    comment = "Servizio veloce e pulito, consigliatissimo!",
                    dateIso = dayOffsetIso(-5)
                )
            )
        )
    }

    var isBarberAuthenticated by mutableStateOf(false)
    var showBarberCodeAuthDialog by mutableStateOf(false)
    var barberCodeError by mutableStateOf<String?>(null)

    fun setRole(role: UserRole) {
        if (role == UserRole.NONE) {
            isBarberAuthenticated = false
        }
        currentRole = role
    }

    fun toggleLanguage() {
        currentLanguage = if (currentLanguage == AppLanguage.IT) AppLanguage.EN else AppLanguage.IT
    }

    fun toggleTheme() {
        currentThemeMode = if (currentThemeMode == AppThemeMode.DARK) AppThemeMode.LIGHT else AppThemeMode.DARK
    }

    fun verifyBarberCode(enteredCode: String): Boolean {
        return if (barberProfile.accessCode.isBlank() || (enteredCode.trim() == barberProfile.accessCode)) {
            isBarberAuthenticated = true
            showBarberCodeAuthDialog = false
            barberCodeError = null
            currentRole = UserRole.BARBER
            true
        } else {
            barberCodeError = "barber_code_incorrect"
            false
        }
    }

    fun saveBarberProfile(name: String, price: Double, durationMinutes: Int, accessCode: String) {
        barberProfile = BarberProfile(
            name = name,
            price = price,
            durationMinutes = durationMinutes,
            accessCode = accessCode.ifBlank { "1234" }
        )
        isBarberAuthenticated = true

        // Update active client appointments price
        for (i in appointments.indices) {
            val app = appointments[i]
            if ((app.status == AppointmentStatus.CONFIRMED) || (app.status == AppointmentStatus.POSTPONED_30_MIN)) {
                appointments[i] = app.copy(price = price)
            }
        }
    }

    fun updateBarberPrice(newPrice: Double) {
        barberProfile = barberProfile.copy(price = newPrice)
        updatedPriceAmount = newPrice
        showPriceUpdateClientNotification = true

        // Update active client appointments price
        for (i in appointments.indices) {
            val app = appointments[i]
            if ((app.status == AppointmentStatus.CONFIRMED) || (app.status == AppointmentStatus.POSTPONED_30_MIN)) {
                appointments[i] = app.copy(price = newPrice)
            }
        }
    }

    fun toggleDayUnavailability(dateIso: String) {
        if (unavailableDates.contains(dateIso)) {
            unavailableDates.remove(dateIso)
        } else {
            unavailableDates.add(dateIso)
        }
    }

    fun saveClientProfile(firstName: String, lastName: String, age: Int, birthDay: Int = 15, birthMonth: Int = 1) {
        clientProfile = ClientProfile(
            firstName = firstName,
            lastName = lastName,
            age = age,
            birthDay = birthDay,
            birthMonth = birthMonth
        )
    }

    fun bookAppointment(dateIso: String, timeSlot: String): Boolean {
        if (unavailableDates.contains(dateIso)) return false

        // Check if slot is available
        val exists = appointments.any { (it.dateIso == dateIso) && (it.timeSlot == timeSlot) && (it.status != AppointmentStatus.CANCELLED) }
        if (exists) return false

        val newApp = Appointment(
            id = "app_${(1000..9999).random()}",
            dateIso = dateIso,
            timeSlot = timeSlot,
            clientId = "${clientProfile.firstName}_${clientProfile.lastName}",
            clientName = "${clientProfile.firstName} ${clientProfile.lastName}",
            clientAge = clientProfile.age,
            barberName = barberProfile.name.ifEmpty { "Dino Barber" },
            price = barberProfile.price,
            durationMinutes = barberProfile.durationMinutes,
            status = AppointmentStatus.CONFIRMED
        )
        appointments.add(newApp)
        return true
    }

    // Barber delays appointment by 30 mins
    fun barberRespondTo30MinCheck(appointmentId: String, needs30MinExtra: Boolean) {
        showBarber30MinPrompt = false
        promptAppointment = null

        if (needs30MinExtra) {
            val idx = appointments.indexOfFirst { it.id == appointmentId }
            if (idx != -1) {
                val app = appointments[idx]
                val updated = app.copy(status = AppointmentStatus.POSTPONED_30_MIN)
                appointments[idx] = updated

                // Trigger client alert
                clientAlertAppointment = updated
                showClientPostponedAlert = true
            }
        }
    }

    // Client response to delay: keep or cancel
    fun clientRespondToPostpone(appointmentId: String, cancel: Boolean, cancelReason: String? = null) {
        showClientPostponedAlert = false
        clientAlertAppointment = null

        val idx = appointments.indexOfFirst { it.id == appointmentId }
        if (idx != -1) {
            val app = appointments[idx]
            if (cancel) {
                appointments[idx] = app.copy(
                    status = AppointmentStatus.CANCELLED,
                    cancelReason = cancelReason?.ifBlank { null }
                )
            } else {
                // Kept appointment
                appointments[idx] = app.copy(status = AppointmentStatus.CONFIRMED)
            }
        }
    }

    fun markAppointmentCompleted(appointmentId: String) {
        val idx = appointments.indexOfFirst { it.id == appointmentId }
        if (idx != -1) {
            val app = appointments[idx]
            appointments[idx] = app.copy(status = AppointmentStatus.COMPLETED)
        }
    }

    fun addReview(appointmentId: String, rating: Float, comment: String, photoUris: List<String> = emptyList()) {
        val app = appointments.find { it.id == appointmentId }
        val newReview = Review(
            id = "rev_${(1000..9999).random()}",
            appointmentId = appointmentId,
            clientName = app?.clientName ?: "${clientProfile.firstName} ${clientProfile.lastName}",
            rating = rating.coerceIn(0.5f, 5.0f),
            comment = comment.trim(),
            dateIso = app?.dateIso ?: todayIso(),
            photoUris = photoUris
        )
        reviews.add(0, newReview)

        // Mark appointment as reviewed
        val idx = appointments.indexOfFirst { it.id == appointmentId }
        if (idx != -1) {
            appointments[idx] = appointments[idx].copy(isReviewed = true)
        }

        showReviewDialog = false
        reviewAppointment = null
    }

    fun triggerBarber30MinPromptForAppointment(app: Appointment) {
        promptAppointment = app
        showBarber30MinPrompt = true
    }

    fun triggerClientReviewDialog(app: Appointment) {
        reviewAppointment = app
        showReviewDialog = true
    }

    companion object {
        fun todayIso(): String {
            // Simple date generator for YYYY-MM-DD
            return "2025-05-15"
        }

        fun dayOffsetIso(offsetDays: Int): String {
            val dayNum = 15 + offsetDays
            val dayStr = if (dayNum < 10) "0$dayNum" else dayNum.toString()
            return "2025-05-$dayStr"
        }
    }
}
