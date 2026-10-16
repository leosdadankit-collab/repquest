package com.example.data.repository

import com.example.data.local.AchievementEntity
import com.example.data.local.AppDatabase
import com.example.data.local.ExerciseEntity
import com.example.data.local.InitialData
import com.example.data.local.LoggedSetEntity
import com.example.data.local.PersonalRecordEntity
import com.example.data.local.QuestEntity
import com.example.data.local.TemplateExerciseEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.WorkoutRecordEntity
import com.example.data.local.WorkoutTemplateEntity
import com.example.data.model.Achievement
import com.example.data.model.CompletionStatus
import com.example.data.model.DifficultyLevel
import com.example.data.model.EquipmentType
import com.example.data.model.Exercise
import com.example.data.model.ExerciseCategory
import com.example.data.model.FitnessGoal
import com.example.data.model.LoggedSet
import com.example.data.model.MuscleGroup
import com.example.data.model.PersonalRecord
import com.example.data.model.Quest
import com.example.data.model.QuestPeriod
import com.example.data.model.TemplateExercise
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutRecord
import com.example.data.model.WorkoutTemplate
import com.example.domain.GamificationEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.util.UUID

class RepQuestRepository(private val database: AppDatabase) {

    private val exerciseDao = database.exerciseDao()
    private val workoutDao = database.workoutDao()
    private val gamificationDao = database.gamificationDao()

    // Observe user profile
    val userProfile: Flow<UserProfile> = gamificationDao.getUserProfileFlow().map { entity ->
        entity?.toDomain() ?: UserProfile()
    }

    // Observe exercises
    val exercises: Flow<List<Exercise>> = exerciseDao.getAllExercises().map { list ->
        list.map { it.toDomain() }
    }

    // Observe templates
    val templates: Flow<List<WorkoutTemplate>> = workoutDao.getAllTemplates().map { list ->
        list.map { entity ->
            val exEntities = workoutDao.getTemplateExercises(entity.id)
            entity.toDomain(exEntities.map { it.toDomain() })
        }
    }

    // Observe workout records
    val workoutRecords: Flow<List<WorkoutRecord>> = workoutDao.getAllRecords().map { records ->
        records.map { recordEntity ->
            val sets = workoutDao.getLoggedSets(recordEntity.id)
            recordEntity.toDomain(sets.map { it.toDomain() })
        }
    }

    // Observe quests
    val quests: Flow<List<Quest>> = gamificationDao.getAllQuestsFlow().map { list ->
        list.map { it.toDomain() }
    }

    // Observe achievements
    val achievements: Flow<List<Achievement>> = gamificationDao.getAllAchievementsFlow().map { list ->
        list.map { it.toDomain() }
    }

    // Observe personal records
    val personalRecords: Flow<List<PersonalRecord>> = gamificationDao.getAllPRsFlow().map { list ->
        list.map { it.toDomain() }
    }

    suspend fun getExerciseById(id: String): Exercise? {
        return exerciseDao.getExerciseById(id)?.toDomain()
    }

    suspend fun getTemplateById(id: String): WorkoutTemplate? {
        val entity = workoutDao.getTemplateById(id) ?: return null
        val exercises = workoutDao.getTemplateExercises(id).map { it.toDomain() }
        return entity.toDomain(exercises)
    }

    suspend fun toggleExerciseFavorite(exerciseId: String, isFavorite: Boolean) {
        exerciseDao.setFavorite(exerciseId, isFavorite)
    }

    suspend fun toggleTemplateFavorite(templateId: String, isFavorite: Boolean) {
        workoutDao.setTemplateFavorite(templateId, isFavorite)
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        gamificationDao.insertOrUpdateUserProfile(profile.toEntity())
    }

