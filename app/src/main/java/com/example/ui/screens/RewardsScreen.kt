package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Achievement
import com.example.data.model.LevelInfo
import com.example.data.model.Quest
import com.example.ui.components.MedalItem
import com.example.ui.components.QuestCardItem
import com.example.ui.components.XpLevelCard
import com.example.ui.components.getAchievementIcon
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.QuestGold
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RewardsScreen(
    levelInfo: LevelInfo,
    quests: List<Quest>,
    achievements: List<Achievement>,
    onClaimQuest: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedAchievementForDetail by remember { mutableStateOf<Achievement?>(null) }

    val unlockedCount = remember(achievements) { achievements.count { it.isUnlocked } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                text = "Rewards & Achievements",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Earn XP, complete quests, and unlock mythical medals",
                style = MaterialTheme.typography.bodySmall,
                color = ElectricCyan
            )
        }

        // Tab selector: Medals vs Quests
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceCard,
            contentColor = TextPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = QuestGold
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Medals ($unlockedCount/${achievements.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Quests (${quests.count { it.isCompleted && !it.isClaimed }} claimable)", fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Level Status Card always on top
            item {
                XpLevelCard(levelInfo = levelInfo)
            }

            if (selectedTab == 0) {
                // Medals section
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Collectible Medals",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = "$unlockedCount of ${achievements.size} Unlocked",
                            style = MaterialTheme.typography.labelSmall,
                            color = QuestGold
                        )
                    }
                }

                items(achievements) { medal ->
                    MedalItem(
                        achievement = medal,
                        onClick = { selectedAchievementForDetail = medal }
                    )
                }
            } else {
                // Quests section
                item {
                    Text(
                        text = "Active Quests",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                }

                items(quests) { quest ->
                    QuestCardItem(
                        quest = quest,
                        onClaimReward = { onClaimQuest(quest.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // Achievement Detail Dialog
    selectedAchievementForDetail?.let { medal ->
        AlertDialog(
            onDismissRequest = { selectedAchievementForDetail = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = getAchievementIcon(medal.badgeIconName),
                        contentDescription = null,
                        tint = if (medal.isUnlocked) QuestGold else TextMuted,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(medal.title, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                }
            },
            text = {
                Column {
                    Text(medal.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("STATUS", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(
                                    text = if (medal.isUnlocked) "UNLOCKED" else "LOCKED",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (medal.isUnlocked) NeonLime else TextMuted,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("REWARD", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text("+${medal.xpReward} XP", style = MaterialTheme.typography.labelSmall, color = QuestGold, fontWeight = FontWeight.Bold)
                            }
                            if (medal.unlockedAtMs != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(medal.unlockedAtMs))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("EARNED ON", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                    Text(dateStr, style = MaterialTheme.typography.labelSmall, color = ElectricCyan)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedAchievementForDetail = null },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonViolet)
                ) {
                    Text("Close")
                }
            },
            containerColor = SurfaceCard,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
