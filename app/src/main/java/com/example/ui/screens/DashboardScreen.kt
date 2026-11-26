package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.AppliedJobRecord
import com.example.data.model.ExamUpdate
import com.example.data.model.JobAlertUiModel
import com.example.ui.components.ApplicationOpenBadge
import com.example.ui.components.DotMatrixCountdownBadge
import com.example.ui.components.EligibilityStripCard
import com.example.ui.components.FeedSyncStatusBanner
import com.example.ui.components.FloatingSearchPill
import com.example.ui.components.GlassyCard
import com.example.ui.components.GlassyDirectoryRowCard
import com.example.ui.components.GlassySectionHeader
import com.example.ui.components.NextPostPullToRefreshBox
import com.example.ui.components.NextPostTopBar
import com.example.ui.components.ZeroAlertsBentoCard
import com.example.ui.theme.AlertPulseTypography
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.LavenderBadgeContainer
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SkyAccent
import com.example.ui.theme.SkyBadgeContainer
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.SubtleBorder
import com.example.ui.theme.TitaniumSilver
import com.example.ui.theme.TitaniumSilverMuted
import com.example.ui.viewmodel.AlertsUiState

@Composable
fun DashboardScreen(
  uiState: AlertsUiState,
  onSearchQueryChange: (String) -> Unit,
  onToggleEligibleOnly: () -> Unit,
  onProfileClick: () -> Unit,
  onOpenGovModule: () -> Unit,
  onOpenStateModule: () -> Unit,
  onOpenPrivateModule: () -> Unit,
  onOpenExamsSavedModule: () -> Unit,
  onOpenOrgDetail: (String) -> Unit,
  onOpenStateDetail: (String) -> Unit,
  onOpenCompanyDetail: (String) -> Unit,
  onOpenJobDetail: (String, String) -> Unit,
  onOpenExamUpdateDetail: (String, String) -> Unit,
  onOpenMyApplicationsHub: () -> Unit,
  onRefreshFeedClick: () -> Unit,
  onConfigureFeedClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val isSearching = uiState.searchQuery.isNotBlank()

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    Column(modifier = Modifier.widthIn(max = 780.dp).fillMaxSize()) {
      NextPostTopBar(onProfileClick = onProfileClick)

      FloatingSearchPill(
        query = uiState.searchQuery,
        onQueryChange = onSearchQueryChange,
        placeholder = "Search organisations, exams, states, or companies...",
      )

      if (isSearching) {
        GlobalSearchResultsList(
          uiState = uiState,
          onOpenOrgDetail = onOpenOrgDetail,
          onOpenStateDetail = onOpenStateDetail,
          onOpenCompanyDetail = onOpenCompanyDetail,
          onOpenJobDetail = { jobId -> onOpenJobDetail(jobId, "Hub → Search") },
          onOpenExamUpdateDetail = { updId -> onOpenExamUpdateDetail(updId, "Hub → Search") },
          onClearSearch = { onSearchQueryChange("") },
        )
      } else {
        NextPostPullToRefreshBox(
          isRefreshing = uiState.isSyncing,
          onRefresh = onRefreshFeedClick,
          modifier = Modifier.weight(1f).fillMaxWidth().testTag("dashboard_pull_to_refresh"),
        ) {
          LazyColumn(
            contentPadding = PaddingValues(top = 4.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize().testTag("dashboard_list"),
          ) {
          item(key = "eligibility_strip") {
            EligibilityStripCard(
              eligibleOnly = uiState.eligibleOnly,
              profile = uiState.candidateProfile,
              openCount = uiState.totalOpenEligibleCount,
              onToggleEligibleOnly = onToggleEligibleOnly,
              onEditProfileClick = onProfileClick,
            )
          }

          item(key = "feed_sync_status") {
            FeedSyncStatusBanner(
              syncMetadata = uiState.syncMetadata,
              isSyncing = uiState.isSyncing,
              onRefreshClick = onRefreshFeedClick,
              onConfigureClick = onConfigureFeedClick,
            )
          }

          item(key = "main_sections_header") {
            GlassySectionHeader(
              eyebrow = "LEVEL 1 • MAIN SECTIONS",
              title = "Recruitment Hub",
              trailingBadge = "${uiState.totalOpenEligibleCount} OPEN",
            )
          }

          item(key = "module_grid_row_1") {
            Row(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              DashboardModuleCard(
                eyebrow = "CENTRAL & ORGS",
                title = "Government Jobs",
                subtitle = "SSC, RRB, IBPS, DRDO, ISRO, UPSC & PSUs",
                countBadge = "${uiState.govOpenCount} OPEN",
                icon = Icons.Outlined.AccountBalance,
                accentColor = SkyAccent,
                onClick = onOpenGovModule,
                testTag = "module_card_gov",
                modifier = Modifier.weight(1f),
              )
              DashboardModuleCard(
                eyebrow = "28 STATES & 8 UTS",
                title = "State Government",
                subtitle = "PSC, Departments, Boards & State Drives",
                countBadge = "${uiState.stateOpenCount} OPEN",
                icon = Icons.Outlined.LocationCity,
                accentColor = SoftLavender,
                onClick = onOpenStateModule,
                testTag = "module_card_state",
                modifier = Modifier.weight(1f),
              )
            }
          }

          item(key = "module_grid_row_2") {
            Row(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              DashboardModuleCard(
                eyebrow = "8 TECH DOMAINS",
                title = "Private & MNC",
                subtitle = "IT, Embedded, R&D, Fab & Graduate Roles",
                countBadge = "${uiState.privateOpenCount} OPEN",
                icon = Icons.Outlined.WorkOutline,
                accentColor = SkyAccent,
                onClick = onOpenPrivateModule,
                testTag = "module_card_private",
                modifier = Modifier.weight(1f),
              )
              DashboardModuleCard(
                eyebrow = "TRACKER & HUB",
                title = "Exams & Saved",
                subtitle = "Updates, Bookmarks, Applied & Schedule",
                countBadge = "${uiState.appliedJobs.size} TRACKED",
                icon = Icons.Outlined.Bookmarks,
                accentColor = SoftLavender,
                onClick = onOpenExamsSavedModule,
                testTag = "module_card_exams_saved",
                modifier = Modifier.weight(1f),
              )
            }
          }

          // Curated Preview 1: Closing Soon (Open Now) — max 2 items
          item(key = "closing_soon_header") {
            Spacer(modifier = Modifier.height(4.dp))
            GlassySectionHeader(
              eyebrow = "CURATED PREVIEW • OPEN NOW",
              title = "Closing Soon",
              trailingBadge = "MAX 2",
            )
          }

          if (uiState.closingSoonPreview.isEmpty()) {
            item(key = "closing_soon_empty") {
              ZeroAlertsBentoCard(
                onEditProfileClick = onProfileClick,
                secondaryActionLabel = if (uiState.eligibleOnly) "Show All Open" else null,
                onSecondaryActionClick = if (uiState.eligibleOnly) onToggleEligibleOnly else null,
              )
            }
          } else {
            items(uiState.closingSoonPreview.take(2), key = { "preview_job_${it.job.id}" }) { item ->
              DashboardJobPreviewCard(
                item = item,
                onClick = { onOpenJobDetail(item.job.id, "Hub → Closing Soon") },
              )
            }
          }

          // Curated Preview 2: Latest Examination Updates — max 2 items
          if (uiState.latestUpdatesPreview.isNotEmpty()) {
            item(key = "latest_updates_header") {
              Spacer(modifier = Modifier.height(4.dp))
              GlassySectionHeader(
                eyebrow = "CURATED PREVIEW • EXAMS",
                title = "Latest Examination Updates",
                trailingBadge = "${uiState.examUpdates.size} LIVE",
              )
            }
            items(uiState.latestUpdatesPreview.take(2), key = { "preview_upd_${it.id}" }) { update ->
              DashboardExamUpdatePreviewCard(
                update = update,
                onClick = { onOpenExamUpdateDetail(update.id, "Hub → Latest Updates") },
              )
            }
          }

          // Curated Preview 3: My Tracked Applications — max 2 items
          if (uiState.trackedApplicationsPreview.isNotEmpty()) {
            item(key = "tracked_apps_header") {
              Spacer(modifier = Modifier.height(4.dp))
              GlassySectionHeader(
                eyebrow = "CURATED PREVIEW • LIFECYCLE",
                title = "My Tracked Applications",
                trailingBadge = "${uiState.appliedJobs.size} SAVED",
              )
            }
            items(
              uiState.trackedApplicationsPreview.take(2),
              key = { "preview_app_${it.job.id}" },
            ) { record ->
              DashboardTrackedAppPreviewCard(
                record = record,
                onClick = onOpenMyApplicationsHub,
              )
            }
          }
        }
        }
      }
    }
  }
}

