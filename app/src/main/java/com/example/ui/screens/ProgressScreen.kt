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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.CompletionStatus
import com.example.data.model.LevelInfo
import com.example.data.model.PersonalRecord
import com.example.data.model.StreakInfo
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutRecord
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
import java.time.LocalDate

@Composable
fun ProgressScreen(
    userProfile: UserProfile,
    levelInfo: LevelInfo,
    streakInfo: StreakInfo,
    workoutRecords: List<WorkoutRecord>,
    personalRecords: List<PersonalRecord>,
    onDeleteRecord: (String) -> Unit,
    onUpdateNotes: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedRecordForDetail by remember { mutableStateOf<WorkoutRecord?>(null) }
    var recordToDelete by remember { mutableStateOf<WorkoutRecord?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Screen Title
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                text = "Progress & History",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Track consistency, volume records, and training logs",
                style = MaterialTheme.typography.bodySmall,
                color = ElectricCyan
            )
        }

        // Tabs: Analytics vs History Log
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceCard,
            contentColor = TextPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = NeonViolet
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Analytics & PRs", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Filled.BarChart, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Workout History (${workoutRecords.size})", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Filled.History, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTab == 0) {
            AnalyticsTabContent(
                levelInfo = levelInfo,
                streakInfo = streakInfo,
                workoutRecords = workoutRecords,
                personalRecords = personalRecords
            )
        } else {
            HistoryTabContent(
                records = workoutRecords,
                onSelectRecord = { selectedRecordForDetail = it },
                onConfirmDelete = { recordToDelete = it }
            )
        }
    }

    // Detail Dialog for Session
    selectedRecordForDetail?.let { record ->
        WorkoutDetailDialog(
            record = record,
            onDismiss = { selectedRecordForDetail = null },
            onDelete = {
                selectedRecordForDetail = null
                recordToDelete = record
            },
            onSaveNotes = { notes ->
                onUpdateNotes(record.id, notes)
            }
        )
    }

    // Confirm Delete Dialog
    recordToDelete?.let { record ->
        ConfirmDialog(
            title = "Delete Workout Record?",
            message = "Deleting '${record.workoutTitle}' will update your total XP, streak, and personal records accordingly.",
            confirmText = "Delete Record",
            isDestructive = true,
            onConfirm = {
                onDeleteRecord(record.id)
                recordToDelete = null
            },
            onDismiss = { recordToDelete = null }
        )
    }
}

