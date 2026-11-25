package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AlertPulseDatabase
import com.example.data.model.AcademicStream
import com.example.data.model.ApplicationLifecycleStage
import com.example.data.model.CandidateProfile
import com.example.data.model.EligibilityEngine
import com.example.data.model.IndianStatesCatalog
import com.example.data.model.JobStatusTag
import com.example.data.model.PersonalApplicationStatus
import com.example.data.model.QualificationLevel
import com.example.data.model.RecruitmentHierarchy
import com.example.data.model.RegionType
import com.example.data.repository.MockGovtCatalog
import com.example.data.repository.MockJobRepository
import com.example.data.repository.MockPrivateAndExamCatalog
import java.util.Calendar
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
  private lateinit var repository: MockJobRepository

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
    repository = MockJobRepository(database.savedJobDao(), database.appliedJobDao())
  }

  @After
  fun tearDown() {
    database.close()
  }

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
  fun `date window gate filters out closed and upcoming jobs and leaves zero open states`() {
    val centralById = MockGovtCatalog.centralGovtJobs.associateBy { it.id }
    assertTrue(centralById.getValue("cen_rrb_ntpc_01").isApplicationOpenNow(nov24Millis))
    assertFalse(centralById.getValue("cen_ssc_cgl_03").isApplicationOpenNow(nov24Millis))
    assertFalse(centralById.getValue("cen_upsc_ese_05").isApplicationOpenNow(nov24Millis))

    val privateById = MockPrivateAndExamCatalog.privateMncJobs.associateBy { it.id }
    assertTrue(privateById.getValue("prv_tcs_nqt_01").isApplicationOpenNow(nov24Millis))
    assertFalse(privateById.getValue("prv_gs_05").isApplicationOpenNow(nov24Millis))

    val emptyStateCodes = listOf("GA", "AS", "HP")
    emptyStateCodes.forEach { code ->
      val openInState =
        MockGovtCatalog.stateGovtJobs.count {
          it.stateCode == code && it.isApplicationOpenNow(nov24Millis)
        }
      assertEquals(0, openInState)
    }
  }

  @Test
  fun `hierarchical eligibility unlocks lower general tiers and enforces trade-specific and age rules`() {
    val jobsById = MockGovtCatalog.centralGovtJobs.associateBy { it.id }

    val btechEce =
      CandidateProfile(
        qualification = QualificationLevel.BTECH,
        stream = AcademicStream.ECE,
        age = 23,
        homeStateCode = "KL",
        hasCompletedOnboarding = true,
      )
    assertTrue(EligibilityEngine.isEligible(jobsById.getValue("cen_rrb_ntpc_01"), btechEce))
    assertTrue(EligibilityEngine.isEligible(jobsById.getValue("cen_ssc_chsl_12"), btechEce))
    assertTrue(EligibilityEngine.isEligible(jobsById.getValue("cen_ssc_mts_10"), btechEce))
    assertTrue(EligibilityEngine.isEligible(jobsById.getValue("cen_drdo_08"), btechEce))
    assertFalse(EligibilityEngine.isEligible(jobsById.getValue("cen_rrb_tech_iti"), btechEce))
    assertFalse(EligibilityEngine.isEligible(jobsById.getValue("cen_isro_diploma"), btechEce))
    assertFalse(EligibilityEngine.isEligible(jobsById.getValue("cen_sebi_pg"), btechEce))

    val itiCandidate =
      btechEce.copy(qualification = QualificationLevel.ITI, stream = AcademicStream.ALL_GENERAL)
    assertTrue(EligibilityEngine.isEligible(jobsById.getValue("cen_rrb_tech_iti"), itiCandidate))
    assertTrue(EligibilityEngine.isEligible(jobsById.getValue("cen_ssc_mts_10"), itiCandidate))
    assertFalse(EligibilityEngine.isEligible(jobsById.getValue("cen_rrb_ntpc_01"), itiCandidate))

    val age34Candidate = btechEce.copy(age = 34)
    assertTrue(EligibilityEngine.isEligible(jobsById.getValue("cen_rrb_ntpc_01"), age34Candidate))
    assertFalse(EligibilityEngine.isEligible(jobsById.getValue("cen_mha_ib_10"), age34Candidate))
    assertFalse(EligibilityEngine.isEligible(jobsById.getValue("cen_ssc_cgl_03"), age34Candidate))
  }

  @Test
  fun `bookmarking saves job sorted by nearest closing date and updates reminder`() = runTest {
    val profileFlow = flowOf(CandidateProfile())
    repository.toggleBookmark("cen_rrb_je_02") // 20 DEC 2026
    repository.toggleBookmark("cen_ssc_cgl_03") // 15 NOV 2026

    val saved = repository.observeSavedJobs(profileFlow).first()
    assertEquals(2, saved.size)
    assertEquals("cen_ssc_cgl_03", saved[0].job.id)
    assertEquals("cen_rrb_je_02", saved[1].job.id)
    assertTrue(saved[0].reminderEnabled)

    repository.setDeadlineReminder("cen_ssc_cgl_03", false)
    val updated = repository.observeSavedJobs(profileFlow).first()
    assertEquals(false, updated.first { it.job.id == "cen_ssc_cgl_03" }.reminderEnabled)
  }

  @Test
  fun `applied status seeding and manual mark applied with stage progression persist in Room`() =
    runTest {
      repository.seedSampleAppliedJobsIfNeeded()
      val seeded = repository.observeAppliedJobs().first()
      assertEquals(3, seeded.size)
      assertTrue(seeded.any { it.stage == ApplicationLifecycleStage.CITY_SLIP })
      assertTrue(seeded.any { it.stage == ApplicationLifecycleStage.ADMIT_CARD })
      assertTrue(seeded.any { it.stage == ApplicationLifecycleStage.APPLIED })

      repository.markJobApplied(
        jobId = "cen_drdo_08",
        registrationNumber = "DRDO-26-77102",
        appliedDateDisplay = "24 NOV 2026",
      )
      repository.updateAppliedStage("cen_drdo_08", ApplicationLifecycleStage.ANSWER_KEY)
      repository.updateRegistrationNumber("cen_drdo_08", "DRDO-26-99900")

      val updated = repository.observeAppliedJobs().first()
      val drdo = updated.first { it.job.id == "cen_drdo_08" }
      assertEquals("DRDO-26-99900", drdo.registrationNumber)
      assertEquals(ApplicationLifecycleStage.ANSWER_KEY, drdo.stage)

      repository.updatePersonalStatus("cen_drdo_08", PersonalApplicationStatus.COMPLETED)
      val afterStatus = repository.observeAppliedJobs().first().first { it.job.id == "cen_drdo_08" }
      assertEquals(PersonalApplicationStatus.COMPLETED, afterStatus.personalStatus)
    }

  @Test
  fun `recruitment hierarchy routes central orgs, private domains, and status tags accurately`() {
    val centralById = MockGovtCatalog.centralGovtJobs.associateBy { it.id }
    val rrbNtpc = centralById.getValue("cen_rrb_ntpc_01")
    assertTrue(RecruitmentHierarchy.matchesOrgSubCategory(rrbNtpc, "RRB", "NTPC_GRAD"))
    assertFalse(RecruitmentHierarchy.matchesOrgSubCategory(rrbNtpc, "RRB", "TECHNICIAN"))
    assertEquals(JobStatusTag.APPLIED, rrbNtpc.resolveStatusTag(isApplied = true, nowMillis = nov24Millis))
    assertEquals(JobStatusTag.EXPIRED, centralById.getValue("cen_ssc_cgl_03").resolveStatusTag(isApplied = false, nowMillis = nov24Millis))
    assertEquals(8, RecruitmentHierarchy.privateDomains.size)
    assertEquals(16, RecruitmentHierarchy.privateCompanies.size)
  }
}
