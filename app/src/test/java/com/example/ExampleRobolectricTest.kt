package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.data.local.AlertPulseDatabase
import com.example.data.local.CandidateProfileStore
import com.example.data.model.AcademicStream
import com.example.data.model.AiBrief
import com.example.data.model.ApplicationLifecycleStage
import com.example.data.model.CandidateProfile
import com.example.data.model.CentralAgencyFilter
import com.example.data.model.DateWindowUtils
import com.example.data.model.EligibilityEngine
import com.example.data.model.ExamUpdate
import com.example.data.model.ExamUpdateType
import com.example.data.model.IndianStatesCatalog
import com.example.data.model.JobAlert
import com.example.data.model.JobSector
import com.example.data.model.JobShareFormatter
import com.example.data.model.JobStatusTag
import com.example.data.model.NotificationPreferences
import com.example.data.model.OfficialSourceVerifier
import com.example.data.model.PersonalApplicationStatus
import com.example.data.model.QualificationLevel
import com.example.data.model.RecruitmentHierarchy
import com.example.data.model.RegionType
import com.example.data.remote.ExamUpdateDto
import com.example.data.remote.JobAlertDto
import com.example.data.remote.OfficialFeedApiService
import com.example.data.remote.OfficialFeedResponseDto
import com.example.data.repository.FeedSyncResult
import com.example.data.repository.LiveVerifiedJobRepository
import com.example.notifications.FeedSyncNotificationEvaluator
import com.example.notifications.NextPostWorkScheduler
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var database: AlertPulseDatabase

  private val nov24Iso = "2026-11-24"

  private val nov24Millis: Long by lazy {
    Calendar.getInstance()
      .apply {
        set(Calendar.YEAR, 2026)
        set(Calendar.MONTH, Calendar.NOVEMBER)
        set(Calendar.DAY_OF_MONTH, 24)
        set(Calendar.HOUR_OF_DAY, 12)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
      }
      .timeInMillis
  }

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database =
      Room.inMemoryDatabaseBuilder(context, AlertPulseDatabase::class.java)
        .allowMainThreadQueries()
        .build()
  }

  @After
  fun tearDown() {
    database.close()
  }

  private fun buildVerifiedDto(
    id: String,
    organization: String = "SSC",
    postTitle: String = "Combined Graduate Level Examination",
    sector: JobSector = JobSector.CENTRAL_GOVT,
    centralAgency: CentralAgencyFilter? = CentralAgencyFilter.SSC,
    startDate: String = "2026-11-01",
    endDate: String = "2026-12-20",
    officialDomain: String = "ssc.gov.in",
    applyUrl: String = "https://ssc.gov.in/portal/apply",
    pdfUrl: String = "https://ssc.gov.in/notice.pdf",
  ): JobAlertDto =
    JobAlertDto(
      id = id,
      organization = organization,
      postTitle = postTitle,
      sector = sector.name,
      centralAgency = centralAgency?.name,
      locationLabel = "All India",
      vacancyCountLabel = "As per official notice",
      qualificationTag = "Any Degree",
      minGeneralQualification = QualificationLevel.GRADUATE.name,
      allowedStreams = listOf(AcademicStream.ALL_GENERAL.name),
      minAge = 18,
      maxAge = 32,
      applicationStartDate = startDate,
      applicationEndDate = endDate,
      officialPdfUrl = pdfUrl,
      directApplyUrl = applyUrl,
      officialDomain = officialDomain,
      lastVerifiedTimestamp = "2026-11-24T09:00:00Z",
      eligibilityBrief = "Bachelor's Degree from a recognized university",
      ageLimitBrief = "18-32 years",
      selectionAndFeeBrief = "Tier-I & Tier-II CBT",
      orgTag = "SSC",
      subCategoryTag = "CGL",
    )

  private fun buildDomainJob(
    id: String,
    startDate: String = "2026-11-01",
    endDate: String = "2026-12-10",
    minQual: QualificationLevel? = QualificationLevel.GRADUATE,
    exactLevels: Set<QualificationLevel> = emptySet(),
    allowedStreams: Set<AcademicStream> = setOf(AcademicStream.ALL_GENERAL),
    minAge: Int = 18,
    maxAge: Int = 35,
    officialDomain: String = "ssc.gov.in",
    directApplyUrl: String = "https://ssc.gov.in/apply",
    officialPdfUrl: String = "https://ssc.gov.in/notice.pdf",
    lastVerifiedTimestamp: String = "2026-11-24T08:30:00Z",
    orgTag: String? = "SSC",
    subCategoryTag: String? = "CGL",
  ): JobAlert =
    JobAlert(
      id = id,
      organization = "Staff Selection Commission",
      postTitle = "SSC CGL Graduate Post",
      sector = JobSector.CENTRAL_GOVT,
      centralAgency = CentralAgencyFilter.SSC,
      locationLabel = "All India",
      vacancyCountLabel = "Verified Vacancies",
      qualificationTag = "Graduate",
      minGeneralQualification = minQual,
      acceptedExactLevels = exactLevels,
      allowedStreams = allowedStreams,
      minAge = minAge,
      maxAge = maxAge,
      applicationStartDate = startDate,
      applicationEndDate = endDate,
      officialPdfUrl = officialPdfUrl,
      directApplyUrl = directApplyUrl,
      aiBrief = AiBrief("Graduate", "$minAge-$maxAge yrs", "CBT"),
      officialDomain = officialDomain,
      lastVerifiedTimestamp = lastVerifiedTimestamp,
      orgTag = orgTag,
      subCategoryTag = subCategoryTag,
    )

  @Test
  fun `app name matches NextPost`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("NextPost", appName)
  }

  @Test
  fun `indian states catalog includes all 28 states and 8 union territories`() {
    val states = IndianStatesCatalog.allRegions.count { it.type == RegionType.STATE }
    val uts = IndianStatesCatalog.allRegions.count { it.type == RegionType.UT }
    assertEquals(28, states)
    assertEquals(8, uts)
  }

  @Test
  fun `official source verifier allows official domains and blocks aggregators or http`() {
    val validGov = buildDomainJob(id = "ssc_1")
    assertTrue(OfficialSourceVerifier.verifyJob(validGov).isVerified)

    val aggregatorJob =
      buildDomainJob(
        id = "bad_agg",
        officialDomain = "sarkariresult.com",
        directApplyUrl = "https://www.sarkariresult.com/rrb-je",
        officialPdfUrl = "https://www.sarkariresult.com/rrb.pdf",
      )
    assertFalse(OfficialSourceVerifier.verifyJob(aggregatorJob).isVerified)

    val httpJob =
      buildDomainJob(
        id = "bad_http",
        directApplyUrl = "http://ssc.gov.in/apply",
      )
    assertFalse(OfficialSourceVerifier.verifyJob(httpJob).isVerified)

    val missingTimestamp = buildDomainJob(id = "bad_ts", lastVerifiedTimestamp = "")
    assertFalse(OfficialSourceVerifier.verifyJob(missingTimestamp).isVerified)

    val badUpdate =
      ExamUpdate(
        id = "upd_fake",
        organization = "RRB",
        examTitle = "Fake City Slip",
        type = ExamUpdateType.CITY_SLIP,
        releasedDateDisplay = "24 NOV 2026",
        summaryNote = "Third-party mirror",
        portalLoginUrl = "https://freejobalert.com/rrb",
        officialDomain = "freejobalert.com",
        lastVerifiedTimestamp = "2026-11-24T10:00:00Z",
      )
    assertFalse(OfficialSourceVerifier.verifyExamUpdate(badUpdate).isVerified)
  }

  @Test
  fun `date window gate dynamically separates active, upcoming, and expired postings`() {
    val activeJob = buildDomainJob(id = "active", startDate = "2026-11-10", endDate = "2026-12-05")
    val closingSoon = buildDomainJob(id = "soon", startDate = "2026-11-01", endDate = "2026-11-28")
    val upcomingJob = buildDomainJob(id = "upcoming", startDate = "2026-12-01", endDate = "2026-12-30")
    val expiredJob = buildDomainJob(id = "expired", startDate = "2026-10-01", endDate = "2026-11-20")

    assertTrue(activeJob.isApplicationOpenNow(nov24Iso))
    assertEquals(JobStatusTag.ACTIVE, activeJob.resolveStatusTag(isApplied = false, currentDateIso = nov24Iso))

    assertTrue(closingSoon.isApplicationOpenNow(nov24Millis))
    assertEquals(JobStatusTag.CLOSING_SOON, closingSoon.resolveStatusTag(isApplied = false, nowMillis = nov24Millis))

    assertFalse(upcomingJob.isApplicationOpenNow(nov24Iso))
    assertTrue(upcomingJob.isUpcoming(nov24Iso))
    assertEquals(JobStatusTag.UPCOMING, upcomingJob.resolveStatusTag(isApplied = false, currentDateIso = nov24Iso))

    assertFalse(expiredJob.isApplicationOpenNow(nov24Iso))
    assertTrue(expiredJob.isExpired(nov24Iso))
    assertEquals(JobStatusTag.EXPIRED, expiredJob.resolveStatusTag(isApplied = false, currentDateIso = nov24Iso))
  }

  @Test
  fun `live repository starts empty and filters out unverified records during Room sync`() =
    runTest {
      val fakeService =
        object : OfficialFeedApiService {
          override suspend fun fetchVerifiedFeed(feedUrl: String): OfficialFeedResponseDto {
            return OfficialFeedResponseDto(
              jobs =
                listOf(
                  buildVerifiedDto(id = "verified_ssc_1"),
                  buildVerifiedDto(
                    id = "blocked_mirror_2",
                    officialDomain = "freejobalert.com",
                    applyUrl = "https://freejobalert.com/apply",
                    pdfUrl = "https://freejobalert.com/notice.pdf",
                  ),
                ),
              examUpdates =
                listOf(
                  ExamUpdateDto(
                    id = "upd_verified_1",
                    organization = "UPSC",
                    examTitle = "Verified Exam Calendar",
                    type = ExamUpdateType.EXAM_DATE.name,
                    releasedDateDisplay = "24 NOV 2026",
                    examDateDisplay = "15 FEB 2027",
                    summaryNote = "Official UPSC calendar update",
                    portalLoginUrl = "https://upsc.gov.in/examinations",
                    noticePdfUrl = "https://upsc.gov.in/calendar.pdf",
                    officialDomain = "upsc.gov.in",
                    lastVerifiedTimestamp = "2026-11-24T09:00:00Z",
                  ),
                  ExamUpdateDto(
                    id = "upd_blocked_2",
                    organization = "RRB",
                    examTitle = "Unverified Notice",
                    type = ExamUpdateType.CITY_SLIP.name,
                    releasedDateDisplay = "24 NOV 2026",
                    summaryNote = "Blocked aggregator",
                    portalLoginUrl = "https://sarkariresult.com/rrb",
                    officialDomain = "sarkariresult.com",
                    lastVerifiedTimestamp = "2026-11-24T09:00:00Z",
                  ),
                ),
            )
          }
        }

      val repository =
        LiveVerifiedJobRepository(
          savedJobDao = database.savedJobDao(),
          appliedJobDao = database.appliedJobDao(),
          cachedFeedDao = database.cachedFeedDao(),
          apiService = fakeService,
          defaultFeedEndpointUrl = "",
        )

      // Zero fabricated dummy data on fresh install
      assertTrue(repository.observeAllJobs().first().isEmpty())
      assertTrue(repository.observeExamUpdates().first().isEmpty())
      assertEquals(FeedSyncResult.EndpointNotConfigured, repository.syncVerifiedFeed())

      // Sync with configured verified endpoint caches only verified official records
      val result = repository.syncVerifiedFeed("https://api.example.gov.in/verified-feed.json")
      assertTrue(result is FeedSyncResult.Success)
      val success = result as FeedSyncResult.Success
      assertEquals(1, success.verifiedJobsCount)
      assertEquals(1, success.verifiedUpdatesCount)
      assertEquals(2, success.blockedRecordsCount)
      assertTrue(success.syncedAtMillis > 0L)

      val cachedJobs = repository.observeAllJobs().first()
      assertEquals(listOf("verified_ssc_1"), cachedJobs.map { it.id })
      val cachedUpdates = repository.observeExamUpdates().first()
      assertEquals(listOf("upd_verified_1"), cachedUpdates.map { it.id })
      val syncMeta = repository.observeSyncMetadata().first()
      assertEquals("VERIFIED_SYNC_OK", syncMeta.syncStatus)
      assertTrue(syncMeta.lastSyncedAtMillis > 0L)
    }

  @Test
  fun `hierarchical eligibility unlocks lower general tiers and enforces trade-specific and age rules`() {
    val graduateJob = buildDomainJob(id = "grad", minQual = QualificationLevel.GRADUATE, maxAge = 34)
    val matricJob = buildDomainJob(id = "mts", minQual = QualificationLevel.TENTH, maxAge = 27)
    val itiTechJob =
      buildDomainJob(
        id = "iti",
        minQual = null,
        exactLevels = setOf(QualificationLevel.ITI),
        maxAge = 30,
      )
    val pgJob = buildDomainJob(id = "pg", minQual = QualificationLevel.POSTGRADUATE, maxAge = 30)

    val btechEce =
      CandidateProfile(
        qualification = QualificationLevel.BTECH,
        stream = AcademicStream.ECE,
        age = 23,
        homeStateCode = "KL",
        hasCompletedOnboarding = true,
      )
    assertTrue(EligibilityEngine.isEligible(graduateJob, btechEce))
    assertTrue(EligibilityEngine.isEligible(matricJob, btechEce))
    assertFalse(EligibilityEngine.isEligible(itiTechJob, btechEce))
    assertFalse(EligibilityEngine.isEligible(pgJob, btechEce))

    val itiCandidate =
      btechEce.copy(qualification = QualificationLevel.ITI, stream = AcademicStream.ALL_GENERAL)
    assertTrue(EligibilityEngine.isEligible(itiTechJob, itiCandidate))
    assertTrue(EligibilityEngine.isEligible(matricJob, itiCandidate))
    assertFalse(EligibilityEngine.isEligible(graduateJob, itiCandidate))

    val age34Candidate = btechEce.copy(age = 34)
    assertTrue(EligibilityEngine.isEligible(graduateJob, age34Candidate))
    assertFalse(EligibilityEngine.isEligible(matricJob, age34Candidate))
  }

  @Test
  fun `bookmarking and applied lifecycle status persist in Room for verified cached jobs`() =
    runTest {
      val fakeService =
        object : OfficialFeedApiService {
          override suspend fun fetchVerifiedFeed(feedUrl: String): OfficialFeedResponseDto {
            return OfficialFeedResponseDto(
              jobs =
                listOf(
                  buildVerifiedDto(id = "ssc_nov", endDate = "2026-11-30"),
                  buildVerifiedDto(id = "ssc_dec", endDate = "2026-12-20"),
                ),
            )
          }
        }
      val repository =
        LiveVerifiedJobRepository(
          savedJobDao = database.savedJobDao(),
          appliedJobDao = database.appliedJobDao(),
          cachedFeedDao = database.cachedFeedDao(),
          apiService = fakeService,
        )
      repository.syncVerifiedFeed("https://api.example.gov.in/feed.json")

      val profileFlow = flowOf(CandidateProfile())
      repository.toggleBookmark("ssc_dec")
      repository.toggleBookmark("ssc_nov")

      val saved = repository.observeSavedJobs(profileFlow).first()
      assertEquals(2, saved.size)
      assertEquals("ssc_nov", saved[0].job.id)
      assertEquals("ssc_dec", saved[1].job.id)
      assertTrue(saved[0].reminderEnabled)

      repository.setDeadlineReminder("ssc_nov", false)
      val updated = repository.observeSavedJobs(profileFlow).first()
      assertFalse(updated.first { it.job.id == "ssc_nov" }.reminderEnabled)

      repository.markJobApplied(
        jobId = "ssc_nov",
        registrationNumber = "SSC-26-77102",
        appliedDateDisplay = DateWindowUtils.formatMillisToDisplay(nov24Millis),
      )
      repository.updateAppliedStage("ssc_nov", ApplicationLifecycleStage.ANSWER_KEY)
      repository.updateRegistrationNumber("ssc_nov", "SSC-26-99900")
      repository.updatePersonalStatus("ssc_nov", PersonalApplicationStatus.COMPLETED)

      val applied = repository.observeAppliedJobs().first().first { it.job.id == "ssc_nov" }
      assertEquals("SSC-26-99900", applied.registrationNumber)
      assertEquals(ApplicationLifecycleStage.ANSWER_KEY, applied.stage)
      assertEquals(PersonalApplicationStatus.COMPLETED, applied.personalStatus)
    }

  @Test
  fun `recruitment hierarchy routes central orgs, private domains, and status tags accurately`() {
    val rrbNtpc =
      JobAlert(
        id = "rrb_ntpc",
        organization = "Railway Recruitment Board",
        postTitle = "RRB NTPC Graduate Goods Train Manager",
        sector = JobSector.CENTRAL_GOVT,
        centralAgency = CentralAgencyFilter.RRB,
        locationLabel = "All India",
        vacancyCountLabel = "Verified",
        qualificationTag = "Graduate",
        applicationStartDate = "2026-11-01",
        applicationEndDate = "2026-12-10",
        officialPdfUrl = "https://indianrailways.gov.in/ntpc.pdf",
        directApplyUrl = "https://rrbapply.gov.in",
        aiBrief = AiBrief("Graduate", "18-36", "CBT"),
        officialDomain = "rrbapply.gov.in",
        lastVerifiedTimestamp = "2026-11-24T09:00:00Z",
        orgTag = "RRB",
        subCategoryTag = "NTPC_GRAD",
      )
    assertTrue(RecruitmentHierarchy.matchesOrgSubCategory(rrbNtpc, "RRB", "NTPC_GRAD"))
    assertFalse(RecruitmentHierarchy.matchesOrgSubCategory(rrbNtpc, "RRB", "TECHNICIAN"))
    assertEquals(JobStatusTag.APPLIED, rrbNtpc.resolveStatusTag(isApplied = true, nowMillis = nov24Millis))
    assertEquals(8, RecruitmentHierarchy.privateDomains.size)
    assertEquals(16, RecruitmentHierarchy.privateCompanies.size)
  }

  @Test
  fun `feed sync evaluator filters new active openings by seen ids and candidate state qualification stream age`() {
    val profile =
      CandidateProfile(
        qualification = QualificationLevel.BTECH,
        stream = AcademicStream.ECE,
        age = 24,
        homeStateCode = "KL",
        hasCompletedOnboarding = true,
      )
    val centralMatch = buildDomainJob(id = "central_new", startDate = "2026-11-10", endDate = "2026-12-10")
    val centralAlreadySeen = buildDomainJob(id = "central_seen", startDate = "2026-11-10", endDate = "2026-12-10")
    val keralaStateMatch =
      buildDomainJob(id = "kl_state_new", startDate = "2026-11-10", endDate = "2026-12-10").copy(
        sector = JobSector.STATE_GOVT,
        centralAgency = null,
        stateCode = "KL",
        officialDomain = "keralapsc.gov.in",
        directApplyUrl = "https://keralapsc.gov.in/apply",
        officialPdfUrl = "https://keralapsc.gov.in/notice.pdf",
      )
    val otherStateMismatch =
      keralaStateMatch.copy(
        id = "mh_state_other",
        stateCode = "MH",
        officialDomain = "mpsc.gov.in",
        directApplyUrl = "https://mpsc.gov.in/apply",
        officialPdfUrl = "https://mpsc.gov.in/notice.pdf",
      )
    val upcomingNotOpen = buildDomainJob(id = "upcoming_not_open", startDate = "2026-12-01", endDate = "2026-12-30")

    val matches =
      FeedSyncNotificationEvaluator.computeNewEligibleMatches(
        activeJobs =
          listOf(centralMatch, centralAlreadySeen, keralaStateMatch, otherStateMismatch, upcomingNotOpen),
        seenJobIds = setOf("central_seen"),
        profile = profile,
        todayIso = nov24Iso,
      )

    assertEquals(listOf("central_new", "kl_state_new"), matches.map { it.id })
  }

  @Test
  fun `deadline reminder delays and WorkManager schedule or cancel on bookmark vs applied`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    WorkManagerTestInitHelper.initializeTestWorkManager(context)
    val workManager = WorkManager.getInstance(context)

    val job = buildDomainJob(id = "ssc_deadline_test", startDate = "2026-11-01", endDate = "2026-11-30")
    val delays = NextPostWorkScheduler.computeReminderDelaysMillis(job.applicationEndDate, nov24Millis)
    assertEquals(listOf(48, 6), delays.map { it.first })
    assertTrue(delays.all { it.second > TimeUnit.DAYS.toMillis(3) })

    // Bookmarking schedules both 48h and 6h reminders
    NextPostWorkScheduler.syncJobReminderState(
      context = context,
      job = job,
      isSaved = true,
      reminderEnabled = true,
      personalStatus = null,
      deadlineRemindersEnabled = true,
      nowMillis = nov24Millis,
    )
    val scheduledWork = workManager.getWorkInfosByTag("deadline_job_${job.id}").get()
    assertEquals(2, scheduledWork.count { it.state == WorkInfo.State.ENQUEUED })

    // Marking as Applied cancels scheduled reminders
    NextPostWorkScheduler.syncJobReminderState(
      context = context,
      job = job,
      isSaved = true,
      reminderEnabled = true,
      personalStatus = PersonalApplicationStatus.APPLIED,
      deadlineRemindersEnabled = true,
      nowMillis = nov24Millis,
    )
    val afterAppliedWork = workManager.getWorkInfosByTag("deadline_job_${job.id}").get()
    assertTrue(afterAppliedWork.all { it.state == WorkInfo.State.CANCELLED })

    // Application In Progress reschedules reminders even if unbookmarked
    NextPostWorkScheduler.syncJobReminderState(
      context = context,
      job = job,
      isSaved = false,
      reminderEnabled = false,
      personalStatus = PersonalApplicationStatus.IN_PROGRESS,
      deadlineRemindersEnabled = true,
      nowMillis = nov24Millis,
    )
    val inProgressWork = workManager.getWorkInfosByTag("deadline_job_${job.id}").get()
    assertEquals(2, inProgressWork.count { it.state == WorkInfo.State.ENQUEUED })
  }

  @Test
  fun `share summary formatter and sync age labels format accurately`() {
    val job =
      buildDomainJob(
        id = "share_1",
        directApplyUrl = "https://ssc.gov.in/apply",
      ).copy(
        organization = "SSC",
        postTitle = "CGL Tier-I 2026",
        qualificationTag = "Any Graduate",
        applicationEndDate = "2026-12-20",
      )

    val expectedShare =
      "SSC — CGL Tier-I 2026\n" +
        "Qualification: Any Graduate | Last Date: 20 DEC 2026\n" +
        "Official Portal: https://ssc.gov.in/apply\n" +
        "Shared via NextPost"
    assertEquals(expectedShare, JobShareFormatter.format(job))

    assertEquals("JUST NOW", DateWindowUtils.relativeSyncAgeLabel(nov24Millis - 20_000L, nov24Millis))
    assertEquals("15M AGO", DateWindowUtils.relativeSyncAgeLabel(nov24Millis - TimeUnit.MINUTES.toMillis(15), nov24Millis))
    assertEquals("2H AGO", DateWindowUtils.relativeSyncAgeLabel(nov24Millis - TimeUnit.HOURS.toMillis(2), nov24Millis))
  }

  @Test
  fun `candidate profile store persists notification preferences and seen job ids`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val store = CandidateProfileStore(context)

    val defaults = store.notificationPrefsFlow.first()
    assertTrue(defaults.newEligibleJobsEnabled)
    assertTrue(defaults.deadlineRemindersEnabled)
    assertTrue(defaults.examUpdatesEnabled)

    store.saveNotificationPreferences(
      NotificationPreferences(
        newEligibleJobsEnabled = true,
        deadlineRemindersEnabled = false,
        examUpdatesEnabled = false,
      )
    )
    store.markJobsSeen(listOf("job_a", "job_b"))

    val updated = store.notificationPrefsFlow.first()
    assertTrue(updated.newEligibleJobsEnabled)
    assertFalse(updated.deadlineRemindersEnabled)
    assertFalse(updated.examUpdatesEnabled)
    assertEquals(setOf("job_a", "job_b"), store.seenActiveJobIdsFlow.first())
  }
}