@Composable
private fun DashboardModuleCard(
  eyebrow: String,
  title: String,
  subtitle: String,
  countBadge: String,
  icon: ImageVector,
  accentColor: Color,
  onClick: () -> Unit,
  testTag: String,
  modifier: Modifier = Modifier,
) {
  GlassyCard(
    onClick = onClick,
    modifier = modifier.height(152.dp).testTag(testTag),
  ) {
    Column(
      modifier = Modifier.fillMaxSize().padding(14.dp),
      verticalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(
          color = SkyBadgeContainer,
          shape = RoundedCornerShape(12.dp),
          border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f)),
          modifier = Modifier.size(36.dp),
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = icon,
              contentDescription = title,
              tint = accentColor,
              modifier = Modifier.size(18.dp),
            )
          }
        }

        Surface(
          color = LavenderBadgeContainer,
          shape = RoundedCornerShape(24.dp),
          border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f)),
        ) {
          Text(
            text = countBadge,
            style = AlertPulseTypography.BadgeMono,
            color = accentColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            maxLines = 1,
          )
        }
      }

      Column {
        Text(
          text = eyebrow,
          style = AlertPulseTypography.BadgeMono,
          color = accentColor,
          maxLines = 1,
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
          color = PrimaryText,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = SecondaryText,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }
  }
}

