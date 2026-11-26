package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.ApplicationLifecycleStage
import com.example.data.model.AppliedJobRecord
import com.example.data.model.ExamUpdateType
import com.example.data.model.JobAlert
import com.example.data.model.JobAlertUiModel
import com.example.data.model.PersonalApplicationStatus
import com.example.data.model.RecruitmentHierarchy
import com.example.ui.components.AppliedJobBentoCard
import com.example.ui.components.EligibilityStripCard
import com.example.ui.components.ExamUpdateCard
import com.example.ui.components.GlassyDirectoryRowCard
import com.example.ui.components.GlassySectionHeader
import com.example.ui.components.GlassyTopBarWithBreadcrumb
import com.example.ui.components.JobAlertCard
import com.example.ui.components.NextPostPullToRefreshBox
import com.example.ui.components.StadiumPill
import com.example.ui.components.ZeroAlertsBentoCard
import com.example.ui.theme.SkyAccent
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.TitaniumSilverMuted
import com.example.ui.viewmodel.AlertsUiState

@Composable
fun ExamsSavedModuleScreen(
  uiState: AlertsUiState,
  onNavigateBack: () -> Unit,
  onSetEligibleOnly: (Boolean) -> Unit,
  onEditProfileClick: () -> Unit,
  onOpenExamUpdates: () -> Unit,
  onOpenSavedJobs: () -> Unit,
  onOpenMyApplications: () -> Unit,
  onOpenExamTracking: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val totalSavedCount =
    uiState.savedJobs.size + uiState.savedNotificationIds.size + uiState.savedOrgIds.size

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    Column(modifier = Modifier.widthIn(max = 780.dp).fillMaxSize()) {
      GlassyTopBarWithBreadcrumb(
        title = "Exams & Saved",
        breadcrumbs = listOf("Hub", "Exams & Saved"),
        onBackClick = onNavigateBack,
      )

      LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize().testTag("exams_saved_hub"),
      ) {
        item(key = "module4_eligibility") {
          EligibilityStripCard(
            eligibleOnly = uiState.eligibleOnly,
            profile = uiState.candidateProfile,
            allOpenCount = uiState.totalAllOpenCount,
            eligibleOpenCount = uiState.totalOpenEligibleCount,
            onSetEligibleOnly = onSetEligibleOnly,
            onEditProfileClick = onEditProfileClick,
            modifier = Modifier.fillMaxWidth(),
          )
        }

        item(key = "module4_header") {
          GlassySectionHeader(
            eyebrow = "MODULE 4 • MANAGEMENT HUB",
            title = "Updates, Bookmarks & Tracking",
            trailingBadge = "${uiState.appliedJobs.size} APPLIED",
          )
        }

        item(key = "hub_exam_updates") {
          GlassyDirectoryRowCard(
            eyebrow = "A. EXAM UPDATES",
            title = "Exam Updates",
            subtitle = "Admit Cards, City Slips, Answer Keys, Results, Exam Dates & Deadlines",
            badgeText = "${uiState.examUpdates.size} LIVE",
            icon = Icons.Outlined.NotificationsActive,
            accentColor = SkyAccent,
            onClick = onOpenExamUpdates,
            testTag = "module4_exam_updates_card",
          )
        }

        item(key = "hub_saved_jobs") {
          GlassyDirectoryRowCard(
            eyebrow = "B. MY SAVED JOBS",
            title = "My Saved Jobs",
            subtitle = "Bookmarked Jobs, Saved Notifications & Saved Organisations",
            badgeText = "$totalSavedCount SAVED",
            icon = Icons.Outlined.BookmarkBorder,
            accentColor = SoftLavender,
            onClick = onOpenSavedJobs,
            testTag = "module4_saved_jobs_card",
          )
        }

        item(key = "hub_my_applications") {
          GlassyDirectoryRowCard(
            eyebrow = "C. MY APPLICATIONS",
            title = "My Applications",
            subtitle = "Track Applied, In Progress & Completed with 5-stage lifecycle progress",
            badgeText = "${uiState.appliedJobs.size} TRACKED",
            icon = Icons.Outlined.AssignmentTurnedIn,
            accentColor = SkyAccent,
            onClick = onOpenMyApplications,
            testTag = "module4_my_applications_card",
          )
        }

        item(key = "hub_exam_tracking") {
          GlassyDirectoryRowCard(
            eyebrow = "D. EXAM TRACKING",
            title = "Exam Tracking",
            subtitle = "Upcoming Exams, Exam Schedule, Examination History & Important Dates",
            badgeText = "${uiState.examUpdates.size + uiState.appliedJobs.size} ITEMS",
            icon = Icons.Outlined.CalendarMonth,
            accentColor = SoftLavender,
            onClick = onOpenExamTracking,
            testTag = "module4_exam_tracking_card",
          )
        }
      }
    }
  }
}

