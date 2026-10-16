package com.example.domain

import com.example.data.model.Achievement
import com.example.data.model.DayStatus
import com.example.data.model.LevelInfo
import com.example.data.model.LoggedSet
import com.example.data.model.Quest
import com.example.data.model.StreakInfo
import com.example.data.model.WorkoutRecord
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

object GamificationEngine {

    const val WORKOUT_COMPLETION_XP = 50
    const val DAILY_QUEST_XP = 15
    const val WEEKLY_QUEST_XP = 50
    const val PERSONAL_RECORD_XP = 25

    fun xpRequiredForLevel(level: Int): Int {
        return 100 + (level - 1) * 50
    }

    fun getLevelTitle(level: Int): String {
        return when {
            level <= 2 -> "Novice Recruit"
            level <= 4 -> "Iron Explorer"
            level <= 6 -> "Dungeon Warrior"
            level <= 8 -> "Relentless Vanguard"
            level <= 11 -> "Champion of Reps"
            level <= 15 -> "Mythic Titan"
            else -> "Immortal Ascendant"
        }
    }

    fun getLevelInfo(totalXp: Int): LevelInfo {
        var level = 1
        var xpRemaining = totalXp
        var xpNeededForCurrent = xpRequiredForLevel(level)

        while (xpRemaining >= xpNeededForCurrent) {
            xpRemaining -= xpNeededForCurrent
            level++
            xpNeededForCurrent = xpRequiredForLevel(level)
        }

        val progressFraction = if (xpNeededForCurrent > 0) {
            (xpRemaining.toFloat() / xpNeededForCurrent.toFloat()).coerceIn(0f, 1f)
        } else 0f

        return LevelInfo(
            level = level,
            title = getLevelTitle(level),
            totalXp = totalXp,
            currentLevelXp = xpRemaining,
            xpForNextLevel = xpNeededForCurrent,
            progressFraction = progressFraction
        )
    }

    /**
     * Calculates streak based on local calendar dates and user's scheduled training days.
     * Planned rest days do not break the streak.
     */
    fun calculateStreak(
        completedDateIsos: Set<String>,
        scheduledDaysOfWeek: List<Int>, // 1=Mon .. 7=Sun
        today: LocalDate = LocalDate.now()
    ): StreakInfo {
        val todayIso = today.toString()
        val todayCompleted = completedDateIsos.contains(todayIso)
        val todayDayOfWeek = today.dayOfWeek.value
        val isTodayScheduled = scheduledDaysOfWeek.contains(todayDayOfWeek)
        val isRestDayToday = !isTodayScheduled

        // Build week status (Monday to Sunday)
        val startOfWeek = today.minusDays((today.dayOfWeek.value - 1).toLong())
        val weekStatusList = mutableListOf<DayStatus>()

        for (i in 0 until 7) {
            val date = startOfWeek.plusDays(i.toLong())
            val dateIso = date.toString()
            val dayVal = date.dayOfWeek.value
            val isScheduled = scheduledDaysOfWeek.contains(dayVal)
            val isCompleted = completedDateIsos.contains(dateIso)
            val isCurrentDay = date == today

            val label = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase()

            weekStatusList.add(
                DayStatus(
                    dayOfWeek = dayVal,
                    dayLabel = label.take(2),
                    dateIso = dateIso,
                    isToday = isCurrentDay,
                    isCompleted = isCompleted,
                    isPlannedRest = !isScheduled
                )
            )
        }

        // Count streak backwards
        var streak = 0
        var checkDate = if (todayCompleted) today else today.minusDays(1)

        // Safety bound: up to 365 days
        for (i in 0..365) {
            val dateIso = checkDate.toString()
            val dayVal = checkDate.dayOfWeek.value
            val isScheduled = scheduledDaysOfWeek.contains(dayVal)
            val wasCompleted = completedDateIsos.contains(dateIso)

            if (wasCompleted) {
                streak++
            } else if (isScheduled) {
                // Missed a planned workout day -> streak breaks
                break
            } else {
                // Planned rest day -> continues streak without breaking
            }
            checkDate = checkDate.minusDays(1)
        }

        val explanation = when {
            todayCompleted -> "You logged today's workout! Streak active."
            isRestDayToday -> "Today is a scheduled recovery day. Your streak is safe!"
            else -> "Today is a scheduled workout day. Complete your workout to continue your streak!"
        }

        return StreakInfo(
            currentStreak = streak,
            longestStreak = maxOf(streak, 0),
            isPlannedRestDay = isRestDayToday,
            completedToday = todayCompleted,
            streakExplanation = explanation,
            weekDaysStatus = weekStatusList
        )
    }

    /**
     * Re-evaluates all achievements against the full workout history to guarantee
     * consistency and correct state after additions, edits, or deletions.
     */
    fun evaluateAchievements(
        workoutRecords: List<WorkoutRecord>,
        allSets: List<LoggedSet>,
        hasCustomWorkout: Boolean,
        currentStreak: Int
    ): Map<String, Pair<Boolean, Int>> {
        // Map of achievementId -> Pair(isUnlocked, progress)
        val result = mutableMapOf<String, Pair<Boolean, Int>>()

        val completedRecords = workoutRecords.filter { it.completionStatus.name == "COMPLETED" }
        val workoutCount = completedRecords.size
        val uniqueDays = completedRecords.map { it.dateIso }.toSet().size
        val totalSetsCount = allSets.filter { it.isCompleted }.size
        val hasPR = allSets.any { it.isPersonalRecord }

        result["first_workout"] = Pair(workoutCount >= 1, minOf(workoutCount, 1))
        result["five_workouts"] = Pair(workoutCount >= 5, minOf(workoutCount, 5))
        result["ten_workouts"] = Pair(workoutCount >= 10, minOf(workoutCount, 10))
        result["monthly_milestone"] = Pair(workoutCount >= 15, minOf(workoutCount, 15))
        result["seven_days"] = Pair(uniqueDays >= 7, minOf(uniqueDays, 7))
        result["ten_sets"] = Pair(totalSetsCount >= 10, minOf(totalSetsCount, 10))
        result["first_pr"] = Pair(hasPR, if (hasPR) 1 else 0)
        result["custom_workout"] = Pair(hasCustomWorkout, if (hasCustomWorkout) 1 else 0)
        result["consistency_champ"] = Pair(currentStreak >= 3, minOf(currentStreak, 3))
        result["week_consistency"] = Pair(workoutCount >= 3, minOf(workoutCount, 1))
        result["weekly_goal"] = Pair(workoutCount >= 3, minOf(workoutCount, 1))
        result["comeback_hero"] = Pair(workoutCount >= 2, minOf(workoutCount, 1))

        return result
    }
}
