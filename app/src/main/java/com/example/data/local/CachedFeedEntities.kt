package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AcademicStream
import com.example.data.model.AiBrief
import com.example.data.model.CentralAgencyFilter
import com.example.data.model.ExamUpdate
import com.example.data.model.ExamUpdateType
import com.example.data.model.JobAlert
import com.example.data.model.JobSector
import com.example.data.model.PrivateDomainFilter
import com.example.data.model.QualificationLevel

@Entity(tableName = "cached_jobs")
data class CachedJobEntity(
  @PrimaryKey val id: String,
  val organization: String,
  val postTitle: String,
  val sectorName: String,
  val centralAgencyName: String?,
  val privateDomainName: String?,
  val stateCode: String?,
  val locationLabel: String,
  val vacancyCountLabel: String,
  val qualificationTag: String,
  val minGeneralQualName: String?,
  val acceptedExactLevelsCsv: String,
  val allowedStreamsCsv: String,
  val minAge: Int,
  val maxAge: Int,
  val applicationStartDate: String,
  val applicationEndDate: String,
  val officialPdfUrl: String,
  val directApplyUrl: String,
  val officialDomain: String,
  val lastVerifiedTimestamp: String,
  val eligibilityBrief: String,
  val ageLimitBrief: String,
  val selectionAndFeeBrief: String,
  val salaryPayScale: String,
  val examDateDisplay: String,
  val experienceRequired: String,
  val generalObcFee: String? = null,
  val scStFemaleFee: String? = null,
  val orgTag: String?,
  val subCategoryTag: String?,
  val lastSyncedAtMillis: Long,
) {
  fun toDomain(): JobAlert? {
    val sector = JobSector.entries.firstOrNull { it.name == sectorName } ?: return null
    val central =
      centralAgencyName?.let { n -> CentralAgencyFilter.entries.firstOrNull { it.name == n } }
    val privateDom =
      privateDomainName?.let { n -> PrivateDomainFilter.entries.firstOrNull { it.name == n } }
    val minQual =
      minGeneralQualName?.let { n -> QualificationLevel.entries.firstOrNull { it.name == n } }
    val exactSet =
      acceptedExactLevelsCsv
        .split(",")
        .mapNotNull { token -> QualificationLevel.entries.firstOrNull { it.name == token.trim() } }
        .toSet()
    val streamSet =
      allowedStreamsCsv
        .split(",")
        .mapNotNull { token -> AcademicStream.entries.firstOrNull { it.name == token.trim() } }
        .toSet()
        .ifEmpty { setOf(AcademicStream.ALL_GENERAL) }

    return JobAlert(
      id = id,
      organization = organization,
      postTitle = postTitle,
      sector = sector,
      centralAgency = central,
      privateDomain = privateDom,
      stateCode = stateCode,
      locationLabel = locationLabel,
      vacancyCountLabel = vacancyCountLabel,
      qualificationTag = qualificationTag,
      minGeneralQualification = minQual,
      acceptedExactLevels = exactSet,
      allowedStreams = streamSet,
      minAge = minAge,
      maxAge = maxAge,
      applicationStartDate = applicationStartDate,
      applicationEndDate = applicationEndDate,
      officialPdfUrl = officialPdfUrl,
      directApplyUrl = directApplyUrl,
      aiBrief =
        AiBrief(
          eligibility = eligibilityBrief,
          ageLimit = ageLimitBrief,
          selectionAndFee = selectionAndFeeBrief,
        ),
      officialDomain = officialDomain,
      lastVerifiedTimestamp = lastVerifiedTimestamp,
      salaryPayScale = salaryPayScale,
      examDateDisplay = examDateDisplay,
      experienceRequired = experienceRequired,
      generalObcFee = generalObcFee,
      scStFemaleFee = scStFemaleFee,
      orgTag = orgTag,
      subCategoryTag = subCategoryTag,
    )
  }

  companion object {
    fun fromDomain(job: JobAlert, syncedAtMillis: Long): CachedJobEntity =
      CachedJobEntity(
        id = job.id,
        organization = job.organization,
        postTitle = job.postTitle,
        sectorName = job.sector.name,
        centralAgencyName = job.centralAgency?.name,
        privateDomainName = job.privateDomain?.name,
        stateCode = job.stateCode,
        locationLabel = job.locationLabel,
        vacancyCountLabel = job.vacancyCountLabel,
        qualificationTag = job.qualificationTag,
        minGeneralQualName = job.minGeneralQualification?.name,
        acceptedExactLevelsCsv = job.acceptedExactLevels.joinToString(",") { it.name },
        allowedStreamsCsv = job.allowedStreams.joinToString(",") { it.name },
        minAge = job.minAge,
        maxAge = job.maxAge,
        applicationStartDate = job.applicationStartDate,
        applicationEndDate = job.applicationEndDate,
        officialPdfUrl = job.officialPdfUrl,
        directApplyUrl = job.directApplyUrl,
        officialDomain = job.officialDomain,
        lastVerifiedTimestamp = job.lastVerifiedTimestamp,
        eligibilityBrief = job.aiBrief.eligibility,
        ageLimitBrief = job.aiBrief.ageLimit,
        selectionAndFeeBrief = job.aiBrief.selectionAndFee,
        salaryPayScale = job.salaryPayScale,
        examDateDisplay = job.examDateDisplay,
        experienceRequired = job.experienceRequired,
        generalObcFee = job.generalObcFee,
        scStFemaleFee = job.scStFemaleFee,
        orgTag = job.orgTag,
        subCategoryTag = job.subCategoryTag,
        lastSyncedAtMillis = syncedAtMillis,
      )
  }
}

