package com.example

import com.example.data.model.CompletionStatus
import com.example.data.model.LoggedSet
import com.example.data.model.WorkoutRecord
import com.example.domain.GamificationEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class GamificationEngineTest {

    @Test
    fun testLevelProgressionCalculation() {
        // Level 1: 0 to 99 XP
        val lvl1 = GamificationEngine.getLevelInfo(0)
        assertEquals(1, lvl1.level)
        assertEquals("Novice Recruit", lvl1.title)
        assertEquals(0, lvl1.currentLevelXp)
        assertEquals(100, lvl1.xpForNextLevel)
        assertEquals(0f, lvl1.progressFraction, 0.001f)

        // Level 2: 100 XP (100 needed for lvl 1->2)
        val lvl2 = GamificationEngine.getLevelInfo(100)
        assertEquals(2, lvl2.level)
        assertEquals(0, lvl2.currentLevelXp)
        assertEquals(150, lvl2.xpForNextLevel)

        // Level 2 midway: 175 XP -> 75 into Level 2 (which needs 150) -> fraction 0.5
        val lvl2Mid = GamificationEngine.getLevelInfo(175)
        assertEquals(2, lvl2Mid.level)
        assertEquals(75, lvl2Mid.currentLevelXp)
        assertEquals(0.5f, lvl2Mid.progressFraction, 0.001f)

        // Higher Level check
        val lvl3 = GamificationEngine.getLevelInfo(250)
        assertEquals(3, lvl3.level)
    }

    @Test
    fun testStreakWithPlannedRestDays() {
        val today = LocalDate.of(2026, 10, 16) // A Friday (day 5)
        val scheduledDays = listOf(1, 3, 5) // Mon, Wed, Fri

        // Completed Mon (12th), Wed (14th), and Fri (16th)
        val completedDates = setOf(
            "2026-10-12",
            "2026-10-14",
            "2026-10-16"
        )

        val streak = GamificationEngine.calculateStreak(
            completedDateIsos = completedDates,
            scheduledDaysOfWeek = scheduledDays,
            today = today
        )

        // Tuesday (13th) and Thursday (15th) were planned rest days, so streak is maintained across all 3 workouts!
        assertEquals(3, streak.currentStreak)
        assertTrue(streak.completedToday)
        assertFalse(streak.isPlannedRestDay)
    }

    @Test
    fun testStreakBreaksOnMissedScheduledDay() {
        val today = LocalDate.of(2026, 10, 16) // Friday (day 5)
        val scheduledDays = listOf(1, 3, 5) // Mon, Wed, Fri

        // Completed Mon (12th), but missed Wed (14th), completed Fri (16th)
        val completedDates = setOf(
            "2026-10-12",
            "2026-10-16"
        )

        val streak = GamificationEngine.calculateStreak(
            completedDateIsos = completedDates,
            scheduledDaysOfWeek = scheduledDays,
            today = today
        )

        // Since Wednesday was missed, streak resets to 1 (just Friday)
        assertEquals(1, streak.currentStreak)
    }

    @Test
    fun testAchievementEvaluationFromHistory() {
        val records = listOf(
            WorkoutRecord(
                id = "w1",
                templateId = null,
                workoutTitle = "Full Body Quest I",
                dateIso = "2026-10-14",
                startTimeMs = 1000L,
                endTimeMs = 2000L,
                durationSeconds = 1800,
                completionStatus = CompletionStatus.COMPLETED,
                totalVolumeKg = 500.0,
                totalSetsCompleted = 5,
                xpEarned = 50
            ),
            WorkoutRecord(
                id = "w2",
                templateId = null,
                workoutTitle = "Upper Body Conqueror",
                dateIso = "2026-10-16",
                startTimeMs = 3000L,
                endTimeMs = 4000L,
                durationSeconds = 2100,
                completionStatus = CompletionStatus.COMPLETED,
                totalVolumeKg = 800.0,
                totalSetsCompleted = 6,
                xpEarned = 50
            )
        )

        val sets = listOf(
            LoggedSet(1, "w1", "bw_pushup", "Push-Up", 1, 0.0, 10, isCompleted = true),
            LoggedSet(2, "w1", "bw_pushup", "Push-Up", 2, 0.0, 10, isCompleted = true),
            LoggedSet(3, "w1", "bw_pushup", "Push-Up", 3, 0.0, 10, isCompleted = true),
            LoggedSet(4, "w1", "bw_squat", "Bodyweight Squat", 1, 0.0, 12, isCompleted = true),
            LoggedSet(5, "w1", "bw_squat", "Bodyweight Squat", 2, 0.0, 12, isCompleted = true),
            LoggedSet(6, "w2", "db_floor_press", "Dumbbell Floor Press", 1, 14.0, 10, isCompleted = true, isPersonalRecord = true),
            LoggedSet(7, "w2", "db_floor_press", "Dumbbell Floor Press", 2, 14.0, 10, isCompleted = true),
            LoggedSet(8, "w2", "db_floor_press", "Dumbbell Floor Press", 3, 14.0, 10, isCompleted = true),
            LoggedSet(9, "w2", "db_bent_over_row", "Dumbbell Bent-Over Row", 1, 12.0, 10, isCompleted = true),
            LoggedSet(10, "w2", "db_bent_over_row", "Dumbbell Bent-Over Row", 2, 12.0, 10, isCompleted = true),
            LoggedSet(11, "w2", "db_bent_over_row", "Dumbbell Bent-Over Row", 3, 12.0, 10, isCompleted = true)
        )

        val eval = GamificationEngine.evaluateAchievements(
            workoutRecords = records,
            allSets = sets,
            hasCustomWorkout = true,
            currentStreak = 2
        )

        // First workout unlocked
        assertTrue(eval["first_workout"]!!.first)
        // 10 sets logged unlocked (11 completed)
        assertTrue(eval["ten_sets"]!!.first)
        // PR unlocked
        assertTrue(eval["first_pr"]!!.first)
        // Custom workout unlocked
        assertTrue(eval["custom_workout"]!!.first)
        // 5 workouts not yet unlocked (only 2 done)
        assertFalse(eval["five_workouts"]!!.first)
        assertEquals(2, eval["five_workouts"]!!.second)
    }
}