    suspend fun createCustomWorkout(
        title: String,
        description: String,
        category: ExerciseCategory,
        difficulty: DifficultyLevel,
        exercises: List<TemplateExercise>
    ): String {
        val id = "custom_" + UUID.randomUUID().toString().take(8)
        val templateEntity = WorkoutTemplateEntity(
            id = id,
            title = title,
            subtitle = "${exercises.size} custom exercises",
            description = description,
            category = category.name,
            difficulty = difficulty.name,
            estimatedDurationMinutes = (exercises.size * 5).coerceAtLeast(15),
            isCustom = true,
            isFavorite = false,
            xpReward = 50
        )
        workoutDao.insertTemplate(templateEntity)
        val exerciseEntities = exercises.mapIndexed { index, ex ->
            TemplateExerciseEntity(
                templateId = id,
                exerciseId = ex.exerciseId,
                exerciseName = ex.exerciseName,
                equipment = ex.equipment.name,
                targetSets = ex.targetSets,
                targetReps = ex.targetReps,
                targetWeightKg = ex.targetWeightKg,
                restSeconds = ex.restSeconds,
                isTimed = ex.isTimed,
                targetDurationSeconds = ex.targetDurationSeconds,
                orderIndex = index
            )
        }
        workoutDao.insertTemplateExercises(exerciseEntities)

        // Evaluate "Architect" achievement for first custom workout
        val currentProfile = gamificationDao.getUserProfile() ?: UserProfileEntity()
        gamificationDao.getAchievementById("custom_workout")?.let { ach ->
            if (!ach.isUnlocked) {
                gamificationDao.updateAchievement(
                    ach.copy(
                        isUnlocked = true,
                        unlockedAtMs = System.currentTimeMillis(),
                        currentProgress = 1
                    )
                )
                awardXp(ach.xpReward)
            }
        }
        return id
    }

    suspend fun deleteWorkoutTemplate(templateId: String) {
        workoutDao.deleteTemplateExercises(templateId)
        workoutDao.deleteTemplateById(templateId)
    }

    /**
     * Saves a completed workout session, evaluates personal records, awards XP,
     * updates quests, streaks, and achievements.
     * Returns list of newly unlocked achievement names.
     */
    suspend fun completeWorkoutSession(
        workoutId: String = UUID.randomUUID().toString(),
        templateId: String?,
        title: String,
        startTimeMs: Long,
        endTimeMs: Long,
        durationSeconds: Int,
        status: CompletionStatus,
        loggedSetsInput: List<LoggedSet>,
        notes: String = ""
    ): Pair<Int, List<String>> {
        val todayIso = LocalDate.now().toString()
        var hasNewPR = false
        val finalLoggedSets = mutableListOf<LoggedSetEntity>()

        // Calculate total volume and verify PRs
        var totalVolume = 0.0
        var completedSetsCount = 0

        for (set in loggedSetsInput) {
            if (set.isCompleted) {
                completedSetsCount++
                val setVolume = set.weightKg * set.reps
                totalVolume += setVolume

                // Check PR if weight > 0 or reps > 0
                val existingPR = gamificationDao.getPRForExercise(set.exerciseId)
                val isWeightPR = existingPR == null || set.weightKg > existingPR.maxWeightKg ||
                        (set.weightKg == existingPR.maxWeightKg && set.reps > existingPR.maxReps)

                var isPR = false
                if (isWeightPR && (set.weightKg > 0 || set.reps > 0)) {
                    isPR = true
                    hasNewPR = true
                    gamificationDao.insertOrUpdatePR(
                        PersonalRecordEntity(
                            exerciseId = set.exerciseId,
                            exerciseName = set.exerciseName,
                            maxWeightKg = set.weightKg,
                            maxReps = set.reps,
                            achievedAtMs = endTimeMs,
                            workoutRecordId = workoutId
                        )
                    )
                }

                finalLoggedSets.add(
                    LoggedSetEntity(
                        workoutRecordId = workoutId,
                        exerciseId = set.exerciseId,
                        exerciseName = set.exerciseName,
                        setNumber = set.setNumber,
                        weightKg = set.weightKg,
                        reps = set.reps,
                        durationSeconds = set.durationSeconds,
                        isCompleted = true,
                        isPersonalRecord = isPR
                    )
                )
            }
        }

        // Calculate XP earned: only award completion XP if session is COMPLETED
        var xpEarned = 0
        if (status == CompletionStatus.COMPLETED) {
            xpEarned += GamificationEngine.WORKOUT_COMPLETION_XP
            if (hasNewPR) {
                xpEarned += GamificationEngine.PERSONAL_RECORD_XP
            }
        } else if (status == CompletionStatus.PARTIAL) {
            xpEarned += (completedSetsCount * 5).coerceAtMost(25)
        }

        val recordEntity = WorkoutRecordEntity(
            id = workoutId,
            templateId = templateId,
            workoutTitle = title,
            dateIso = todayIso,
            startTimeMs = startTimeMs,
            endTimeMs = endTimeMs,
            durationSeconds = durationSeconds,
            completionStatus = status.name,
            totalVolumeKg = totalVolume,
            totalSetsCompleted = completedSetsCount,
            xpEarned = xpEarned,
            notes = notes
        )

        workoutDao.insertWorkoutRecord(recordEntity)
        workoutDao.insertLoggedSets(finalLoggedSets)

        // Award workout XP to profile
        awardXp(xpEarned)

        // Update Quests for today
        updateQuestsOnWorkout(todayIso, completedSetsCount, status == CompletionStatus.COMPLETED)

        // Evaluate newly unlocked achievements
        val newlyUnlocked = evaluateAndPersistAchievements()

        return Pair(xpEarned, newlyUnlocked)
    }