private enum class ExamUpdatesFilterChip(val label: String, val type: ExamUpdateType?) {
  ALL("All Updates", null),
  ADMIT_CARD("Admit Cards", ExamUpdateType.ADMIT_CARD),
  CITY_SLIP("City Intimation Slips", ExamUpdateType.CITY_SLIP),
  ANSWER_KEY("Answer Keys", ExamUpdateType.ANSWER_KEY),
  RESULT("Results", ExamUpdateType.RESULT),
  EXAM_DATE("Exam Dates", ExamUpdateType.EXAM_DATE),
  NOTIFICATION("Notifications", ExamUpdateType.NOTIFICATION),
  DEADLINE("Application Deadlines", ExamUpdateType.DEADLINE),
}

@Composable
fun ExamUpdatesHubScreen(
  initialTypeName: String,
  uiState: AlertsUiState,
  onNavigateBack: () -> Unit,
  onToggleSavedNotice: (String) -> Unit,
  onOpenUpdateDetail: (String) -> Unit,
  onRefreshFeedClick: () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  var selectedFilter by rememberSaveable(initialTypeName) {
    mutableStateOf(
      ExamUpdatesFilterChip.entries.firstOrNull { it.name == initialTypeName }
        ?: ExamUpdatesFilterChip.ALL
    )
  }

  val filteredUpdates =
    uiState.examUpdates.filter { update ->
      selectedFilter.type == null || update.type == selectedFilter.type
    }

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    Column(modifier = Modifier.widthIn(max = 780.dp).fillMaxSize()) {
      GlassyTopBarWithBreadcrumb(
        title = "Exam Updates",
        breadcrumbs = listOf("Hub", "Exams & Saved", "Exam Updates"),
        onBackClick = onNavigateBack,
      )

      LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        items(ExamUpdatesFilterChip.entries, key = { it.name }) { chip ->
          StadiumPill(
            label = chip.label,
            selected = chip == selectedFilter,
            onClick = { selectedFilter = chip },
            testTag = "exam_updates_chip_${chip.name}",
          )
        }
      }

      NextPostPullToRefreshBox(
        isRefreshing = uiState.isSyncing,
        onRefresh = onRefreshFeedClick,
        modifier = Modifier.weight(1f).fillMaxWidth().testTag("exam_updates_pull_to_refresh"),
      ) {
        LazyColumn(
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier.fillMaxSize().testTag("exam_updates_hub_list"),
        ) {
          if (filteredUpdates.isEmpty()) {
            item(key = "updates_empty") {
              ZeroAlertsBentoCard(
                headline = "NO UPDATES IN THIS CATEGORY",
                subtitle = "No active exam updates matched ${selectedFilter.label}.",
                secondaryActionLabel =
                  if (selectedFilter != ExamUpdatesFilterChip.ALL) "Show All Updates" else null,
                onSecondaryActionClick = { selectedFilter = ExamUpdatesFilterChip.ALL },
              )
            }
          } else {
            items(filteredUpdates, key = { it.id }) { update ->
              ExamUpdateCard(
                update = update,
                isSaved = update.id in uiState.savedNotificationIds,
                onToggleSaved = { onToggleSavedNotice(update.id) },
                onCardClick = { onOpenUpdateDetail(update.id) },
              )
            }
          }
        }
      }
    }
  }
}

