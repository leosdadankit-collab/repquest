package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DifficultyLevel
import com.example.data.model.FitnessGoal
import com.example.data.model.UserProfile
import com.example.ui.components.ConfirmDialog
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.QuestGold
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VibrantCoral

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    onUpdateProfile: (UserProfile) -> Unit,
    onResetProgress: () -> Unit,
    modifier: Modifier = Modifier
) {
    var displayName by remember(userProfile) { mutableStateOf(userProfile.displayName) }
    var selectedGoal by remember(userProfile) { mutableStateOf(userProfile.primaryGoal) }
    var experienceLevel by remember(userProfile) { mutableStateOf(userProfile.experienceLevel) }
    var hasBarbell by remember(userProfile) { mutableStateOf(userProfile.hasBarbell) }
    var hasDumbbells by remember(userProfile) { mutableStateOf(userProfile.hasDumbbells) }
    var hasBodyweight by remember(userProfile) { mutableStateOf(userProfile.hasBodyweight) }
    var preferredUnit by remember(userProfile) { mutableStateOf(userProfile.preferredWeightUnit) }
    var notificationsEnabled by remember(userProfile) { mutableStateOf(userProfile.notificationsEnabled) }

    var showResetConfirm by remember { mutableStateOf(false) }
    var showSavedToast by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                text = "Profile & Settings",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Manage your preferences, equipment, and training targets",
                style = MaterialTheme.typography.bodySmall,
                color = ElectricCyan
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Profile Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = NeonViolet, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Hero Identity", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = displayName,
                            onValueChange = { displayName = it },
                            label = { Text("Display Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_display_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonViolet,
                                unfocusedBorderColor = SurfaceBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Primary Quest Goal", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        FitnessGoal.entries.forEach { goal ->
                            FilterChip(
                                selected = selectedGoal == goal,
                                onClick = { selectedGoal = goal },
                                label = { Text(goal.title) },
                                modifier = Modifier.padding(vertical = 2.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NeonViolet,
                                    selectedLabelColor = Color.White,
                                    containerColor = SurfaceElevated,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }
                }
            }

            // Equipment Settings Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.FitnessCenter, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Available Equipment", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Strictly home equipment: Barbell, Dumbbells, Bodyweight only.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        EquipmentToggleProfileRow("Two Dumbbells", hasDumbbells) { hasDumbbells = !hasDumbbells }
                        EquipmentToggleProfileRow("One Barbell with Plates", hasBarbell) { hasBarbell = !hasBarbell }
                        EquipmentToggleProfileRow("Bodyweight (No Equipment)", hasBodyweight) { hasBodyweight = !hasBodyweight }
                    }
                }
            }

            // Preferences (Units & Notifications)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Tune, contentDescription = null, tint = QuestGold, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Units & Reminders", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Weight Unit", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = preferredUnit == "kg",
                                onClick = { preferredUnit = "kg" },
                                label = { Text("Kilograms (kg)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = QuestGold,
                                    selectedLabelColor = Color.Black,
                                    containerColor = SurfaceElevated,
                                    labelColor = TextSecondary
                                )
                            )
                            FilterChip(
                                selected = preferredUnit == "lbs",
                                onClick = { preferredUnit = "lbs" },
                                label = { Text("Pounds (lbs)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = QuestGold,
                                    selectedLabelColor = Color.Black,
                                    containerColor = SurfaceElevated,
                                    labelColor = TextSecondary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Workout Reminders", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                                Text("Gentle notices for planned training days", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            }
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = NeonViolet
                                )
                            )
                        }
                    }
                }
            }

            // Safety Guidance Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceElevated)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Security, contentDescription = null, tint = NeonLime, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Health & Safety Guidance", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "RepQuest prioritizes sustainable habits and joint safety. Always lift with controlled form, stop immediately if sharp pain occurs, and honor planned recovery days. Never compensate for missed days with excessive volume.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Save Settings Button
            item {
                Button(
                    onClick = {
                        val updated = userProfile.copy(
                            displayName = if (displayName.isNotBlank()) displayName.trim() else "Adventurer",
                            primaryGoal = selectedGoal,
                            experienceLevel = experienceLevel,
                            hasBarbell = hasBarbell,
                            hasDumbbells = hasDumbbells,
                            hasBodyweight = hasBodyweight,
                            preferredWeightUnit = preferredUnit,
                            notificationsEnabled = notificationsEnabled
                        )
                        onUpdateProfile(updated)
                        showSavedToast = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_profile_settings_button")
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
                if (showSavedToast) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Settings saved successfully!",
                        color = NeonLime,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            // Reset Progress Danger Zone
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(VibrantCoral.copy(alpha = 0.5f))
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Reset Progress", style = MaterialTheme.typography.titleSmall, color = VibrantCoral, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Clears workout history, unlocks, and resets XP to Level 1. Irreversible action.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { showResetConfirm = true },
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantCoral),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("reset_progress_button")
                        ) {
                            Icon(Icons.Filled.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset All Data")
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    if (showResetConfirm) {
        ConfirmDialog(
            title = "Reset All Progress?",
            message = "This will permanently wipe all logged workout sessions, personal records, and achievements. Are you sure you want to start fresh?",
            confirmText = "Reset Everything",
            isDestructive = true,
            onConfirm = {
                showResetConfirm = false
                onResetProgress()
            },
            onDismiss = { showResetConfirm = false }
        )
    }
}

@Composable
fun EquipmentToggleProfileRow(
    title: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ElectricCyan
            )
        )
    }
}
