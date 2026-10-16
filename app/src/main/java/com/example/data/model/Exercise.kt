package com.example.data.model

enum class EquipmentType(val displayName: String) {
    BODYWEIGHT("Bodyweight"),
    DUMBBELLS("Dumbbells"),
    BARBELL("Barbell")
}

enum class MuscleGroup(val displayName: String) {
    CHEST("Chest"),
    BACK("Back"),
    SHOULDERS("Shoulders"),
    BICEPS("Biceps"),
    TRICEPS("Triceps"),
    LEGS("Legs / Quads & Glutes"),
    HAMSTRINGS("Hamstrings & Glutes"),
    CORE("Core & Abs"),
    FULL_BODY("Full Body"),
    MOBILITY("Mobility & Flexibility")
}

enum class ExerciseCategory(val displayName: String) {
    FULL_BODY("Full Body"),
    UPPER_BODY("Upper Body"),
    LOWER_BODY("Lower Body"),
    CORE("Core"),
    STRENGTH("Strength"),
    BODYWEIGHT("Bodyweight"),
    MOBILITY("Mobility & Stretch"),
    SHORT_CIRCUIT("Short Workout")
}

enum class DifficultyLevel(val displayName: String) {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced")
}

data class Exercise(
    val id: String,
    val name: String,
    val description: String,
    val primaryMuscle: MuscleGroup,
    val secondaryMuscles: List<MuscleGroup> = emptyList(),
    val equipment: EquipmentType,
    val category: ExerciseCategory,
    val difficulty: DifficultyLevel,
    val instructions: List<String>,
    val commonMistakes: List<String>,
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
