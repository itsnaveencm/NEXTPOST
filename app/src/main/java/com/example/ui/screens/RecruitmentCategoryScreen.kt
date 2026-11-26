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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.IndianStatesCatalog
import com.example.data.model.JobAlertUiModel
import com.example.data.model.JobWindowFilter
import com.example.data.model.RecruitmentHierarchy
import com.example.data.model.StateQualificationTier
import com.example.ui.components.EligibilityStripCard
import com.example.ui.components.GlassyTopBarWithBreadcrumb
import com.example.ui.components.JobAlertCard
import com.example.ui.components.NextPostPullToRefreshBox
import com.example.ui.components.StadiumPill
import com.example.ui.components.WindowFilterStrip
import com.example.ui.components.ZeroAlertsBentoCard
import com.example.ui.viewmodel.AlertsUiState

@Composable
fun RecruitmentCategoryScreen(
  parentId: String,
  categoryId: String,
  uiState: AlertsUiState,
  onNavigateBack: () -> Unit,
  onToggleEligibleOnly: () -> Unit,
  onSelectWindowFilter: (JobWindowFilter) -> Unit,
  onEditProfileClick: () -> Unit,
  onToggleBookmark: (String) -> Unit,
  onMarkAppliedClick: (JobAlertUiModel) -> Unit,
  onApplyNowLaunched: (JobAlertUiModel) -> Unit,
  onOpenJobDetail: (String, String) -> Unit,
  onRefreshFeedClick: () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  val isStateParent = parentId.startsWith("state:")
  var selectedTier by rememberSaveable(parentId, categoryId) {
    mutableStateOf(StateQualificationTier.ALL)
  }

  val breadcrumbs: List<String>
  val screenTitle: String
  val filteredJobs: List<JobAlertUiModel>

  if (isStateParent) {
    val stateCode = parentId.removePrefix("state:")
    val stateName = IndianStatesCatalog.findByCode(stateCode)?.name ?: stateCode
    val subTitle =
      RecruitmentHierarchy.stateSubsections.firstOrNull { it.id == categoryId }?.title ?: categoryId
    breadcrumbs = listOf("Hub", "State Gov", stateName, subTitle)
    screenTitle = "$stateName • $subTitle"
    filteredJobs = uiState.jobsForStateSubsection(stateCode, categoryId, selectedTier)
  } else {
    val orgId = parentId.removePrefix("org:")
    val org = RecruitmentHierarchy.findGovOrg(orgId)
    val orgShort = org?.shortName ?: orgId
    val subTitle =
      org?.subCategories?.firstOrNull { it.id == categoryId }?.title ?: categoryId
    breadcrumbs = listOf("Hub", "Gov Jobs", orgShort, subTitle)
    screenTitle = "$orgShort • $subTitle"
    filteredJobs = uiState.jobsForOrg(orgId, categoryId)
  }

  val breadcrumbPrefix = breadcrumbs.joinToString(" → ")

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    Column(modifier = Modifier.widthIn(max = 780.dp).fillMaxSize()) {
      GlassyTopBarWithBreadcrumb(
        title = screenTitle,
        breadcrumbs = breadcrumbs,
        onBackClick = onNavigateBack,
      )

      WindowFilterStrip(
        selected = uiState.windowFilter,
        onSelect = onSelectWindowFilter,
      )

      if (isStateParent) {
        LazyRow(
          modifier = Modifier.fillMaxWidth(),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          items(StateQualificationTier.entries, key = { it.name }) { tier ->
            StadiumPill(
              label = tier.label,
              selected = tier == selectedTier,
              onClick = { selectedTier = tier },
              testTag = "state_tier_${tier.name}",
            )
          }
        }
      }

      NextPostPullToRefreshBox(
        isRefreshing = uiState.isSyncing,
        onRefresh = onRefreshFeedClick,
        modifier = Modifier.weight(1f).fillMaxWidth().testTag("recruitment_pull_to_refresh"),
      ) {
        LazyColumn(
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier.fillMaxSize().testTag("recruitment_category_list"),
        ) {
        item(key = "cat_eligibility") {
          EligibilityStripCard(
            eligibleOnly = uiState.eligibleOnly,
            profile = uiState.candidateProfile,
            openCount = filteredJobs.size,
            onToggleEligibleOnly = onToggleEligibleOnly,
            onEditProfileClick = onEditProfileClick,
            modifier = Modifier.padding(bottom = 2.dp),
          )
        }

        if (filteredJobs.isEmpty()) {
          item(key = "cat_empty") {
            ZeroAlertsBentoCard(
              headline =
                if (uiState.windowFilter == JobWindowFilter.UPCOMING) {
                  "NO VERIFIED UPCOMING NOTIFICATIONS"
                } else {
                  "NO VERIFIED ACTIVE OPENINGS"
                },
              subtitle =
                if (uiState.windowFilter == JobWindowFilter.UPCOMING) {
                  "No verified upcoming openings scheduled yet — Connected to official portals"
                } else {
                  "No verified active openings right now — Connected to official portals"
                },
              onEditProfileClick = onEditProfileClick,
              secondaryActionLabel = if (uiState.eligibleOnly) "Show All Open" else null,
              onSecondaryActionClick = if (uiState.eligibleOnly) onToggleEligibleOnly else null,
            )
          }
        } else {
          items(filteredJobs, key = { it.job.id }) { item ->
            JobAlertCard(
              job = item.job,
              isEligible = item.isEligible,
              isSaved = item.isSaved,
              isApplied = item.isApplied,
              registrationNumber = item.registrationNumber,
              onToggleBookmark = { onToggleBookmark(item.job.id) },
              onMarkAppliedClick = { onMarkAppliedClick(item) },
              onApplyNowLaunched = { onApplyNowLaunched(item) },
              onCardClick = { onOpenJobDetail(item.job.id, breadcrumbPrefix) },
            )
          }
        }
        }
      }
    }
  }
}
