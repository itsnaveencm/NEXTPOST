package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedJobDao {
  @Query("SELECT * FROM saved_jobs ORDER BY savedAtMillis DESC")
  fun observeSavedJobs(): Flow<List<SavedJobEntity>>

  @Query("SELECT * FROM saved_jobs WHERE jobId = :jobId LIMIT 1")
  suspend fun getSavedJob(jobId: String): SavedJobEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertSavedJob(entity: SavedJobEntity)

  @Query("DELETE FROM saved_jobs WHERE jobId = :jobId")
  suspend fun deleteByJobId(jobId: String)

  @Query("UPDATE saved_jobs SET reminderEnabled = :enabled WHERE jobId = :jobId")
  suspend fun updateReminder(jobId: String, enabled: Boolean)
}
