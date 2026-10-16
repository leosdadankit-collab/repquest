package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DifficultyLevel
import com.example.data.model.Exercise
import com.example.data.model.ExerciseCategory
import com.example.data.model.TemplateExercise
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VibrantCoral

@Composable
fun CustomWorkoutScreen(
    exerciseCatalog: List<Exercise>,
    onSaveCustomWorkout: (String, String, ExerciseCategory, DifficultyLevel, List<TemplateExercise>) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ExerciseCategory.FULL_BODY) }
    var selectedDifficulty by remember { mutableStateOf(DifficultyLevel.INTERMEDIATE) }

    val addedExercises = remember { mutableStateListOf<TemplateExercise>() }
    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("custom_workout_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Text(
                text = "Build Custom Workout",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )

            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorMessage = "Please enter a workout title"
                    } else if (addedExercises.isEmpty()) {
                        errorMessage = "Please add at least one exercise"
                    } else {
                        onSaveCustomWorkout(
                            title.trim(),
                            if (description.isNotBlank()) description.trim() else "Custom tailored home session",
                            selectedCategory,
                            selectedDifficulty,
                            addedExercises.toList()
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                shape = RoundedCornerShape(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.testTag("save_custom_workout_button")
            ) {
                Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        errorMessage = null
                    },
                    label = { Text("Workout Name (e.g. Arms & Core Fury)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_workout_title_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonViolet,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard
                    ),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Short Description") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonViolet,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard
                    ),
                    maxLines = 2
                )
            }

            item {
                Text("Category", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(ExerciseCategory.FULL_BODY, ExerciseCategory.UPPER_BODY, ExerciseCategory.LOWER_BODY, ExerciseCategory.CORE).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.displayName) },
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

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Exercises (${addedExercises.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )

                    Button(
                        onClick = { showAddExerciseDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("add_exercise_to_custom_button")
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = ElectricCyan)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Exercise", color = ElectricCyan, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            if (errorMessage != null) {
                item {
                    Text(
                        text = errorMessage ?: "",
                        color = VibrantCoral,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            itemsIndexed(addedExercises) { index, ex ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceElevated),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${index + 1}", color = NeonViolet, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(ex.exerciseName, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                Text("${ex.targetSets} sets × ${ex.targetReps} reps • ${ex.equipment.displayName}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        }

                        IconButton(
                            onClick = { addedExercises.removeAt(index) }
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Remove", tint = VibrantCoral)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    if (showAddExerciseDialog) {
        AddExerciseDialog(
            exerciseCatalog = exerciseCatalog,
            onDismiss = { showAddExerciseDialog = false },
            onAddExercise = { selectedEx ->
                addedExercises.add(
                    TemplateExercise(
                        exerciseId = selectedEx.id,
                        exerciseName = selectedEx.name,
                        equipment = selectedEx.equipment,
                        targetSets = selectedEx.defaultSets,
                        targetReps = selectedEx.defaultReps,
                        targetWeightKg = selectedEx.defaultWeightKg,
                        restSeconds = selectedEx.defaultRestSeconds,
                        isTimed = selectedEx.isTimed,
                        targetDurationSeconds = selectedEx.defaultDurationSeconds,
                        orderIndex = addedExercises.size
                    )
                )
                showAddExerciseDialog = false
            }
        )
    }
}

@Composable
fun AddExerciseDialog(
    exerciseCatalog: List<Exercise>,
    onDismiss: () -> Unit,
    onAddExercise: (Exercise) -> Unit
) {
    var search by remember { mutableStateOf("") }
    val filtered = remember(exerciseCatalog, search) {
        if (search.isBlank()) exerciseCatalog
        else exerciseCatalog.filter { it.name.contains(search, ignoreCase = true) || it.primaryMuscle.displayName.contains(search, ignoreCase = true) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Select Exercise", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = { Text("Search exercises...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(modifier = Modifier.height(260.dp)) {
                    items(filtered) { ex ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAddExercise(ex) }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(ex.name, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                                Text("${ex.primaryMuscle.displayName} • ${ex.equipment.displayName}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                            Icon(Icons.Filled.Add, contentDescription = null, tint = ElectricCyan)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondary)
            }
        },
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(20.dp)
    )
}
