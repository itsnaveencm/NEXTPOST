package com.example.data.repository

import com.example.data.local.AppliedJobDao
import com.example.data.local.AppliedJobEntity
import com.example.data.local.CachedExamUpdateEntity
import com.example.data.local.CachedFeedDao
import com.example.data.local.CachedJobEntity
import com.example.data.local.FeedSyncMetadataEntity
import com.example.data.local.SavedJobDao
import com.example.data.local.SavedJobEntity
import com.example.data.model.ApplicationLifecycleStage
import com.example.data.model.AppliedJobRecord
import com.example.data.model.CandidateProfile
import com.example.data.model.EligibilityEngine
import com.example.data.model.ExamUpdate
import com.example.data.model.JobAlert
import com.example.data.model.JobSector
import com.example.data.model.OfficialSourceVerifier
import com.example.data.model.PersonalApplicationStatus
import com.example.data.model.SavedJobRecord
import com.example.data.remote.OfficialFeedApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LiveVerifiedJobRepository(
  private val savedJobDao: SavedJobDao,
  private val appliedJobDao: AppliedJobDao,
  private val cachedFeedDao: CachedFeedDao,
  private val apiService: OfficialFeedApiService,
  private val defaultFeedEndpointUrl: String = "",
) : JobRepository {

  private val verifiedJobsFlow: Flow<List<JobAlert>> =
    cachedFeedDao.observeCachedJobs().map { entities ->
      entities
        .mapNotNull { it.toDomain() }
        .filter { OfficialSourceVerifier.verifyJob(it).isVerified }
    }

  private val verifiedUpdatesFlow: Flow<List<ExamUpdate>> =
    cachedFeedDao.observeCachedExamUpdates().map { entities ->
      entities
        .mapNotNull { it.toDomain() }
        .filter { OfficialSourceVerifier.verifyExamUpdate(it).isVerified }
    }

  override fun observeCentralGovtJobs(): Flow<List<JobAlert>> =
    verifiedJobsFlow.map { jobs -> jobs.filter { it.sector == JobSector.CENTRAL_GOVT } }

  override fun observeStateGovtJobs(stateCode: String?): Flow<List<JobAlert>> =
    verifiedJobsFlow.map { jobs ->
      jobs.filter {
        it.sector == JobSector.STATE_GOVT &&
          (stateCode == null || it.stateCode.equals(stateCode, ignoreCase = true))
      }
    }

  override fun observePrivateJobs(): Flow<List<JobAlert>> =
    verifiedJobsFlow.map { jobs -> jobs.filter { it.sector == JobSector.PRIVATE_MNC } }

  override fun observeStatePrivateJobs(stateCode: String?): Flow<List<JobAlert>> =
    verifiedJobsFlow.map { jobs ->
      jobs.filter {
        it.sector == JobSector.STATE_PRIVATE &&
          (stateCode == null || it.stateCode.equals(stateCode, ignoreCase = true))
      }
    }

  override fun observeAllJobs(): Flow<List<JobAlert>> = verifiedJobsFlow

  override fun observeExamUpdates(): Flow<List<ExamUpdate>> = verifiedUpdatesFlow

  override fun observeSyncMetadata(): Flow<FeedSyncMetadata> =
    cachedFeedDao.observeSyncMetadata().map { entity ->
      if (entity == null) {
        FeedSyncMetadata(
          endpointUrl = defaultFeedEndpointUrl,
          lastSyncedAtMillis = 0L,
          syncStatus =
            if (defaultFeedEndpointUrl.isBlank()) "CONNECTED_PORTALS_READY"
            else "PENDING_SYNC",
        )
      } else {
        FeedSyncMetadata(
          endpointUrl = entity.endpointUrl,
          lastSyncedAtMillis = entity.lastSyncedAtMillis,
          syncStatus = entity.syncStatus,
          verifiedJobsCount = entity.verifiedJobsCount,
          verifiedUpdatesCount = entity.verifiedUpdatesCount,
          blockedRecordsCount = entity.blockedRecordsCount,
        )
      }
    }

  override suspend fun syncVerifiedFeed(endpointUrlOverride: String?): FeedSyncResult {
    val targetUrl =
      endpointUrlOverride?.trim().takeUnless { it.isNullOrBlank() }
        ?: defaultFeedEndpointUrl.trim()

    if (targetUrl.isBlank()) {
      seedLocalVerifiedFallback(
        nowMillis = System.currentTimeMillis(),
        endpointUrl = "",
        syncStatus = "LOCAL_VERIFIED_FALLBACK",
        blockedRecordsCount = 0,
      )
      return FeedSyncResult.EndpointNotConfigured
    }

    return try {
      val response = apiService.fetchVerifiedFeed(targetUrl)
      val now = System.currentTimeMillis()
      var blockedCount = 0

      val verifiedJobs = mutableListOf<CachedJobEntity>()
      for (dto in response.jobs) {
        val domain = dto.toDomain()
        if (domain != null && OfficialSourceVerifier.verifyJob(domain).isVerified) {
          verifiedJobs.add(CachedJobEntity.fromDomain(domain, now))
        } else {
          blockedCount++
        }
      }

      val verifiedUpdates = mutableListOf<CachedExamUpdateEntity>()
      for (dto in response.examUpdates) {
        val domain = dto.toDomain()
        if (domain != null && OfficialSourceVerifier.verifyExamUpdate(domain).isVerified) {
          verifiedUpdates.add(CachedExamUpdateEntity.fromDomain(domain, now))
        } else {
          blockedCount++
        }
      }

      if (verifiedJobs.isEmpty()) {
        val fallbackPair =
          seedLocalVerifiedFallback(
            nowMillis = now,
            endpointUrl = targetUrl,
            syncStatus = "LOCAL_VERIFIED_FALLBACK",
            blockedRecordsCount = blockedCount,
            overrideUpdates = verifiedUpdates.takeIf { it.isNotEmpty() },
          )
        return FeedSyncResult.Success(
          verifiedJobsCount = fallbackPair.first,
          verifiedUpdatesCount = fallbackPair.second,
          blockedRecordsCount = blockedCount,
          syncedAtMillis = now,
        )
      }

      cachedFeedDao.replaceVerifiedFeed(
        jobs = verifiedJobs,
        updates = verifiedUpdates,
        metadata =
          FeedSyncMetadataEntity(
            endpointUrl = targetUrl,
            lastSyncedAtMillis = now,
            syncStatus = "VERIFIED_SYNC_OK",
            verifiedJobsCount = verifiedJobs.size,
            verifiedUpdatesCount = verifiedUpdates.size,
            blockedRecordsCount = blockedCount,
          ),
      )

      FeedSyncResult.Success(
        verifiedJobsCount = verifiedJobs.size,
        verifiedUpdatesCount = verifiedUpdates.size,
        blockedRecordsCount = blockedCount,
        syncedAtMillis = now,
      )
    } catch (e: Exception) {
      val message = e.localizedMessage ?: "Official feed sync failed"
      val existingJobs = cachedFeedDao.observeCachedJobs().first()
      val existingUpdates = cachedFeedDao.observeCachedExamUpdates().first()
      val previousMeta = cachedFeedDao.getSyncMetadataSync()
      val now = System.currentTimeMillis()

      if (existingJobs.isEmpty()) {
        seedLocalVerifiedFallback(
          nowMillis = now,
          endpointUrl = targetUrl,
          syncStatus = "SYNC_ERROR",
          blockedRecordsCount = previousMeta?.blockedRecordsCount ?: 0,
          overrideUpdates = existingUpdates.takeIf { it.isNotEmpty() },
        )
      } else {
        cachedFeedDao.upsertSyncMetadata(
          FeedSyncMetadataEntity(
            endpointUrl = targetUrl,
            lastSyncedAtMillis = previousMeta?.lastSyncedAtMillis ?: now,
            syncStatus = "SYNC_ERROR",
            verifiedJobsCount = existingJobs.size,
            verifiedUpdatesCount = existingUpdates.size,
            blockedRecordsCount = previousMeta?.blockedRecordsCount ?: 0,
          )
        )
      }
      FeedSyncResult.Error(message)
    }
  }

  private suspend fun seedLocalVerifiedFallback(
    nowMillis: Long,
    endpointUrl: String,
    syncStatus: String,
    blockedRecordsCount: Int,
    overrideUpdates: List<CachedExamUpdateEntity>? = null,
  ): Pair<Int, Int> {
    val fallbackJobs =
      VerifiedOfficialCatalog.jobs
        .filter { OfficialSourceVerifier.verifyJob(it).isVerified }
        .map { CachedJobEntity.fromDomain(it, nowMillis) }
    val fallbackUpdates =
      overrideUpdates
        ?: VerifiedOfficialCatalog.examUpdates
          .filter { OfficialSourceVerifier.verifyExamUpdate(it).isVerified }
          .map { CachedExamUpdateEntity.fromDomain(it, nowMillis) }

    cachedFeedDao.replaceVerifiedFeed(
      jobs = fallbackJobs,
      updates = fallbackUpdates,
      metadata =
        FeedSyncMetadataEntity(
          endpointUrl = endpointUrl,
          lastSyncedAtMillis = nowMillis,
          syncStatus = syncStatus,
          verifiedJobsCount = fallbackJobs.size,
          verifiedUpdatesCount = fallbackUpdates.size,
          blockedRecordsCount = blockedRecordsCount,
        ),
    )
    return fallbackJobs.size to fallbackUpdates.size
  }

  override fun observeSavedJobs(profileFlow: Flow<CandidateProfile>): Flow<List<SavedJobRecord>> =
    combine(
      savedJobDao.observeSavedJobs(),
      appliedJobDao.observeAppliedJobs(),
      verifiedJobsFlow,
      profileFlow,
    ) { savedEntities, appliedEntities, verifiedJobs, profile ->
      val jobsById = verifiedJobs.associateBy { it.id }
      val appliedByJobId = appliedEntities.associateBy { it.jobId }
      savedEntities
        .mapNotNull { entity ->
          jobsById[entity.jobId]?.let { job ->
            val applied = appliedByJobId[entity.jobId]
            SavedJobRecord(
              job = job,
              isEligible = EligibilityEngine.isEligible(job, profile),
              isApplied = applied != null,
              registrationNumber = applied?.registrationNumber,
              reminderEnabled = entity.reminderEnabled,
              savedAtMillis = entity.savedAtMillis,
            )
          }
        }
        .sortedBy { it.job.lastDateEpochDay() }
    }

  override fun observeSavedJobIds(): Flow<Set<String>> =
    savedJobDao.observeSavedJobs().map { entities -> entities.map { it.jobId }.toSet() }

  override fun observeAppliedJobs(): Flow<List<AppliedJobRecord>> =
    combine(appliedJobDao.observeAppliedJobs(), verifiedJobsFlow) { entities, verifiedJobs ->
      val jobsById = verifiedJobs.associateBy { it.id }
      entities.mapNotNull { entity ->
        jobsById[entity.jobId]?.let { job ->
          val stage =
            ApplicationLifecycleStage.entries.firstOrNull { it.name == entity.stageName }
              ?: ApplicationLifecycleStage.APPLIED
          val personalStatus =
            PersonalApplicationStatus.entries.firstOrNull {
              it.name == entity.personalStatusName
            } ?: PersonalApplicationStatus.APPLIED
          AppliedJobRecord(
            job = job,
            registrationNumber = entity.registrationNumber,
            appliedDateDisplay = entity.appliedDateDisplay,
            appliedAtMillis = entity.appliedAtMillis,
            stage = stage,
            personalStatus = personalStatus,
            portalUrl = job.directApplyUrl,
            statusNote = entity.statusNote,
          )
        }
      }
    }

  override fun observeAppliedJobIds(): Flow<Set<String>> =
    appliedJobDao.observeAppliedJobs().map { entities -> entities.map { it.jobId }.toSet() }

  override suspend fun toggleBookmark(jobId: String) {
    val existing = savedJobDao.getSavedJob(jobId)
    if (existing != null) {
      savedJobDao.deleteByJobId(jobId)
    } else {
      savedJobDao.upsertSavedJob(
        SavedJobEntity(
          jobId = jobId,
          savedAtMillis = System.currentTimeMillis(),
          reminderEnabled = true,
        )
      )
    }
  }

  override suspend fun setDeadlineReminder(jobId: String, enabled: Boolean) {
    savedJobDao.updateReminder(jobId, enabled)
  }

  override suspend fun markJobApplied(
    jobId: String,
    registrationNumber: String,
    appliedDateDisplay: String,
    stage: ApplicationLifecycleStage,
    personalStatus: PersonalApplicationStatus,
    statusNote: String?,
  ) {
    val existing = appliedJobDao.getAppliedJob(jobId)
    appliedJobDao.upsertAppliedJob(
      AppliedJobEntity(
        jobId = jobId,
        registrationNumber = registrationNumber.trim(),
        appliedDateDisplay = existing?.appliedDateDisplay ?: appliedDateDisplay,
        appliedAtMillis = existing?.appliedAtMillis ?: System.currentTimeMillis(),
        stageName = existing?.stageName ?: stage.name,
        personalStatusName = existing?.personalStatusName ?: personalStatus.name,
        statusNote = existing?.statusNote ?: statusNote,
      )
    )
  }

  override suspend fun removeAppliedJob(jobId: String) {
    appliedJobDao.deleteByJobId(jobId)
  }

  override suspend fun updateAppliedStage(jobId: String, stage: ApplicationLifecycleStage) {
    appliedJobDao.updateStage(jobId, stage.name)
  }

  override suspend fun updatePersonalStatus(jobId: String, status: PersonalApplicationStatus) {
    appliedJobDao.updatePersonalStatus(jobId, status.name)
  }

  override suspend fun updateRegistrationNumber(jobId: String, registrationNumber: String) {
    appliedJobDao.updateRegistrationNumber(jobId, registrationNumber.trim())
  }
}
