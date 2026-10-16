package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ExerciseEntity::class,
        WorkoutTemplateEntity::class,
        TemplateExerciseEntity::class,
        WorkoutRecordEntity::class,
        LoggedSetEntity::class,
        UserProfileEntity::class,
        QuestEntity::class,
        AchievementEntity::class,
        PersonalRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun gamificationDao(): GamificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "repquest_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate default data on DB creation
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context).populateInitialData()
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun populateInitialData() {
        if (exerciseDao().getExerciseCount() == 0) {
            exerciseDao().insertExercises(InitialData.getDefaultExercises())
        }
        if (workoutDao().getTemplateCount() == 0) {
            workoutDao().insertTemplates(InitialData.getDefaultTemplates())
            workoutDao().insertTemplateExercises(InitialData.getDefaultTemplateExercises())
        }
        if (gamificationDao().getAchievementCount() == 0) {
            gamificationDao().insertAchievements(InitialData.getDefaultAchievements())
        }
        if (gamificationDao().getUserProfile() == null) {
            gamificationDao().insertOrUpdateUserProfile(InitialData.getDefaultUserProfile())
        }
        // Initialize starter quests
        val todayIso = java.time.LocalDate.now().toString()
        if (gamificationDao().getQuestsForPeriodKey(todayIso).isEmpty()) {
            gamificationDao().insertQuests(InitialData.getDefaultQuests(todayIso))
        }
    }
}
