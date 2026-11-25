package com.example.data.model

data class ExamUpdate(
  val id: String,
  val organization: String,
  val examTitle: String,
  val type: ExamUpdateType,
  val releasedDateDisplay: String,
  val examDateDisplay: String = "Not Announced",
  val statusLabel: String = "VERIFIED NOTICE",
  val summaryNote: String,
  val portalLoginUrl: String,
  val noticePdfUrl: String? = null,
  val officialDomain: String = "",
  val lastVerifiedTimestamp: String = "",
)
