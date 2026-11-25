package com.example.navigation

import kotlinx.serialization.Serializable

// Level 1: Home Dashboard
@Serializable data object DashboardRoute

// Level 2: Module Landing Screens
@Serializable data object GovJobsModuleRoute

@Serializable data object StateGovModuleRoute

@Serializable data object PrivateModuleRoute

@Serializable data object ExamsSavedModuleRoute

// Level 3: Organisations / States / Private Domains & Companies / Module 4 Sub-Hubs
@Serializable data class OrgDetailRoute(val orgId: String)

@Serializable data class StateDetailRoute(val stateCode: String)

@Serializable data class PrivateDomainRoute(val domainId: String)

@Serializable data class CompanyDetailRoute(val companyId: String)

@Serializable data class ExamUpdatesHubRoute(val initialTypeName: String = "ALL")

@Serializable data object SavedJobsHubRoute

@Serializable data object MyApplicationsHubRoute

@Serializable data object ExamTrackingHubRoute

// Level 4: Recruitment Types / Sub-categories & State Qualification Tiers
@Serializable data class RecruitmentCategoryRoute(val parentId: String, val categoryId: String)

// Level 5: Individual Recruitment / Exam Update Details
@Serializable data class JobDetailRoute(val jobId: String, val breadcrumbPrefix: String = "Hub")

@Serializable data class ExamUpdateDetailRoute(val updateId: String, val breadcrumbPrefix: String = "Hub")

// Candidate Eligibility Profile
@Serializable data object CandidateProfileRoute
