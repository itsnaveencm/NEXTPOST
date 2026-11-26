package com.example.data.remote

import com.example.data.model.AcademicStream
import com.example.data.model.AiBrief
import com.example.data.model.CentralAgencyFilter
import com.example.data.model.ExamUpdate
import com.example.data.model.ExamUpdateType
import com.example.data.model.JobAlert
import com.example.data.model.JobSector
import com.example.data.model.PrivateDomainFilter
import com.example.data.model.QualificationLevel
import kotlinx.serialization.Serializable

@Serializable
data class OfficialFeedResponseDto(
  val jobs: List<JobAlertDto> = emptyList(),
  val examUpdates: List<ExamUpdateDto> = emptyList(),
  val generatedAt: String = "",
)

@Serializable
data class JobAlertDto(
  val id: String,
  val organization: String,
  val postTitle: String,
  val sector: String,
  val centralAgency: String? = null,
  val privateDomain: String? = null,
  val stateCode: String? = null,
  val locationLabel: String,
  val vacancyCountLabel: String,
  val qualificationTag: String,
  val minGeneralQualification: String? = null,
  val acceptedExactLevels: List<String> = emptyList(),
  val allowedStreams: List<String> = listOf(AcademicStream.ALL_GENERAL.name),
  val minAge: Int = 18,
  val maxAge: Int = 35,
  val applicationStartDate: String,
  val applicationEndDate: String,
  val officialPdfUrl: String,
  val directApplyUrl: String,
  val officialDomain: String,
  val lastVerifiedTimestamp: String,
  val eligibilityBrief: String,
  val ageLimitBrief: String,
  val selectionAndFeeBrief: String,
  val salaryPayScale: String = "As per Official Notification",
  val examDateDisplay: String = "Not Announced",
  val experienceRequired: String = "Freshers Eligible",
  val generalObcFee: String? = null,
  val scStFemaleFee: String? = null,
  val orgTag: String? = null,
  val subCategoryTag: String? = null,
) {
  fun toDomain(): JobAlert? {
    val parsedSector = JobSector.entries.firstOrNull { it.name == sector } ?: return null
    val parsedCentral =
      centralAgency?.let { name -> CentralAgencyFilter.entries.firstOrNull { it.name == name } }
    val parsedPrivate =
      privateDomain?.let { name -> PrivateDomainFilter.entries.firstOrNull { it.name == name } }
    val parsedMinQual =
      minGeneralQualification?.let { name ->
        QualificationLevel.entries.firstOrNull { it.name == name }
      }
    val parsedExact =
      acceptedExactLevels
        .mapNotNull { name -> QualificationLevel.entries.firstOrNull { it.name == name } }
        .toSet()
    val parsedStreams =
      allowedStreams
        .mapNotNull { name -> AcademicStream.entries.firstOrNull { it.name == name } }
        .toSet()
        .ifEmpty { setOf(AcademicStream.ALL_GENERAL) }

    return JobAlert(
      id = id,
      organization = organization,
      postTitle = postTitle,
      sector = parsedSector,
      centralAgency = parsedCentral,
      privateDomain = parsedPrivate,
      stateCode = stateCode,
      locationLabel = locationLabel,
      vacancyCountLabel = vacancyCountLabel,
      qualificationTag = qualificationTag,
      minGeneralQualification = parsedMinQual,
      acceptedExactLevels = parsedExact,
      allowedStreams = parsedStreams,
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
      examDateDisplay = examDateDisplay.ifBlank { "Not Announced" },
      experienceRequired = experienceRequired,
      generalObcFee = generalObcFee?.takeIf { it.isNotBlank() },
      scStFemaleFee = scStFemaleFee?.takeIf { it.isNotBlank() },
      orgTag = orgTag,
      subCategoryTag = subCategoryTag,
    )
  }
}

@Serializable
data class ExamUpdateDto(
  val id: String,
  val organization: String,
  val examTitle: String,
  val type: String,
  val releasedDateDisplay: String,
  val examDateDisplay: String = "Not Announced",
  val statusLabel: String = "VERIFIED NOTICE",
  val summaryNote: String,
  val portalLoginUrl: String,
  val noticePdfUrl: String? = null,
  val officialDomain: String,
  val lastVerifiedTimestamp: String,
) {
  fun toDomain(): ExamUpdate? {
    val parsedType = ExamUpdateType.entries.firstOrNull { it.name == type } ?: return null
    return ExamUpdate(
      id = id,
      organization = organization,
      examTitle = examTitle,
      type = parsedType,
      releasedDateDisplay = releasedDateDisplay,
      examDateDisplay = examDateDisplay.ifBlank { "Not Announced" },
      statusLabel = statusLabel,
      summaryNote = summaryNote,
      portalLoginUrl = portalLoginUrl,
      noticePdfUrl = noticePdfUrl,
      officialDomain = officialDomain,
      lastVerifiedTimestamp = lastVerifiedTimestamp,
    )
  }
}