    private suspend fun awardXp(amount: Int) {
        if (amount <= 0) return
        val currentProfile = gamificationDao.getUserProfile() ?: InitialData.getDefaultUserProfile()
        val newTotalXp = currentProfile.totalXp + amount
        val newLevel = GamificationEngine.getLevelInfo(newTotalXp).level
        gamificationDao.insertOrUpdateUserProfile(
            currentProfile.copy(
                totalXp = newTotalXp,
                currentLevel = newLevel
            )
        )
    }

    private suspend fun updateQuestsOnWorkout(
        todayIso: String,
        setsCount: Int,
        isCompletedWorkout: Boolean
    ) {
        var dailyQuests = gamificationDao.getQuestsForPeriodKey(todayIso)
        if (dailyQuests.isEmpty()) {
            dailyQuests = InitialData.getDefaultQuests(todayIso)
            gamificationDao.insertQuests(dailyQuests)
        }

        for (quest in dailyQuests) {
            var newProgress = quest.currentValue
            if (quest.id.contains("workout") && isCompletedWorkout) {
                newProgress = 1
            } else if (quest.id.contains("sets")) {
                newProgress += setsCount
            }
            val completed = newProgress >= quest.targetValue
            gamificationDao.updateQuest(
                quest.copy(
                    currentValue = newProgress,
                    isCompleted = completed
                )
            )
        }
    }

    suspend fun claimQuestReward(questId: String) {
        val quests = gamificationDao.getQuestsForPeriodKey(LocalDate.now().toString())
        val quest = quests.find { it.id == questId } ?: return
        if (quest.isCompleted && !quest.isClaimed) {
            gamificationDao.claimQuest(questId)
            awardXp(quest.xpReward)
        }
    }

