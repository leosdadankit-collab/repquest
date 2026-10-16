package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CompletionStatus
import com.example.data.model.EquipmentType
import com.example.ui.components.ConfirmDialog
import com.example.ui.components.RestTimerOverlay
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
import com.example.ui.viewmodel.ActiveExerciseState
import com.example.ui.viewmodel.ActiveSetState
import com.example.ui.viewmodel.ActiveWorkoutSession
import com.example.ui.viewmodel.RestTimerState

@Composable
fun ActiveWorkoutScreen(
    session: ActiveWorkoutSession,
    restTimerState: RestTimerState,
    onLogSet: (Int, Int, Double, Int) -> Unit,
    onUpdateSetValues: (Int, Int, Double, Int) -> Unit,
    onNextExercise: () -> Unit,
    onPrevExercise: () -> Unit,
    onSelectExerciseIndex: (Int) -> Unit,
    onTogglePause: () -> Unit,
    onFinishWorkout: (CompletionStatus) -> Unit,
    onCancelWorkout: () -> Unit,
    onUpdateNotes: (String) -> Unit,
    onAddRestSeconds: (Int) -> Unit,
    onToggleRestPause: () -> Unit,
    onSkipRest: () -> Unit,
    onViewExerciseInfo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFinishOptionsDialog by remember { mutableStateOf(false) }
    var showCancelConfirmDialog by remember { mutableStateOf(false) }

    // Intercept back button to confirm cancellation rather than losing state
    BackHandler {
        showCancelConfirmDialog = true
    }

    val currentEx = session.exercises.getOrNull(session.currentExerciseIndex)
    val totalSets = session.exercises.sumOf { it.sets.size }
    val completedSets = session.exercises.sumOf { it.sets.count { s -> s.isCompleted } }
    val workoutProgress = if (totalSets > 0) (completedSets.toFloat() / totalSets.toFloat()).coerceIn(0f, 1f) else 0f

    val mins = session.elapsedSeconds / 60
    val secs = session.elapsedSeconds % 60
    val timeFormatted = String.format("%02d:%02d", mins, secs)

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showCancelConfirmDialog = true },
                    modifier = Modifier.testTag("exit_workout_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Exit Workout",
                        tint = TextMuted
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = session.workoutTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Timer,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (session.isPaused) "$timeFormatted (Paused)" else timeFormatted,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (session.isPaused) QuestGold else ElectricCyan
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onTogglePause,
                        modifier = Modifier.testTag("pause_resume_workout_button")
                    ) {
                        Icon(
                            imageVector = if (session.isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                            contentDescription = if (session.isPaused) "Resume" else "Pause",
                            tint = QuestGold
                        )
                    }

                    Button(
                        onClick = { showFinishOptionsDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("finish_workout_header_button")
                    ) {
                        Text("Finish", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                }
            }

            // Overall Session Progress Bar
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Exercise ${session.currentExerciseIndex + 1} of ${session.exercises.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "$completedSets / $totalSets sets",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { workoutProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = NeonViolet,
                    trackColor = SurfaceElevated
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (currentEx != null) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Current Exercise Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(listOf(NeonViolet, ElectricCyan))
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = NeonViolet.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = currentEx.exercise.equipment.displayName.uppercase(),
                                            color = ElectricCyan,
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { onViewExerciseInfo(currentEx.exercise.id) }
                                    ) {
                                        Icon(Icons.Filled.Info, contentDescription = "Instructions", tint = ElectricCyan)
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = currentEx.exercise.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = currentEx.exercise.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )

                                if (currentEx.exercise.easierVariation != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = SurfaceElevated
                                    ) {
                                        Text(
                                            text = "Easier option: ${currentEx.exercise.easierVariation}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = NeonLime,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Sets Logging Table Header
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("SET", style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.width(36.dp))
                            Text(
                                if (currentEx.exercise.equipment == EquipmentType.BODYWEIGHT) "TARGET" else "KG",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                modifier = Modifier.width(80.dp),
                                textAlign = TextAlign.Center
                            )
                            Text("REPS", style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.width(80.dp), textAlign = TextAlign.Center)
                            Text("DONE", style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.width(48.dp), textAlign = TextAlign.Center)
                        }
                    }

                    // Logged Sets Rows
                    itemsIndexed(currentEx.sets) { setIndex, setItem ->
                        SetRowItem(
                            isBodyweight = currentEx.exercise.equipment == EquipmentType.BODYWEIGHT,
                            isTimed = currentEx.exercise.isTimed,
                            setState = setItem,
                            onToggleComplete = {
                                onLogSet(
                                    session.currentExerciseIndex,
                                    setIndex,
                                    setItem.actualWeightKg,
                                    setItem.actualReps
                                )
                            },
                            onWeightChange = { newW ->
                                onUpdateSetValues(
                                    session.currentExerciseIndex,
                                    setIndex,
                                    newW,
                                    setItem.actualReps
                                )
                            },
                            onRepsChange = { newR ->
                                onUpdateSetValues(
                                    session.currentExerciseIndex,
                                    setIndex,
                                    setItem.actualWeightKg,
                                    newR
                                )
                            }
                        )
                    }

                    // Exercise Navigation controls
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = onPrevExercise,
                                enabled = session.currentExerciseIndex > 0,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Previous")
                            }

                            Button(
                                onClick = onNextExercise,
                                enabled = session.currentExerciseIndex < session.exercises.lastIndex,
                                colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("next_exercise_button")
                            ) {
                                Text("Next Exercise")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    // Session Notes
                    item {
                        OutlinedTextField(
                            value = session.notes,
                            onValueChange = onUpdateNotes,
                            placeholder = { Text("Session notes or how you felt today...", color = TextMuted) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("session_notes_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonViolet,
                                unfocusedBorderColor = SurfaceBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                unfocusedContainerColor = SurfaceCard,
                                focusedContainerColor = SurfaceCard
                            ),
                            maxLines = 3
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }

        // Floating Rest Timer Overlay when active
        RestTimerOverlay(
            timerState = restTimerState,
            onAddSeconds = onAddRestSeconds,
            onTogglePause = onToggleRestPause,
            onSkip = onSkipRest,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // Cancel Workout Dialog
    if (showCancelConfirmDialog) {
        ConfirmDialog(
            title = "End Workout Early?",
            message = "Do you want to discard this workout or save your completed sets as a partial session?",
            confirmText = "Discard",
            isDestructive = true,
            onConfirm = {
                showCancelConfirmDialog = false
                onCancelWorkout()
            },
            onDismiss = { showCancelConfirmDialog = false }
        )
    }

    // Finish Workout Dialog
    if (showFinishOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showFinishOptionsDialog = false },
            title = {
                Text(
                    text = "Finish Session",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "You completed $completedSets of $totalSets planned sets.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (completedSets >= (totalSets * 0.7)) "Eligible for full workout completion rewards (+50 XP)!"
                        else "Will be saved honestly as a partial session based on actual completed sets.",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (completedSets >= (totalSets * 0.7)) NeonLime else QuestGold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFinishOptionsDialog = false
                        val status = if (completedSets >= (totalSets * 0.7)) CompletionStatus.COMPLETED else CompletionStatus.PARTIAL
                        onFinishWorkout(status)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                    modifier = Modifier.testTag("confirm_finish_workout_button")
                ) {
                    Text("Complete & Claim XP", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishOptionsDialog = false }) {
                    Text("Keep Training", color = TextSecondary)
                }
            },
            containerColor = SurfaceCard,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun SetRowItem(
    isBodyweight: Boolean,
    isTimed: Boolean,
    setState: ActiveSetState,
    onToggleComplete: () -> Unit,
    onWeightChange: (Double) -> Unit,
    onRepsChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("set_row_${setState.setNumber}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (setState.isCompleted) NeonLime.copy(alpha = 0.15f) else SurfaceCard
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = if (setState.isCompleted) Brush.horizontalGradient(listOf(NeonLime, NeonLime))
            else Brush.horizontalGradient(listOf(SurfaceBorder, SurfaceBorder))
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Set Number
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(SurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${setState.setNumber}",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Weight or Bodyweight Stepper
            if (isBodyweight) {
                Text(
                    text = "Bodyweight",
                    style = MaterialTheme.typography.bodySmall,
                    color = ElectricCyan,
                    modifier = Modifier.width(80.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                Row(
                    modifier = Modifier.width(96.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onWeightChange((setState.actualWeightKg - 2.5).coerceAtLeast(0.0)) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Filled.Remove, contentDescription = "Decrease Weight", tint = TextSecondary, modifier = Modifier.size(16.dp))
                    }
                    Text(
                        text = "${setState.actualWeightKg.toInt()}k",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = { onWeightChange(setState.actualWeightKg + 2.5) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Increase Weight", tint = TextSecondary, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Reps Stepper
            Row(
                modifier = Modifier.width(96.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onRepsChange((setState.actualReps - 1).coerceAtLeast(1)) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Filled.Remove, contentDescription = "Decrease Reps", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
                Text(
                    text = "${setState.actualReps}",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = { onRepsChange(setState.actualReps + 1) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Increase Reps", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
            }

            // Complete checkmark button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (setState.isCompleted) NeonLime else SurfaceElevated)
                    .clickable { onToggleComplete() }
                    .testTag("log_set_button_${setState.setNumber}"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Log Set",
                    tint = if (setState.isCompleted) Color.Black else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
