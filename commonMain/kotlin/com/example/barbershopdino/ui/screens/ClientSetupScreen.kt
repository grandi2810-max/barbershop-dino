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
import androidx.compose.ui.unit.dp
import com.example.barbershopdino.localization.Strings
import com.example.barbershopdino.model.AppLanguage
import com.example.barbershopdino.model.ClientProfile

@Composable
fun ClientSetupScreen(
    currentProfile: ClientProfile,
    language: AppLanguage,
    onSaveProfile: (firstName: String, lastName: String, age: Int, birthDay: Int, birthMonth: Int) -> Unit,
    onBackToRoleSelect: () -> Unit
) {
    var firstName by remember { mutableStateOf(currentProfile.firstName.ifEmpty { "Marco" }) }
    var lastName by remember { mutableStateOf(currentProfile.lastName.ifEmpty { "Rossi" }) }
    var ageText by remember { mutableStateOf(currentProfile.age.toString()) }
    var birthDayText by remember { mutableStateOf(if (currentProfile.birthDay < 10) "0${currentProfile.birthDay}" else currentProfile.birthDay.toString()) }
    var birthMonthText by remember { mutableStateOf(if (currentProfile.birthMonth < 10) "0${currentProfile.birthMonth}" else currentProfile.birthMonth.toString()) }

    // Helper month name calculation for preview
    val monthNum = birthMonthText.toIntOrNull() ?: 1
    val monthName = when (monthNum.coerceIn(1, 12)) {
        1 -> if (language == AppLanguage.IT) "Gennaio" else "January"
        2 -> if (language == AppLanguage.IT) "Febbraio" else "February"
        3 -> if (language == AppLanguage.IT) "Marzo" else "March"
        4 -> if (language == AppLanguage.IT) "Aprile" else "April"
        5 -> if (language == AppLanguage.IT) "Maggio" else "May"
        6 -> if (language == AppLanguage.IT) "Giugno" else "June"
        7 -> if (language == AppLanguage.IT) "Luglio" else "July"
        8 -> if (language == AppLanguage.IT) "Agosto" else "August"
        9 -> if (language == AppLanguage.IT) "Settembre" else "September"
        10 -> if (language == AppLanguage.IT) "Ottobre" else "October"
        11 -> if (language == AppLanguage.IT) "Novembre" else "November"
        12 -> if (language == AppLanguage.IT) "Dicembre" else "December"
        else -> if (language == AppLanguage.IT) "Gennaio" else "January"
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
                    text = Strings.get("client_setup_title", language),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // First Name (Nome)
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text(Strings.get("first_name", language)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Last Name (Cognome)
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text(Strings.get("last_name", language)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Age (Quanti anni hai?)
                OutlinedTextField(
                    value = ageText,
                    onValueChange = { ageText = it },
                    label = { Text("🎂 " + Strings.get("how_old_are_you", language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Birthday Section (Quando compi gli anni? Giorno e Mese in numeri, es. 15/01)
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "🎈 " + Strings.get("when_is_your_birthday", language),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Giorno (es. 15)
                            OutlinedTextField(
                                value = birthDayText,
                                onValueChange = { if (it.length <= 2) birthDayText = it },
                                label = { Text(Strings.get("birthday_day_hint", language)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Text("/", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

                            // Mese in numeri (es. 01)
                            OutlinedTextField(
                                value = birthMonthText,
                                onValueChange = { if (it.length <= 2) birthMonthText = it },
                                label = { Text(Strings.get("birthday_month_hint", language)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        // Preview text
                        val dNum = birthDayText.padStart(2, '0')
                        val mNum = birthMonthText.padStart(2, '0')
                        Text(
                            text = "🎉 Compleanno: $dNum/$mNum ($monthName)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Save / Continue Button
            Button(
                onClick = {
                    val parsedAge = ageText.toIntOrNull() ?: 25
                    val parsedDay = (birthDayText.toIntOrNull() ?: 15).coerceIn(1, 31)
                    val parsedMonth = (birthMonthText.toIntOrNull() ?: 1).coerceIn(1, 12)

                    onSaveProfile(
                        firstName.ifBlank { "Marco" },
                        lastName.ifBlank { "Rossi" },
                        parsedAge,
                        parsedDay,
                        parsedMonth
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = Strings.get("continue_btn", language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