    private suspend fun evaluateAndPersistAchievements(): List<String> {
        val allRecords = workoutDao.getAllRecordsList()
        val allSets = workoutDao.getAllLoggedSetsList()
        val profile = gamificationDao.getUserProfile() ?: InitialData.getDefaultUserProfile()
        val scheduledDays = profile.preferredTrainingDays.split(",").mapNotNull { it.trim().toIntOrNull() }

        val completedDates = allRecords.filter { it.completionStatus == "COMPLETED" }.map { it.dateIso }.toSet()
        val streakInfo = GamificationEngine.calculateStreak(completedDates, scheduledDays)

        // Update streak in profile
        gamificationDao.insertOrUpdateUserProfile(
            profile.copy(
                currentStreak = streakInfo.currentStreak,
                longestStreak = maxOf(profile.longestStreak, streakInfo.currentStreak),
                lastWorkoutDateIso = LocalDate.now().toString()
            )
        )

        val hasCustom = workoutDao.getCustomTemplateCount() > 0
        val evalMap = GamificationEngine.evaluateAchievements(
            workoutRecords = allRecords.map { it.toDomain(emptyList()) },
            allSets = allSets.map { it.toDomain() },
            hasCustomWorkout = hasCustom,
            currentStreak = streakInfo.currentStreak
        )

        val newlyUnlockedNames = mutableListOf<String>()

        for ((achId, pair) in evalMap) {
            val isUnlocked = pair.first
            val progress = pair.second
            val existing = gamificationDao.getAchievementById(achId)
            if (existing != null) {
                if (!existing.isUnlocked && isUnlocked) {
                    gamificationDao.updateAchievement(
                        existing.copy(
                            isUnlocked = true,
                            unlockedAtMs = System.currentTimeMillis(),
                            currentProgress = progress
                        )
                    )
                    awardXp(existing.xpReward)
                    newlyUnlockedNames.add(existing.title)
                } else if (!existing.isUnlocked) {
                    gamificationDao.updateAchievement(existing.copy(currentProgress = progress))
                }
            }
        }

        return newlyUnlockedNames
    }

    suspend fun deleteWorkoutRecord(recordId: String) {
        val record = workoutDao.getRecordById(recordId) ?: return
        workoutDao.deleteLoggedSetsForRecord(recordId)
        workoutDao.deleteRecordById(recordId)

        // Recalculate derived statistics, XP, PRs, and achievements
        recalculateAllGamificationState()
    }

    suspend fun updateWorkoutNotes(recordId: String, notes: String) {
        workoutDao.updateRecordNotes(recordId, notes)
    }

    suspend fun resetAllProgress() {
        workoutDao.clearAllWorkoutRecords()
        workoutDao.clearAllLoggedSets()
        gamificationDao.clearPersonalRecords()
        // Reset achievements
        val achievements = InitialData.getDefaultAchievements()
        gamificationDao.insertAchievements(achievements)
        // Reset profile
        val profile = InitialData.getDefaultUserProfile().copy(hasCompletedOnboarding = true)
        gamificationDao.insertOrUpdateUserProfile(profile)
        // Reset quests
        val today = LocalDate.now().toString()
        gamificationDao.insertQuests(InitialData.getDefaultQuests(today))
    }

    suspend fun recalculateAllGamificationState() {
        val records = workoutDao.getAllRecordsList()
        val totalWorkoutXp = records.sumOf { it.xpEarned }

        val profile = gamificationDao.getUserProfile() ?: InitialData.getDefaultUserProfile()
        val newLevel = GamificationEngine.getLevelInfo(totalWorkoutXp).level

        val scheduledDays = profile.preferredTrainingDays.split(",").mapNotNull { it.trim().toIntOrNull() }
        val completedDates = records.filter { it.completionStatus == "COMPLETED" }.map { it.dateIso }.toSet()
        val streakInfo = GamificationEngine.calculateStreak(completedDates, scheduledDays)

        gamificationDao.insertOrUpdateUserProfile(
            profile.copy(
                totalXp = totalWorkoutXp,
                currentLevel = newLevel,
                currentStreak = streakInfo.currentStreak
            )
        )

        // Re-evaluate achievements
        val allSets = workoutDao.getAllLoggedSetsList()
        val evalMap = GamificationEngine.evaluateAchievements(
            workoutRecords = records.map { it.toDomain(emptyList()) },
            allSets = allSets.map { it.toDomain() },
            hasCustomWorkout = workoutDao.getCustomTemplateCount() > 0,
            currentStreak = streakInfo.currentStreak
        )

        for ((achId, pair) in evalMap) {
            val existing = gamificationDao.getAchievementById(achId) ?: continue
            val unlocked = pair.first
            val progress = pair.second
            gamificationDao.updateAchievement(
                existing.copy(
                    isUnlocked = unlocked,
                    unlockedAtMs = if (unlocked) (existing.unlockedAtMs ?: System.currentTimeMillis()) else null,
                    currentProgress = progress
                )
            )
        }
    }
}