private enum class SavedSectionTab(val label: String) {
  BOOKMARKED_JOBS("Bookmarked Jobs"),
  SAVED_NOTIFICATIONS("Saved Notifications"),
  SAVED_ORGS("Saved Organisations"),
}

@Composable
fun SavedJobsHubScreen(
  uiState: AlertsUiState,
  onNavigateBack: () -> Unit,
  onToggleBookmark: (String) -> Unit,
  onToggleReminder: (String, Boolean) -> Unit,
  onMarkAppliedClick: (JobAlertUiModel) -> Unit,
  onApplyNowLaunched: (JobAlertUiModel) -> Unit,
  onOpenJobDetail: (String) -> Unit,
  onToggleSavedNotice: (String) -> Unit,
  onOpenUpdateDetail: (String) -> Unit,
  onToggleSavedOrg: (String) -> Unit,
  onOpenOrgDetail: (String) -> Unit,
  onOpenCompanyDetail: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var selectedTab by rememberSaveable { mutableStateOf(SavedSectionTab.BOOKMARKED_JOBS) }

  val savedNotices = uiState.examUpdates.filter { it.id in uiState.savedNotificationIds }
  val savedGovOrgs = RecruitmentHierarchy.govOrganisations.filter { it.id in uiState.savedOrgIds }
  val savedCompanies = RecruitmentHierarchy.privateCompanies.filter { it.id in uiState.savedOrgIds }

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    Column(modifier = Modifier.widthIn(max = 780.dp).fillMaxSize()) {
      GlassyTopBarWithBreadcrumb(
        title = "My Saved Jobs",
        breadcrumbs = listOf("Hub", "Exams & Saved", "My Saved Jobs"),
        onBackClick = onNavigateBack,
      )

      LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        items(SavedSectionTab.entries, key = { it.name }) { tab ->
          StadiumPill(
            label = tab.label,
            selected = tab == selectedTab,
            onClick = { selectedTab = tab },
            testTag = "saved_tab_${tab.name}",
          )
        }
      }

      when (selectedTab) {
        SavedSectionTab.BOOKMARKED_JOBS -> {
          if (uiState.savedJobs.isEmpty()) {
            ZeroAlertsBentoCard(
              headline = "NO BOOKMARKED JOBS YET",
              subtitle = "Bookmark open recruitment alerts to track deadlines and toggle reminders here.",
            )
          } else {
            LazyColumn(
              contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp),
              modifier = Modifier.fillMaxSize().testTag("saved_jobs_list"),
            ) {
              items(uiState.savedJobs, key = { it.job.id }) { record ->
                val uiModel =
                  JobAlertUiModel(
                    job = record.job,
                    isEligible = record.isEligible,
                    isSaved = true,
                    isApplied = record.isApplied,
                    registrationNumber = record.registrationNumber,
                  )
                JobAlertCard(
                  job = record.job,
                  isEligible = record.isEligible,
                  isSaved = true,
                  isApplied = record.isApplied,
                  registrationNumber = record.registrationNumber,
                  onToggleBookmark = { onToggleBookmark(record.job.id) },
                  onMarkAppliedClick = { onMarkAppliedClick(uiModel) },
                  onApplyNowLaunched = { onApplyNowLaunched(uiModel) },
                  onCardClick = { onOpenJobDetail(record.job.id) },
                  reminderEnabled = record.reminderEnabled,
                  onToggleReminder = { enabled -> onToggleReminder(record.job.id, enabled) },
                )
              }
            }
          }
        }

        SavedSectionTab.SAVED_NOTIFICATIONS -> {
          if (savedNotices.isEmpty()) {
            ZeroAlertsBentoCard(
              headline = "NO SAVED NOTIFICATIONS",
              subtitle = "Bookmark Admit Cards, City Slips, or Results from Exam Updates to pin them here.",
            )
          } else {
            LazyColumn(
              contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp),
              modifier = Modifier.fillMaxSize().testTag("saved_notifications_list"),
            ) {
              items(savedNotices, key = { it.id }) { notice ->
                ExamUpdateCard(
                  update = notice,
                  isSaved = true,
                  onToggleSaved = { onToggleSavedNotice(notice.id) },
                  onCardClick = { onOpenUpdateDetail(notice.id) },
                )
              }
            }
          }
        }

        SavedSectionTab.SAVED_ORGS -> {
          if (savedGovOrgs.isEmpty() && savedCompanies.isEmpty()) {
            ZeroAlertsBentoCard(
              headline = "NO SAVED ORGANISATIONS",
              subtitle = "Bookmark any Government board or Private company to jump directly to its openings.",
            )
          } else {
            LazyColumn(
              contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp),
              modifier = Modifier.fillMaxSize().testTag("saved_orgs_list"),
            ) {
              items(savedGovOrgs, key = { "org_${it.id}" }) { org ->
                GlassyDirectoryRowCard(
                  eyebrow = org.shortName,
                  title = org.fullName,
                  subtitle = org.subtitle,
                  badgeText = "${uiState.openJobsForOrg(org.id).size} OPEN",
                  onClick = { onOpenOrgDetail(org.id) },
                  trailingAction = {
                    IconButton(
                      onClick = { onToggleSavedOrg(org.id) },
                      modifier = Modifier.minimumInteractiveComponentSize(),
                    ) {
                      Icon(
                        imageVector = Icons.Rounded.Bookmark,
                        contentDescription = "Remove saved organisation",
                        tint = SoftLavender,
                      )
                    }
                  },
                )
              }
              items(savedCompanies, key = { "cmp_${it.id}" }) { company ->
                GlassyDirectoryRowCard(
                  eyebrow = company.headquarters,
                  title = company.name,
                  subtitle = company.tagline,
                  badgeText = "${uiState.openJobsForCompany(company.id).size} OPEN",
                  onClick = { onOpenCompanyDetail(company.id) },
                  trailingAction = {
                    IconButton(
                      onClick = { onToggleSavedOrg(company.id) },
                      modifier = Modifier.minimumInteractiveComponentSize(),
                    ) {
                      Icon(
                        imageVector = Icons.Rounded.Bookmark,
                        contentDescription = "Remove saved company",
                        tint = SoftLavender,
                      )
                    }
                  },
                )
              }
            }
          }
        }
      }
    }
  }
}

