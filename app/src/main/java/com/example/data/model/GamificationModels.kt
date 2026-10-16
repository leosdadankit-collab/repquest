package com.example.data.model

enum class QuestPeriod {
    DAILY,
    WEEKLY,
    CHALLENGE
}

data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val period: QuestPeriod,
    val targetValue: Int,
    val currentValue: Int,
    val xpReward: Int,
    val isCompleted: Boolean,
    val isClaimed: Boolean,
    val periodKey: String // e.g. "2026-10-16" for daily, "2026-W42" for weekly
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val badgeIconName: String,
    val xpReward: Int,
    val isUnlocked: Boolean,
    val unlockedAtMs: Long? = null,
    val currentProgress: Int = 0,
    val targetProgress: Int = 1
)

data class PersonalRecord(
    val exerciseId: String,
    val exerciseName: String,
    val maxWeightKg: Double,
    val maxReps: Int,
    val achievedAtMs: Long,
    val workoutRecordId: String
)

data class LevelInfo(
    val level: Int,
    val title: String,
    val totalXp: Int,
    val currentLevelXp: Int,
    val xpForNextLevel: Int,
    val progressFraction: Float
)

data class StreakInfo(
    val currentStreak: Int,
    val longestStreak: Int,
    val isPlannedRestDay: Boolean,
    val completedToday: Boolean,
    val streakExplanation: String,
    val weekDaysStatus: List<DayStatus>
)

data class DayStatus(
    val dayOfWeek: Int, // 1=Mon .. 7=Sun
    val dayLabel: String,
    val dateIso: String,
    val isToday: Boolean,
    val isCompleted: Boolean,
    val isPlannedRest: Boolean
)
