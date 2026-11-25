package com.example.data.repository

import com.example.data.model.ApplicationLifecycleStage
import com.example.data.model.AppliedJobRecord
import com.example.data.model.CandidateProfile
import com.example.data.model.ExamUpdate
import com.example.data.model.JobAlert
import com.example.data.model.PersonalApplicationStatus
import com.example.data.model.SavedJobRecord
import kotlinx.coroutines.flow.Flow

data class FeedSyncMetadata(
  val endpointUrl: String = "",
  val lastSyncedAtMillis: Long = 0L,
  val syncStatus: String = "AWAITING_LIVE_FEED",
  val verifiedJobsCount: Int = 0,
  val verifiedUpdatesCount: Int = 0,
  val blockedRecordsCount: Int = 0,
)

sealed class FeedSyncResult {
  data class Success(
    val verifiedJobsCount: Int,
    val verifiedUpdatesCount: Int,
    val blockedRecordsCount: Int,
    val syncedAtMillis: Long,
  ) : FeedSyncResult()

  data object EndpointNotConfigured : FeedSyncResult()

  data class Error(val message: String) : FeedSyncResult()
}

interface JobRepository {
  fun observeCentralGovtJobs(): Flow<List<JobAlert>>

  fun observeStateGovtJobs(stateCode: String? = null): Flow<List<JobAlert>>

  fun observePrivateJobs(): Flow<List<JobAlert>>

  fun observeStatePrivateJobs(stateCode: String? = null): Flow<List<JobAlert>>

  fun observeAllJobs(): Flow<List<JobAlert>>

  fun observeExamUpdates(): Flow<List<ExamUpdate>>

  fun observeSyncMetadata(): Flow<FeedSyncMetadata>

  suspend fun syncVerifiedFeed(endpointUrlOverride: String? = null): FeedSyncResult

  fun observeSavedJobs(profileFlow: Flow<CandidateProfile>): Flow<List<SavedJobRecord>>

  fun observeSavedJobIds(): Flow<Set<String>>

  fun observeAppliedJobs(): Flow<List<AppliedJobRecord>>

  fun observeAppliedJobIds(): Flow<Set<String>>

  suspend fun toggleBookmark(jobId: String)

  suspend fun setDeadlineReminder(jobId: String, enabled: Boolean)

  suspend fun markJobApplied(
    jobId: String,
    registrationNumber: String,
    appliedDateDisplay: String,
    stage: ApplicationLifecycleStage = ApplicationLifecycleStage.APPLIED,
    personalStatus: PersonalApplicationStatus = PersonalApplicationStatus.APPLIED,
    statusNote: String? = null,
  )

  suspend fun removeAppliedJob(jobId: String)

  suspend fun updateAppliedStage(jobId: String, stage: ApplicationLifecycleStage)

  suspend fun updatePersonalStatus(jobId: String, status: PersonalApplicationStatus)

  suspend fun updateRegistrationNumber(jobId: String, registrationNumber: String)
}
