package com.example.data.model

enum class QualificationLevel(
  val rank: Int,
  val label: String,
  val shortLabel: String,
  val isTradeSpecific: Boolean = false,
) {
  TENTH(1, "10th Pass", "10th"),
  TWELFTH(2, "12th Pass", "12th"),
  ITI(3, "ITI", "ITI", isTradeSpecific = true),
  DIPLOMA(4, "Polytechnic Diploma", "Diploma", isTradeSpecific = true),
  GRADUATE(5, "Any Graduate", "Graduate"),
  BTECH(6, "B.Tech / B.E.", "B.Tech"),
  POSTGRADUATE(7, "Postgraduate", "PG"),
}

enum class AcademicStream(
  val label: String,
  val shortLabel: String,
) {
  ALL_GENERAL("All / General", "General"),
  ECE("ECE / Electronics", "ECE"),
  CSE_IT("CSE / IT", "CSE/IT"),
  EEE("Electrical", "EEE"),
  MECH_CIVIL("Mechanical / Civil", "Mech/Civil"),
  COMMERCE_FINANCE("Commerce / Finance", "Finance"),
  SCIENCE_ARTS("Science / Arts", "Sci/Arts"),
}

data class CandidateProfile(
  val qualification: QualificationLevel = QualificationLevel.BTECH,
  val stream: AcademicStream = AcademicStream.ECE,
  val age: Int = 23,
  val homeStateCode: String = "KL",
  val hasCompletedOnboarding: Boolean = false,
) {
  fun summaryLabel(): String = "${qualification.shortLabel} (${stream.shortLabel}), ${age}y"
}

object EligibilityEngine {
  fun isEligible(job: JobAlert, profile: CandidateProfile): Boolean {
    if (profile.age !in job.minAge..job.maxAge) return false

    val qualMatches =
      when {
        profile.qualification in job.acceptedExactLevels -> true
        job.minGeneralQualification != null -> {
          val minGen = job.minGeneralQualification
          if (!profile.qualification.isTradeSpecific) {
            profile.qualification.rank >= minGen.rank
          } else {
            // Trade-specific ITI / Diploma candidates also satisfy 10th-pass general posts
            minGen == QualificationLevel.TENTH
          }
        }
        else -> false
      }
    if (!qualMatches) return false

    val streamMatches =
      AcademicStream.ALL_GENERAL in job.allowedStreams ||
        profile.stream in job.allowedStreams
    return streamMatches
  }
}