// Helpers for DAO queries needed above
private suspend fun com.example.data.local.WorkoutDao.getAllRecordsList(): List<WorkoutRecordEntity> {
    return getRecordsBetweenDates("2000-01-01", "2099-12-31")
}

private suspend fun com.example.data.local.WorkoutDao.getAllLoggedSetsList(): List<LoggedSetEntity> {
    val records = getAllRecordsList()
    val all = mutableListOf<LoggedSetEntity>()
    for (r in records) {
        all.addAll(getLoggedSets(r.id))
    }
    return all
}

private suspend fun com.example.data.local.WorkoutDao.getCustomTemplateCount(): Int {
    val all = getTemplateCount()
    return all // non-zero indicates templates exist
}

// Extension mappers
fun ExerciseEntity.toDomain() = Exercise(
    id = id,
    name = name,
    description = description,
    primaryMuscle = MuscleGroup.valueOf(primaryMuscle),
    secondaryMuscles = if (secondaryMuscles.isNotBlank()) secondaryMuscles.split(",").map { MuscleGroup.valueOf(it.trim()) } else emptyList(),
    equipment = EquipmentType.valueOf(equipment),
    category = ExerciseCategory.valueOf(category),
    difficulty = DifficultyLevel.valueOf(difficulty),
    instructions = instructions.split("|"),
    commonMistakes = commonMistakes.split("|"),
    easierVariation = easierVariation,
    progression = progression,
    defaultSets = defaultSets,
    defaultReps = defaultReps,
    defaultWeightKg = defaultWeightKg,
    defaultRestSeconds = defaultRestSeconds,
    isTimed = isTimed,
    defaultDurationSeconds = defaultDurationSeconds,
    isFavorite = isFavorite
)

fun WorkoutTemplateEntity.toDomain(exercises: List<TemplateExercise>) = WorkoutTemplate(
    id = id,
    title = title,
    subtitle = subtitle,
    description = description,
    category = ExerciseCategory.valueOf(category),
    difficulty = DifficultyLevel.valueOf(difficulty),
    estimatedDurationMinutes = estimatedDurationMinutes,
    exercises = exercises,
    isCustom = isCustom,
    isFavorite = isFavorite,
    xpReward = xpReward
)

fun TemplateExerciseEntity.toDomain() = TemplateExercise(
    exerciseId = exerciseId,
    exerciseName = exerciseName,
    equipment = EquipmentType.valueOf(equipment),
    targetSets = targetSets,
    targetReps = targetReps,
    targetWeightKg = targetWeightKg,
    restSeconds = restSeconds,
    isTimed = isTimed,
    targetDurationSeconds = targetDurationSeconds,
    orderIndex = orderIndex
)

fun WorkoutRecordEntity.toDomain(sets: List<LoggedSet>) = WorkoutRecord(
    id = id,
    templateId = templateId,
    workoutTitle = workoutTitle,
    dateIso = dateIso,
    startTimeMs = startTimeMs,
    endTimeMs = endTimeMs,
    durationSeconds = durationSeconds,
    completionStatus = CompletionStatus.valueOf(completionStatus),
    totalVolumeKg = totalVolumeKg,
    totalSetsCompleted = totalSetsCompleted,
    xpEarned = xpEarned,
    notes = notes,
    loggedSets = sets
)

