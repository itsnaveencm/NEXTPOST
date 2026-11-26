package com.example.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.BuildConfig
import com.example.data.local.AlertPulseDatabase
import com.example.data.local.CandidateProfileStore
import com.example.data.model.PersonalApplicationStatus
import com.example.data.remote.OfficialFeedApiService
import com.example.data.repository.FeedSyncResult
import com.example.data.repository.LiveVerifiedJobRepository
import kotlinx.coroutines.flow.first

class FeedSyncWorker(
  appContext: Context,
  workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

  override suspend fun doWork(): Result {
    val context = applicationContext
    val database = AlertPulseDatabase.getInstance(context)
    val profileStore = CandidateProfileStore(context)
    val repository =
      LiveVerifiedJobRepository(
        savedJobDao = database.savedJobDao(),
        appliedJobDao = database.appliedJobDao(),
        cachedFeedDao = database.cachedFeedDao(),
        apiService = OfficialFeedApiService.create(),
        defaultFeedEndpointUrl = BuildConfig.NEXTPOST_FEED_API_URL,
      )

    val savedEndpoint = profileStore.feedEndpointUrlFlow.first()
    val syncResult = repository.syncVerifiedFeed(savedEndpoint.ifBlank { null })

    val allJobs = repository.observeAllJobs().first()
    val allUpdates = repository.observeExamUpdates().first()
    FeedSyncNotificationEvaluator.evaluateAndNotify(
      context = context,
      profileStore = profileStore,
      allVerifiedJobs = allJobs,
      allVerifiedUpdates = allUpdates,
    )

    // Keep 48h/6h deadline reminders aligned with latest verified dates
    val notifPrefs = profileStore.notificationPrefsFlow.first()
    val savedMap = database.savedJobDao().observeSavedJobs().first().associateBy { it.jobId }
    val appliedMap = database.appliedJobDao().observeAppliedJobs().first().associateBy { it.jobId }
    allJobs.forEach { job ->
      val saved = savedMap[job.id]
      val applied = appliedMap[job.id]
      val personalStatus =
        applied?.personalStatusName?.let { name ->
          PersonalApplicationStatus.entries.firstOrNull { it.name == name }
        }
      NextPostWorkScheduler.syncJobReminderState(
        context = context,
        job = job,
        isSaved = saved != null,
        reminderEnabled = saved?.reminderEnabled ?: true,
        personalStatus = personalStatus,
        deadlineRemindersEnabled = notifPrefs.deadlineRemindersEnabled,
      )
    }

    return when (syncResult) {
      is FeedSyncResult.Success,
      FeedSyncResult.EndpointNotConfigured -> Result.success()
      is FeedSyncResult.Error -> {
        // Offline / transient network errors fall back gracefully to local Room cache
        if (allJobs.isNotEmpty() || runAttemptCount >= 2) Result.success() else Result.retry()
      }
    }
  }
}
