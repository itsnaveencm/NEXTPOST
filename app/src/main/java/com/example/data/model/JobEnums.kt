package com.example.data.model

enum class JobSector(val label: String) {
  CENTRAL_GOVT("CENTRAL GOVT"),
  STATE_GOVT("STATE GOVT"),
  PRIVATE_MNC("PRIVATE / MNC"),
  STATE_PRIVATE("STATE PRIVATE"),
}

enum class CentralAgencyFilter(val label: String) {
  ALL("All"),
  RRB("RRB"),
  SSC("SSC"),
  UPSC("UPSC"),
  BANKING("Banking"),
  DRDO_ISRO("DRDO/ISRO"),
  MHA_IB("MHA/IB"),
  PSUS("PSUs"),
}

enum class PrivateDomainFilter(val label: String) {
  ALL("All"),
  IT_SOFTWARE("IT & Software"),
  CORE_ENGINEERING("Core Engineering"),
  FINANCE_OPS("Finance & Ops"),
  REMOTE_WFH("Remote"),
}

enum class ExamUpdateType(val badgeLabel: String) {
  ADMIT_CARD("ADMIT CARD"),
  CITY_SLIP("CITY SLIP"),
  ANSWER_KEY("ANSWER KEY"),
  RESULT("RESULT"),
  EXAM_DATE("EXAM DATE"),
  NOTIFICATION("NOTIFICATION"),
  DEADLINE("DEADLINE"),
}

enum class ApplicationLifecycleStage(
  val stepNumber: Int,
  val stepLabel: String,
  val liveBannerText: String,
  val actionButtonLabel: String,
) {
  APPLIED(
    stepNumber = 1,
    stepLabel = "Applied",
    liveBannerText = "APPLICATION SUBMITTED • AWAITING EXAM DATE",
    actionButtonLabel = "Check Status / Portal Login ↗",
  ),
  CITY_SLIP(
    stepNumber = 2,
    stepLabel = "City Slip",
    liveBannerText = "CITY INTIMATION SLIP LIVE",
    actionButtonLabel = "Check Status / Portal Login ↗",
  ),
  ADMIT_CARD(
    stepNumber = 3,
    stepLabel = "Admit Card",
    liveBannerText = "ADMIT CARD AVAILABLE",
    actionButtonLabel = "Download Admit Card / Result ↗",
  ),
  ANSWER_KEY(
    stepNumber = 4,
    stepLabel = "Answer Key",
    liveBannerText = "ANSWER KEY RELEASED",
    actionButtonLabel = "Download Admit Card / Result ↗",
  ),
  RESULT(
    stepNumber = 5,
    stepLabel = "Result",
    liveBannerText = "FINAL RESULT DECLARED",
    actionButtonLabel = "Download Admit Card / Result ↗",
  ),
}

enum class PersonalApplicationStatus(val label: String) {
  APPLIED("Applied"),
  IN_PROGRESS("Application In Progress"),
  COMPLETED("Completed"),
  NOT_APPLIED("Not Applied"),
}

enum class JobWindowFilter(val label: String) {
  ACTIVE("Active Now"),
  UPCOMING("Upcoming"),
}
