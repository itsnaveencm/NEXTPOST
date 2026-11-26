package com.example.data.model

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class AiBrief(
  val eligibility: String,
  val ageLimit: String,
  val selectionAndFee: String,
)

enum class JobStatusTag(val label: String) {
  APPLIED("APPLIED"),
  CLOSING_SOON("CLOSING SOON"),
  ACTIVE("ACTIVE"),
  UPCOMING("UPCOMING"),
  EXPIRED("EXPIRED"),
}

object DateWindowUtils {
  private val monthNames =
    arrayOf("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")

  /**
   * Uses the device's real-time clock via [LocalDate.now] on Android 8.0+ (API 26+)
   * with a Calendar/SimpleDateFormat fallback on API 24-25.
   */
  fun currentLocalDateIso(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      LocalDate.now().toString()
    } else {
      SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }
  }

  fun millisToLocalDateIso(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return formatter.format(Date(millis))
  }

  fun parseIsoEpochDay(isoDate: String): Long {
    val parts = isoDate.trim().split("-")
    require(parts.size == 3) { "Invalid ISO date: $isoDate" }
    val year = parts[0].toInt()
    val month = parts[1].toInt()
    val day = parts[2].toInt()
    val cal =
      Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1)
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
      }
    return TimeUnit.MILLISECONDS.toDays(cal.timeInMillis)
  }

  fun formatIsoToDisplay(isoDate: String): String {
    val parts = isoDate.trim().split("-")
    if (parts.size != 3) return isoDate
    val year = parts[0]
    val monthIdx = (parts[1].toIntOrNull() ?: 1).coerceIn(1, 12) - 1
    val day = parts[2].padStart(2, '0')
    return "$day ${monthNames[monthIdx]} $year"
  }

  fun formatMillisToDisplay(nowMillis: Long = System.currentTimeMillis()): String {
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.US)
    return formatter.format(Date(nowMillis)).uppercase(Locale.US)
  }

  fun formatSyncTimestamp(syncMillis: Long): String {
    if (syncMillis <= 0L) return "Not synced yet"
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US)
    return formatter.format(Date(syncMillis))
  }

  fun relativeSyncAgeLabel(
    syncMillis: Long,
    nowMillis: Long = System.currentTimeMillis(),
  ): String {
    if (syncMillis <= 0L) return "NEVER"
    val deltaMillis = (nowMillis - syncMillis).coerceAtLeast(0L)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(deltaMillis)
    return when {
      minutes < 2 -> "JUST NOW"
      minutes < 60 -> "${minutes}M AGO"
      minutes < 24 * 60 -> "${TimeUnit.MILLISECONDS.toHours(deltaMillis)}H AGO"
      else -> "${TimeUnit.MILLISECONDS.toDays(deltaMillis)}D AGO"
    }
  }

  fun deadlineEndOfDayMillis(isoDate: String): Long? {
    val parts = isoDate.trim().split("-")
    if (parts.size != 3) return null
    val year = parts[0].toIntOrNull() ?: return null
    val month = parts[1].toIntOrNull() ?: return null
    val day = parts[2].toIntOrNull() ?: return null
    return Calendar.getInstance()
      .apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1)
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
      }
      .timeInMillis
  }
}

