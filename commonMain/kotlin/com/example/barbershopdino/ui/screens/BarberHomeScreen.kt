package com.example.barbershopdino.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.barbershopdino.localization.Strings
import com.example.barbershopdino.model.AppLanguage
import com.example.barbershopdino.model.AppThemeMode
import com.example.barbershopdino.model.Appointment
import com.example.barbershopdino.model.AppointmentStatus
import com.example.barbershopdino.model.BarberProfile
import com.example.barbershopdino.model.Review
import com.example.barbershopdino.openGoogleCalendarSync
import com.example.barbershopdino.util.DateUtils

@Composable
fun BarberHomeScreen(
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
    onTrigger30MinCheck: (Appointment) -> Unit,
    onMarkCompleted: (String) -> Unit,
    onUpdatePrice: (Double) -> Unit,
    unavailableDates: List<String> = emptyList(),
    onToggleUnavailability: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Calendar/Appointments, 1: Reviews
    var showEditPriceDialog by remember { mutableStateOf(false) }
    var editPriceText by remember { mutableStateOf(barberProfile.price.toString()) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("✂️ ", fontSize = 20.sp)
                                Text(
                                    text = barberProfile.name.ifEmpty { "Dino Barber" },
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${Strings.get("price_tag", language)}: €${barberProfile.price} | ${Strings.get("duration_tag", language)}: ${barberProfile.durationMinutes} min",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                TextButton(
                                    onClick = {
                                        editPriceText = barberProfile.price.toString()
                                        showEditPriceDialog = true
                                    },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                    modifier = Modifier.height(24.dp)
                                ) {
                                    Text("✏️ " + if (language == AppLanguage.IT) "Modifica" else "Edit", style = MaterialTheme.typography.labelSmall)
                                }
                            }
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

                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Text(
                                text = "${appointments.size} " + if (language == AppLanguage.IT) "prenotazioni" else "bookings",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Tabs Header
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = Strings.get("monthly_schedule", language),
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = Strings.get("reviews_title", language) + " (${reviews.size})",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            // Tab Contents
            if (selectedTab == 0) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Month Calendar Table Section (Synchronized with Device Date Month & Year)
                    item {
                        Text(
                            text = Strings.get("monthly_schedule", language) + " (${DateUtils.getMonthYearHeader(selectedDateIso, language)})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    item {
                        BarberMonthCalendarGrid(
                            selectedDateIso = selectedDateIso,
                            appointments = appointments,
                            unavailableDates = unavailableDates,
                            onDateSelected = onSelectDate
                        )
                    }

                    // Barber Unavailability Toggle Action Button
                    item {
                        val isUnavailable = unavailableDates.contains(selectedDateIso)
                        OutlinedButton(
                            onClick = { onToggleUnavailability(selectedDateIso) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = if (isUnavailable)
                                ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                            else
                                ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text(
                                text = if (isUnavailable)
                                    "🟢 " + (if (language == AppLanguage.IT) "Rendi Disponibile $selectedDateIso" else "Make Available $selectedDateIso")
                                else
                                    "🔴 " + (if (language == AppLanguage.IT) "Segna $selectedDateIso come Indisponibile / Ferie" else "Mark $selectedDateIso Unavailable"),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Daily Appointments List Section
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Text(
                            text = Strings.get("appointments_for", language) + " $selectedDateIso",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    val dayAppointments = appointments.filter { it.dateIso == selectedDateIso }

                    if (dayAppointments.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = Strings.get("no_appointments", language),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(dayAppointments) { app ->
                            BarberAppointmentCard(
                                appointment = app,
                                language = language,
                                onTrigger30MinCheck = { onTrigger30MinCheck(app) },
                                onMarkCompleted = { onMarkCompleted(app.id) }
                            )
                        }
                    }
                }
            } else {
                ReviewsScreen(reviews = reviews, language = language)
            }

            // Edit Haircut Price Dialog
            if (showEditPriceDialog) {
                AlertDialog(
                    onDismissRequest = { showEditPriceDialog = false },
                    shape = RoundedCornerShape(20.dp),
                    title = {
                        Text(
                            text = "🏷️ " + (if (language == AppLanguage.IT) "Modifica Prezzo Taglio" else "Edit Haircut Price"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = if (language == AppLanguage.IT) "Inserisci il nuovo costo del taglio (€):" else "Enter new haircut price (€):",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            OutlinedTextField(
                                value = editPriceText,
                                onValueChange = { editPriceText = it },
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = { Text("€", fontWeight = FontWeight.Bold) }
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val newPrice = editPriceText.toDoubleOrNull()
                                if ((newPrice != null) && (newPrice > 0)) {
                                    onUpdatePrice(newPrice)
                                }
                                showEditPriceDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(Strings.get("save", language))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditPriceDialog = false }) {
                            Text(Strings.get("cancel", language))
                        }
                    }
                )
            }
        }
    }
}

// Monthly Grid Component for Barber
@Composable
fun BarberMonthCalendarGrid(
    selectedDateIso: String,
    appointments: List<Appointment>,
    unavailableDates: List<String> = emptyList(),
    onDateSelected: (String) -> Unit
) {
    val daysInMonth = (1..31).toList()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Days of the week header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf("Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom").forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Grid of days
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(260.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(daysInMonth) { dayNum ->
                    val dayStr = if (dayNum < 10) "0$dayNum" else dayNum.toString()
                    val dateIso = "2025-05-$dayStr"
                    val isSelected = dateIso == selectedDateIso
                    val isUnavailable = unavailableDates.contains(dateIso)
                    val bookedCount = appointments.count { (it.dateIso == dateIso) && (it.status != AppointmentStatus.CANCELLED) }

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                when {
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    isUnavailable -> MaterialTheme.colorScheme.errorContainer
                                    bookedCount > 0 -> MaterialTheme.colorScheme.primaryContainer
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            )
                            .clickable { onDateSelected(dateIso) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = dayNum.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected || bookedCount > 0) FontWeight.Bold else FontWeight.Normal,
                                color = when {
                                    isSelected -> MaterialTheme.colorScheme.onPrimary
                                    isUnavailable -> MaterialTheme.colorScheme.onErrorContainer
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                            if (isUnavailable) {
                                Text("⛔", fontSize = 9.sp)
                            } else if (bookedCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Appointment Card for Barber view
@Composable
fun BarberAppointmentCard(
    appointment: Appointment,
    language: AppLanguage,
    onTrigger30MinCheck: () -> Unit,
    onMarkCompleted: () -> Unit
) {
    val statusColor = when (appointment.status) {
        AppointmentStatus.CONFIRMED -> MaterialTheme.colorScheme.primary
        AppointmentStatus.POSTPONED_30_MIN -> MaterialTheme.colorScheme.error
        AppointmentStatus.CANCELLED -> MaterialTheme.colorScheme.outline
        AppointmentStatus.COMPLETED -> MaterialTheme.colorScheme.secondary
    }

    val statusText = when (appointment.status) {
        AppointmentStatus.CONFIRMED -> Strings.get("status_confirmed", language)
        AppointmentStatus.POSTPONED_30_MIN -> Strings.get("status_postponed", language)
        AppointmentStatus.CANCELLED -> Strings.get("status_cancelled", language) + (appointment.cancelReason?.let { " ($it)" } ?: "")
        AppointmentStatus.COMPLETED -> Strings.get("status_completed", language)
    }

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
                    text = "⏰ ${appointment.timeSlot}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                SuggestionChip(
                    onClick = {},
                    label = { Text(statusText, style = MaterialTheme.typography.labelSmall) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = statusColor.copy(alpha = 0.15f),
                        labelColor = statusColor
                    )
                )
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "👤 ${appointment.clientName}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${Strings.get("age", language)}: ${appointment.clientAge} " + if (language == AppLanguage.IT) "anni" else "years old",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "€${appointment.price}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (appointment.status == AppointmentStatus.CONFIRMED || appointment.status == AppointmentStatus.POSTPONED_30_MIN) {
                OutlinedButton(
                    onClick = {
                        openGoogleCalendarSync(
                            dateIso = appointment.dateIso,
                            timeSlot = appointment.timeSlot,
                            barberName = appointment.clientName,
                            price = appointment.price
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "📅 " + (if (language == AppLanguage.IT) "Aggiungi a Google Calendar" else "Add to Google Calendar"),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Trigger 30 min check prompt
                    Button(
                        onClick = onTrigger30MinCheck,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = Strings.get("trigger_check", language),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Mark as Completed button
                    OutlinedButton(
                        onClick = onMarkCompleted,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = Strings.get("mark_completed", language),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}