@Composable
private fun DashboardJobPreviewCard(
  item: JobAlertUiModel,
  onClick: () -> Unit,
) {
  val job = item.job
  GlassyCard(
    onClick = onClick,
    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).testTag("hub_job_preview_${job.id}"),
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = job.organization,
          style = MaterialTheme.typography.labelLarge,
          color = SkyAccent,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(8.dp))
        DotMatrixCountdownBadge(
          text = job.countdownBadgeText(),
          isUrgent = job.daysRemaining() in 0..7,
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = job.postTitle,
        style = MaterialTheme.typography.titleSmall,
        color = PrimaryText,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        ApplicationOpenBadge()
        Text(
          text = "LAST DATE: ${job.lastDateDisplay}",
          style = AlertPulseTypography.DateMono,
          color = TitaniumSilver,
        )
      }
    }
  }
}

@Composable
private fun DashboardExamUpdatePreviewCard(
  update: ExamUpdate,
  onClick: () -> Unit,
) {
  GlassyCard(
    onClick = onClick,
    modifier =
      Modifier.fillMaxWidth().padding(horizontal = 16.dp).testTag("hub_update_preview_${update.id}"),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "${update.organization} • ${update.type.badgeLabel}",
          style = AlertPulseTypography.BadgeMono,
          color = SoftLavender,
          maxLines = 1,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = update.examTitle,
          style = MaterialTheme.typography.titleSmall,
          color = PrimaryText,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = "Exam: ${update.examDateDisplay}",
          style = AlertPulseTypography.DateMono,
          color = SecondaryText,
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
      Icon(
        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
        contentDescription = "Open update details",
        tint = TitaniumSilverMuted,
        modifier = Modifier.size(16.dp),
      )
    }
  }
}

@Composable
private fun DashboardTrackedAppPreviewCard(
  record: AppliedJobRecord,
  onClick: () -> Unit,
) {
  GlassyCard(
    onClick = onClick,
    modifier =
      Modifier.fillMaxWidth().padding(horizontal = 16.dp).testTag("hub_tracked_preview_${record.job.id}"),
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = record.job.organization,
          style = MaterialTheme.typography.labelLarge,
          color = SkyAccent,
          maxLines = 1,
        )
        Surface(
          color = EmeraldContainer,
          shape = RoundedCornerShape(24.dp),
          border = BorderStroke(1.dp, EmeraldSoft.copy(alpha = 0.35f)),
        ) {
          Text(
            text = record.stage.stepLabel.uppercase(),
            style = AlertPulseTypography.BadgeMono,
            color = EmeraldSoft,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          )
        }
      }
      Spacer(modifier = Modifier.height(5.dp))
      Text(
        text = record.job.postTitle,
        style = MaterialTheme.typography.titleSmall,
        color = PrimaryText,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Spacer(modifier = Modifier.height(6.dp))
      HorizontalDivider(color = SubtleBorder, thickness = 1.dp)
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text =
            if (record.registrationNumber.isNotBlank()) "REG: ${record.registrationNumber}"
            else record.personalStatus.label.uppercase(),
          style = AlertPulseTypography.DateMono,
          color = TitaniumSilver,
        )
        Text(
          text = "STEP ${record.stage.stepNumber}/5",
          style = AlertPulseTypography.BadgeMono,
          color = SoftLavender,
        )
      }
    }
  }
}