private enum class ApplicationFilterChip(val label: String, val status: PersonalApplicationStatus?) {
  ALL("All", null),
  APPLIED("Applied", PersonalApplicationStatus.APPLIED),
  IN_PROGRESS("Application In Progress", PersonalApplicationStatus.IN_PROGRESS),
  COMPLETED("Completed", PersonalApplicationStatus.COMPLETED),
  NOT_APPLIED("Not Applied", PersonalApplicationStatus.NOT_APPLIED),
}

@Composable
fun MyApplicationsHubScreen(
  uiState: AlertsUiState,
  onNavigateBack: () -> Unit,
  onEditRegistrationClick: (AppliedJobRecord) -> Unit,
  onUpdateAppliedStage: (String, ApplicationLifecycleStage) -> Unit,
  onUpdatePersonalStatus: (String, PersonalApplicationStatus) -> Unit,
  onRemoveAppliedJob: (String) -> Unit,
  onToggleBookmark: (String) -> Unit,
  onMarkAppliedClick: (JobAlertUiModel) -> Unit,
  onApplyNowLaunched: (JobAlertUiModel) -> Unit,
  onOpenJobDetail: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var selectedFilter by rememberSaveable { mutableStateOf(ApplicationFilterChip.ALL) }

  val filteredApplied =
    when (selectedFilter) {
      ApplicationFilterChip.ALL -> uiState.appliedJobs
      ApplicationFilterChip.NOT_APPLIED -> emptyList()
      else -> uiState.appliedJobs.filter { it.personalStatus == selectedFilter.status }
    }
  val notAppliedList =
    if (selectedFilter == ApplicationFilterChip.NOT_APPLIED) uiState.notAppliedOpenJobs() else emptyList()

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    Column(modifier = Modifier.widthIn(max = 780.dp).fillMaxSize()) {
      GlassyTopBarWithBreadcrumb(
        title = "My Applications",
        breadcrumbs = listOf("Hub", "Exams & Saved", "My Applications"),
        onBackClick = onNavigateBack,
      )

      LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        items(ApplicationFilterChip.entries, key = { it.name }) { chip ->
          StadiumPill(
            label = chip.label,
            selected = chip == selectedFilter,
            onClick = { selectedFilter = chip },
            testTag = "app_filter_${chip.name}",
          )
        }
      }

      if (selectedFilter == ApplicationFilterChip.NOT_APPLIED) {
        if (notAppliedList.isEmpty()) {
          ZeroAlertsBentoCard(
            headline = "ALL OPEN ELIGIBLE JOBS TRACKED",
            subtitle = "You have already tracked every currently open eligible alert.",
          )
        } else {
          LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize().testTag("not_applied_list"),
          ) {
            items(notAppliedList, key = { it.job.id }) { item ->
              JobAlertCard(
                job = item.job,
                isEligible = item.isEligible,
                isSaved = item.isSaved,
                isApplied = item.isApplied,
                registrationNumber = item.registrationNumber,
                onToggleBookmark = { onToggleBookmark(item.job.id) },
                onMarkAppliedClick = { onMarkAppliedClick(item) },
                onApplyNowLaunched = { onApplyNowLaunched(item) },
                onCardClick = { onOpenJobDetail(item.job.id) },
              )
            }
          }
        }
      } else if (filteredApplied.isEmpty()) {
        ZeroAlertsBentoCard(
          headline = "NO APPLICATIONS IN ${selectedFilter.label.uppercase()}",
          subtitle = "Tap Mark as Applied on any open recruitment to store your Registration ID and track its 5-stage progress.",
        )
      } else {
        LazyColumn(
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier.fillMaxSize().testTag("my_applications_list"),
        ) {
          items(filteredApplied, key = { it.job.id }) { record ->
            AppliedJobBentoCard(
              record = record,
              onEditRegistrationClick = { onEditRegistrationClick(record) },
              onStageSelected = { stage -> onUpdateAppliedStage(record.job.id, stage) },
              onPersonalStatusSelected = { status -> onUpdatePersonalStatus(record.job.id, status) },
              onRemoveAppliedClick = { onRemoveAppliedJob(record.job.id) },
              onCardClick = { onOpenJobDetail(record.job.id) },
            )
          }
        }
      }
    }
  }
}

