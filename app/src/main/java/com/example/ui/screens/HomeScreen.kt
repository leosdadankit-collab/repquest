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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Achievement
import com.example.data.model.CompletionStatus
import com.example.data.model.LevelInfo
import com.example.data.model.Quest
import com.example.data.model.StreakInfo
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutRecord
import com.example.data.model.WorkoutTemplate
import com.example.ui.components.QuestCardItem
import com.example.ui.components.StreakCard
import com.example.ui.components.XpLevelCard
import com.example.ui.components.getAchievementIcon
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
import java.time.LocalDate

@Composable
fun HomeScreen(
    userProfile: UserProfile,
    levelInfo: LevelInfo,
    streakInfo: StreakInfo,
    templates: List<WorkoutTemplate>,
    workoutRecords: List<WorkoutRecord>,
    quests: List<Quest>,
    achievements: List<Achievement>,
    onStartWorkout: (WorkoutTemplate) -> Unit,
    onNavigateToWorkouts: () -> Unit,
    onNavigateToRewards: () -> Unit,
    onClaimQuest: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val todayIso = remember { LocalDate.now().toString() }
    val isRestDay = streakInfo.isPlannedRestDay

    // Recommend workout based on today
    val recommendedTemplate = remember(templates, isRestDay) {
        if (isRestDay) {
            templates.find { it.id == "template_mobility_sanctuary" }
                ?: templates.firstOrNull()
        } else {
            templates.find { it.id == "template_full_body_quest_1" }
                ?: templates.firstOrNull()
        }
    }

    // Weekly workouts count
    val completedThisWeek = remember(workoutRecords, todayIso) {
        val today = LocalDate.now()
        val monday = today.minusDays((today.dayOfWeek.value - 1).toLong()).toString()
        workoutRecords.count {
            it.completionStatus == CompletionStatus.COMPLETED && it.dateIso >= monday
        }
    }

    // Motivational copy
    val motivationalQuote = remember(streakInfo.currentStreak, completedThisWeek, isRestDay) {
        when {
            streakInfo.currentStreak >= 5 -> "Unstoppable momentum! You are forging legendary discipline."
            streakInfo.currentStreak >= 2 -> "Great rhythm! Keep this consistency flame burning."
            isRestDay -> "Rest is where strength regenerates. Hydrate and recover well!"
            completedThisWeek > 0 -> "Solid progress this week. Step up for today's challenge!"
            else -> "Welcome to the quest! Every rep builds your future power."
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hello, ${userProfile.displayName}!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = motivationalQuote,
                        style = MaterialTheme.typography.bodySmall,
                        color = ElectricCyan,
                        maxLines = 2
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = SurfaceElevated,
                    modifier = Modifier.clickable { onNavigateToRewards() }
                ) {
                    Box(
                        modifier = Modifier.padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = "Rewards",
                            tint = QuestGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // XP & Level Card
        item {
            XpLevelCard(levelInfo = levelInfo)
        }

        // Streak & Weekly Dots Card
        item {
            StreakCard(streakInfo = streakInfo)
        }

        // Today's Recommendation & Primary Start Workout Card
        item {
            recommendedTemplate?.let { template ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("today_recommended_workout_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(NeonViolet, ElectricCyan))
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isRestDay) NeonLime.copy(alpha = 0.2f) else NeonViolet.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isRestDay) Icons.Filled.SelfImprovement else Icons.Filled.FitnessCenter,
                                        contentDescription = null,
                                        tint = if (isRestDay) NeonLime else NeonViolet,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isRestDay) "RECOVERY RECOMMENDATION" else "TODAY'S MISSION",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isRestDay) NeonLime else NeonViolet,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Text(
                                text = "${template.estimatedDurationMinutes} mins",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = template.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = template.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = QuestGold.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.Stars, contentDescription = null, tint = QuestGold, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+${template.xpReward} XP", color = QuestGold, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                            }

                            Button(
                                onClick = { onStartWorkout(template) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("start_recommended_workout_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Start Workout", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Weekly Target Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("weekly_target_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Weekly Workout Target",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary
                        )
                        Text(
                            text = "$completedThisWeek / ${userProfile.weeklyTargetWorkouts} sessions",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (completedThisWeek >= userProfile.weeklyTargetWorkouts) NeonLime else ElectricCyan
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    val progressFrac = (completedThisWeek.toFloat() / userProfile.weeklyTargetWorkouts.toFloat()).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progressFrac },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (completedThisWeek >= userProfile.weeklyTargetWorkouts) NeonLime else ElectricCyan,
                        trackColor = SurfaceElevated
                    )
                }
            }
        }

        // Active Quests
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Quests",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Text(
                    text = "See All",
                    style = MaterialTheme.typography.labelSmall,
                    color = ElectricCyan,
                    modifier = Modifier.clickable { onNavigateToRewards() }
                )
            }
        }

        items(quests.take(2)) { quest ->
            QuestCardItem(
                quest = quest,
                onClaimReward = { onClaimQuest(quest.id) }
            )
        }

        // Recently Earned Medals
        val unlockedMedals = achievements.filter { it.isUnlocked }
        if (unlockedMedals.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Medals Earned",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "${unlockedMedals.size} Unlocked",
                        style = MaterialTheme.typography.labelSmall,
                        color = QuestGold
                    )
                }
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(unlockedMedals.take(5)) { medal ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SurfaceCard,
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(listOf(QuestGold, NeonViolet))
                            ),
                            modifier = Modifier
                                .clickable { onNavigateToRewards() }
                                .testTag("recent_medal_${medal.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(QuestGold, NeonViolet))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = getAchievementIcon(medal.badgeIconName),
                                        contentDescription = medal.title,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = medal.title,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "+${medal.xpReward} XP",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = QuestGold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
