package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
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
import com.example.data.model.RecruitmentHierarchy
import com.example.ui.components.EligibilityStripCard
import com.example.ui.components.FloatingSearchPill
import com.example.ui.components.GlassyDirectoryRowCard
import com.example.ui.components.GlassySectionHeader
import com.example.ui.components.GlassyTopBarWithBreadcrumb
import com.example.ui.components.StateDirectoryView
import com.example.ui.components.ZeroAlertsBentoCard
import com.example.ui.theme.SoftLavender
import com.example.ui.viewmodel.AlertsUiState

@Composable
fun StateGovModuleScreen(
  uiState: AlertsUiState,
  onNavigateBack: () -> Unit,
  onToggleEligibleOnly: () -> Unit,
  onEditProfileClick: () -> Unit,
  onStateClick: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var stateQuery by rememberSaveable { mutableStateOf("") }

  val filteredStates =
    if (stateQuery.isBlank()) {
      uiState.stateDirectory
    } else {
      val q = stateQuery.trim()
      uiState.stateDirectory.filter { item ->
        item.state.name.contains(q, ignoreCase = true) ||
          item.state.code.contains(q, ignoreCase = true) ||
          item.state.boardShort.contains(q, ignoreCase = true) ||
          item.state.primaryHub.contains(q, ignoreCase = true)
      }
    }

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    Column(modifier = Modifier.widthIn(max = 780.dp).fillMaxSize()) {
      GlassyTopBarWithBreadcrumb(
        title = "State Government",
        breadcrumbs = listOf("Hub", "State Government"),
        onBackClick = onNavigateBack,
      )

      FloatingSearchPill(
        query = stateQuery,
        onQueryChange = { stateQuery = it },
        placeholder = "Search 28 Indian States & 8 Union Territories...",
      )

      EligibilityStripCard(
        eligibleOnly = uiState.eligibleOnly,
        profile = uiState.candidateProfile,
        openCount = uiState.stateOpenCount,
        onToggleEligibleOnly = onToggleEligibleOnly,
        onEditProfileClick = onEditProfileClick,
        modifier = Modifier.padding(bottom = 8.dp),
      )

      StateDirectoryView(
        items = filteredStates,
        isPrivateMode = false,
        onStateClick = { onStateClick(it.state.code) },
        emptyLabel = "No State or UT matched \"$stateQuery\".",
        modifier = Modifier.weight(1f),
      )
    }
  }
}

@Composable
fun StateDetailScreen(
  stateCode: String,
  uiState: AlertsUiState,
  onNavigateBack: () -> Unit,
  onToggleEligibleOnly: () -> Unit,
  onEditProfileClick: () -> Unit,
  onSubsectionClick: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val region = IndianStatesCatalog.findByCode(stateCode)
  val stateTitle = region?.name ?: stateCode
  val availableSubsections = uiState.availableStateSubsections(stateCode)
  val totalOpenInState = availableSubsections.firstOrNull { it.first == "STATE_LEVEL" }?.second ?: 0

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    Column(modifier = Modifier.widthIn(max = 780.dp).fillMaxSize()) {
      GlassyTopBarWithBreadcrumb(
        title = stateTitle,
        breadcrumbs = listOf("Hub", "State Government", stateTitle),
        onBackClick = onNavigateBack,
      )

      LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize().testTag("state_detail_list"),
      ) {
        item(key = "state_eligibility") {
          EligibilityStripCard(
            eligibleOnly = uiState.eligibleOnly,
            profile = uiState.candidateProfile,
            openCount = totalOpenInState,
            onToggleEligibleOnly = onToggleEligibleOnly,
            onEditProfileClick = onEditProfileClick,
          )
        }

        if (availableSubsections.isEmpty()) {
          item(key = "state_empty") {
            ZeroAlertsBentoCard(
              headline = "NO ACTIVE JOB ALERTS RIGHT NOW",
              subtitle = "No active job alerts right now — We will notify you when applications open in $stateTitle.",
              onEditProfileClick = onEditProfileClick,
              secondaryActionLabel = if (uiState.eligibleOnly) "Show All Open" else null,
              onSecondaryActionClick = if (uiState.eligibleOnly) onToggleEligibleOnly else null,
            )
          }
        } else {
          item(key = "state_sub_header") {
            GlassySectionHeader(
              eyebrow = "AVAILABLE SUBSECTIONS • ${region?.boardShort ?: stateCode}",
              title = "$stateTitle Boards & Departments",
              trailingBadge = "$totalOpenInState OPEN",
            )
          }

          items(availableSubsections, key = { it.first }) { (subId, count) ->
            val subMeta = RecruitmentHierarchy.stateSubsections.firstOrNull { it.id == subId }
            if (subMeta != null) {
              GlassyDirectoryRowCard(
                eyebrow = region?.code ?: stateCode,
                title = subMeta.title,
                subtitle = subMeta.subtitle,
                badgeText = "$count OPEN",
                icon = Icons.Outlined.AccountBalance,
                accentColor = SoftLavender,
                onClick = { onSubsectionClick(subMeta.id) },
                testTag = "state_sub_${stateCode}_${subMeta.id}",
              )
            }
          }
        }
      }
    }
  }
}
