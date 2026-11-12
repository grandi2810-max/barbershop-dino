package com.example.barbershopdino.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.barbershopdino.localization.Strings
import com.example.barbershopdino.model.AppLanguage
import com.example.barbershopdino.model.BarberProfile
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun BarberSetupScreen(
    currentProfile: BarberProfile,
    language: AppLanguage,
    onSaveProfile: (name: String, price: Double, durationMinutes: Int, accessCode: String) -> Unit,
    onBackToRoleSelect: () -> Unit,
) {
    var name by remember { mutableStateOf(currentProfile.name.ifEmpty { "Dino Barber" }) }
    var priceText by remember { mutableStateOf(currentProfile.price.toString()) }
    var durationMinutes by remember { mutableStateOf(currentProfile.durationMinutes) }
    var accessCode by remember { mutableStateOf(currentProfile.accessCode.ifEmpty { "1234" }) }
    var isCodeVisible by remember { mutableStateOf(false) }

    // 5-second auto-hide timer for code visibility
    LaunchedEffect(isCodeVisible) {
        if (isCodeVisible) {
            delay(5.seconds)
            isCodeVisible = false
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onBackToRoleSelect) {
                        Text("← " + Strings.get("switch_role", language))
                    }
                }

                Text(
                    text = Strings.get("barber_setup_title", language),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Barber Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(Strings.get("barber_name", language)) },
                    placeholder = { Text(Strings.get("barber_name_hint", language)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Barber Access Code (Codice Parrucchiere) + 5s Eye Toggle
                OutlinedTextField(
                    value = accessCode,
                    onValueChange = { accessCode = it },
                    label = { Text(Strings.get("barber_code_label", language)) },
                    placeholder = { Text(Strings.get("barber_code_hint", language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = if (isCodeVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = { isCodeVisible = !isCodeVisible }) {
                            Text(if (isCodeVisible) "👁️" else "🙈", fontSize = 18.sp)
                        }
                    }
                )

                // Haircut Price Input (€)
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text(Strings.get("haircut_price", language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = { Text("€", fontWeight = FontWeight.Bold) }
                )

                // Estimated Duration Selection (min)
                Text(
                    text = Strings.get("estimated_duration", language) + ": $durationMinutes " + Strings.get("minutes", language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(15, 30, 45, 60).forEach { mins ->
                        FilterChip(
                            selected = durationMinutes == mins,
                            onClick = { durationMinutes = mins },
                            label = { Text("$mins min") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Save / Continue Button
            Button(
                onClick = {
                    val parsedPrice = priceText.toDoubleOrNull() ?: 20.0
                    onSaveProfile(name.ifBlank { "Dino Barber" }, parsedPrice, durationMinutes, accessCode.ifBlank { "1234" })
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = Strings.get("save", language) + " & " + Strings.get("continue_btn", language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
