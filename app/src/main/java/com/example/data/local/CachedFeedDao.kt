package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedFeedDao {
  @Query("SELECT * FROM cached_jobs ORDER BY applicationEndDate ASC")
  fun observeCachedJobs(): Flow<List<CachedJobEntity>>

  @Query("SELECT * FROM cached_jobs WHERE id = :jobId LIMIT 1")
  suspend fun getCachedJob(jobId: String): CachedJobEntity?

  @Query("SELECT * FROM cached_exam_updates ORDER BY lastSyncedAtMillis DESC")
  fun observeCachedExamUpdates(): Flow<List<CachedExamUpdateEntity>>

  @Query("SELECT * FROM feed_sync_metadata WHERE id = 'main_sync' LIMIT 1")
  fun observeSyncMetadata(): Flow<FeedSyncMetadataEntity?>

  @Query("SELECT * FROM feed_sync_metadata WHERE id = 'main_sync' LIMIT 1")
  suspend fun getSyncMetadataSync(): FeedSyncMetadataEntity?

  @Query("DELETE FROM cached_jobs")
  suspend fun clearCachedJobs()

  @Query("DELETE FROM cached_exam_updates")
  suspend fun clearCachedExamUpdates()

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCachedJobs(jobs: List<CachedJobEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCachedExamUpdates(updates: List<CachedExamUpdateEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertSyncMetadata(metadata: FeedSyncMetadataEntity)

  @Transaction
  suspend fun replaceVerifiedFeed(
    jobs: List<CachedJobEntity>,
    updates: List<CachedExamUpdateEntity>,
    metadata: FeedSyncMetadataEntity,
  ) {
    clearCachedJobs()
    clearCachedExamUpdates()
    if (jobs.isNotEmpty()) insertCachedJobs(jobs)
    if (updates.isNotEmpty()) insertCachedExamUpdates(updates)
    upsertSyncMetadata(metadata)
  }
}
