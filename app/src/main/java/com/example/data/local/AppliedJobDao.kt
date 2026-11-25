package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppliedJobDao {
  @Query("SELECT * FROM applied_jobs ORDER BY appliedAtMillis DESC")
  fun observeAppliedJobs(): Flow<List<AppliedJobEntity>>

  @Query("SELECT * FROM applied_jobs WHERE jobId = :jobId LIMIT 1")
  suspend fun getAppliedJob(jobId: String): AppliedJobEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAppliedJob(entity: AppliedJobEntity)

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertIfAbsent(entities: List<AppliedJobEntity>)

  @Query("DELETE FROM applied_jobs WHERE jobId = :jobId")
  suspend fun deleteByJobId(jobId: String)

  @Query("UPDATE applied_jobs SET stageName = :stageName WHERE jobId = :jobId")
  suspend fun updateStage(jobId: String, stageName: String)

  @Query("UPDATE applied_jobs SET personalStatusName = :personalStatusName WHERE jobId = :jobId")
  suspend fun updatePersonalStatus(jobId: String, personalStatusName: String)

  @Query("UPDATE applied_jobs SET registrationNumber = :registrationNumber WHERE jobId = :jobId")
  suspend fun updateRegistrationNumber(jobId: String, registrationNumber: String)
}