@Entity(tableName = "cached_exam_updates")
data class CachedExamUpdateEntity(
  @PrimaryKey val id: String,
  val organization: String,
  val examTitle: String,
  val typeName: String,
  val releasedDateDisplay: String,
  val examDateDisplay: String,
  val statusLabel: String,
  val summaryNote: String,
  val portalLoginUrl: String,
  val noticePdfUrl: String?,
  val officialDomain: String,
  val lastVerifiedTimestamp: String,
  val lastSyncedAtMillis: Long,
) {
  fun toDomain(): ExamUpdate? {
    val parsedType = ExamUpdateType.entries.firstOrNull { it.name == typeName } ?: return null
    return ExamUpdate(
      id = id,
      organization = organization,
      examTitle = examTitle,
      type = parsedType,
      releasedDateDisplay = releasedDateDisplay,
      examDateDisplay = examDateDisplay,
      statusLabel = statusLabel,
      summaryNote = summaryNote,
      portalLoginUrl = portalLoginUrl,
      noticePdfUrl = noticePdfUrl,
      officialDomain = officialDomain,
      lastVerifiedTimestamp = lastVerifiedTimestamp,
    )
  }

  companion object {
    fun fromDomain(update: ExamUpdate, syncedAtMillis: Long): CachedExamUpdateEntity =
      CachedExamUpdateEntity(
        id = update.id,
        organization = update.organization,
        examTitle = update.examTitle,
        typeName = update.type.name,
        releasedDateDisplay = update.releasedDateDisplay,
        examDateDisplay = update.examDateDisplay,
        statusLabel = update.statusLabel,
        summaryNote = update.summaryNote,
        portalLoginUrl = update.portalLoginUrl,
        noticePdfUrl = update.noticePdfUrl,
        officialDomain = update.officialDomain,
        lastVerifiedTimestamp = update.lastVerifiedTimestamp,
        lastSyncedAtMillis = syncedAtMillis,
      )
  }
}

@Entity(tableName = "feed_sync_metadata")
data class FeedSyncMetadataEntity(
  @PrimaryKey val id: String = MAIN_SYNC_ID,
  val endpointUrl: String = "",
  val lastSyncedAtMillis: Long = 0L,
  val syncStatus: String = "AWAITING_LIVE_FEED",
  val verifiedJobsCount: Int = 0,
  val verifiedUpdatesCount: Int = 0,
  val blockedRecordsCount: Int = 0,
) {
  companion object {
    const val MAIN_SYNC_ID = "main_sync"
  }
}
