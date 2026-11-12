package com.example.barbershopdino.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.barbershopdino.localization.Strings
import com.example.barbershopdino.model.AppLanguage
import com.example.barbershopdino.model.AppThemeMode
import com.example.barbershopdino.model.UserRole

@Composable
fun RoleSelectionScreen(
    currentLanguage: AppLanguage,
    currentThemeMode: AppThemeMode,
    onSelectRole: (UserRole) -> Unit,
    onToggleLanguage: () -> Unit,
    onToggleTheme: () -> Unit
) {
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
            // Top Controls Bar (Language & Theme toggle)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Language Switcher Chip
                FilterChip(
                    selected = true,
                    onClick = onToggleLanguage,
                    label = {
                        Text(if (currentLanguage == AppLanguage.IT) "🇮🇹 IT" else "🇬🇧 EN")
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )

                // Theme Switcher Chip
                FilterChip(
                    selected = true,
                    onClick = onToggleTheme,
                    label = {
                        Text(if (currentThemeMode == AppThemeMode.DARK) "🌙 Dark" else "☀️ Light")
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        selectedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App Logo & Header - SECRET CLICK ON LOGO OPENS BARBER VERIFICATION!
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable { onSelectRole(UserRole.BARBER) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "💈",
                        fontSize = 48.sp
                    )
                }

                Text(
                    text = Strings.get("app_title", currentLanguage),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = Strings.get("select_role_subtitle", currentLanguage),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Client Entry Card Only -> "ACCEDI"
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectRole(UserRole.CLIENT) },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "👤",
                            fontSize = 40.sp,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        Column {
                            Text(
                                text = if (currentLanguage == AppLanguage.IT) "Accedi" else "Login",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.IT)
                                    "Prenota tagli e gestisci appuntamenti"
                                else
                                    "Book haircuts & manage appointments",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "BarberShop Dino © 2025",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}
