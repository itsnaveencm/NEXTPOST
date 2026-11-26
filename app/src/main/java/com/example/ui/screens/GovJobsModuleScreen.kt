package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.GovOrgGroup
import com.example.data.model.GovOrganisationNode
import com.example.data.model.RecruitmentHierarchy
import com.example.ui.components.EligibilityStripCard
import com.example.ui.components.GlassyDirectoryRowCard
import com.example.ui.components.GlassySectionHeader
import com.example.ui.components.GlassyTopBarWithBreadcrumb
import com.example.ui.components.ZeroAlertsBentoCard
import com.example.ui.theme.SkyAccent
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.TitaniumSilverMuted
import com.example.ui.viewmodel.AlertsUiState

@Composable
fun GovJobsModuleScreen(
  uiState: AlertsUiState,
  onNavigateBack: () -> Unit,
  onSetEligibleOnly: (Boolean) -> Unit,
  onEditProfileClick: () -> Unit,
  onToggleSaveOrg: (String) -> Unit,
  onOrgClick: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val centralOrgs =
    RecruitmentHierarchy.govOrganisations.filter { it.group == GovOrgGroup.CENTRAL_GOVERNMENT }
  val otherOrgs =
    RecruitmentHierarchy.govOrganisations.filter { it.group == GovOrgGroup.OTHER_GOVERNMENT }

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    Column(modifier = Modifier.widthIn(max = 780.dp).fillMaxSize()) {
      GlassyTopBarWithBreadcrumb(
        title = "Government Jobs",
        breadcrumbs = listOf("Hub", "Government Jobs"),
        onBackClick = onNavigateBack,
      )

      LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize().testTag("gov_module_list"),
      ) {
        item(key = "gov_eligibility") {
          EligibilityStripCard(
            eligibleOnly = uiState.eligibleOnly,
            profile = uiState.candidateProfile,
            allOpenCount = uiState.govAllOpenCount,
            eligibleOpenCount = uiState.govEligibleOpenCount,
            onSetEligibleOnly = onSetEligibleOnly,
            onEditProfileClick = onEditProfileClick,
            modifier = Modifier.fillMaxWidth(),
          )
        }

        item(key = "central_header") {
          GlassySectionHeader(
            eyebrow = "SECTION A",
            title = "Central Government",
            trailingBadge = "${centralOrgs.size} BOARDS",
          )
        }

        items(centralOrgs, key = { it.id }) { org ->
          GovOrgRowCard(
            org = org,
            openCount = uiState.openJobsForOrg(org.id).size,
            isSaved = org.id in uiState.savedOrgIds,
            onToggleSave = { onToggleSaveOrg(org.id) },
            onClick = { onOrgClick(org.id) },
          )
        }

        item(key = "other_header") {
          GlassySectionHeader(
            eyebrow = "SECTION B",
            title = "Other Government Organisations",
            trailingBadge = "${otherOrgs.size} BODIES",
          )
        }

        items(otherOrgs, key = { it.id }) { org ->
          GovOrgRowCard(
            org = org,
            openCount = uiState.openJobsForOrg(org.id).size,
            isSaved = org.id in uiState.savedOrgIds,
            onToggleSave = { onToggleSaveOrg(org.id) },
            onClick = { onOrgClick(org.id) },
          )
        }
      }
    }
  }
}

@Composable
private fun GovOrgRowCard(
  org: GovOrganisationNode,
  openCount: Int,
  isSaved: Boolean,
  onToggleSave: () -> Unit,
  onClick: () -> Unit,
) {
  val icon =
    when (org.id) {
      "DRDO", "ISRO", "RESEARCH" -> Icons.Outlined.Science
      "PSUS", "INSTITUTIONS" -> Icons.Outlined.Business
      else -> Icons.Outlined.AccountBalance
    }

  GlassyDirectoryRowCard(
    eyebrow = org.shortName,
    title = org.fullName,
    subtitle = org.subtitle,
    badgeText = "$openCount OPEN",
    icon = icon,
    accentColor = if (org.group == GovOrgGroup.CENTRAL_GOVERNMENT) SkyAccent else SoftLavender,
    onClick = onClick,
    testTag = "org_card_${org.id}",
    trailingAction = {
      IconButton(
        onClick = onToggleSave,
        modifier = Modifier.minimumInteractiveComponentSize().testTag("save_org_btn_${org.id}"),
      ) {
        Icon(
          imageVector = if (isSaved) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
          contentDescription = if (isSaved) "Remove saved organisation" else "Save organisation",
          tint = if (isSaved) SoftLavender else TitaniumSilverMuted,
        )
      }
    },
  )
}

@Composable
fun OrgDetailScreen(
  orgId: String,
  uiState: AlertsUiState,
  onNavigateBack: () -> Unit,
  onToggleSaveOrg: (String) -> Unit,
  onSetEligibleOnly: (Boolean) -> Unit,
  onEditProfileClick: () -> Unit,
  onSubCategoryClick: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val org = RecruitmentHierarchy.findGovOrg(orgId)
  val totalOpen = uiState.openJobsForOrg(orgId).size
  val allOpenInOrg = uiState.openJobsForOrg(orgId, eligibleOnlyOverride = false).size
  val eligibleOpenInOrg = uiState.openJobsForOrg(orgId, eligibleOnlyOverride = true).size
  val isSaved = orgId in uiState.savedOrgIds

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    Column(modifier = Modifier.widthIn(max = 780.dp).fillMaxSize()) {
      GlassyTopBarWithBreadcrumb(
        title = org?.shortName ?: orgId,
        breadcrumbs = listOf("Hub", "Government Jobs", org?.shortName ?: orgId),
        onBackClick = onNavigateBack,
        trailingContent = {
          IconButton(
            onClick = { onToggleSaveOrg(orgId) },
            modifier = Modifier.minimumInteractiveComponentSize().testTag("org_detail_save_btn"),
          ) {
            Icon(
              imageVector = if (isSaved) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
              contentDescription = if (isSaved) "Remove saved organisation" else "Save organisation",
              tint = if (isSaved) SoftLavender else TitaniumSilverMuted,
            )
          }
        },
      )

      if (org == null) {
        ZeroAlertsBentoCard(onEditProfileClick = onEditProfileClick)
      } else {
        LazyColumn(
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxSize().testTag("org_detail_list"),
        ) {
          item(key = "org_eligibility") {
            EligibilityStripCard(
              eligibleOnly = uiState.eligibleOnly,
              profile = uiState.candidateProfile,
              allOpenCount = allOpenInOrg,
              eligibleOpenCount = eligibleOpenInOrg,
              onSetEligibleOnly = onSetEligibleOnly,
              onEditProfileClick = onEditProfileClick,
            )
          }

          item(key = "org_subcategories_header") {
            GlassySectionHeader(
              eyebrow = "LEVEL 3 • ${org.shortName}",
              title = org.fullName,
              trailingBadge = "$totalOpen OPEN",
            )
          }

          items(org.subCategories, key = { it.id }) { sub ->
            val subCount = uiState.openJobsForOrg(org.id, sub.id).size
            GlassyDirectoryRowCard(
              eyebrow = org.shortName,
              title = sub.title,
              subtitle = sub.subtitle,
              badgeText = "$subCount OPEN",
              accentColor = if (subCount > 0) SkyAccent else SoftLavender,
              onClick = { onSubCategoryClick(sub.id) },
              testTag = "org_sub_${org.id}_${sub.id}",
            )
          }
        }
      }
    }
  }
}
