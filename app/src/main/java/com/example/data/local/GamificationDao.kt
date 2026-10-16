package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GamificationDao {
    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 'default_user' LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 'default_user' LIMIT 1")
    suspend fun getUserProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserProfile(profile: UserProfileEntity)

    // Quests
    @Query("SELECT * FROM quests ORDER BY period ASC, isCompleted ASC")
    fun getAllQuestsFlow(): Flow<List<QuestEntity>>

    @Query("SELECT * FROM quests WHERE periodKey = :periodKey")
    suspend fun getQuestsForPeriodKey(periodKey: String): List<QuestEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuests(quests: List<QuestEntity>)

    @Update
    suspend fun updateQuest(quest: QuestEntity)

    @Query("UPDATE quests SET isClaimed = 1 WHERE id = :questId")
    suspend fun claimQuest(questId: String)

    // Achievements
    @Query("SELECT * FROM achievements ORDER BY isUnlocked DESC, id ASC")
    fun getAllAchievementsFlow(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE id = :id LIMIT 1")
    suspend fun getAchievementById(id: String): AchievementEntity?

    @Query("SELECT COUNT(*) FROM achievements")
    suspend fun getAchievementCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)

    // Personal Records
    @Query("SELECT * FROM personal_records ORDER BY achievedAtMs DESC")
    fun getAllPRsFlow(): Flow<List<PersonalRecordEntity>>

    @Query("SELECT * FROM personal_records WHERE exerciseId = :exerciseId LIMIT 1")
    suspend fun getPRForExercise(exerciseId: String): PersonalRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePR(pr: PersonalRecordEntity)

    @Query("DELETE FROM personal_records")
    suspend fun clearPersonalRecords()
}
