package com.example.notifications

import android.content.Context
import com.example.data.local.CandidateProfileStore
import com.example.data.model.CandidateProfile
import com.example.data.model.DateWindowUtils
import com.example.data.model.EligibilityEngine
import com.example.data.model.ExamUpdate
import com.example.data.model.JobAlert
import com.example.data.model.NotificationPreferences
import com.example.data.model.OfficialSourceVerifier
import kotlinx.coroutines.flow.first

data class SyncDiffResult(
  val eligibleNewJobs: List<JobAlert>,
  val newExamUpdates: List<ExamUpdate>,
)

object FeedSyncNotificationEvaluator {

  fun computeNewEligibleMatches(
    activeJobs: List<JobAlert>,
    seenJobIds: Set<String>,
    profile: CandidateProfile,
    todayIso: String = DateWindowUtils.currentLocalDateIso(),
  ): List<JobAlert> {
    return activeJobs.filter { job ->
      job.id !in seenJobIds &&
        OfficialSourceVerifier.verifyJob(job).isVerified &&
        job.isApplicationOpenNow(todayIso) &&
        EligibilityEngine.isEligibleForNotification(job, profile)
    }
  }

  fun computeNewExamUpdates(
    updates: List<ExamUpdate>,
    seenUpdateIds: Set<String>,
  ): List<ExamUpdate> {
    return updates.filter { update ->
      update.id !in seenUpdateIds && OfficialSourceVerifier.verifyExamUpdate(update).isVerified
    }
  }

  suspend fun evaluateAndNotify(
    context: Context,
    profileStore: CandidateProfileStore,
    allVerifiedJobs: List<JobAlert>,
    allVerifiedUpdates: List<ExamUpdate>,
    todayIso: String = DateWindowUtils.currentLocalDateIso(),
  ): SyncDiffResult {
    val profile = profileStore.profileFlow.first()
    val prefs: NotificationPreferences = profileStore.notificationPrefsFlow.first()
    val seenJobIds = profileStore.seenActiveJobIdsFlow.first()
    val seenUpdateIds = profileStore.seenExamUpdateIdsFlow.first()

    val activeOpenJobs =
      allVerifiedJobs.filter {
        OfficialSourceVerifier.verifyJob(it).isVerified && it.isApplicationOpenNow(todayIso)
      }
    val eligibleNewJobs =
      computeNewEligibleMatches(
        activeJobs = activeOpenJobs,
        seenJobIds = seenJobIds,
        profile = profile,
        todayIso = todayIso,
      )
    val newExamUpdates =
      computeNewExamUpdates(
        updates = allVerifiedUpdates,
        seenUpdateIds = seenUpdateIds,
      )

    if (prefs.newEligibleJobsEnabled && eligibleNewJobs.isNotEmpty()) {
      NextPostNotificationHelper.showEligibleJobsNotification(context, eligibleNewJobs)
    }
    if (prefs.examUpdatesEnabled && newExamUpdates.isNotEmpty()) {
      NextPostNotificationHelper.showExamUpdatesNotification(context, newExamUpdates)
    }

    profileStore.markJobsSeen(activeOpenJobs.map { it.id })
    profileStore.markExamUpdatesSeen(allVerifiedUpdates.map { it.id })

    return SyncDiffResult(
      eligibleNewJobs = eligibleNewJobs,
      newExamUpdates = newExamUpdates,
    )
  }
}