@Composable
private fun GlobalSearchResultsList(
  uiState: AlertsUiState,
  onOpenOrgDetail: (String) -> Unit,
  onOpenStateDetail: (String) -> Unit,
  onOpenCompanyDetail: (String) -> Unit,
  onOpenJobDetail: (String) -> Unit,
  onOpenExamUpdateDetail: (String) -> Unit,
  onClearSearch: () -> Unit,
) {
  val results = uiState.globalSearchResults
  if (results.isEmpty) {
    ZeroAlertsBentoCard(
      headline = "NO MATCHING RECORDS",
      subtitle = "No organisations, states, companies, or open recruitments matched \"${uiState.searchQuery}\".",
      secondaryActionLabel = "Clear Search",
      onSecondaryActionClick = onClearSearch,
    )
    return
  }

  LazyColumn(
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
    modifier = Modifier.fillMaxSize().testTag("global_search_results"),
  ) {
    if (results.orgs.isNotEmpty()) {
      item {
        Text(
          text = "GOVERNMENT ORGANISATIONS (${results.orgs.size})",
          style = AlertPulseTypography.BadgeMono,
          color = SkyAccent,
        )
      }
      items(results.orgs, key = { "sr_org_${it.id}" }) { org ->
        GlassyDirectoryRowCard(
          eyebrow = org.shortName,
          title = org.fullName,
          subtitle = org.subtitle,
          badgeText = "${uiState.openJobsForOrg(org.id).size} OPEN",
          onClick = { onOpenOrgDetail(org.id) },
        )
      }
    }

    if (results.states.isNotEmpty()) {
      item {
        Text(
          text = "STATES & UNION TERRITORIES (${results.states.size})",
          style = AlertPulseTypography.BadgeMono,
          color = SoftLavender,
        )
      }
      items(results.states, key = { "sr_state_${it.state.code}" }) { st ->
        GlassyDirectoryRowCard(
          eyebrow = st.state.code,
          title = st.state.name,
          subtitle = st.state.boardShort,
          badgeText = "${st.activeAlertCount} OPEN",
          onClick = { onOpenStateDetail(st.state.code) },
        )
      }
    }

    if (results.companies.isNotEmpty()) {
      item {
        Text(
          text = "COMPANIES (${results.companies.size})",
          style = AlertPulseTypography.BadgeMono,
          color = SkyAccent,
        )
      }
      items(results.companies, key = { "sr_cmp_${it.id}" }) { company ->
        GlassyDirectoryRowCard(
          eyebrow = company.headquarters,
          title = company.name,
          subtitle = company.tagline,
          badgeText = "${uiState.openJobsForCompany(company.id).size} OPEN",
          onClick = { onOpenCompanyDetail(company.id) },
        )
      }
    }

    if (results.openJobs.isNotEmpty()) {
      item {
        Text(
          text = "OPEN RECRUITMENTS (${results.openJobs.size})",
          style = AlertPulseTypography.BadgeMono,
          color = EmeraldSoft,
        )
      }
      items(results.openJobs, key = { "sr_job_${it.job.id}" }) { item ->
        GlassyDirectoryRowCard(
          eyebrow = item.job.organization,
          title = item.job.postTitle,
          subtitle = "${item.job.qualificationTag} • Last date ${item.job.lastDateDisplay}",
          badgeText = item.job.countdownBadgeText(),
          onClick = { onOpenJobDetail(item.job.id) },
        )
      }
    }

    if (results.updates.isNotEmpty()) {
      item {
        Text(
          text = "EXAMINATION UPDATES (${results.updates.size})",
          style = AlertPulseTypography.BadgeMono,
          color = SoftLavender,
        )
      }
      items(results.updates, key = { "sr_upd_${it.id}" }) { upd ->
        GlassyDirectoryRowCard(
          eyebrow = "${upd.organization} • ${upd.type.badgeLabel}",
          title = upd.examTitle,
          subtitle = "${upd.statusLabel} • Exam: ${upd.examDateDisplay}",
          badgeText = upd.releasedDateDisplay,
          onClick = { onOpenExamUpdateDetail(upd.id) },
        )
      }
    }
  }
}
