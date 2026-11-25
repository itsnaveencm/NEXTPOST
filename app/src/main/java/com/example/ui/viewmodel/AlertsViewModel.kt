package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.CandidateProfileStore
import com.example.data.model.ApplicationLifecycleStage
import com.example.data.model.AppliedJobRecord
import com.example.data.model.CandidateProfile
import com.example.data.model.DateWindowUtils
import com.example.data.model.EligibilityEngine
import com.example.data.model.ExamUpdate
import com.example.data.model.GovOrganisationNode
import com.example.data.model.IndianState
import com.example.data.model.IndianStatesCatalog
import com.example.data.model.JobAlert
import com.example.data.model.JobAlertUiModel
import com.example.data.model.JobSector
import com.example.data.model.JobWindowFilter
import com.example.data.model.OfficialSourceVerifier
import com.example.data.model.PersonalApplicationStatus
import com.example.data.model.PrivateCompanyNode
import com.example.data.model.PrivateDomainNode
import com.example.data.model.RecruitmentHierarchy
import com.example.data.model.SavedJobRecord
import com.example.data.model.StateQualificationTier
import com.example.data.repository.FeedSyncMetadata
import com.example.data.repository.JobRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class StateDirectoryItem(
  val state: IndianState,
  val activeAlertCount: Int,
  val maxSectorAlertCount: Int,
  val isHomeState: Boolean,
)

data class GlobalSearchResults(
  val orgs: List<GovOrganisationNode> = emptyList(),
  val states: List<StateDirectoryItem> = emptyList(),
  val companies: List<PrivateCompanyNode> = emptyList(),
  val openJobs: List<JobAlertUiModel> = emptyList(),
  val updates: List<ExamUpdate> = emptyList(),
) {
  val isEmpty: Boolean
    get() =
      orgs.isEmpty() &&
        states.isEmpty() &&
        companies.isEmpty() &&
        openJobs.isEmpty() &&
        updates.isEmpty()
}