fun LoggedSetEntity.toDomain() = LoggedSet(
    id = id,
    workoutRecordId = workoutRecordId,
    exerciseId = exerciseId,
    exerciseName = exerciseName,
    setNumber = setNumber,
    weightKg = weightKg,
    reps = reps,
    durationSeconds = durationSeconds,
    isCompleted = isCompleted,
    isPersonalRecord = isPersonalRecord
)

fun UserProfileEntity.toDomain() = UserProfile(
    id = id,
    displayName = displayName,
    ageRange = ageRange,
    experienceLevel = DifficultyLevel.valueOf(experienceLevel),
    primaryGoal = FitnessGoal.valueOf(primaryGoal),
    preferredTrainingDays = preferredTrainingDays.split(",").mapNotNull { it.trim().toIntOrNull() },
    preferredDurationMinutes = preferredDurationMinutes,
    hasBarbell = hasBarbell,
    hasDumbbells = hasDumbbells,
    hasBodyweight = hasBodyweight,
    avoidedMovements = if (avoidedMovements.isNotBlank()) avoidedMovements.split(",") else emptyList(),
    heightCm = heightCm,
    weightKg = weightKg,
    preferredWeightUnit = preferredWeightUnit,
    hasCompletedOnboarding = hasCompletedOnboarding,
    notificationsEnabled = notificationsEnabled,
    reminderHour = reminderHour,
    reminderMinute = reminderMinute,
    currentStreak = currentStreak,
    longestStreak = longestStreak,
    lastWorkoutDateIso = lastWorkoutDateIso,
    totalXp = totalXp,
    currentLevel = currentLevel,
    weeklyTargetWorkouts = weeklyTargetWorkouts,
    dailyTargetMinutes = dailyTargetMinutes
)

fun UserProfile.toEntity() = UserProfileEntity(
    id = id,
    displayName = displayName,
    ageRange = ageRange,
    experienceLevel = experienceLevel.name,
    primaryGoal = primaryGoal.name,
    preferredTrainingDays = preferredTrainingDays.joinToString(","),
    preferredDurationMinutes = preferredDurationMinutes,
    hasBarbell = hasBarbell,
    hasDumbbells = hasDumbbells,
    hasBodyweight = hasBodyweight,
    avoidedMovements = avoidedMovements.joinToString(","),
    heightCm = heightCm,
    weightKg = weightKg,
    preferredWeightUnit = preferredWeightUnit,
    hasCompletedOnboarding = hasCompletedOnboarding,
    notificationsEnabled = notificationsEnabled,
    reminderHour = reminderHour,
    reminderMinute = reminderMinute,
    currentStreak = currentStreak,
    longestStreak = longestStreak,
    lastWorkoutDateIso = lastWorkoutDateIso,
    totalXp = totalXp,
    currentLevel = currentLevel,
    weeklyTargetWorkouts = weeklyTargetWorkouts,
    dailyTargetMinutes = dailyTargetMinutes
)

fun QuestEntity.toDomain() = Quest(
    id = id,
    title = title,
    description = description,
    period = QuestPeriod.valueOf(period),
    targetValue = targetValue,
    currentValue = currentValue,
    xpReward = xpReward,
    isCompleted = isCompleted,
    isClaimed = isClaimed,
    periodKey = periodKey
)

fun AchievementEntity.toDomain() = Achievement(
    id = id,
    title = title,
    description = description,
    badgeIconName = badgeIconName,
    xpReward = xpReward,
    isUnlocked = isUnlocked,
    unlockedAtMs = unlockedAtMs,
    currentProgress = currentProgress,
    targetProgress = targetProgress
)

fun PersonalRecordEntity.toDomain() = PersonalRecord(
    exerciseId = exerciseId,
    exerciseName = exerciseName,
    maxWeightKg = maxWeightKg,
    maxReps = maxReps,
    achievedAtMs = achievedAtMs,
    workoutRecordId = workoutRecordId
)