data class JobAlert(
  val id: String,
  val organization: String,
  val postTitle: String,
  val sector: JobSector,
  val centralAgency: CentralAgencyFilter? = null,
  val privateDomain: PrivateDomainFilter? = null,
  val stateCode: String? = null,
  val locationLabel: String,
  val vacancyCountLabel: String,
  val qualificationTag: String,
  val minGeneralQualification: QualificationLevel? = null,
  val acceptedExactLevels: Set<QualificationLevel> = emptySet(),
  val allowedStreams: Set<AcademicStream> = setOf(AcademicStream.ALL_GENERAL),
  val minAge: Int = 18,
  val maxAge: Int = 35,
  val applicationStartDate: String,
  val applicationEndDate: String,
  val officialPdfUrl: String,
  val directApplyUrl: String,
  val aiBrief: AiBrief,
  val officialDomain: String = "",
  val lastVerifiedTimestamp: String = "",
  val salaryPayScale: String = "As per Official Notification",
  val examDateDisplay: String = "Not Announced",
  val experienceRequired: String = "Freshers Eligible",
  val generalObcFee: String? = null,
  val scStFemaleFee: String? = null,
  val orgTag: String? = null,
  val subCategoryTag: String? = null,
) {
  val lastDateDisplay: String
    get() = DateWindowUtils.formatIsoToDisplay(applicationEndDate)

  val startDateDisplay: String
    get() = DateWindowUtils.formatIsoToDisplay(applicationStartDate)

  fun startDateEpochDay(): Long = DateWindowUtils.parseIsoEpochDay(applicationStartDate)

  fun lastDateEpochDay(): Long = DateWindowUtils.parseIsoEpochDay(applicationEndDate)

  fun isApplicationOpenNow(currentDateIso: String = DateWindowUtils.currentLocalDateIso()): Boolean {
    val today = DateWindowUtils.parseIsoEpochDay(currentDateIso)
    return today in startDateEpochDay()..lastDateEpochDay()
  }

  fun isApplicationOpenNow(nowMillis: Long): Boolean =
    isApplicationOpenNow(DateWindowUtils.millisToLocalDateIso(nowMillis))

  fun isUpcoming(currentDateIso: String = DateWindowUtils.currentLocalDateIso()): Boolean {
    val today = DateWindowUtils.parseIsoEpochDay(currentDateIso)
    return today < startDateEpochDay()
  }

  fun isExpired(currentDateIso: String = DateWindowUtils.currentLocalDateIso()): Boolean {
    val today = DateWindowUtils.parseIsoEpochDay(currentDateIso)
    return today > lastDateEpochDay()
  }

  fun daysRemaining(currentDateIso: String = DateWindowUtils.currentLocalDateIso()): Int {
    val today = DateWindowUtils.parseIsoEpochDay(currentDateIso)
    return (lastDateEpochDay() - today).toInt()
  }

  fun daysUntilOpen(currentDateIso: String = DateWindowUtils.currentLocalDateIso()): Int {
    val today = DateWindowUtils.parseIsoEpochDay(currentDateIso)
    return (startDateEpochDay() - today).toInt()
  }

  fun countdownBadgeText(currentDateIso: String = DateWindowUtils.currentLocalDateIso()): String {
    if (isUpcoming(currentDateIso)) {
      val days = daysUntilOpen(currentDateIso)
      return if (days <= 0) "OPENS SOON" else "OPENS IN ${days}D"
    }
    val days = daysRemaining(currentDateIso)
    return when {
      days < 0 -> "EXPIRED"
      days == 0 -> "TODAY"
      days < 10 -> "0${days}D LEFT"
      else -> "${days}D LEFT"
    }
  }

  fun resolveStatusTag(
    isApplied: Boolean,
    currentDateIso: String = DateWindowUtils.currentLocalDateIso(),
  ): JobStatusTag {
    if (isApplied) return JobStatusTag.APPLIED
    if (isExpired(currentDateIso)) return JobStatusTag.EXPIRED
    if (isUpcoming(currentDateIso)) return JobStatusTag.UPCOMING
    val days = daysRemaining(currentDateIso)
    return if (days in 0..7) JobStatusTag.CLOSING_SOON else JobStatusTag.ACTIVE
  }

  fun resolveStatusTag(isApplied: Boolean, nowMillis: Long): JobStatusTag =
    resolveStatusTag(isApplied, DateWindowUtils.millisToLocalDateIso(nowMillis))
}

data class JobAlertUiModel(
  val job: JobAlert,
  val isEligible: Boolean,
  val isSaved: Boolean,
  val isApplied: Boolean = false,
  val registrationNumber: String? = null,
  val isSourceVerified: Boolean = OfficialSourceVerifier.verifyJob(job).isVerified,
)

data class SavedJobRecord(
  val job: JobAlert,
  val isEligible: Boolean,
  val isApplied: Boolean,
  val registrationNumber: String?,
  val reminderEnabled: Boolean,
  val savedAtMillis: Long,
)

data class AppliedJobRecord(
  val job: JobAlert,
  val registrationNumber: String,
  val appliedDateDisplay: String,
  val appliedAtMillis: Long,
  val stage: ApplicationLifecycleStage,
  val personalStatus: PersonalApplicationStatus = PersonalApplicationStatus.APPLIED,
  val portalUrl: String,
  val statusNote: String? = null,
)

object JobShareFormatter {
  fun format(job: JobAlert): String =
    buildString {
      appendLine("${job.organization} — ${job.postTitle}")
      appendLine("Qualification: ${job.qualificationTag} | Last Date: ${job.lastDateDisplay}")
      appendLine("Official Portal: ${job.directApplyUrl}")
      append("Shared via NextPost")
    }
}
