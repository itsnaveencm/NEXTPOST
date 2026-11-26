package com.example.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.local.AlertPulseDatabase
import com.example.data.local.CandidateProfileStore
import com.example.data.model.DateWindowUtils
import com.example.data.model.PersonalApplicationStatus
import kotlinx.coroutines.flow.first

class DeadlineReminderWorker(
  appContext: Context,
  workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

  override suspend fun doWork(): Result {
    val jobId = inputData.getString(KEY_JOB_ID) ?: return Result.success()
    val organization = inputData.getString(KEY_ORG).orEmpty()
    val postTitle = inputData.getString(KEY_TITLE).orEmpty()
    val lastDateDisplay = inputData.getString(KEY_LAST_DATE).orEmpty()
    val endDateIso = inputData.getString(KEY_END_DATE_ISO).orEmpty()
    val hoursBefore = inputData.getInt(KEY_HOURS_BEFORE, 48)

    val context = applicationContext
    val profileStore = CandidateProfileStore(context)
    val notifPrefs = profileStore.notificationPrefsFlow.first()
    if (!notifPrefs.deadlineRemindersEnabled) return Result.success()

    if (endDateIso.isNotBlank()) {
      val today = DateWindowUtils.parseIsoEpochDay(DateWindowUtils.currentLocalDateIso())
      val endDay = runCatching { DateWindowUtils.parseIsoEpochDay(endDateIso) }.getOrNull()
      if (endDay != null && today > endDay) {
        return Result.success()
      }
    }

    val database = AlertPulseDatabase.getInstance(context)
    val saved = database.savedJobDao().getSavedJob(jobId)
    val applied = database.appliedJobDao().getAppliedJob(jobId)

    val appliedStatus =
      applied?.personalStatusName?.let { name ->
        PersonalApplicationStatus.entries.firstOrNull { it.name == name }
      }
    val isFinalized =
      appliedStatus == PersonalApplicationStatus.APPLIED ||
        appliedStatus == PersonalApplicationStatus.COMPLETED
    if (isFinalized) {
      return Result.success()
    }

    val shouldNotify =
      (saved != null && saved.reminderEnabled) ||
        appliedStatus == PersonalApplicationStatus.IN_PROGRESS
    if (!shouldNotify) {
      return Result.success()
    }

    NextPostNotificationHelper.showDeadlineReminderNotification(
      context = context,
      jobId = jobId,
      organization = organization,
      postTitle = postTitle,
      lastDateDisplay = lastDateDisplay,
      hoursBefore = hoursBefore,
    )
    return Result.success()
  }

  companion object {
    const val KEY_JOB_ID = "job_id"
    const val KEY_ORG = "org"
    const val KEY_TITLE = "title"
    const val KEY_LAST_DATE = "last_date"
    const val KEY_END_DATE_ISO = "end_date_iso"
    const val KEY_HOURS_BEFORE = "hours_before"
  }
}
