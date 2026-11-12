package com.example.barbershopdino

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.barbershopdino.data.AppViewModel
import com.example.barbershopdino.model.AppLanguage
import com.example.barbershopdino.model.UserRole
import com.example.barbershopdino.ui.components.Barber30MinPromptDialog
import com.example.barbershopdino.ui.components.BarberCodeAuthDialog
import com.example.barbershopdino.ui.components.ClientPostponedAlertDialog
import com.example.barbershopdino.ui.components.ReviewSubmissionDialog
import com.example.barbershopdino.ui.screens.BarberHomeScreen
import com.example.barbershopdino.ui.screens.BarberSetupScreen
import com.example.barbershopdino.ui.screens.ClientHomeScreen
import com.example.barbershopdino.ui.screens.ClientSetupScreen
import com.example.barbershopdino.ui.screens.RoleSelectionScreen
import com.example.barbershopdino.ui.theme.BarberShopTheme

@Composable
@Preview
fun App(
    viewModel: AppViewModel = viewModel { AppViewModel() }
) {
    BarberShopTheme(themeMode = viewModel.currentThemeMode) {
        Surface(color = MaterialTheme.colorScheme.background) {
            when (viewModel.currentRole) {
                UserRole.NONE -> {
                    RoleSelectionScreen(
                        currentLanguage = viewModel.currentLanguage,
                        currentThemeMode = viewModel.currentThemeMode,
                        onSelectRole = { role ->
                            if (role == UserRole.BARBER) {
                                if (viewModel.barberProfile.name.isNotBlank() && !viewModel.isBarberAuthenticated) {
                                    viewModel.showBarberCodeAuthDialog = true
                                } else {
                                    viewModel.setRole(UserRole.BARBER)
                                }
                            } else {
                                viewModel.setRole(role)
                            }
                        },
                        onToggleLanguage = { viewModel.toggleLanguage() },
                        onToggleTheme = { viewModel.toggleTheme() }
                    )
                }

                UserRole.BARBER -> {
                    if (viewModel.barberProfile.name.isBlank()) {
                        BarberSetupScreen(
                            currentProfile = viewModel.barberProfile,
                            language = viewModel.currentLanguage,
                            onSaveProfile = { name, price, duration, code ->
                                viewModel.saveBarberProfile(name, price, duration, code)
                                viewModel.setRole(UserRole.BARBER)
                            },
                            onBackToRoleSelect = { viewModel.setRole(UserRole.NONE) }
                        )
                    } else {
                        BarberHomeScreen(
                            barberProfile = viewModel.barberProfile,
                            appointments = viewModel.appointments,
                            reviews = viewModel.reviews,
                            selectedDateIso = viewModel.selectedDateIso,
                            onSelectDate = { viewModel.selectedDateIso = it },
                            language = viewModel.currentLanguage,
                            themeMode = viewModel.currentThemeMode,
                            onToggleLanguage = { viewModel.toggleLanguage() },
                            onToggleTheme = { viewModel.toggleTheme() },
                            onSwitchRole = { viewModel.setRole(UserRole.NONE) },
                            onTrigger30MinCheck = { app ->
                                viewModel.triggerBarber30MinPromptForAppointment(app)
                            },
                            onMarkCompleted = { id ->
                                viewModel.markAppointmentCompleted(id)
                            },
                            onUpdatePrice = { newPrice ->
                                viewModel.updateBarberPrice(newPrice)
                            },
                            unavailableDates = viewModel.unavailableDates,
                            onToggleUnavailability = { date ->
                                viewModel.toggleDayUnavailability(date)
                            }
                        )
                    }
                }

                UserRole.CLIENT -> {
                    if (viewModel.clientProfile.firstName.isBlank()) {
                        ClientSetupScreen(
                            currentProfile = viewModel.clientProfile,
                            language = viewModel.currentLanguage,
                            onSaveProfile = { first, last, age, day, month ->
                                viewModel.saveClientProfile(first, last, age, day, month)
                            },
                            onBackToRoleSelect = { viewModel.setRole(UserRole.NONE) }
                        )
                    } else {
                        ClientHomeScreen(
                            clientProfile = viewModel.clientProfile,
                            barberProfile = viewModel.barberProfile,
                            appointments = viewModel.appointments,
                            reviews = viewModel.reviews,
                            selectedDateIso = viewModel.selectedDateIso,
                            onSelectDate = { viewModel.selectedDateIso = it },
                            language = viewModel.currentLanguage,
                            themeMode = viewModel.currentThemeMode,
                            onToggleLanguage = { viewModel.toggleLanguage() },
                            onToggleTheme = { viewModel.toggleTheme() },
                            onSwitchRole = { viewModel.setRole(UserRole.NONE) },
                            onBookSlot = { date, time ->
                                viewModel.bookAppointment(date, time)
                            },
                            onOpenPostponedAlert = { app ->
                                viewModel.clientAlertAppointment = app
                                viewModel.showClientPostponedAlert = true
                            },
                            onOpenReviewDialog = { app ->
                                viewModel.triggerClientReviewDialog(app)
                            },
                            unavailableDates = viewModel.unavailableDates
                        )
                    }
                }
            }

            // Barber Access Code Verification Dialog
            if (viewModel.showBarberCodeAuthDialog) {
                BarberCodeAuthDialog(
                    language = viewModel.currentLanguage,
                    errorMessage = viewModel.barberCodeError,
                    onVerifyCode = { enteredCode ->
                        viewModel.verifyBarberCode(enteredCode)
                    },
                    onDismiss = {
                        viewModel.showBarberCodeAuthDialog = false
                        viewModel.barberCodeError = null
                    }
                )
            }

            // Client Price Update Notification Alert
            if (viewModel.showPriceUpdateClientNotification && viewModel.currentRole == UserRole.CLIENT) {
                AlertDialog(
                    onDismissRequest = { viewModel.showPriceUpdateClientNotification = false },
                    shape = RoundedCornerShape(20.dp),
                    title = {
                        Text(
                            text = "🏷️ " + (if (viewModel.currentLanguage == AppLanguage.IT) "Nuovo Prezzo Taglio!" else "New Haircut Price!"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    text = {
                        Text(
                            text = if (viewModel.currentLanguage == AppLanguage.IT)
                                "Il parrucchiere ha aggiornato il costo del taglio a €${viewModel.updatedPriceAmount}."
                            else
                                "The barber updated the haircut price to €${viewModel.updatedPriceAmount}.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = { viewModel.showPriceUpdateClientNotification = false },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("OK")
                        }
                    }
                )
            }

            // Notification Overlay Dialogs
            if (viewModel.showBarber30MinPrompt && viewModel.promptAppointment != null) {
                Barber30MinPromptDialog(
                    appointment = viewModel.promptAppointment!!,
                    language = viewModel.currentLanguage,
                    onResponse = { needs30MinExtra ->
                        viewModel.barberRespondTo30MinCheck(
                            appointmentId = viewModel.promptAppointment!!.id,
                            needs30MinExtra = needs30MinExtra
                        )
                    }
                )
            }

            if (viewModel.showClientPostponedAlert && viewModel.clientAlertAppointment != null) {
                ClientPostponedAlertDialog(
                    appointment = viewModel.clientAlertAppointment!!,
                    language = viewModel.currentLanguage,
                    onResponse = { cancel, cancelReason ->
                        viewModel.clientRespondToPostpone(
                            appointmentId = viewModel.clientAlertAppointment!!.id,
                            cancel = cancel,
                            cancelReason = cancelReason
                        )
                    }
                )
            }

            if (viewModel.showReviewDialog && viewModel.reviewAppointment != null) {
                ReviewSubmissionDialog(
                    appointment = viewModel.reviewAppointment!!,
                    language = viewModel.currentLanguage,
                    onSubmit = { rating, comment, photos ->
                        viewModel.addReview(
                            appointmentId = viewModel.reviewAppointment!!.id,
                            rating = rating,
                            comment = comment,
                            photoUris = photos
                        )
                    },
                    onDismiss = {
                        viewModel.showReviewDialog = false
                        viewModel.reviewAppointment = null
                    }
                )
            }
        }
    }
}
