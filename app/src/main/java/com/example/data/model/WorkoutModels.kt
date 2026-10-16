package com.example.data.model

enum class CompletionStatus {
    COMPLETED,
    PARTIAL,
    CANCELLED
}

data class TemplateExercise(
    val exerciseId: String,
    val exerciseName: String,
    val equipment: EquipmentType,
    val targetSets: Int = 3,
    val targetReps: Int = 10,
    val targetWeightKg: Double = 0.0,
    val restSeconds: Int = 60,
    val isTimed: Boolean = false,
    val targetDurationSeconds: Int = 0,
    val orderIndex: Int = 0
)

data class WorkoutTemplate(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val category: ExerciseCategory,
    val difficulty: DifficultyLevel,
    val estimatedDurationMinutes: Int,
    val exercises: List<TemplateExercise>,
    val isCustom: Boolean = false,
    val isFavorite: Boolean = false,
    val xpReward: Int = 50
)

data class LoggedSet(
    val id: Long = 0,
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

data class WorkoutRecord(
    val id: String,
    val templateId: String?,
    val workoutTitle: String,
    val dateIso: String, // e.g. "2026-10-16"
    val startTimeMs: Long,
    val endTimeMs: Long,
    val durationSeconds: Int,
    val completionStatus: CompletionStatus,
    val totalVolumeKg: Double,
    val totalSetsCompleted: Int,
    val xpEarned: Int,
    val notes: String = "",
    val loggedSets: List<LoggedSet> = emptyList()
)
