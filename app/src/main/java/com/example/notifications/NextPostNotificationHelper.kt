package com.example.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.ExamUpdate
import com.example.data.model.JobAlert

object NextPostNotificationHelper {
  const val EXTRA_JOB_ID = "extra_job_id"
  const val EXTRA_UPDATE_ID = "extra_update_id"

  const val CHANNEL_ELIGIBLE_JOBS = "nextpost_eligible_jobs"
  const val CHANNEL_DEADLINE_REMINDERS = "nextpost_deadline_reminders"
  const val CHANNEL_EXAM_UPDATES = "nextpost_exam_updates"

  private const val NOTIFICATION_ID_ELIGIBLE = 4101
  private const val NOTIFICATION_ID_UPDATES = 4201

  fun ensureChannels(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
      ?: return

    val channels =
      listOf(
        NotificationChannel(
            CHANNEL_ELIGIBLE_JOBS,
            "New Eligible Job Alerts",
            NotificationManager.IMPORTANCE_HIGH,
          )
          .apply {
            description =
              "High-priority alerts when verified openings match your qualification, stream, age, and home state."
          },
        NotificationChannel(
            CHANNEL_DEADLINE_REMINDERS,
            "Closing Deadline Reminders (48h & 6h)",
            NotificationManager.IMPORTANCE_HIGH,
          )
          .apply {
            description =
              "Automated 48-hour and 6-hour warnings before bookmarked or in-progress applications close."
          },
        NotificationChannel(
            CHANNEL_EXAM_UPDATES,
            "Exam Updates (Admit Cards & Results)",
            NotificationManager.IMPORTANCE_DEFAULT,
          )
          .apply {
            description =
              "Verified admit card, city intimation slip, answer key, and result releases."
          },
      )
    manager.createNotificationChannels(channels)
  }

  fun canPostNotifications(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      val granted =
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
          PackageManager.PERMISSION_GRANTED
      if (!granted) return false
    }
    return NotificationManagerCompat.from(context).areNotificationsEnabled()
  }

  fun buildJobDetailIntent(context: Context, jobId: String): Intent =
    Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
      putExtra(EXTRA_JOB_ID, jobId)
    }

  private fun buildPendingIntentForJob(
    context: Context,
    jobId: String,
    requestCode: Int,
  ): PendingIntent {
    val intent = buildJobDetailIntent(context, jobId)
    return PendingIntent.getActivity(
      context,
      requestCode,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
  }

  private fun buildPendingIntentForUpdate(
    context: Context,
    updateId: String,
    requestCode: Int,
  ): PendingIntent {
    val intent =
      Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra(EXTRA_UPDATE_ID, updateId)
      }
    return PendingIntent.getActivity(
      context,
      requestCode,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
  }

  fun showEligibleJobsNotification(context: Context, eligibleJobs: List<JobAlert>) {
    if (eligibleJobs.isEmpty()) return
    ensureChannels(context)
    if (!canPostNotifications(context)) return

    val primary = eligibleJobs.first()
    val pendingIntent =
      buildPendingIntentForJob(
        context = context,
        jobId = primary.id,
        requestCode = primary.id.hashCode(),
      )

    val title =
      if (eligibleJobs.size == 1) {
        "${primary.organization}: ${primary.postTitle}"
      } else {
        "${eligibleJobs.size} New Eligible Openings Found"
      }

    val summaryText =
      if (eligibleJobs.size == 1) {
        "Last Date: ${primary.lastDateDisplay} • Tap to view official details"
      } else {
        "${primary.organization} — ${primary.postTitle} (Last Date: ${primary.lastDateDisplay}) +${eligibleJobs.size - 1} more"
      }

    val builder =
      NotificationCompat.Builder(context, CHANNEL_ELIGIBLE_JOBS)
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setContentTitle(title)
        .setContentText(summaryText)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)

    if (eligibleJobs.size > 1) {
      val inbox = NotificationCompat.InboxStyle().setBigContentTitle(title)
      eligibleJobs.take(5).forEach { job ->
        inbox.addLine("${job.organization} — ${job.postTitle} (${job.lastDateDisplay})")
      }
      builder.setStyle(inbox)
    } else {
      builder.setStyle(NotificationCompat.BigTextStyle().bigText(summaryText))
    }

    try {
      NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_ELIGIBLE, builder.build())
    } catch (_: SecurityException) {
      // Permission revoked between check and notify
    }
  }

  fun showDeadlineReminderNotification(
    context: Context,
    jobId: String,
    organization: String,
    postTitle: String,
    lastDateDisplay: String,
    hoursBefore: Int,
  ) {
    ensureChannels(context)
    if (!canPostNotifications(context)) return

    val pendingIntent =
      buildPendingIntentForJob(
        context = context,
        jobId = jobId,
        requestCode = ("deadline_${hoursBefore}_$jobId").hashCode(),
      )

    val title = "Closing in ${hoursBefore}h: $organization"
    val body = "$postTitle closes on $lastDateDisplay (11:59 PM). Tap to apply or update status."

    val notification =
      NotificationCompat.Builder(context, CHANNEL_DEADLINE_REMINDERS)
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setContentTitle(title)
        .setContentText(body)
        .setStyle(NotificationCompat.BigTextStyle().bigText(body))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_REMINDER)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()

    try {
      NotificationManagerCompat.from(context)
        .notify(("deadline_${hoursBefore}_$jobId").hashCode(), notification)
    } catch (_: SecurityException) {
      // Permission revoked between check and notify
    }
  }

  fun showExamUpdatesNotification(context: Context, updates: List<ExamUpdate>) {
    if (updates.isEmpty()) return
    ensureChannels(context)
    if (!canPostNotifications(context)) return

    val primary = updates.first()
    val pendingIntent =
      buildPendingIntentForUpdate(
        context = context,
        updateId = primary.id,
        requestCode = primary.id.hashCode(),
      )

    val title =
      if (updates.size == 1) {
        "${primary.organization} • ${primary.type.badgeLabel}"
      } else {
        "${updates.size} New Verified Exam Updates"
      }
    val body =
      if (updates.size == 1) {
        "${primary.examTitle} (${primary.statusLabel})"
      } else {
        "${primary.organization}: ${primary.examTitle} +${updates.size - 1} more"
      }

    val notification =
      NotificationCompat.Builder(context, CHANNEL_EXAM_UPDATES)
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setContentTitle(title)
        .setContentText(body)
        .setStyle(NotificationCompat.BigTextStyle().bigText(body))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()

    try {
      NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_UPDATES, notification)
    } catch (_: SecurityException) {
      // Permission revoked between check and notify
    }
  }
}
