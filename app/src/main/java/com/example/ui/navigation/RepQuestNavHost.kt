package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.model.WorkoutTemplate
import com.example.ui.components.BottomNavTab
import com.example.ui.components.CelebrationDialog
import com.example.ui.components.RepQuestBottomNav
import com.example.ui.screens.ActiveWorkoutScreen
import com.example.ui.screens.CustomWorkoutScreen
import com.example.ui.screens.ExerciseDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.RewardsScreen
import com.example.ui.screens.WorkoutDetailScreen
import com.example.ui.screens.WorkoutsScreen
import com.example.ui.viewmodel.RepQuestViewModel

object NavRoutes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val WORKOUTS = "workouts"
    const val PROGRESS = "progress"
    const val REWARDS = "rewards"
    const val PROFILE = "profile"
    const val ACTIVE_WORKOUT = "active_workout"
    const val CUSTOM_WORKOUT = "custom_workout"
    const val WORKOUT_DETAIL = "workout_detail"
    const val EXERCISE_DETAIL = "exercise_detail"
}

@Composable
fun RepQuestApp(
    viewModel: RepQuestViewModel,
    navController: NavHostController = rememberNavController()
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val levelInfo by viewModel.levelInfo.collectAsStateWithLifecycle()
    val streakInfo by viewModel.streakInfo.collectAsStateWithLifecycle()
    val templates by viewModel.templates.collectAsStateWithLifecycle()
    val exercises by viewModel.exercises.collectAsStateWithLifecycle()
    val workoutRecords by viewModel.workoutRecords.collectAsStateWithLifecycle()
    val quests by viewModel.quests.collectAsStateWithLifecycle()
    val achievements by viewModel.achievements.collectAsStateWithLifecycle()
    val personalRecords by viewModel.personalRecords.collectAsStateWithLifecycle()
    val activeSession by viewModel.activeSession.collectAsStateWithLifecycle()
    val restTimerState by viewModel.restTimerState.collectAsStateWithLifecycle()
    val lastSummary by viewModel.lastSummary.collectAsStateWithLifecycle()

    var selectedDetailTemplate by remember { mutableStateOf<WorkoutTemplate?>(null) }
    var selectedDetailExerciseId by remember { mutableStateOf<String?>(null) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavRoutes.HOME

    val showBottomNav = currentRoute in listOf(
        NavRoutes.HOME,
        NavRoutes.WORKOUTS,
        NavRoutes.PROGRESS,
        NavRoutes.REWARDS,
        NavRoutes.PROFILE
    )

    // Celebration modal when a workout ends
    lastSummary?.let { summary ->
        CelebrationDialog(
            summaryData = summary,
            onDismiss = { viewModel.dismissSummary() }
        )
    }

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                RepQuestBottomNav(
                    currentRoute = currentRoute,
                    onNavigateToTab = { tab ->
                        navController.navigate(tab.route) {
                            popUpTo(NavRoutes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val startDestination = if (userProfile.hasCompletedOnboarding) NavRoutes.HOME else NavRoutes.ONBOARDING

            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable(NavRoutes.ONBOARDING) {
                    OnboardingScreen(
                        initialProfile = userProfile,
                        onCompleteOnboarding = { newProfile ->
                            viewModel.saveOnboardingProfile(newProfile) {
                                navController.navigate(NavRoutes.HOME) {
                                    popUpTo(NavRoutes.ONBOARDING) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable(NavRoutes.HOME) {
                    HomeScreen(
                        userProfile = userProfile,
                        levelInfo = levelInfo,
                        streakInfo = streakInfo,
                        templates = templates,
                        workoutRecords = workoutRecords,
                        quests = quests,
                        achievements = achievements,
                        onStartWorkout = { template ->
                            viewModel.startWorkoutFromTemplate(template, exercises)
                            navController.navigate(NavRoutes.ACTIVE_WORKOUT)
                        },
                        onNavigateToWorkouts = {
                            navController.navigate(NavRoutes.WORKOUTS)
                        },
                        onNavigateToRewards = {
                            navController.navigate(NavRoutes.REWARDS)
                        },
                        onClaimQuest = { questId ->
                            viewModel.claimQuest(questId)
                        }
                    )
                }

                composable(NavRoutes.WORKOUTS) {
                    WorkoutsScreen(
                        templates = templates,
                        onSelectTemplate = { template ->
                            selectedDetailTemplate = template
                            navController.navigate(NavRoutes.WORKOUT_DETAIL)
                        },
                        onStartWorkout = { template ->
                            viewModel.startWorkoutFromTemplate(template, exercises)
                            navController.navigate(NavRoutes.ACTIVE_WORKOUT)
                        },
                        onToggleFavorite = { id, isFav ->
                            viewModel.toggleTemplateFavorite(id, isFav)
                        },
                        onCreateCustomWorkout = {
                            navController.navigate(NavRoutes.CUSTOM_WORKOUT)
                        }
                    )
                }

                composable(NavRoutes.WORKOUT_DETAIL) {
                    selectedDetailTemplate?.let { template ->
                        WorkoutDetailScreen(
                            template = template,
                            onStartWorkout = {
                                viewModel.startWorkoutFromTemplate(template, exercises)
                                navController.navigate(NavRoutes.ACTIVE_WORKOUT)
                            },
                            onBack = { navController.popBackStack() },
                            onDeleteTemplate = if (template.isCustom) {
                                {
                                    viewModel.deleteTemplate(template.id)
                                    navController.popBackStack()
                                }
                            } else null,
                            onViewExerciseDetails = { exId ->
                                selectedDetailExerciseId = exId
                                navController.navigate(NavRoutes.EXERCISE_DETAIL)
                            }
                        )
                    }
                }

                composable(NavRoutes.ACTIVE_WORKOUT) {
                    val session = activeSession
                    if (session != null) {
                        ActiveWorkoutScreen(
                            session = session,
                            restTimerState = restTimerState,
                            onLogSet = { exIdx, setIdx, w, r ->
                                viewModel.logSet(exIdx, setIdx, w, r)
                            },
                            onUpdateSetValues = { exIdx, setIdx, w, r ->
                                viewModel.updateSetValues(exIdx, setIdx, w, r)
                            },
                            onNextExercise = { viewModel.nextExercise() },
                            onPrevExercise = { viewModel.prevExercise() },
                            onSelectExerciseIndex = { viewModel.setCurrentExerciseIndex(it) },
                            onTogglePause = { viewModel.toggleWorkoutPause() },
                            onFinishWorkout = { status ->
                                viewModel.finishWorkout(status) {
                                    navController.popBackStack(NavRoutes.HOME, false)
                                }
                            },
                            onCancelWorkout = {
                                viewModel.cancelWorkout()
                                navController.popBackStack()
                            },
                            onUpdateNotes = { viewModel.updateSessionNotes(it) },
                            onAddRestSeconds = { viewModel.addRestTime(it) },
                            onToggleRestPause = { viewModel.toggleRestTimerPause() },
                            onSkipRest = { viewModel.skipRestTimer() },
                            onViewExerciseInfo = { exId ->
                                selectedDetailExerciseId = exId
                                navController.navigate(NavRoutes.EXERCISE_DETAIL)
                            }
                        )
                    } else {
                        // Fallback if session is null
                        navController.popBackStack()
                    }
                }

                composable(NavRoutes.CUSTOM_WORKOUT) {
                    CustomWorkoutScreen(
                        exerciseCatalog = exercises,
                        onSaveCustomWorkout = { title, desc, cat, diff, exList ->
                            viewModel.createCustomWorkout(title, desc, cat, diff, exList) {
                                navController.popBackStack()
                            }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(NavRoutes.EXERCISE_DETAIL) {
                    val ex = exercises.find { it.id == selectedDetailExerciseId }
                    if (ex != null) {
                        ExerciseDetailScreen(
                            exercise = ex,
                            onToggleFavorite = { viewModel.toggleExerciseFavorite(ex.id, it) },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }

                composable(NavRoutes.PROGRESS) {
                    ProgressScreen(
                        userProfile = userProfile,
                        levelInfo = levelInfo,
                        streakInfo = streakInfo,
                        workoutRecords = workoutRecords,
                        personalRecords = personalRecords,
                        onDeleteRecord = { id -> viewModel.deleteWorkoutRecord(id) },
                        onUpdateNotes = { id, notes -> viewModel.updateRecordNotes(id, notes) }
                    )
                }

                composable(NavRoutes.REWARDS) {
                    RewardsScreen(
                        levelInfo = levelInfo,
                        quests = quests,
                        achievements = achievements,
                        onClaimQuest = { id -> viewModel.claimQuest(id) }
                    )
                }

                composable(NavRoutes.PROFILE) {
                    ProfileScreen(
                        userProfile = userProfile,
                        onUpdateProfile = { newProfile -> viewModel.updateUserProfile(newProfile) },
                        onResetProgress = { viewModel.resetAllProgress() }
                    )
                }
            }
        }
    }
}