data class AlertsUiState(
  val candidateProfile: CandidateProfile = CandidateProfile(),
  val eligibleOnly: Boolean = true,
  val windowFilter: JobWindowFilter = JobWindowFilter.ACTIVE,
  val searchQuery: String = "",
  val todayIsoDate: String = DateWindowUtils.currentLocalDateIso(),
  val syncMetadata: FeedSyncMetadata = FeedSyncMetadata(),
  val configuredFeedUrl: String = "",
  val isSyncing: Boolean = false,
  val totalOpenEligibleCount: Int = 0,
  val totalUpcomingCount: Int = 0,
  val govOpenCount: Int = 0,
  val stateOpenCount: Int = 0,
  val privateOpenCount: Int = 0,
  val allCatalogJobsUi: List<JobAlertUiModel> = emptyList(),
  val openJobsUi: List<JobAlertUiModel> = emptyList(),
  val upcomingJobsUi: List<JobAlertUiModel> = emptyList(),
  val closingSoonPreview: List<JobAlertUiModel> = emptyList(),
  val stateDirectory: List<StateDirectoryItem> = emptyList(),
  val examUpdates: List<ExamUpdate> = emptyList(),
  val latestUpdatesPreview: List<ExamUpdate> = emptyList(),
  val appliedJobs: List<AppliedJobRecord> = emptyList(),
  val trackedApplicationsPreview: List<AppliedJobRecord> = emptyList(),
  val appliedJobIds: Set<String> = emptySet(),
  val savedJobs: List<SavedJobRecord> = emptyList(),
  val savedJobIds: Set<String> = emptySet(),
  val savedNotificationIds: Set<String> = emptySet(),
  val savedOrgIds: Set<String> = emptySet(),
  val globalSearchResults: GlobalSearchResults = GlobalSearchResults(),
  val applyConfirmationJob: JobAlert? = null,
) {
  private fun windowPool(): List<JobAlertUiModel> =
    if (windowFilter == JobWindowFilter.UPCOMING) upcomingJobsUi else openJobsUi

  fun findJobUi(jobId: String): JobAlertUiModel? =
    allCatalogJobsUi.firstOrNull { it.job.id == jobId }

  fun findUpdate(updateId: String): ExamUpdate? =
    examUpdates.firstOrNull { it.id == updateId }

  fun jobsForOrg(orgId: String, categoryId: String = "ALL"): List<JobAlertUiModel> =
    windowPool().filter { item ->
      (!eligibleOnly || item.isEligible) &&
        RecruitmentHierarchy.matchesOrgSubCategory(item.job, orgId, categoryId)
    }

  fun openJobsForOrg(orgId: String, categoryId: String = "ALL"): List<JobAlertUiModel> =
    openJobsUi.filter { item ->
      (!eligibleOnly || item.isEligible) &&
        RecruitmentHierarchy.matchesOrgSubCategory(item.job, orgId, categoryId)
    }

  fun jobsForStateSubsection(
    stateCode: String,
    subsectionId: String,
    tier: StateQualificationTier = StateQualificationTier.ALL,
  ): List<JobAlertUiModel> =
    windowPool().filter { item ->
      item.job.sector == JobSector.STATE_GOVT &&
        item.job.stateCode.equals(stateCode, ignoreCase = true) &&
        (!eligibleOnly || item.isEligible) &&
        (subsectionId == "STATE_LEVEL" ||
          RecruitmentHierarchy.resolveStateSubsection(item.job) == subsectionId) &&
        (tier == StateQualificationTier.ALL ||
          RecruitmentHierarchy.resolveStateQualTier(item.job) == tier)
    }

  fun openJobsForStateSubsection(
    stateCode: String,
    subsectionId: String,
    tier: StateQualificationTier = StateQualificationTier.ALL,
  ): List<JobAlertUiModel> =
    openJobsUi.filter { item ->
      item.job.sector == JobSector.STATE_GOVT &&
        item.job.stateCode.equals(stateCode, ignoreCase = true) &&
        (!eligibleOnly || item.isEligible) &&
        (subsectionId == "STATE_LEVEL" ||
          RecruitmentHierarchy.resolveStateSubsection(item.job) == subsectionId) &&
        (tier == StateQualificationTier.ALL ||
          RecruitmentHierarchy.resolveStateQualTier(item.job) == tier)
    }

  fun availableStateSubsections(stateCode: String): List<Pair<String, Int>> {
    val stateOpen =
      openJobsUi.filter { item ->
        item.job.sector == JobSector.STATE_GOVT &&
          item.job.stateCode.equals(stateCode, ignoreCase = true) &&
          (!eligibleOnly || item.isEligible)
      }
    if (stateOpen.isEmpty()) return emptyList()
    return RecruitmentHierarchy.stateSubsections
      .map { sub ->
        val count =
          if (sub.id == "STATE_LEVEL") stateOpen.size
          else stateOpen.count { RecruitmentHierarchy.resolveStateSubsection(it.job) == sub.id }
        sub.id to count
      }
      .filter { it.second > 0 }
  }

  fun jobsForCompany(companyId: String): List<JobAlertUiModel> =
    windowPool().filter { item ->
      (!eligibleOnly || item.isEligible) && RecruitmentHierarchy.matchesCompany(item.job, companyId)
    }

  fun openJobsForCompany(companyId: String): List<JobAlertUiModel> =
    openJobsUi.filter { item ->
      (!eligibleOnly || item.isEligible) && RecruitmentHierarchy.matchesCompany(item.job, companyId)
    }

  fun openCountForDomain(domain: PrivateDomainNode): Int =
    RecruitmentHierarchy.privateCompanies
      .filter { domain.id in it.domainIds }
      .sumOf { openJobsForCompany(it.id).size }

  fun notAppliedOpenJobs(): List<JobAlertUiModel> =
    openJobsUi.filter { item ->
      (!eligibleOnly || item.isEligible) && item.job.id !in appliedJobIds
    }
}

