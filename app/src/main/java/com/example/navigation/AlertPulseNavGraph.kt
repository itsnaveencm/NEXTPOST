package com.example.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.data.model.AppliedJobRecord
import com.example.data.model.JobAlertUiModel
import com.example.ui.components.MarkAppliedDialog
import com.example.ui.screens.CandidateProfileScreen
import com.example.ui.screens.CompanyDetailScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExamTrackingHubScreen
import com.example.ui.screens.ExamUpdateDetailScreen
import com.example.ui.screens.ExamUpdatesHubScreen
import com.example.ui.screens.ExamsSavedModuleScreen
import com.example.ui.screens.GovJobsModuleScreen
import com.example.ui.screens.JobDetailScreen
import com.example.ui.screens.MyApplicationsHubScreen
import com.example.ui.screens.OrgDetailScreen
import com.example.ui.screens.PrivateDomainScreen
import com.example.ui.screens.PrivateModuleScreen
import com.example.ui.screens.RecruitmentCategoryScreen
import com.example.ui.screens.SavedJobsHubScreen
import com.example.ui.screens.StateDetailScreen
import com.example.ui.screens.StateGovModuleScreen
import com.example.ui.theme.AmoledBlack
import com.example.ui.viewmodel.AlertsViewModel

@Composable
fun AlertPulseApp(viewModel: AlertsViewModel) {
  val navController = rememberNavController()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val lifecycleOwner = LocalLifecycleOwner.current

  var manualMarkTarget by remember { mutableStateOf<JobAlertUiModel?>(null) }
  var editingAppliedRecord by remember { mutableStateOf<AppliedJobRecord?>(null) }

  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      when (event) {
        Lifecycle.Event.ON_PAUSE -> viewModel.onHostPaused()
        Lifecycle.Event.ON_RESUME -> viewModel.onHostResumed()
        else -> Unit
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
  }

  Scaffold(
    containerColor = AmoledBlack,
    contentWindowInsets = WindowInsets.safeDrawing,
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = DashboardRoute,
      enterTransition = {
        slideInHorizontally(animationSpec = tween(220)) { it / 6 } + fadeIn(tween(200))
      },
      exitTransition = {
        slideOutHorizontally(animationSpec = tween(180)) { -it / 6 } + fadeOut(tween(160))
      },
      popEnterTransition = {
        slideInHorizontally(animationSpec = tween(220)) { -it / 6 } + fadeIn(tween(200))
      },
      popExitTransition = {
        slideOutHorizontally(animationSpec = tween(180)) { it / 6 } + fadeOut(tween(160))
      },
      modifier = Modifier.fillMaxSize().padding(innerPadding),
    ) {
      // LEVEL 1: Recruitment Hub
      composable<DashboardRoute> {
        DashboardScreen(
          uiState = uiState,
          onSearchQueryChange = viewModel::updateSearchQuery,
          onToggleEligibleOnly = viewModel::toggleEligibleOnly,
          onProfileClick = { navController.navigate(CandidateProfileRoute) },
          onOpenGovModule = { navController.navigate(GovJobsModuleRoute) },
          onOpenStateModule = { navController.navigate(StateGovModuleRoute) },
          onOpenPrivateModule = { navController.navigate(PrivateModuleRoute) },
          onOpenExamsSavedModule = { navController.navigate(ExamsSavedModuleRoute) },
          onOpenOrgDetail = { orgId -> navController.navigate(OrgDetailRoute(orgId)) },
          onOpenStateDetail = { code -> navController.navigate(StateDetailRoute(code)) },
          onOpenCompanyDetail = { companyId ->
            navController.navigate(CompanyDetailRoute(companyId))
          },
          onOpenJobDetail = { jobId, prefix ->
            navController.navigate(JobDetailRoute(jobId = jobId, breadcrumbPrefix = prefix))
          },
          onOpenExamUpdateDetail = { updateId, prefix ->
            navController.navigate(
              ExamUpdateDetailRoute(updateId = updateId, breadcrumbPrefix = prefix)
            )
          },
          onOpenMyApplicationsHub = { navController.navigate(MyApplicationsHubRoute) },
        )
      }

      // LEVEL 2: Module 1 — Government Jobs
      composable<GovJobsModuleRoute> {
        GovJobsModuleScreen(
          uiState = uiState,
          onNavigateBack = { navController.popBackStack() },
          onToggleEligibleOnly = viewModel::toggleEligibleOnly,
          onEditProfileClick = { navController.navigate(CandidateProfileRoute) },
          onToggleSaveOrg = viewModel::toggleSavedOrg,
          onOrgClick = { orgId -> navController.navigate(OrgDetailRoute(orgId)) },
        )
      }

      // LEVEL 3: Government Organisation Sub-categories
      composable<OrgDetailRoute> { entry ->
        val route = entry.toRoute<OrgDetailRoute>()
        OrgDetailScreen(
          orgId = route.orgId,
          uiState = uiState,
          onNavigateBack = { navController.popBackStack() },
          onToggleSaveOrg = viewModel::toggleSavedOrg,
          onToggleEligibleOnly = viewModel::toggleEligibleOnly,
          onEditProfileClick = { navController.navigate(CandidateProfileRoute) },
          onSubCategoryClick = { categoryId ->
            navController.navigate(
              RecruitmentCategoryRoute(parentId = "org:${route.orgId}", categoryId = categoryId)
            )
          },
        )
      }

      // LEVEL 2: Module 2 — State Government Directory
      composable<StateGovModuleRoute> {
        StateGovModuleScreen(
          uiState = uiState,
          onNavigateBack = { navController.popBackStack() },
          onToggleEligibleOnly = viewModel::toggleEligibleOnly,
          onEditProfileClick = { navController.navigate(CandidateProfileRoute) },
          onStateClick = { stateCode -> navController.navigate(StateDetailRoute(stateCode)) },
        )
      }

      // LEVEL 3: State Detail Subsections
      composable<StateDetailRoute> { entry ->
        val route = entry.toRoute<StateDetailRoute>()
        StateDetailScreen(
          stateCode = route.stateCode,
          uiState = uiState,
          onNavigateBack = { navController.popBackStack() },
          onToggleEligibleOnly = viewModel::toggleEligibleOnly,
          onEditProfileClick = { navController.navigate(CandidateProfileRoute) },
          onSubsectionClick = { subId ->
            navController.navigate(
              RecruitmentCategoryRoute(parentId = "state:${route.stateCode}", categoryId = subId)
            )
          },
        )
      }

      // LEVEL 2: Module 3 — Private & MNC Domains
      composable<PrivateModuleRoute> {
        PrivateModuleScreen(
          uiState = uiState,
          onNavigateBack = { navController.popBackStack() },
          onToggleEligibleOnly = viewModel::toggleEligibleOnly,
          onEditProfileClick = { navController.navigate(CandidateProfileRoute) },
          onDomainClick = { domainId -> navController.navigate(PrivateDomainRoute(domainId)) },
        )
      }

      // LEVEL 3A: Domain Company Directory
      composable<PrivateDomainRoute> { entry ->
        val route = entry.toRoute<PrivateDomainRoute>()
        PrivateDomainScreen(
          domainId = route.domainId,
          uiState = uiState,
          onNavigateBack = { navController.popBackStack() },
          onToggleSaveCompany = viewModel::toggleSavedOrg,
          onCompanyClick = { companyId ->
            navController.navigate(CompanyDetailRoute(companyId))
          },
        )
      }

      // LEVEL 3B: Company Openings
      composable<CompanyDetailRoute> { entry ->
        val route = entry.toRoute<CompanyDetailRoute>()
        CompanyDetailScreen(
          companyId = route.companyId,
          uiState = uiState,
          onNavigateBack = { navController.popBackStack() },
          onToggleSaveCompany = viewModel::toggleSavedOrg,
          onToggleEligibleOnly = viewModel::toggleEligibleOnly,
          onEditProfileClick = { navController.navigate(CandidateProfileRoute) },
          onToggleBookmark = viewModel::toggleBookmark,
          onMarkAppliedClick = { item -> manualMarkTarget = item },
          onApplyNowLaunched = { item -> viewModel.onExternalApplyLaunched(item.job) },
          onOpenJobDetail = { jobId, prefix ->
            navController.navigate(JobDetailRoute(jobId = jobId, breadcrumbPrefix = prefix))
          },
        )
      }

      // LEVEL 4: Recruitment Category / State Subsection Listings
      composable<RecruitmentCategoryRoute> { entry ->
        val route = entry.toRoute<RecruitmentCategoryRoute>()
        RecruitmentCategoryScreen(
          parentId = route.parentId,
          categoryId = route.categoryId,
          uiState = uiState,
          onNavigateBack = { navController.popBackStack() },
          onToggleEligibleOnly = viewModel::toggleEligibleOnly,
          onEditProfileClick = { navController.navigate(CandidateProfileRoute) },
          onToggleBookmark = viewModel::toggleBookmark,
          onMarkAppliedClick = { item -> manualMarkTarget = item },
          onApplyNowLaunched = { item -> viewModel.onExternalApplyLaunched(item.job) },
          onOpenJobDetail = { jobId, prefix ->
            navController.navigate(JobDetailRoute(jobId = jobId, breadcrumbPrefix = prefix))
          },
        )
      }

      // LEVEL 2: Module 4 — Exams & Saved Management Hub
      composable<ExamsSavedModuleRoute> {
        ExamsSavedModuleScreen(
          uiState = uiState,
          onNavigateBack = { navController.popBackStack() },
          onOpenExamUpdates = { navController.navigate(ExamUpdatesHubRoute()) },
          onOpenSavedJobs = { navController.navigate(SavedJobsHubRoute) },
          onOpenMyApplications = { navController.navigate(MyApplicationsHubRoute) },
          onOpenExamTracking = { navController.navigate(ExamTrackingHubRoute) },
        )
      }

      // MODULE 4A: Exam Updates Screen
      composable<ExamUpdatesHubRoute> { entry ->
        val route = entry.toRoute<ExamUpdatesHubRoute>()
        ExamUpdatesHubScreen(
          initialTypeName = route.initialTypeName,
          uiState = uiState,
          onNavigateBack = { navController.popBackStack() },
          onToggleSavedNotice = viewModel::toggleSavedNotification,
          onOpenUpdateDetail = { updateId ->
            navController.navigate(
              ExamUpdateDetailRoute(
                updateId = updateId,
                breadcrumbPrefix = "Hub → Exams & Saved → Exam Updates",
              )
            )
          },
        )
      }

      // MODULE 4B: My Saved Jobs Screen
      composable<SavedJobsHubRoute> {
        SavedJobsHubScreen(
          uiState = uiState,
          onNavigateBack = { navController.popBackStack() },
          onToggleBookmark = viewModel::toggleBookmark,
          onToggleReminder = viewModel::setDeadlineReminder,
          onMarkAppliedClick = { item -> manualMarkTarget = item },
          onApplyNowLaunched = { item -> viewModel.onExternalApplyLaunched(item.job) },
          onOpenJobDetail = { jobId ->
            navController.navigate(
              JobDetailRoute(
                jobId = jobId,
                breadcrumbPrefix = "Hub → Exams & Saved → Saved Jobs",
              )
            )
          },
          onToggleSavedNotice = viewModel::toggleSavedNotification,
          onOpenUpdateDetail = { updateId ->
            navController.navigate(
              ExamUpdateDetailRoute(
                updateId = updateId,
                breadcrumbPrefix = "Hub → Exams & Saved → Saved Notices",
              )
            )
          },
          onToggleSavedOrg = viewModel::toggleSavedOrg,
          onOpenOrgDetail = { orgId -> navController.navigate(OrgDetailRoute(orgId)) },
          onOpenCompanyDetail = { companyId ->
            navController.navigate(CompanyDetailRoute(companyId))
          },
        )
      }

      // MODULE 4C: My Applications Screen
      composable<MyApplicationsHubRoute> {
        MyApplicationsHubScreen(
          uiState = uiState,
          onNavigateBack = { navController.popBackStack() },
          onEditRegistrationClick = { record -> editingAppliedRecord = record },
          onUpdateAppliedStage = viewModel::updateAppliedStage,
          onUpdatePersonalStatus = viewModel::updatePersonalStatus,
          onRemoveAppliedJob = viewModel::removeAppliedJob,
          onToggleBookmark = viewModel::toggleBookmark,
          onMarkAppliedClick = { item -> manualMarkTarget = item },
          onApplyNowLaunched = { item -> viewModel.onExternalApplyLaunched(item.job) },
          onOpenJobDetail = { jobId ->
            navController.navigate(
              JobDetailRoute(
                jobId = jobId,
                breadcrumbPrefix = "Hub → Exams & Saved → My Applications",
              )
            )
          },
        )
      }

      // MODULE 4D: Exam Tracking Screen
      composable<ExamTrackingHubRoute> {
        ExamTrackingHubScreen(
          uiState = uiState,
          onNavigateBack = { navController.popBackStack() },
          onOpenUpdateDetail = { updateId ->
            navController.navigate(
              ExamUpdateDetailRoute(
                updateId = updateId,
                breadcrumbPrefix = "Hub → Exams & Saved → Exam Tracking",
              )
            )
          },
          onOpenJobDetail = { jobId ->
            navController.navigate(
              JobDetailRoute(
                jobId = jobId,
                breadcrumbPrefix = "Hub → Exams & Saved → Exam Tracking",
              )
            )
          },
        )
      }

      // LEVEL 5A: Individual Recruitment Detail Screen
      composable<JobDetailRoute> { entry ->
        val route = entry.toRoute<JobDetailRoute>()
        JobDetailScreen(
          jobId = route.jobId,
          breadcrumbPrefix = route.breadcrumbPrefix,
          uiState = uiState,
          onNavigateBack = { navController.popBackStack() },
          onToggleBookmark = viewModel::toggleBookmark,
          onMarkAppliedClick = { item -> manualMarkTarget = item },
          onApplyNowLaunched = { item -> viewModel.onExternalApplyLaunched(item.job) },
        )
      }

      // LEVEL 5B: Individual Exam Update Detail Screen
      composable<ExamUpdateDetailRoute> { entry ->
        val route = entry.toRoute<ExamUpdateDetailRoute>()
        ExamUpdateDetailScreen(
          updateId = route.updateId,
          breadcrumbPrefix = route.breadcrumbPrefix,
          uiState = uiState,
          onNavigateBack = { navController.popBackStack() },
          onToggleSavedNotice = viewModel::toggleSavedNotification,
        )
      }

      // Candidate Eligibility Profile
      composable<CandidateProfileRoute> {
        CandidateProfileScreen(
          currentProfile = uiState.candidateProfile,
          onSavePreferences = { updatedProfile ->
            viewModel.saveCandidateProfile(updatedProfile)
            navController.popBackStack()
          },
          onNavigateBack = { navController.popBackStack() },
        )
      }
    }
  }

  // Automatic prompt when returning from external Apply Now browser link
  val promptJob = uiState.applyConfirmationJob
  if (promptJob != null) {
    MarkAppliedDialog(
      job = promptJob,
      isFromBrowserReturn = true,
      isAlreadyApplied = false,
      initialRegistrationNumber = "",
      onSave = { regNumber ->
        viewModel.markJobApplied(promptJob.id, regNumber)
        viewModel.dismissApplyConfirmation()
      },
      onDismiss = viewModel::dismissApplyConfirmation,
    )
  }

  // Manual "Mark as Applied" or edit from JobAlertCard / JobDetailScreen
  val targetItem = manualMarkTarget
  if (targetItem != null) {
    MarkAppliedDialog(
      job = targetItem.job,
      isFromBrowserReturn = false,
      isAlreadyApplied = targetItem.isApplied,
      initialRegistrationNumber = targetItem.registrationNumber.orEmpty(),
      onSave = { regNumber ->
        viewModel.markJobApplied(targetItem.job.id, regNumber)
        manualMarkTarget = null
      },
      onRemoveApplied = {
        viewModel.removeAppliedJob(targetItem.job.id)
        manualMarkTarget = null
      },
      onDismiss = { manualMarkTarget = null },
    )
  }

  // Edit Registration ID from AppliedJobBentoCard
  val editRecord = editingAppliedRecord
  if (editRecord != null) {
    MarkAppliedDialog(
      job = editRecord.job,
      isFromBrowserReturn = false,
      isAlreadyApplied = true,
      initialRegistrationNumber = editRecord.registrationNumber,
      onSave = { regNumber ->
        viewModel.updateRegistrationNumber(editRecord.job.id, regNumber)
        editingAppliedRecord = null
      },
      onRemoveApplied = {
        viewModel.removeAppliedJob(editRecord.job.id)
        editingAppliedRecord = null
      },
      onDismiss = { editingAppliedRecord = null },
    )
  }
}
