package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Achievement
import com.example.data.model.CompletionStatus
import com.example.data.model.DifficultyLevel
import com.example.data.model.Exercise
import com.example.data.model.ExerciseCategory
import com.example.data.model.LevelInfo
import com.example.data.model.LoggedSet
import com.example.data.model.PersonalRecord
import com.example.data.model.Quest
import com.example.data.model.StreakInfo
import com.example.data.model.TemplateExercise
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutRecord
import com.example.data.model.WorkoutTemplate
import com.example.data.repository.RepQuestRepository
import com.example.domain.GamificationEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ActiveSetState(
    val setNumber: Int,
    val targetReps: Int,
    val targetWeightKg: Double,
    val actualReps: Int,
    val actualWeightKg: Double,
    val isCompleted: Boolean = false,
    val durationSeconds: Int = 0
)

data class ActiveExerciseState(
    val exercise: Exercise,
    val sets: List<ActiveSetState>,
    val restSeconds: Int = 60,
    val isCompleted: Boolean = false
)

data class ActiveWorkoutSession(
    val templateId: String?,
    val workoutTitle: String,
    val startTimeMs: Long,
    val elapsedSeconds: Int = 0,
    val currentExerciseIndex: Int = 0,
    val exercises: List<ActiveExerciseState>,
    val notes: String = "",
    val isPaused: Boolean = false
)

data class RestTimerState(
    val isActive: Boolean = false,
    val isPaused: Boolean = false,
    val remainingSeconds: Int = 0,
    val totalSeconds: Int = 60
)

data class WorkoutSummaryData(
    val workoutTitle: String,
    val durationSeconds: Int,
    val totalVolumeKg: Double,
    val totalSetsCompleted: Int,
    val xpEarned: Int,
    val newlyUnlockedAchievements: List<String>,
    val dateIso: String
)

class RepQuestViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RepQuestRepository
    init {
        val database = AppDatabase.getInstance(application)
        repository = RepQuestRepository(database)
        viewModelScope.launch {
            database.populateInitialData()
        }
    }

    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    val exercises: StateFlow<List<Exercise>> = repository.exercises
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val templates: StateFlow<List<WorkoutTemplate>> = repository.templates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workoutRecords: StateFlow<List<WorkoutRecord>> = repository.workoutRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quests: StateFlow<List<Quest>> = repository.quests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val achievements: StateFlow<List<Achievement>> = repository.achievements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val personalRecords: StateFlow<List<PersonalRecord>> = repository.personalRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val levelInfo: StateFlow<LevelInfo> = userProfile.combine(workoutRecords) { profile, _ ->
        GamificationEngine.getLevelInfo(profile.totalXp)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GamificationEngine.getLevelInfo(0))

    val streakInfo: StateFlow<StreakInfo> = combine(workoutRecords, userProfile) { records, profile ->
        val completedDates = records.filter { it.completionStatus == CompletionStatus.COMPLETED }.map { it.dateIso }.toSet()
        GamificationEngine.calculateStreak(completedDates, profile.preferredTrainingDays)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        GamificationEngine.calculateStreak(emptySet(), listOf(1, 3, 5))
    )

    // Active Workout Session State
    private val _activeSession = MutableStateFlow<ActiveWorkoutSession?>(null)
    val activeSession: StateFlow<ActiveWorkoutSession?> = _activeSession.asStateFlow()

    // Rest Timer State
    private val _restTimerState = MutableStateFlow(RestTimerState())
    val restTimerState: StateFlow<RestTimerState> = _restTimerState.asStateFlow()

    // Last completed summary for celebration modal
    private val _lastSummary = MutableStateFlow<WorkoutSummaryData?>(null)
    val lastSummary: StateFlow<WorkoutSummaryData?> = _lastSummary.asStateFlow()

    private var workoutTickerJob: Job? = null
    private var restTimerJob: Job? = null

    fun startWorkoutFromTemplate(template: WorkoutTemplate, exerciseCatalog: List<Exercise>) {
        val activeExercises = template.exercises.map { templateEx ->
            val exObj = exerciseCatalog.find { it.id == templateEx.exerciseId }
                ?: Exercise(
                    id = templateEx.exerciseId,
                    name = templateEx.exerciseName,
                    description = "",
                    primaryMuscle = com.example.data.model.MuscleGroup.FULL_BODY,
                    equipment = templateEx.equipment,
                    category = ExerciseCategory.FULL_BODY,
                    difficulty = DifficultyLevel.BEGINNER,
                    instructions = listOf("Follow safe lifting form"),
                    commonMistakes = listOf("Rushing repetitions")
                )

            val sets = (1..templateEx.targetSets).map { setIndex ->
                ActiveSetState(
                    setNumber = setIndex,
                    targetReps = templateEx.targetReps,
                    targetWeightKg = templateEx.targetWeightKg,
                    actualReps = templateEx.targetReps,
                    actualWeightKg = templateEx.targetWeightKg,
                    isCompleted = false,
                    durationSeconds = templateEx.targetDurationSeconds
                )
            }
            ActiveExerciseState(
                exercise = exObj,
                sets = sets,
                restSeconds = templateEx.restSeconds
            )
        }

        _activeSession.value = ActiveWorkoutSession(
            templateId = template.id,
            workoutTitle = template.title,
            startTimeMs = System.currentTimeMillis(),
            elapsedSeconds = 0,
            currentExerciseIndex = 0,
            exercises = activeExercises
        )

        startWorkoutTimer()
    }

    private fun startWorkoutTimer() {
        workoutTickerJob?.cancel()
        workoutTickerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val current = _activeSession.value ?: break
                if (!current.isPaused) {
                    _activeSession.value = current.copy(elapsedSeconds = current.elapsedSeconds + 1)
                }
            }
        }
    }

    fun toggleWorkoutPause() {
        val current = _activeSession.value ?: return
        _activeSession.value = current.copy(isPaused = !current.isPaused)
    }

    fun logSet(exerciseIndex: Int, setIndex: Int, actualWeight: Double, actualReps: Int) {
        val current = _activeSession.value ?: return
        if (exerciseIndex !in current.exercises.indices) return
        val currentEx = current.exercises[exerciseIndex]
        if (setIndex !in currentEx.sets.indices) return

        val updatedSets = currentEx.sets.toMutableList()
        val targetSet = updatedSets[setIndex]
        val newCompleted = !targetSet.isCompleted
        updatedSets[setIndex] = targetSet.copy(
            actualWeightKg = actualWeight,
            actualReps = actualReps,
            isCompleted = newCompleted
        )

        val updatedExercises = current.exercises.toMutableList()
        val allSetsDone = updatedSets.all { it.isCompleted }
        updatedExercises[exerciseIndex] = currentEx.copy(sets = updatedSets, isCompleted = allSetsDone)

        _activeSession.value = current.copy(exercises = updatedExercises)

        if (newCompleted) {
            // Automatically launch rest timer
            startRestTimer(currentEx.restSeconds)
        }
    }

    fun updateSetValues(exerciseIndex: Int, setIndex: Int, weight: Double, reps: Int) {
        val current = _activeSession.value ?: return
        if (exerciseIndex !in current.exercises.indices) return
        val currentEx = current.exercises[exerciseIndex]
        if (setIndex !in currentEx.sets.indices) return

        val updatedSets = currentEx.sets.toMutableList()
        val targetSet = updatedSets[setIndex]
        updatedSets[setIndex] = targetSet.copy(
            actualWeightKg = weight,
            actualReps = reps
        )
        val updatedExercises = current.exercises.toMutableList()
        updatedExercises[exerciseIndex] = currentEx.copy(sets = updatedSets)
        _activeSession.value = current.copy(exercises = updatedExercises)
    }

    fun nextExercise() {
        val current = _activeSession.value ?: return
        if (current.currentExerciseIndex < current.exercises.lastIndex) {
            _activeSession.value = current.copy(currentExerciseIndex = current.currentExerciseIndex + 1)
        }
    }

    fun prevExercise() {
        val current = _activeSession.value ?: return
        if (current.currentExerciseIndex > 0) {
            _activeSession.value = current.copy(currentExerciseIndex = current.currentExerciseIndex - 1)
        }
    }

    fun setCurrentExerciseIndex(index: Int) {
        val current = _activeSession.value ?: return
        if (index in current.exercises.indices) {
            _activeSession.value = current.copy(currentExerciseIndex = index)
        }
    }

    fun updateSessionNotes(notes: String) {
        val current = _activeSession.value ?: return
        _activeSession.value = current.copy(notes = notes)
    }

    // Rest Timer Controls
    fun startRestTimer(seconds: Int) {
        restTimerJob?.cancel()
        _restTimerState.value = RestTimerState(
            isActive = true,
            isPaused = false,
            remainingSeconds = seconds,
            totalSeconds = seconds
        )

        restTimerJob = viewModelScope.launch {
            while (_restTimerState.value.remainingSeconds > 0) {
                delay(1000)
                val state = _restTimerState.value
                if (state.isActive && !state.isPaused) {
                    val remaining = state.remainingSeconds - 1
                    _restTimerState.value = state.copy(remainingSeconds = remaining)
                    if (remaining == 0) {
                        triggerVibration()
                    }
                }
            }
            delay(1500)
            _restTimerState.value = RestTimerState(isActive = false)
        }
    }

    fun addRestTime(secondsToAdd: Int) {
        val current = _restTimerState.value
        if (current.isActive) {
            _restTimerState.value = current.copy(
                remainingSeconds = current.remainingSeconds + secondsToAdd,
                totalSeconds = current.totalSeconds + secondsToAdd
            )
        }
    }

    fun toggleRestTimerPause() {
        val current = _restTimerState.value
        if (current.isActive) {
            _restTimerState.value = current.copy(isPaused = !current.isPaused)
        }
    }

    fun skipRestTimer() {
        restTimerJob?.cancel()
        _restTimerState.value = RestTimerState(isActive = false)
    }

    private fun triggerVibration() {
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(500)
            }
        } catch (_: Exception) {}
    }

    fun finishWorkout(status: CompletionStatus, onFinished: () -> Unit) {
        val session = _activeSession.value ?: return
        workoutTickerJob?.cancel()
        restTimerJob?.cancel()
        _restTimerState.value = RestTimerState(isActive = false)

        viewModelScope.launch {
            val allLoggedSets = mutableListOf<LoggedSet>()
            for (ex in session.exercises) {
                for (s in ex.sets) {
                    allLoggedSets.add(
                        LoggedSet(
                            workoutRecordId = "",
                            exerciseId = ex.exercise.id,
                            exerciseName = ex.exercise.name,
                            setNumber = s.setNumber,
                            weightKg = s.actualWeightKg,
                            reps = s.actualReps,
                            durationSeconds = s.durationSeconds,
                            isCompleted = s.isCompleted
                        )
                    )
                }
            }

            val endTime = System.currentTimeMillis()
            val totalSets = allLoggedSets.count { it.isCompleted }
            val totalVol = allLoggedSets.filter { it.isCompleted }.sumOf { it.weightKg * it.reps }

            val (xpEarned, newlyUnlocked) = repository.completeWorkoutSession(
                templateId = session.templateId,
                title = session.workoutTitle,
                startTimeMs = session.startTimeMs,
                endTimeMs = endTime,
                durationSeconds = session.elapsedSeconds,
                status = status,
                loggedSetsInput = allLoggedSets,
                notes = session.notes
            )

            _lastSummary.value = WorkoutSummaryData(
                workoutTitle = session.workoutTitle,
                durationSeconds = session.elapsedSeconds,
                totalVolumeKg = totalVol,
                totalSetsCompleted = totalSets,
                xpEarned = xpEarned,
                newlyUnlockedAchievements = newlyUnlocked,
                dateIso = LocalDate.now().toString()
            )

            _activeSession.value = null
            onFinished()
        }
    }

    fun cancelWorkout() {
        workoutTickerJob?.cancel()
        restTimerJob?.cancel()
        _activeSession.value = null
        _restTimerState.value = RestTimerState(isActive = false)
    }

    fun dismissSummary() {
        _lastSummary.value = null
    }

    fun saveOnboardingProfile(profile: UserProfile, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.saveUserProfile(profile.copy(hasCompletedOnboarding = true))
            onComplete()
        }
    }

    fun updateUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.saveUserProfile(profile)
        }
    }

    fun toggleExerciseFavorite(id: String, isFav: Boolean) {
        viewModelScope.launch {
            repository.toggleExerciseFavorite(id, isFav)
        }
    }

    fun toggleTemplateFavorite(id: String, isFav: Boolean) {
        viewModelScope.launch {
            repository.toggleTemplateFavorite(id, isFav)
        }
    }

    fun deleteWorkoutRecord(recordId: String) {
        viewModelScope.launch {
            repository.deleteWorkoutRecord(recordId)
        }
    }

    fun updateRecordNotes(recordId: String, notes: String) {
        viewModelScope.launch {
            repository.updateWorkoutNotes(recordId, notes)
        }
    }

    fun createCustomWorkout(
        title: String,
        description: String,
        category: ExerciseCategory,
        difficulty: DifficultyLevel,
        exercises: List<TemplateExercise>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.createCustomWorkout(title, description, category, difficulty, exercises)
            onSuccess()
        }
    }

    fun deleteTemplate(templateId: String) {
        viewModelScope.launch {
            repository.deleteWorkoutTemplate(templateId)
        }
    }

    fun claimQuest(questId: String) {
        viewModelScope.launch {
            repository.claimQuestReward(questId)
        }
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            repository.resetAllProgress()
        }
    }
}
