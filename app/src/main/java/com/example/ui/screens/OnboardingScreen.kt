package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DifficultyLevel
import com.example.data.model.FitnessGoal
import com.example.data.model.UserProfile
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    initialProfile: UserProfile,
    onCompleteOnboarding: (UserProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(1) }
    var displayName by remember { mutableStateOf(initialProfile.displayName) }
    var selectedGoal by remember { mutableStateOf(initialProfile.primaryGoal) }
    var experienceLevel by remember { mutableStateOf(initialProfile.experienceLevel) }
    var durationMinutes by remember { mutableIntStateOf(initialProfile.preferredDurationMinutes) }
    val selectedDays = remember { mutableStateListOf(*initialProfile.preferredTrainingDays.toTypedArray()) }
    var hasBarbell by remember { mutableStateOf(initialProfile.hasBarbell) }
    var hasDumbbells by remember { mutableStateOf(initialProfile.hasDumbbells) }
    var hasBodyweight by remember { mutableStateOf(initialProfile.hasBodyweight) }
    var preferredUnit by remember { mutableStateOf(initialProfile.preferredWeightUnit) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(NeonViolet, ElectricCyan))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.FitnessCenter,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "RepQuest",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Step $step of 3 • Adventure Setup",
                        style = MaterialTheme.typography.bodySmall,
                        color = ElectricCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (step) {
                1 -> {
                    Text(
                        text = "Name your hero & choose your primary path",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "We adapt your workout quest to what matters to you without extreme diets or body shaming.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                    Text("Hero Name / Nickname", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonViolet,
                            unfocusedBorderColor = SurfaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    Text("Select Your Main Quest", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))

                    FitnessGoal.entries.forEach { goal ->
                        val isSelected = selectedGoal == goal
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedGoal = goal }
                                .testTag("goal_option_${goal.name}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) SurfaceElevated else SurfaceCard
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = if (isSelected) Brush.horizontalGradient(listOf(NeonViolet, ElectricCyan))
                                else Brush.horizontalGradient(listOf(SurfaceBorder, SurfaceBorder))
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) NeonViolet else SurfaceBorder),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(goal.title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                    Text(goal.subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                }
                            }
                        }
                    }
                }
                2 -> {
                    Text(
                        text = "Available Home Equipment",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "RepQuest strictly uses home gear. No gym machines, cables, or ellipticals will ever be generated.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    EquipmentToggleRow(
                        title = "Two Dumbbells",
                        subtitle = "For pressing, rows, goblet squats & curls",
                        isChecked = hasDumbbells,
                        onToggle = { hasDumbbells = !hasDumbbells }
                    )

                    EquipmentToggleRow(
                        title = "One Barbell with Plates",
                        subtitle = "For floor press, rows & Romanian deadlifts",
                        isChecked = hasBarbell,
                        onToggle = { hasBarbell = !hasBarbell }
                    )

                    EquipmentToggleRow(
                        title = "Bodyweight (No Equipment)",
                        subtitle = "Push-ups, squats, lunges, planks & core",
                        isChecked = hasBodyweight,
                        onToggle = { hasBodyweight = !hasBodyweight }
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                    Text("Fitness Experience", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DifficultyLevel.entries.forEach { diff ->
                            FilterChip(
                                selected = experienceLevel == diff,
                                onClick = { experienceLevel = diff },
                                label = { Text(diff.displayName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NeonViolet,
                                    selectedLabelColor = Color.White,
                                    containerColor = SurfaceCard,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }
                }
                3 -> {
                    Text(
                        text = "Training Schedule & Preferences",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Planned rest days never break your streak! Pick the days that suit your life.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Weekly Training Days", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))

                    val daysMap = listOf(
                        1 to "Mon", 2 to "Tue", 3 to "Wed",
                        4 to "Thu", 5 to "Fri", 6 to "Sat", 7 to "Sun"
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        daysMap.forEach { (dayInt, label) ->
                            val isSel = selectedDays.contains(dayInt)
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (isSel) NeonViolet else SurfaceCard)
                                    .clickable {
                                        if (isSel && selectedDays.size > 1) selectedDays.remove(dayInt)
                                        else if (!isSel) selectedDays.add(dayInt)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSel) Color.White else TextMuted,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Text("Target Workout Duration", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(15, 30, 45, 60).forEach { mins ->
                            FilterChip(
                                selected = durationMinutes == mins,
                                onClick = { durationMinutes = mins },
                                label = { Text("${mins}m") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricCyan,
                                    selectedLabelColor = Color.Black,
                                    containerColor = SurfaceCard,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Text("Preferred Unit", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = preferredUnit == "kg",
                            onClick = { preferredUnit = "kg" },
                            label = { Text("Kilograms (kg)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = QuestGold,
                                selectedLabelColor = Color.Black,
                                containerColor = SurfaceCard,
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
                                containerColor = SurfaceCard,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }
            }
        }

        // Action button bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (step > 1) {
                OutlinedButton(
                    onClick = { step-- },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Back", color = TextSecondary)
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            Button(
                onClick = {
                    if (step < 3) {
                        step++
                    } else {
                        val updatedProfile = initialProfile.copy(
                            displayName = if (displayName.isNotBlank()) displayName.trim() else "Adventurer",
                            primaryGoal = selectedGoal,
                            experienceLevel = experienceLevel,
                            hasBarbell = hasBarbell,
                            hasDumbbells = hasDumbbells,
                            hasBodyweight = hasBodyweight,
                            preferredDurationMinutes = durationMinutes,
                            preferredTrainingDays = selectedDays.sorted(),
                            preferredWeightUnit = preferredUnit,
                            weeklyTargetWorkouts = selectedDays.size
                        )
                        onCompleteOnboarding(updatedProfile)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("onboarding_continue_button")
            ) {
                Text(
                    text = if (step < 3) "Next Step" else "Begin Quest!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun EquipmentToggleRow(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onToggle() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (isChecked) SurfaceElevated else SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = if (isChecked) Brush.horizontalGradient(listOf(NeonViolet, ElectricCyan))
            else Brush.horizontalGradient(listOf(SurfaceBorder, SurfaceBorder))
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isChecked) NeonLime else SurfaceBorder),
                contentAlignment = Alignment.Center
            ) {
                if (isChecked) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}
