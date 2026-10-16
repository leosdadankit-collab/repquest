package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Achievement
import com.example.data.model.DayStatus
import com.example.data.model.LevelInfo
import com.example.data.model.Quest
import com.example.data.model.QuestPeriod
import com.example.data.model.StreakInfo
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.NeonVioletGlow
import com.example.ui.theme.QuestGold
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VibrantCoral

@Composable
fun XpLevelCard(
    levelInfo: LevelInfo,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(targetValue = levelInfo.progressFraction, label = "xp_progress")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("xp_level_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(NeonViolet, ElectricCyan)))
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(NeonViolet, ElectricCyan)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "L${levelInfo.level}",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = levelInfo.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = "${levelInfo.totalXp} Total XP",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElectricCyan
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceElevated
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Stars,
                            contentDescription = null,
                            tint = QuestGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+${levelInfo.xpForNextLevel - levelInfo.currentLevelXp} to L${levelInfo.level + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = QuestGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = NeonViolet,
                trackColor = SurfaceElevated
            )

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${levelInfo.currentLevelXp} XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    text = "${levelInfo.xpForNextLevel} XP needed",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
fun StreakCard(
    streakInfo: StreakInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("streak_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(QuestGold, VibrantCoral)))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(QuestGold, VibrantCoral)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalFireDepartment,
                            contentDescription = "Streak Fire",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${streakInfo.currentStreak}",
                                style = MaterialTheme.typography.headlineMedium,
                                color = QuestGold,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (streakInfo.currentStreak == 1) "Day Streak" else "Days Streak",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "Best Streak: ${streakInfo.longestStreak} days",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                if (streakInfo.isPlannedRestDay) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceElevated
                    ) {
                        Text(
                            text = "Rest Day",
                            color = NeonLime,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else if (streakInfo.completedToday) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NeonLime.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Done Today!",
                            color = NeonLime,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Week days dots
            WeeklyCalendarStrip(dayStatuses = streakInfo.weekDaysStatus)

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = streakInfo.streakExplanation,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun WeeklyCalendarStrip(
    dayStatuses: List<DayStatus>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        dayStatuses.forEach { day ->
            val bg = when {
                day.isCompleted -> NeonLime
                day.isToday -> NeonViolet
                day.isPlannedRest -> SurfaceElevated
                else -> SurfaceBorder
            }
            val textColor = when {
                day.isCompleted -> Color.Black
                day.isToday -> Color.White
                else -> TextSecondary
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = day.dayLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (day.isToday) ElectricCyan else TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(bg)
                        .then(
                            if (day.isToday) Modifier.border(2.dp, ElectricCyan, CircleShape) else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (day.isCompleted) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Completed",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    } else if (day.isPlannedRest) {
                        Text("R", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Text("•", color = TextMuted, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun QuestCardItem(
    quest: Quest,
    onClaimReward: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("quest_item_${quest.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = if (quest.isCompleted && !quest.isClaimed) Brush.horizontalGradient(listOf(QuestGold, NeonLime))
            else Brush.horizontalGradient(listOf(SurfaceBorder, SurfaceBorder))
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (quest.period == QuestPeriod.DAILY) NeonViolet.copy(alpha = 0.2f) else ElectricCyan.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (quest.period == QuestPeriod.DAILY) "DAILY QUEST" else "WEEKLY QUEST",
                        color = if (quest.period == QuestPeriod.DAILY) NeonVioletGlow else ElectricCyan,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = QuestGold.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Stars,
                            contentDescription = null,
                            tint = QuestGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+${quest.xpReward} XP",
                            color = QuestGold,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = quest.title,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary
            )
            Text(
                text = quest.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))
            val progressFrac = (quest.currentValue.toFloat() / quest.targetValue.toFloat()).coerceIn(0f, 1f)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    LinearProgressIndicator(
                        progress = { progressFrac },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (quest.isCompleted) NeonLime else NeonViolet,
                        trackColor = SurfaceElevated
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${minOf(quest.currentValue, quest.targetValue)} / ${quest.targetValue}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                if (quest.isCompleted && !quest.isClaimed) {
                    Button(
                        onClick = onClaimReward,
                        colors = ButtonDefaults.buttonColors(containerColor = QuestGold),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("claim_quest_button_${quest.id}")
                    ) {
                        Text("Claim", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else if (quest.isClaimed) {
                    Text("Claimed", color = NeonLime, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun MedalItem(
    achievement: Achievement,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = getAchievementIcon(achievement.badgeIconName)
    val isUnlocked = achievement.isUnlocked

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("achievement_badge_${achievement.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isUnlocked) SurfaceElevated else SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = if (isUnlocked) Brush.horizontalGradient(listOf(QuestGold, NeonViolet))
            else Brush.horizontalGradient(listOf(SurfaceBorder, SurfaceBorder))
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) Brush.linearGradient(listOf(QuestGold, NeonViolet))
                        else Brush.linearGradient(listOf(SurfaceBorder, SurfaceElevated))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isUnlocked) icon else Icons.Filled.Lock,
                    contentDescription = achievement.title,
                    tint = if (isUnlocked) Color.White else TextMuted,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isUnlocked) TextPrimary else TextMuted,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isUnlocked) TextSecondary else TextMuted,
                    maxLines = 2
                )

                if (!isUnlocked) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Progress: ${achievement.currentProgress}/${achievement.targetProgress}",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isUnlocked) QuestGold.copy(alpha = 0.2f) else SurfaceElevated
            ) {
                Text(
                    text = "+${achievement.xpReward} XP",
                    color = if (isUnlocked) QuestGold else TextMuted,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }
        }
    }
}

fun getAchievementIcon(iconName: String): ImageVector {
    return when (iconName) {
        "military_tech" -> Icons.Filled.MilitaryTech
        "explore" -> Icons.Filled.Explore
        "shield" -> Icons.Filled.Shield
        "stars" -> Icons.Filled.Stars
        "local_fire_department" -> Icons.Filled.LocalFireDepartment
        "emoji_events" -> Icons.Filled.EmojiEvents
        "construction" -> Icons.Filled.Construction
        "trending_up" -> Icons.Filled.TrendingUp
        "fitness_center" -> Icons.Filled.FitnessCenter
        "bolt" -> Icons.Filled.Bolt
        "auto_awesome" -> Icons.Filled.AutoAwesome
        "workspace_premium" -> Icons.Filled.WorkspacePremium
        else -> Icons.Filled.EmojiEvents
    }
}
