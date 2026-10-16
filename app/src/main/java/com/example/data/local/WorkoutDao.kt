package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    // Templates
    @Query("SELECT * FROM workout_templates ORDER BY isCustom DESC, title ASC")
    fun getAllTemplates(): Flow<List<WorkoutTemplateEntity>>

    @Query("SELECT * FROM workout_templates WHERE id = :id LIMIT 1")
    suspend fun getTemplateById(id: String): WorkoutTemplateEntity?

    @Query("SELECT COUNT(*) FROM workout_templates")
    suspend fun getTemplateCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplates(templates: List<WorkoutTemplateEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: WorkoutTemplateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplateExercises(exercises: List<TemplateExerciseEntity>)

    @Query("SELECT * FROM template_exercises WHERE templateId = :templateId ORDER BY orderIndex ASC")
    fun getTemplateExercisesFlow(templateId: String): Flow<List<TemplateExerciseEntity>>

    @Query("SELECT * FROM template_exercises WHERE templateId = :templateId ORDER BY orderIndex ASC")
    suspend fun getTemplateExercises(templateId: String): List<TemplateExerciseEntity>

    @Query("DELETE FROM workout_templates WHERE id = :templateId")
    suspend fun deleteTemplateById(templateId: String)

    @Query("DELETE FROM template_exercises WHERE templateId = :templateId")
    suspend fun deleteTemplateExercises(templateId: String)

    @Query("UPDATE workout_templates SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setTemplateFavorite(id: String, isFavorite: Boolean)

    // Workout Records
    @Query("SELECT * FROM workout_records ORDER BY startTimeMs DESC")
    fun getAllRecords(): Flow<List<WorkoutRecordEntity>>

    @Query("SELECT * FROM workout_records WHERE id = :id LIMIT 1")
    suspend fun getRecordById(id: String): WorkoutRecordEntity?

    @Query("SELECT * FROM workout_records WHERE dateIso = :dateIso ORDER BY startTimeMs DESC")
    fun getRecordsForDate(dateIso: String): Flow<List<WorkoutRecordEntity>>

    @Query("SELECT * FROM workout_records WHERE dateIso BETWEEN :startDate AND :endDate ORDER BY startTimeMs ASC")
    suspend fun getRecordsBetweenDates(startDate: String, endDate: String): List<WorkoutRecordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutRecord(record: WorkoutRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoggedSets(sets: List<LoggedSetEntity>)

    @Query("SELECT * FROM logged_sets WHERE workoutRecordId = :recordId ORDER BY setNumber ASC")
    fun getLoggedSetsFlow(recordId: String): Flow<List<LoggedSetEntity>>

    @Query("SELECT * FROM logged_sets WHERE workoutRecordId = :recordId ORDER BY setNumber ASC")
    suspend fun getLoggedSets(recordId: String): List<LoggedSetEntity>

    @Query("SELECT * FROM logged_sets WHERE exerciseId = :exerciseId ORDER BY weightKg DESC, reps DESC")
    suspend fun getLoggedSetsForExercise(exerciseId: String): List<LoggedSetEntity>

    @Query("DELETE FROM workout_records WHERE id = :id")
    suspend fun deleteRecordById(id: String)

    @Query("DELETE FROM logged_sets WHERE workoutRecordId = :recordId")
    suspend fun deleteLoggedSetsForRecord(recordId: String)

    @Query("UPDATE workout_records SET notes = :notes WHERE id = :id")
    suspend fun updateRecordNotes(id: String, notes: String)

    @Query("DELETE FROM workout_records")
    suspend fun clearAllWorkoutRecords()

    @Query("DELETE FROM logged_sets")
    suspend fun clearAllLoggedSets()
}
