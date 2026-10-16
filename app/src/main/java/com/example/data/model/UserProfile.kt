package com.example.data.model

enum class FitnessGoal(val title: String, val subtitle: String) {
    CONSISTENCY("Build Consistency", "Form a lasting habit with steady, achievable routines"),
    STRENGTH("Improve Strength", "Master fundamental progressive resistance training"),
    FITNESS("General Fitness & Health", "Enhance full-body energy, stamina, and agility"),
    BODY_COMPOSITION("Body Composition", "Build lean muscle and improve overall conditioning")
}

data class UserProfile(
    val id: String = "default_user",
    val displayName: String = "Adventurer",
    val ageRange: String = "25-34",
    val experienceLevel: DifficultyLevel = DifficultyLevel.BEGINNER,
    val primaryGoal: FitnessGoal = FitnessGoal.CONSISTENCY,
    val preferredTrainingDays: List<Int> = listOf(1, 3, 5), // 1=Mon, 3=Wed, 5=Fri
    val preferredDurationMinutes: Int = 30,
    val hasBarbell: Boolean = true,
    val hasDumbbells: Boolean = true,
    val hasBodyweight: Boolean = true,
    val avoidedMovements: List<String> = emptyList(),
    val heightCm: Double? = null,
    val weightKg: Double? = null,
    val preferredWeightUnit: String = "kg", // "kg" or "lbs"
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