private enum class ExamTrackingTab(val label: String) {
  UPCOMING_EXAMS("Upcoming Exams"),
  EXAM_SCHEDULE("Exam Schedule"),
  EXAM_HISTORY("Examination History"),
  IMPORTANT_DATES("Important Dates"),
}

@Composable
fun ExamTrackingHubScreen(
  uiState: AlertsUiState,
  onNavigateBack: () -> Unit,
  onOpenUpdateDetail: (String) -> Unit,
  onOpenJobDetail: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var selectedTab by rememberSaveable { mutableStateOf(ExamTrackingTab.UPCOMING_EXAMS) }

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    Column(modifier = Modifier.widthIn(max = 780.dp).fillMaxSize()) {
      GlassyTopBarWithBreadcrumb(
        title = "Exam Tracking",
        breadcrumbs = listOf("Hub", "Exams & Saved", "Exam Tracking"),
        onBackClick = onNavigateBack,
      )

      LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        items(ExamTrackingTab.entries, key = { it.name }) { tab ->
          StadiumPill(
            label = tab.label,
            selected = tab == selectedTab,
            onClick = { selectedTab = tab },
            testTag = "exam_tracking_tab_${tab.name}",
          )
        }
      }

      when (selectedTab) {
        ExamTrackingTab.UPCOMING_EXAMS -> {
          val upcomingUpdates =
            uiState.examUpdates.filter {
              it.type == ExamUpdateType.ADMIT_CARD ||
                it.type == ExamUpdateType.CITY_SLIP ||
                it.type == ExamUpdateType.EXAM_DATE
            }
          LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize().testTag("tracking_upcoming_list"),
          ) {
            items(upcomingUpdates, key = { it.id }) { update ->
              GlassyDirectoryRowCard(
                eyebrow = "${update.organization} • ${update.type.badgeLabel}",
                title = update.examTitle,
                subtitle = update.summaryNote,
                badgeText = update.examDateDisplay.ifBlank { "Not Announced" },
                onClick = { onOpenUpdateDetail(update.id) },
              )
            }
          }
        }

        ExamTrackingTab.EXAM_SCHEDULE -> {
          val scheduledJobs =
            uiState.openJobsUi.filter { !uiState.eligibleOnly || it.isEligible }
          LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize().testTag("tracking_schedule_list"),
          ) {
            items(scheduledJobs, key = { it.job.id }) { item ->
              GlassyDirectoryRowCard(
                eyebrow = item.job.organization,
                title = item.job.postTitle,
                subtitle = "Last Date: ${item.job.lastDateDisplay} • Exam: ${item.job.examDateDisplay}",
                badgeText = item.job.examDateDisplay,
                onClick = { onOpenJobDetail(item.job.id) },
              )
            }
          }
        }

        ExamTrackingTab.EXAM_HISTORY -> {
          val historyItems =
            uiState.examUpdates.filter {
              it.type == ExamUpdateType.RESULT || it.type == ExamUpdateType.ANSWER_KEY
            }
          val completedApps =
            uiState.appliedJobs.filter {
              it.personalStatus == PersonalApplicationStatus.COMPLETED ||
                it.stage == ApplicationLifecycleStage.RESULT
            }
          if (historyItems.isEmpty() && completedApps.isEmpty()) {
            ZeroAlertsBentoCard(
              headline = "NO EXAMINATION HISTORY YET",
              subtitle = "Completed exams, answer keys, and declared results will appear here.",
            )
          } else {
            LazyColumn(
              contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp),
              modifier = Modifier.fillMaxSize().testTag("tracking_history_list"),
            ) {
              items(completedApps, key = { "hist_app_${it.job.id}" }) { app ->
                GlassyDirectoryRowCard(
                  eyebrow = "${app.job.organization} • ${app.personalStatus.label.uppercase()}",
                  title = app.job.postTitle,
                  subtitle = "Applied ${app.appliedDateDisplay} • Stage: ${app.stage.stepLabel}",
                  badgeText = app.stage.stepLabel.uppercase(),
                  onClick = { onOpenJobDetail(app.job.id) },
                )
              }
              items(historyItems, key = { "hist_upd_${it.id}" }) { update ->
                GlassyDirectoryRowCard(
                  eyebrow = "${update.organization} • ${update.type.badgeLabel}",
                  title = update.examTitle,
                  subtitle = update.summaryNote,
                  badgeText = update.releasedDateDisplay,
                  onClick = { onOpenUpdateDetail(update.id) },
                )
              }
            }
          }
        }

        ExamTrackingTab.IMPORTANT_DATES -> {
          val importantDeadlines: List<JobAlert> =
            uiState.openJobsUi
              .filter { !uiState.eligibleOnly || it.isEligible }
              .map { it.job }
          LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize().testTag("tracking_dates_list"),
          ) {
            items(importantDeadlines, key = { "date_${it.id}" }) { job ->
              GlassyDirectoryRowCard(
                eyebrow = "CLOSES ${job.lastDateDisplay}",
                title = "${job.organization} — ${job.postTitle}",
                subtitle = "Start: ${job.startDateDisplay} • Exam: ${job.examDateDisplay}",
                badgeText = job.countdownBadgeText(),
                onClick = { onOpenJobDetail(job.id) },
              )
            }
          }
        }
      }
    }
  }
}
