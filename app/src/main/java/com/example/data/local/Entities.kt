package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.data.model.CompletionStatus
import com.example.data.model.DifficultyLevel
import com.example.data.model.EquipmentType
import com.example.data.model.ExerciseCategory
import com.example.data.model.FitnessGoal
import com.example.data.model.MuscleGroup
import com.example.data.model.QuestPeriod

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val primaryMuscle: String,
    val secondaryMuscles: String, // comma separated
    val equipment: String,
    val category: String,
    val difficulty: String,
    val instructions: String, // pipe separated or newline separated
    val commonMistakes: String,
    val easierVariation: String? = null,
    val progression: String? = null,
    val defaultSets: Int = 3,
    val defaultReps: Int = 10,
    val defaultWeightKg: Double = 0.0,
    val defaultRestSeconds: Int = 60,
    val isTimed: Boolean = false,
    val defaultDurationSeconds: Int = 0,
    val isFavorite: Boolean = false
)

@Entity(tableName = "workout_templates")
data class WorkoutTemplateEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val category: String,
    val difficulty: String,
    val estimatedDurationMinutes: Int,
    val isCustom: Boolean = false,
    val isFavorite: Boolean = false,
    val xpReward: Int = 50
)

@Entity(
    tableName = "template_exercises",
    primaryKeys = ["templateId", "orderIndex"]
)
data class TemplateExerciseEntity(
    val templateId: String,
    val exerciseId: String,
    val exerciseName: String,
    val equipment: String,
    val targetSets: Int,
    val targetReps: Int,
    val targetWeightKg: Double,
    val restSeconds: Int,
    val isTimed: Boolean,
    val targetDurationSeconds: Int,
    val orderIndex: Int
)

@Entity(tableName = "workout_records")
data class WorkoutRecordEntity(
    @PrimaryKey val id: String,
    val templateId: String?,
    val workoutTitle: String,
    val dateIso: String,
    val startTimeMs: Long,
    val endTimeMs: Long,
    val durationSeconds: Int,
    val completionStatus: String,
    val totalVolumeKg: Double,
    val totalSetsCompleted: Int,
    val xpEarned: Int,
    val notes: String = ""
)

@Entity(tableName = "logged_sets")
data class LoggedSetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutRecordId: String,
    val exerciseId: String,
    val exerciseName: String,
    val setNumber: Int,
    val weightKg: Double,
    val reps: Int,
    val durationSeconds: Int = 0,
    val isCompleted: Boolean = true,
    val isPersonalRecord: Boolean = false
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = "default_user",
    val displayName: String = "Adventurer",
    val ageRange: String = "25-34",
    val experienceLevel: String = DifficultyLevel.BEGINNER.name,
    val primaryGoal: String = FitnessGoal.CONSISTENCY.name,
    val preferredTrainingDays: String = "1,3,5", // comma-separated
    val preferredDurationMinutes: Int = 30,
    val hasBarbell: Boolean = true,
    val hasDumbbells: Boolean = true,
    val hasBodyweight: Boolean = true,
    val avoidedMovements: String = "",
    val heightCm: Double? = null,
    val weightKg: Double? = null,
    val preferredWeightUnit: String = "kg",
    val hasCompletedOnboarding: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val reminderHour: Int = 18,
    val reminderMinute: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastWorkoutDateIso: String = "",
    val totalXp: Int = 0,
    val currentLevel: Int = 1,
    val weeklyTargetWorkouts: Int = 3,
    val dailyTargetMinutes: Int = 30
)

@Entity(tableName = "quests")
data class QuestEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val period: String,
    val targetValue: Int,
    val currentValue: Int,
    val xpReward: Int,
    val isCompleted: Boolean,
    val isClaimed: Boolean,
    val periodKey: String
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val badgeIconName: String,
    val xpReward: Int,
    val isUnlocked: Boolean,
    val unlockedAtMs: Long? = null,
    val currentProgress: Int = 0,
    val targetProgress: Int = 1
)

@Entity(tableName = "personal_records")
data class PersonalRecordEntity(
    @PrimaryKey val exerciseId: String,
    val exerciseName: String,
    val maxWeightKg: Double,
    val maxReps: Int,
    val achievedAtMs: Long,
    val workoutRecordId: String
)
