package com.example.barbershopdino.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.barbershopdino.localization.Strings
import com.example.barbershopdino.model.AppLanguage
import com.example.barbershopdino.model.Appointment
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun Barber30MinPromptDialog(
    appointment: Appointment,
    language: AppLanguage,
    onResponse: (needs30MinExtra: Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* forced action */ },
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = Strings.get("barber_prompt_title", language),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = Strings.get("barber_prompt_desc", language),
                    style = MaterialTheme.typography.bodyMedium
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Cliente: ${appointment.clientName} (${appointment.clientAge} anni)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Orario: ${appointment.timeSlot} | €${appointment.price}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onResponse(false) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(Strings.get("on_time", language))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = { onResponse(true) }
            ) {
                Text(Strings.get("need_30_min", language))
            }
        }
    )
}

@Composable
fun ClientPostponedAlertDialog(
    appointment: Appointment,
    language: AppLanguage,
    onResponse: (cancel: Boolean, cancelReason: String?) -> Unit
) {
    var isCancelling by remember { mutableStateOf(false) }
    var cancelReason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { /* forced action */ },
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = Strings.get("client_alert_title", language),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = Strings.get("client_alert_desc", language),
                    style = MaterialTheme.typography.bodyMedium
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Parrucchiere: ${appointment.barberName}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Orario originale: ${appointment.timeSlot} ➔ Nuovo orario: +30 min",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                if (isCancelling) {
                    Text(
                        text = Strings.get("cancel_reason_label", language),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                    OutlinedTextField(
                        value = cancelReason,
                        onValueChange = { cancelReason = it },
                        placeholder = { Text(Strings.get("cancel_reason_hint", language)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 3
                    )
                }
            }
        },
        confirmButton = {
            if (isCancelling) {
                Button(
                    onClick = { onResponse(true, cancelReason) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(Strings.get("confirm", language))
                }
            } else {
                Button(
                    onClick = { onResponse(false, null) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(Strings.get("keep_appointment", language))
                }
            }
        },
        dismissButton = {
            if (isCancelling) {
                TextButton(onClick = { isCancelling = false }) {
                    Text(Strings.get("cancel", language))
                }
            } else {
                OutlinedButton(
                    onClick = { isCancelling = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(Strings.get("cancel_appointment", language))
                }
            }
        }
    )
}

@Composable
fun ReviewSubmissionDialog(
    appointment: Appointment,
    language: AppLanguage,
    onSubmit: (rating: Float, comment: String, photoUris: List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var rating by remember { mutableStateOf(5.0f) }
    var commentText by remember { mutableStateOf("") }
    val attachedPhotos = remember { mutableStateListOf<String>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = Strings.get("review_dialog_title", language),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${appointment.barberName} - ${appointment.dateIso}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = Strings.get("star_rating_label", language),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                StarRatingBar(
                    rating = rating,
                    onRatingChanged = { rating = it }
                )

                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    label = { Text(Strings.get("review_text_label", language)) },
                    placeholder = { Text(Strings.get("review_text_hint", language)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 3
                )

                // Photo Attachment Section (Galleria Foto)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == AppLanguage.IT) "📷 Foto taglio (opzionale):" else "📷 Haircut photos (optional):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedButton(
                        onClick = {
                            val photoName = "Photo_${attachedPhotos.size + 1}.jpg"
                            attachedPhotos.add(photoName)
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(if (language == AppLanguage.IT) "+ Galleria" else "+ Gallery", style = MaterialTheme.typography.labelSmall)
                    }
                }

                if (attachedPhotos.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        attachedPhotos.forEach { photo ->
                            SuggestionChip(
                                onClick = {},
                                label = { Text("🖼️ $photo", style = MaterialTheme.typography.labelSmall) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(rating, commentText, attachedPhotos.toList()) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(Strings.get("submit_review", language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(Strings.get("cancel", language))
            }
        }
    )
}

@Composable
fun BarberCodeAuthDialog(
    language: AppLanguage,
    errorMessage: String?,
    onVerifyCode: (enteredCode: String) -> Unit,
    onDismiss: () -> Unit
) {
    var codeInput by remember { mutableStateOf("") }
    var isCodeVisible by remember { mutableStateOf(false) }

    LaunchedEffect(isCodeVisible) {
        if (isCodeVisible) {
            delay(5.seconds)
            isCodeVisible = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "🔑 " + Strings.get("barber_code_dialog_title", language),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = Strings.get("barber_code_label", language),
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = codeInput,
                    onValueChange = { codeInput = it },
                    placeholder = { Text(Strings.get("barber_code_hint", language)) },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword
                    ),
                    visualTransformation = if (isCodeVisible)
                        androidx.compose.ui.text.input.VisualTransformation.None
                    else
                        androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { isCodeVisible = !isCodeVisible }) {
                            Text(if (isCodeVisible) "👁️" else "🙈")
                        }
                    }
                )

                errorMessage?.let { errKey ->
                    Text(
                        text = Strings.get(errKey, language),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onVerifyCode(codeInput) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(Strings.get("confirm", language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(Strings.get("cancel", language))
            }
        }
    )
}