@Composable
fun AnalyticsTabContent(
    levelInfo: LevelInfo,
    streakInfo: StreakInfo,
    workoutRecords: List<WorkoutRecord>,
    personalRecords: List<PersonalRecord>
) {
    val totalVolumeKg = remember(workoutRecords) {
        workoutRecords.sumOf { it.totalVolumeKg }
    }
    val totalSetsCount = remember(workoutRecords) {
        workoutRecords.sumOf { it.totalSetsCompleted }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Stats Summary Cards Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "TOTAL SESSIONS",
                    value = "${workoutRecords.size}",
                    color = NeonViolet,
                    icon = Icons.Filled.FitnessCenter,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "SETS LOGGED",
                    value = "$totalSetsCount",
                    color = ElectricCyan,
                    icon = Icons.Filled.TrendingUp,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "TOTAL VOLUME",
                    value = "${totalVolumeKg.toInt()} kg",
                    color = QuestGold,
                    icon = Icons.Filled.Stars,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "BEST STREAK",
                    value = "${streakInfo.longestStreak} days",
                    color = NeonLime,
                    icon = Icons.Filled.EmojiEvents,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Weekly Frequency Visualizer
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Recent Training Consistency",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "Workouts logged across the past 4 weeks",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 4-week bar representation
                    val today = LocalDate.now()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        for (w in 3 downTo 0) {
                            val startWeek = today.minusWeeks(w.toLong()).minusDays((today.dayOfWeek.value - 1).toLong())
                            val endWeek = startWeek.plusDays(6)
                            val count = workoutRecords.count {
                                it.completionStatus == CompletionStatus.COMPLETED &&
                                        it.dateIso >= startWeek.toString() &&
                                        it.dateIso <= endWeek.toString()
                            }
                            val barHeight = (count * 20 + 20).coerceAtMost(100).dp

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$count", style = MaterialTheme.typography.labelSmall, color = if (count > 0) NeonLime else TextMuted)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(28.dp)
                                        .height(barHeight)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(
                                            if (count > 0) Brush.verticalGradient(listOf(NeonViolet, ElectricCyan))
                                            else Brush.verticalGradient(listOf(SurfaceElevated, SurfaceElevated))
                                        )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (w == 0) "This Wk" else "${w}w ago",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Personal Records Section
        item {
            Text(
                text = "Personal Records (PRs)",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
        }

        if (personalRecords.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No Personal Records Logged Yet", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                        Text("Complete workouts with weights or reps to forge new PRs!", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
            }
        } else {
            items(personalRecords) { pr ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(QuestGold, QuestGold))
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(pr.exerciseName, style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text("Best Performance", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = QuestGold.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (pr.maxWeightKg > 0) "${pr.maxWeightKg} kg × ${pr.maxReps} reps" else "${pr.maxReps} reps",
                                color = QuestGold,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun HistoryTabContent(
    records: List<WorkoutRecord>,
    onSelectRecord: (WorkoutRecord) -> Unit,
    onConfirmDelete: (WorkoutRecord) -> Unit
) {
    if (records.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Filled.History, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text("No Workouts Recorded", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Text("Your completed sessions will appear here with full logs and volume records.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(records) { record ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectRecord(record) }
                        .testTag("workout_record_item_${record.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = record.dateIso,
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricCyan
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (record.completionStatus == CompletionStatus.COMPLETED) NeonLime.copy(alpha = 0.2f) else QuestGold.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = record.completionStatus.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (record.completionStatus == CompletionStatus.COMPLETED) NeonLime else QuestGold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = record.workoutTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${record.totalSetsCompleted} sets",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "• ${record.durationSeconds / 60}m",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                                if (record.totalVolumeKg > 0) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "• ${record.totalVolumeKg.toInt()} kg",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = QuestGold.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "+${record.xpEarned} XP",
                                    color = QuestGold,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun WorkoutDetailDialog(
    record: WorkoutRecord,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onSaveNotes: (String) -> Unit
) {
    var notesText by remember { mutableStateOf(record.notes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(record.workoutTitle, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                Text("${record.dateIso} • ${record.durationSeconds / 60} minutes", style = MaterialTheme.typography.bodySmall, color = ElectricCyan)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SETS", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text("${record.totalSetsCompleted}", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("VOLUME", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text("${record.totalVolumeKg.toInt()} kg", style = MaterialTheme.typography.titleMedium, color = QuestGold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("XP", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text("+${record.xpEarned}", style = MaterialTheme.typography.titleMedium, color = NeonLime)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sets List
                Text("Logged Sets", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                LazyColumn(modifier = Modifier.height(120.dp)) {
                    items(record.loggedSets) { setItem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${setItem.exerciseName} (Set ${setItem.setNumber})",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            Text(
                                text = if (setItem.weightKg > 0) "${setItem.weightKg}kg × ${setItem.reps}" else "${setItem.reps} reps",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (setItem.isPersonalRecord) QuestGold else TextPrimary,
                                fontWeight = if (setItem.isPersonalRecord) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text("Notes", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = notesText,
                    onValueChange = {
                        notesText = it
                        onSaveNotes(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonViolet,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = NeonViolet)
            ) {
                Text("Done")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDelete,
                colors = ButtonDefaults.textButtonColors(contentColor = VibrantCoral)
            ) {
                Text("Delete Log")
            }
        },
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun StatCard(
    title: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, color = color, fontWeight = FontWeight.Black)
        }
    }
}
