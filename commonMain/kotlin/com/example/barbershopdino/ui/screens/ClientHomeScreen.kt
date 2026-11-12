package com.example.barbershopdino.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.barbershopdino.localization.Strings
import com.example.barbershopdino.model.AppLanguage
import com.example.barbershopdino.model.AppThemeMode
import com.example.barbershopdino.model.Appointment
import com.example.barbershopdino.model.AppointmentStatus
import com.example.barbershopdino.model.BarberProfile
import com.example.barbershopdino.model.ClientProfile
import com.example.barbershopdino.model.Review
import com.example.barbershopdino.openGoogleCalendarSync
import com.example.barbershopdino.util.DateUtils

@Composable
fun ClientHomeScreen(
    clientProfile: ClientProfile,
    barberProfile: BarberProfile,
    appointments: List<Appointment>,
    reviews: List<Review>,
    selectedDateIso: String,
    onSelectDate: (String) -> Unit,
    language: AppLanguage,
    themeMode: AppThemeMode,
    onToggleLanguage: () -> Unit,
    onToggleTheme: () -> Unit,
    onSwitchRole: () -> Unit,
    onBookSlot: (dateIso: String, timeSlot: String) -> Boolean,
    onOpenPostponedAlert: (Appointment) -> Unit,
    onOpenReviewDialog: (Appointment) -> Unit,
    unavailableDates: List<String> = emptyList()
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Booking, 1: My Appointments, 2: Reviews
    var reviewSubTab by remember { mutableStateOf(0) } // 0: Barber Reviews, 1: My Reviews
    var selectedTimeSlot by remember { mutableStateOf<String?>(null) }
    var bookingMessage by remember { mutableStateOf<String?>(null) }

    val myClientName = "${clientProfile.firstName} ${clientProfile.lastName}"
    val myClientId = "${clientProfile.firstName}_${clientProfile.lastName}"
    val myAppointments = appointments.filter { it.clientId == myClientId }
    val lastCompletedAppointment = myAppointments.firstOrNull { (it.status == AppointmentStatus.COMPLETED) && (!it.isReviewed) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "👤 ${clientProfile.firstName} ${clientProfile.lastName}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            val bDay = clientProfile.birthDay.toString().padStart(2, '0')
                            val bMonth = clientProfile.birthMonth.toString().padStart(2, '0')
                            Text(
                                text = "${Strings.get("age", language)}: ${clientProfile.age} " + (if (language == AppLanguage.IT) "anni" else "years old") + " | 🎈 $bDay/$bMonth",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilterChip(
                                selected = true,
                                onClick = onToggleLanguage,
                                label = { Text(if (language == AppLanguage.IT) "🇮🇹 IT" else "🇬🇧 EN") },
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            FilterChip(
                                selected = true,
                                onClick = onToggleTheme,
                                label = { Text(if (themeMode == AppThemeMode.DARK) "🌙" else "☀️") }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onSwitchRole) {
                            Text("🔄 " + Strings.get("switch_role", language))
                        }

                        // Barber Info pill
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text(
                                text = "✂️ ${barberProfile.name.ifEmpty { "Dino Barber" }} (€${barberProfile.price})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Navigation Tabs
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(Strings.get("client_dashboard", language), fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(Strings.get("my_appointments", language) + " (${myAppointments.size})", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text(Strings.get("reviews_title", language), fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
                )
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // BOOKING TAB
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = Strings.get("select_date", language) + " (${DateUtils.getMonthYearHeader(selectedDateIso, language)})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        // Date Selector Strip
                        item {
                            ClientDateSelectorStrip(
                                selectedDateIso = selectedDateIso,
                                onDateSelected = {
                                    onSelectDate(it)
                                    selectedTimeSlot = null
                                    bookingMessage = null
                                }
                            )
                        }

                        item {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            val isSunday = DateUtils.isSunday(selectedDateIso)
                            val isSummer = DateUtils.isSummerPeriod(selectedDateIso)
                            val isUnavailableByBarber = unavailableDates.contains(selectedDateIso)

                            val scheduleInfo = when {
                                isUnavailableByBarber -> if (language == AppLanguage.IT) "🔴 Parrucchiere Non Disponibile / Ferie" else "🔴 Barber Unavailable"
                                isSunday -> Strings.get("schedule_sunday_closed", language)
                                isSummer -> Strings.get("schedule_type_summer", language)
                                else -> Strings.get("schedule_type_standard", language)
                            }

                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = when {
                                        isUnavailableByBarber || isSunday -> MaterialTheme.colorScheme.errorContainer
                                        isSummer -> MaterialTheme.colorScheme.primaryContainer
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            ) {
                                Text(
                                    text = scheduleInfo,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        isUnavailableByBarber || isSunday -> MaterialTheme.colorScheme.onErrorContainer
                                        isSummer -> MaterialTheme.colorScheme.onPrimaryContainer
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.padding(12.dp)
                                )
                            }

                            Text(
                                text = Strings.get("available_slots", language) + " - $selectedDateIso",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Available & Anonymized Booked Slots Grid
                        item {
                            val isUnavailableByBarber = unavailableDates.contains(selectedDateIso)
                            val availableSlots = DateUtils.getTimeSlotsForDate(selectedDateIso)

                            if (isUnavailableByBarber) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (language == AppLanguage.IT) "🔴 Il parrucchiere è in ferie o non disponibile in questo giorno." else "🔴 Barber is unavailable on this day.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            } else if (availableSlots.isEmpty()) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (language == AppLanguage.IT) "🔴 Chiuso la domenica. Seleziona un altro giorno." else "🔴 Closed on Sunday. Please select another day.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            } else {
                                // Existing appointments for selected date
                                val bookedTimes = appointments
                                    .filter { it.dateIso == selectedDateIso && it.status != AppointmentStatus.CANCELLED }
                                    .map { it.timeSlot }
                                    .toSet()

                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3),
                                    modifier = Modifier.height(240.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(availableSlots) { slot ->
                                        val isBooked = bookedTimes.contains(slot)
                                        val isSelected = selectedTimeSlot == slot

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(
                                                    when {
                                                        isSelected -> MaterialTheme.colorScheme.primary
                                                        isBooked -> MaterialTheme.colorScheme.surfaceVariant
                                                        else -> MaterialTheme.colorScheme.primaryContainer
                                                    }
                                                )
                                                .clickable(enabled = !isBooked) {
                                                    selectedTimeSlot = slot
                                                    bookingMessage = null
                                                }
                                                .padding(12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = slot,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = when {
                                                        isSelected -> MaterialTheme.colorScheme.onPrimary
                                                        isBooked -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                                        else -> MaterialTheme.colorScheme.onPrimaryContainer
                                                    }
                                                )
                                                Text(
                                                    text = if (isBooked)
                                                        Strings.get("slot_booked_anonymous", language) // Privacy: "Occupato" without name!
                                                    else
                                                        Strings.get("slot_available", language),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = when {
                                                        isSelected -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                                                        isBooked -> MaterialTheme.colorScheme.error
                                                        else -> MaterialTheme.colorScheme.primary
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Booking Action Button
                        item {
                            if (selectedTimeSlot != null) {
                                Button(
                                    onClick = {
                                        val success = onBookSlot(selectedDateIso, selectedTimeSlot!!)
                                        if (success) {
                                            bookingMessage = Strings.get("booking_success", language)
                                            selectedTimeSlot = null
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text(
                                        text = "${Strings.get("confirm_booking", language)} ($selectedTimeSlot) - €${barberProfile.price}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            bookingMessage?.let { msg ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "✓ $msg",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // MY APPOINTMENTS TAB
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (myAppointments.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = Strings.get("no_appointments", language),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(myAppointments) { app ->
                                ClientAppointmentCard(
                                    appointment = app,
                                    language = language,
                                    onOpenPostponedAlert = { onOpenPostponedAlert(app) },
                                    onOpenReviewDialog = { onOpenReviewDialog(app) }
                                )
                            }
                        }
                    }
                }

                2 -> {
                    // REVIEWS TAB WITH 3 ACTION BUTTONS / FILTERS
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 3 Buttons Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Button 1: Barber Reviews
                            FilterChip(
                                selected = reviewSubTab == 0,
                                onClick = { reviewSubTab = 0 },
                                label = { Text(if (language == AppLanguage.IT) "💈 Barbiere" else "💈 Barber", style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.weight(1f)
                            )
                            // Button 2: My Reviews
                            FilterChip(
                                selected = reviewSubTab == 1,
                                onClick = { reviewSubTab = 1 },
                                label = { Text(if (language == AppLanguage.IT) "👤 Le Mie" else "👤 Mine", style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.weight(1f)
                            )
                            // Button 3: Leave Review for Last Haircut
                            Button(
                                onClick = {
                                    if (lastCompletedAppointment != null) {
                                        onOpenReviewDialog(lastCompletedAppointment)
                                    } else {
                                        // Sample fallback appointment for review if none completed yet
                                        val sampleApp = myAppointments.lastOrNull() ?: Appointment(
                                            id = "app_demo",
                                            dateIso = DateUtils.getDeviceCurrentDateIso(),
                                            timeSlot = "16:00",
                                            clientId = myClientId,
                                            clientName = myClientName,
                                            clientAge = clientProfile.age,
                                            barberName = barberProfile.name.ifEmpty { "Dino Barber" },
                                            price = barberProfile.price,
                                            durationMinutes = barberProfile.durationMinutes,
                                            status = AppointmentStatus.COMPLETED
                                        )
                                        onOpenReviewDialog(sampleApp)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Text("⭐ " + if (language == AppLanguage.IT) "Scrivi" else "Write", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }

                        val displayedReviews = if (reviewSubTab == 1) {
                            reviews.filter { it.clientName == myClientName }
                        } else {
                            reviews
                        }

                        ReviewsScreen(reviews = displayedReviews, language = language)
                    }
                }
            }
        }
    }
}

// Date selector strip for Client
@Composable
fun ClientDateSelectorStrip(
    selectedDateIso: String,
    onDateSelected: (String) -> Unit
) {
    val sampleDates = listOf(
        "2025-05-15" to "15 Mag",
        "2025-05-16" to "16 Mag",
        "2025-05-17" to "17 Mag",
        "2025-05-18" to "18 Mag ☀️", // Domenica / Festivo
        "2025-06-01" to "01 Giu ☀️", // Estivo
        "2025-07-15" to "15 Lug ☀️"  // Estivo
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        sampleDates.forEach { (dateIso, label) ->
            val isSelected = dateIso == selectedDateIso
            val isSummerHoliday = DateUtils.isSummerOrHoliday(dateIso)

            FilterChip(
                selected = isSelected,
                onClick = { onDateSelected(dateIso) },
                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = if (isSummerHoliday) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                    selectedLabelColor = if (isSummerHoliday) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// Client Appointment Card
@Composable
fun ClientAppointmentCard(
    appointment: Appointment,
    language: AppLanguage,
    onOpenPostponedAlert: () -> Unit,
    onOpenReviewDialog: () -> Unit
) {
    val isPostponed = appointment.status == AppointmentStatus.POSTPONED_30_MIN

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📅 ${appointment.dateIso} - ⏰ ${appointment.timeSlot}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                val (statusText, statusColor) = when (appointment.status) {
                    AppointmentStatus.CONFIRMED -> Strings.get("status_confirmed", language) to MaterialTheme.colorScheme.primary
                    AppointmentStatus.POSTPONED_30_MIN -> Strings.get("status_postponed", language) to MaterialTheme.colorScheme.error
                    AppointmentStatus.CANCELLED -> Strings.get("status_cancelled", language) to MaterialTheme.colorScheme.outline
                    AppointmentStatus.COMPLETED -> Strings.get("status_completed", language) to MaterialTheme.colorScheme.secondary
                }

                SuggestionChip(
                    onClick = {},
                    label = { Text(statusText, style = MaterialTheme.typography.labelSmall) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = statusColor.copy(alpha = 0.15f),
                        labelColor = statusColor
                    )
                )
            }

            Text(
                text = "✂️ Barber: ${appointment.barberName} | €${appointment.price}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Google Calendar Sync Button Requirement
            if (appointment.status == AppointmentStatus.CONFIRMED || appointment.status == AppointmentStatus.POSTPONED_30_MIN) {
                OutlinedButton(
                    onClick = {
                        openGoogleCalendarSync(
                            dateIso = appointment.dateIso,
                            timeSlot = appointment.timeSlot,
                            barberName = appointment.barberName,
                            price = appointment.price
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "📅 " + (if (language == AppLanguage.IT) "Sincronizza con Google Calendar" else "Sync with Google Calendar"),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Postponed Banner Alert Requirement
            if (isPostponed) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "⚠️ " + Strings.get("client_alert_title", language),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = Strings.get("client_alert_desc", language),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Button(
                            onClick = onOpenPostponedAlert,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = Strings.get("confirm", language) + " / " + Strings.get("cancel", language),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }

            // Completed -> Review Action Requirement
            if (appointment.status == AppointmentStatus.COMPLETED) {
                if (appointment.isReviewed) {
                    Text(
                        text = "✓ " + if (language == AppLanguage.IT) "Recensione inviata!" else "Review submitted!",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Button(
                        onClick = onOpenReviewDialog,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "⭐ " + Strings.get("leave_review", language),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