class AlertsViewModel(
  private val repository: JobRepository,
  private val profileStore: CandidateProfileStore,
  private val currentDateProvider: () -> String = { DateWindowUtils.currentLocalDateIso() },
) : ViewModel() {

  private val eligibleOnly = MutableStateFlow(true)
  private val windowFilter = MutableStateFlow(JobWindowFilter.ACTIVE)
  private val searchQuery = MutableStateFlow("")
  private val isSyncing = MutableStateFlow(false)

  private val pendingExternalApplyJob = MutableStateFlow<JobAlert?>(null)
  private val sawPauseForExternalApply = MutableStateFlow(false)
  private val applyConfirmationJob = MutableStateFlow<JobAlert?>(null)

  init {
    viewModelScope.launch {
      val savedEndpoint = profileStore.feedEndpointUrlFlow.first()
      refreshVerifiedFeed(savedEndpoint.ifBlank { null })
    }
  }

  private val filterControlsFlow =
    combine(eligibleOnly, windowFilter, searchQuery) { onlyEligible, filter, query ->
      FilterBundle(onlyEligible, filter, query)
    }

  private val metaFlow =
    combine(
      profileStore.savedNotificationIdsFlow,
      profileStore.savedOrgIdsFlow,
      profileStore.feedEndpointUrlFlow,
      isSyncing,
      applyConfirmationJob,
    ) { savedNotices, savedOrgs, endpointUrl, syncing, promptJob ->
      MetaBundle(savedNotices, savedOrgs, endpointUrl, syncing, promptJob)
    }

  private val recordsFlow =
    combine(
      repository.observeAllJobs(),
      repository.observeExamUpdates(),
      repository.observeSavedJobs(profileStore.profileFlow),
      repository.observeAppliedJobs(),
      repository.observeSyncMetadata(),
    ) { allJobs, examUpdates, savedJobs, appliedJobs, syncMeta ->
      RecordsBundle(allJobs, examUpdates, savedJobs, appliedJobs, syncMeta)
    }

  val uiState: StateFlow<AlertsUiState> =
    combine(
      filterControlsFlow,
      profileStore.profileFlow,
      recordsFlow,
      metaFlow,
    ) { filters, profile, records, meta ->
      val todayIso = currentDateProvider()
      val savedIds = records.savedJobs.map { it.job.id }.toSet()
      val appliedByJobId = records.appliedJobs.associateBy { it.job.id }
      val appliedIds = appliedByJobId.keys

      // Enforce Official Source Verification across every job & exam update
      val verifiedCatalogUi =
        records.allJobs
          .filter { OfficialSourceVerifier.verifyJob(it).isVerified }
          .map { job ->
            val applied = appliedByJobId[job.id]
            JobAlertUiModel(
              job = job,
              isEligible = EligibilityEngine.isEligible(job, profile),
              isSaved = job.id in savedIds,
              isApplied = applied != null,
              registrationNumber = applied?.registrationNumber,
              isSourceVerified = true,
            )
          }

      val verifiedUpdates =
        records.examUpdates.filter { OfficialSourceVerifier.verifyExamUpdate(it).isVerified }

      // Real-time LocalDate.now() clock gate:
      // ACTIVE only when applicationStartDate <= today <= applicationEndDate
      val openJobsUi =
        verifiedCatalogUi
          .filter { it.job.isApplicationOpenNow(todayIso) }
          .sortedBy { it.job.lastDateEpochDay() }

      // UPCOMING only when today < applicationStartDate; EXPIRED (> endDate) automatically hidden
      val upcomingJobsUi =
        verifiedCatalogUi
          .filter { it.job.isUpcoming(todayIso) }
          .sortedBy { it.job.startDateEpochDay() }

      val activeOpenJobs =
        if (filters.onlyEligible) openJobsUi.filter { it.isEligible } else openJobsUi

      val govOpenCount = activeOpenJobs.count { it.job.sector == JobSector.CENTRAL_GOVT }
      val stateOpenCount = activeOpenJobs.count { it.job.sector == JobSector.STATE_GOVT }
      val privateOpenCount =
        activeOpenJobs.count {
          it.job.sector == JobSector.PRIVATE_MNC || it.job.sector == JobSector.STATE_PRIVATE
        }

      val stateCounts =
        activeOpenJobs
          .filter { it.job.sector == JobSector.STATE_GOVT }
          .groupingBy { it.job.stateCode.orEmpty() }
          .eachCount()

      val stateDirectory =
        buildStateDirectory(
          query = "",
          homeStateCode = profile.homeStateCode,
          countsByState = stateCounts,
        )

      val query = filters.query.trim()
      val globalSearch =
        if (query.isEmpty()) {
          GlobalSearchResults()
        } else {
          GlobalSearchResults(
            orgs =
              RecruitmentHierarchy.govOrganisations.filter {
                it.shortName.contains(query, ignoreCase = true) ||
                  it.fullName.contains(query, ignoreCase = true) ||
                  it.subtitle.contains(query, ignoreCase = true)
              },
            states =
              buildStateDirectory(
                query = query,
                homeStateCode = profile.homeStateCode,
                countsByState = stateCounts,
              ),
            companies =
              RecruitmentHierarchy.privateCompanies.filter {
                it.name.contains(query, ignoreCase = true) ||
                  it.tagline.contains(query, ignoreCase = true) ||
                  it.headquarters.contains(query, ignoreCase = true)
              },
            openJobs =
              activeOpenJobs.filter { item ->
                item.job.postTitle.contains(query, ignoreCase = true) ||
                  item.job.organization.contains(query, ignoreCase = true) ||
                  item.job.locationLabel.contains(query, ignoreCase = true) ||
                  item.job.qualificationTag.contains(query, ignoreCase = true) ||
                  item.job.officialDomain.contains(query, ignoreCase = true)
              },
            updates =
              verifiedUpdates.filter { update ->
                update.examTitle.contains(query, ignoreCase = true) ||
                  update.organization.contains(query, ignoreCase = true) ||
                  update.type.badgeLabel.contains(query, ignoreCase = true) ||
                  update.officialDomain.contains(query, ignoreCase = true)
              },
          )
        }

      AlertsUiState(
        candidateProfile = profile,
        eligibleOnly = filters.onlyEligible,
        windowFilter = filters.windowFilter,
        searchQuery = filters.query,
        todayIsoDate = todayIso,
        syncMetadata = records.syncMeta,
        configuredFeedUrl = meta.endpointUrl.ifBlank { records.syncMeta.endpointUrl },
        isSyncing = meta.syncing,
        totalOpenEligibleCount = openJobsUi.count { it.isEligible },
        totalUpcomingCount =
          if (filters.onlyEligible) upcomingJobsUi.count { it.isEligible } else upcomingJobsUi.size,
        govOpenCount = govOpenCount,
        stateOpenCount = stateOpenCount,
        privateOpenCount = privateOpenCount,
        allCatalogJobsUi = verifiedCatalogUi,
        openJobsUi = openJobsUi,
        upcomingJobsUi = upcomingJobsUi,
        closingSoonPreview = activeOpenJobs.take(2),
        stateDirectory = stateDirectory,
        examUpdates = verifiedUpdates,
        latestUpdatesPreview = verifiedUpdates.take(2),
        appliedJobs = records.appliedJobs,
        trackedApplicationsPreview = records.appliedJobs.take(2),
        appliedJobIds = appliedIds,
        savedJobs = records.savedJobs,
        savedJobIds = savedIds,
        savedNotificationIds = meta.savedNotices,
        savedOrgIds = meta.savedOrgs,
        globalSearchResults = globalSearch,
        applyConfirmationJob = meta.promptJob,
      )
    }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AlertsUiState(),
      )

  fun refreshVerifiedFeed(endpointOverride: String? = null) {
    viewModelScope.launch {
      isSyncing.value = true
      try {
        val savedEndpoint = profileStore.feedEndpointUrlFlow.first()
        val target = endpointOverride?.trim() ?: savedEndpoint.ifBlank { null }
        repository.syncVerifiedFeed(target)
      } finally {
        isSyncing.value = false
      }
    }
  }

  fun saveFeedEndpointAndSync(endpointUrl: String) {
    viewModelScope.launch {
      val clean = endpointUrl.trim()
      profileStore.saveFeedEndpointUrl(clean)
      refreshVerifiedFeed(clean)
    }
  }

  fun setWindowFilter(filter: JobWindowFilter) {
    windowFilter.value = filter
  }

  fun toggleEligibleOnly() {
    eligibleOnly.value = !eligibleOnly.value
  }

  fun updateSearchQuery(query: String) {
    searchQuery.value = query
  }

  fun saveCandidateProfile(profile: CandidateProfile) {
    viewModelScope.launch { profileStore.saveProfile(profile) }
  }

  fun toggleBookmark(jobId: String) {
    viewModelScope.launch { repository.toggleBookmark(jobId) }
  }

  fun setDeadlineReminder(jobId: String, enabled: Boolean) {
    viewModelScope.launch { repository.setDeadlineReminder(jobId, enabled) }
  }

  fun toggleSavedNotification(updateId: String) {
    viewModelScope.launch { profileStore.toggleSavedNotification(updateId) }
  }

  fun toggleSavedOrg(orgOrCompanyId: String) {
    viewModelScope.launch { profileStore.toggleSavedOrg(orgOrCompanyId) }
  }

  fun markJobApplied(
    jobId: String,
    registrationNumber: String,
    personalStatus: PersonalApplicationStatus = PersonalApplicationStatus.APPLIED,
  ) {
    viewModelScope.launch {
      val appliedDate = DateWindowUtils.formatMillisToDisplay()
      repository.markJobApplied(
        jobId = jobId,
        registrationNumber = registrationNumber,
        appliedDateDisplay = appliedDate,
        personalStatus = personalStatus,
      )
    }
  }

  fun removeAppliedJob(jobId: String) {
    viewModelScope.launch { repository.removeAppliedJob(jobId) }
  }

  fun updateAppliedStage(jobId: String, stage: ApplicationLifecycleStage) {
    viewModelScope.launch { repository.updateAppliedStage(jobId, stage) }
  }

  fun updatePersonalStatus(jobId: String, status: PersonalApplicationStatus) {
    viewModelScope.launch { repository.updatePersonalStatus(jobId, status) }
  }

  fun updateRegistrationNumber(jobId: String, registrationNumber: String) {
    viewModelScope.launch { repository.updateRegistrationNumber(jobId, registrationNumber) }
  }

  fun onExternalApplyLaunched(job: JobAlert) {
    if (job.id !in uiState.value.appliedJobIds) {
      pendingExternalApplyJob.value = job
      sawPauseForExternalApply.value = false
    }
  }

  fun onHostPaused() {
    if (pendingExternalApplyJob.value != null) {
      sawPauseForExternalApply.value = true
    }
  }

  fun onHostResumed() {
    val pending = pendingExternalApplyJob.value
    if (pending != null && sawPauseForExternalApply.value) {
      pendingExternalApplyJob.value = null
      sawPauseForExternalApply.value = false
      if (pending.id !in uiState.value.appliedJobIds) {
        applyConfirmationJob.value = pending
      }
    }
  }

  fun dismissApplyConfirmation() {
    applyConfirmationJob.value = null
  }

  private fun buildStateDirectory(
    query: String,
    homeStateCode: String,
    countsByState: Map<String, Int>,
  ): List<StateDirectoryItem> {
    val maxCount = (countsByState.values.maxOrNull() ?: 1).coerceAtLeast(1)
    val filtered =
      if (query.isEmpty()) {
        IndianStatesCatalog.allRegions
      } else {
        IndianStatesCatalog.allRegions.filter { region ->
          region.name.contains(query, ignoreCase = true) ||
            region.code.contains(query, ignoreCase = true) ||
            region.primaryHub.contains(query, ignoreCase = true) ||
            region.boardShort.contains(query, ignoreCase = true)
        }
      }

    return filtered
      .map { region ->
        StateDirectoryItem(
          state = region,
          activeAlertCount = countsByState[region.code] ?: 0,
          maxSectorAlertCount = maxCount,
          isHomeState = region.code.equals(homeStateCode, ignoreCase = true),
        )
      }
      .sortedWith(
        compareByDescending<StateDirectoryItem> { it.isHomeState }
          .thenByDescending { it.activeAlertCount }
      )
  }

  private data class FilterBundle(
    val onlyEligible: Boolean,
    val windowFilter: JobWindowFilter,
    val query: String,
  )

  private data class RecordsBundle(
    val allJobs: List<JobAlert>,
    val examUpdates: List<ExamUpdate>,
    val savedJobs: List<SavedJobRecord>,
    val appliedJobs: List<AppliedJobRecord>,
    val syncMeta: FeedSyncMetadata,
  )

  private data class MetaBundle(
    val savedNotices: Set<String>,
    val savedOrgs: Set<String>,
    val endpointUrl: String,
    val syncing: Boolean,
    val promptJob: JobAlert?,
  )

  class Factory(
    private val repository: JobRepository,
    private val profileStore: CandidateProfileStore,
  ) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(AlertsViewModel::class.java)) {
        return AlertsViewModel(repository, profileStore) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
  }
}
