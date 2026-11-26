package com.example.notifications

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.model.DateWindowUtils
import com.example.data.model.JobAlert
import com.example.data.model.PersonalApplicationStatus
import java.util.concurrent.TimeUnit

object NextPostWorkScheduler {
  const val PERIODIC_SYNC_WORK_NAME = "nextpost_periodic_feed_sync"
  const val IMMEDIATE_SYNC_WORK_NAME = "nextpost_immediate_feed_sync"

  private val reminderOffsetsHours = listOf(48, 6)

  fun schedulePeriodicFeedSync(context: Context) {
    val constraints =
      Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

    val request =
      PeriodicWorkRequestBuilder<FeedSyncWorker>(6, TimeUnit.HOURS)
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context)
      .enqueueUniquePeriodicWork(
        PERIODIC_SYNC_WORK_NAME,
        ExistingPeriodicWorkPolicy.KEEP,
        request,
      )
  }

  fun triggerImmediateSync(context: Context) {
    val constraints =
      Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

    val request =
      OneTimeWorkRequestBuilder<FeedSyncWorker>().setConstraints(constraints).build()

    WorkManager.getInstance(context)
      .enqueueUniqueWork(
        IMMEDIATE_SYNC_WORK_NAME,
        ExistingWorkPolicy.REPLACE,
        request,
      )
  }

  fun computeReminderDelaysMillis(
    applicationEndDateIso: String,
    nowMillis: Long = System.currentTimeMillis(),
  ): List<Pair<Int, Long>> {
    if (applicationEndDateIso == "2099-12-31") return emptyList()
    val deadlineMillis = DateWindowUtils.deadlineEndOfDayMillis(applicationEndDateIso)
      ?: return emptyList()
    return reminderOffsetsHours.mapNotNull { hours ->
      val triggerAtMillis = deadlineMillis - TimeUnit.HOURS.toMillis(hours.toLong())
      val delayMillis = triggerAtMillis - nowMillis
      if (delayMillis > 0L) hours to delayMillis else null
    }
  }

  fun uniqueReminderWorkName(jobId: String, hoursBefore: Int): String =
    "deadline_${hoursBefore}h_$jobId"

  fun scheduleDeadlineReminders(
    context: Context,
    job: JobAlert,
    nowMillis: Long = System.currentTimeMillis(),
  ) {
    val delays = computeReminderDelaysMillis(job.applicationEndDate, nowMillis)
    if (delays.isEmpty()) {
      cancelDeadlineReminders(context, job.id)
      return
    }

    val workManager = WorkManager.getInstance(context)
    val scheduledHours = delays.map { it.first }.toSet()

    // Cancel any window that has already passed
    reminderOffsetsHours.filter { it !in scheduledHours }.forEach { passedHours ->
      workManager.cancelUniqueWork(uniqueReminderWorkName(job.id, passedHours))
    }

    delays.forEach { (hoursBefore, delayMillis) ->
      val inputData =
        Data.Builder()
          .putString(DeadlineReminderWorker.KEY_JOB_ID, job.id)
          .putString(DeadlineReminderWorker.KEY_ORG, job.organization)
          .putString(DeadlineReminderWorker.KEY_TITLE, job.postTitle)
          .putString(DeadlineReminderWorker.KEY_LAST_DATE, job.lastDateDisplay)
          .putString(DeadlineReminderWorker.KEY_END_DATE_ISO, job.applicationEndDate)
          .putInt(DeadlineReminderWorker.KEY_HOURS_BEFORE, hoursBefore)
          .build()

      val request =
        OneTimeWorkRequestBuilder<DeadlineReminderWorker>()
          .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
          .setInputData(inputData)
          .addTag("deadline_job_${job.id}")
          .build()

      workManager.enqueueUniqueWork(
        uniqueReminderWorkName(job.id, hoursBefore),
        ExistingWorkPolicy.REPLACE,
        request,
      )
    }
  }

  fun cancelDeadlineReminders(context: Context, jobId: String) {
    val workManager = WorkManager.getInstance(context)
    reminderOffsetsHours.forEach { hours ->
      workManager.cancelUniqueWork(uniqueReminderWorkName(jobId, hours))
    }
    workManager.cancelAllWorkByTag("deadline_job_$jobId")
  }

  fun syncJobReminderState(
    context: Context,
    job: JobAlert,
    isSaved: Boolean,
    reminderEnabled: Boolean,
    personalStatus: PersonalApplicationStatus?,
    deadlineRemindersEnabled: Boolean,
    nowMillis: Long = System.currentTimeMillis(),
  ) {
    val isFinalized =
      personalStatus == PersonalApplicationStatus.APPLIED ||
        personalStatus == PersonalApplicationStatus.COMPLETED
    val shouldSchedule =
      deadlineRemindersEnabled &&
        !isFinalized &&
        ((isSaved && reminderEnabled) || personalStatus == PersonalApplicationStatus.IN_PROGRESS)

    if (shouldSchedule) {
      scheduleDeadlineReminders(context, job, nowMillis)
    } else {
      cancelDeadlineReminders(context, job.id)
    }
  }
}
