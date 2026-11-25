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
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.PrecisionManufacturing
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.JobAlertUiModel
import com.example.data.model.RecruitmentHierarchy
import com.example.ui.components.BrowserLauncher
import com.example.ui.components.EligibilityStripCard
import com.example.ui.components.GlassyCard
import com.example.ui.components.GlassyDirectoryRowCard
import com.example.ui.components.GlassySectionHeader
import com.example.ui.components.GlassyTopBarWithBreadcrumb
import com.example.ui.components.JobAlertCard
import com.example.ui.components.ZeroAlertsBentoCard
import com.example.ui.theme.AlertPulseTypography
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SkyAccent
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.TitaniumSilverMuted
import com.example.ui.viewmodel.AlertsUiState

@Composable
fun PrivateModuleScreen(
  uiState: AlertsUiState,
  onNavigateBack: () -> Unit,
  onToggleEligibleOnly: () -> Unit,
  onEditProfileClick: () -> Unit,
  onDomainClick: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    Column(modifier = Modifier.widthIn(max = 780.dp).fillMaxSize()) {
      GlassyTopBarWithBreadcrumb(
        title = "Private & MNC",
        breadcrumbs = listOf("Hub", "Private & MNC"),
        onBackClick = onNavigateBack,
      )

      LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize().testTag("private_domains_list"),
      ) {
        item(key = "private_eligibility") {
          EligibilityStripCard(
            eligibleOnly = uiState.eligibleOnly,
            profile = uiState.candidateProfile,
            openCount = uiState.privateOpenCount,
            onToggleEligibleOnly = onToggleEligibleOnly,
            onEditProfileClick = onEditProfileClick,
          )
        }

        item(key = "private_domain_header") {
          GlassySectionHeader(
            eyebrow = "LEVEL 2 • 8 SPECIALIZED DOMAINS",
            title = "Select Industry Domain",
            trailingBadge = "${uiState.privateOpenCount} OPEN",
          )
        }

        items(RecruitmentHierarchy.privateDomains, key = { it.id }) { domain ->
          val openCount = uiState.openCountForDomain(domain)
          val icon =
            when (domain.id) {
              "ELECTRONICS_EMBEDDED", "RND" -> Icons.Outlined.Memory
              "MANUFACTURING", "CORE_ENGG" -> Icons.Outlined.PrecisionManufacturing
              "GRAD_TRAINEE", "INTERNSHIP" -> Icons.Outlined.School
              else -> Icons.Outlined.WorkOutline
            }
          GlassyDirectoryRowCard(
            eyebrow = domain.id.replace("_", " "),
            title = domain.title,
            subtitle = domain.subtitle,
            badgeText = "$openCount OPEN",
            icon = icon,
            accentColor = if (openCount > 0) SkyAccent else SoftLavender,
            onClick = { onDomainClick(domain.id) },
            testTag = "private_domain_${domain.id}",
          )
        }
      }
    }
  }
}

@Composable
fun PrivateDomainScreen(
  domainId: String,
  uiState: AlertsUiState,
  onNavigateBack: () -> Unit,
  onToggleSaveCompany: (String) -> Unit,
  onCompanyClick: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val domain = RecruitmentHierarchy.findPrivateDomain(domainId)
  val domainTitle = domain?.title ?: domainId
  val companies =
    RecruitmentHierarchy.privateCompanies.filter { domainId in it.domainIds }

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    Column(modifier = Modifier.widthIn(max = 780.dp).fillMaxSize()) {
      GlassyTopBarWithBreadcrumb(
        title = domainTitle,
        breadcrumbs = listOf("Hub", "Private & MNC", domainTitle),
        onBackClick = onNavigateBack,
      )

      if (companies.isEmpty()) {
        ZeroAlertsBentoCard()
      } else {
        LazyColumn(
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxSize().testTag("private_company_directory"),
        ) {
          item(key = "domain_companies_header") {
            GlassySectionHeader(
              eyebrow = "COMPANIES IN DOMAIN",
              title = domainTitle,
              trailingBadge = "${companies.size} COMPANIES",
            )
          }

          items(companies, key = { it.id }) { company ->
            val openRoles = uiState.openJobsForCompany(company.id).size
            val isSaved = company.id in uiState.savedOrgIds
            GlassyDirectoryRowCard(
              eyebrow = company.headquarters,
              title = company.name,
              subtitle = company.tagline,
              badgeText = "$openRoles OPEN",
              icon = Icons.Outlined.Business,
              accentColor = if (openRoles > 0) SkyAccent else SoftLavender,
              onClick = { onCompanyClick(company.id) },
              testTag = "company_card_${company.id}",
              trailingAction = {
                IconButton(
                  onClick = { onToggleSaveCompany(company.id) },
                  modifier =
                    Modifier.minimumInteractiveComponentSize()
                      .testTag("save_company_btn_${company.id}"),
                ) {
                  Icon(
                    imageVector =
                      if (isSaved) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription =
                      if (isSaved) "Remove saved company" else "Save company",
                    tint = if (isSaved) SoftLavender else TitaniumSilverMuted,
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

@Composable
fun CompanyDetailScreen(
  companyId: String,
  uiState: AlertsUiState,
  onNavigateBack: () -> Unit,
  onToggleSaveCompany: (String) -> Unit,
  onToggleEligibleOnly: () -> Unit,
  onEditProfileClick: () -> Unit,
  onToggleBookmark: (String) -> Unit,
  onMarkAppliedClick: (JobAlertUiModel) -> Unit,
  onApplyNowLaunched: (JobAlertUiModel) -> Unit,
  onOpenJobDetail: (String, String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val company = RecruitmentHierarchy.findPrivateCompany(companyId)
  val companyTitle = company?.name ?: companyId
  val openJobs = uiState.openJobsForCompany(companyId)
  val isSaved = companyId in uiState.savedOrgIds

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    Column(modifier = Modifier.widthIn(max = 780.dp).fillMaxSize()) {
      GlassyTopBarWithBreadcrumb(
        title = companyTitle,
        breadcrumbs = listOf("Hub", "Private & MNC", companyTitle),
        onBackClick = onNavigateBack,
        trailingContent = {
          IconButton(
            onClick = { onToggleSaveCompany(companyId) },
            modifier = Modifier.minimumInteractiveComponentSize().testTag("company_detail_save_btn"),
          ) {
            Icon(
              imageVector = if (isSaved) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
              contentDescription = if (isSaved) "Remove saved company" else "Save company",
              tint = if (isSaved) SoftLavender else TitaniumSilverMuted,
            )
          }
        },
      )

      LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize().testTag("company_detail_list"),
      ) {
        if (company != null) {
          item(key = "company_summary") {
            GlassyCard(modifier = Modifier.fillMaxWidth()) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(
                  text = "COMPANY PROFILE",
                  style = AlertPulseTypography.BadgeMono,
                  color = SkyAccent,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = company.name,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = PrimaryText,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = company.tagline,
                  style = MaterialTheme.typography.bodyMedium,
                  color = SecondaryText,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = null,
                    tint = SoftLavender,
                    modifier = Modifier.size(16.dp),
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Hub: ${company.headquarters}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PrimaryText,
                  )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                  onClick = { BrowserLauncher.openOfficialUrl(context, company.careerPortalUrl) },
                  shape = RoundedCornerShape(24.dp),
                  colors =
                    ButtonDefaults.buttonColors(
                      containerColor = SoftLavender,
                      contentColor = AmoledBlack,
                    ),
                  modifier = Modifier.fillMaxWidth().height(42.dp).testTag("company_portal_btn"),
                ) {
                  Text(
                    text = "Official Career Portal",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Icon(
                    imageVector = Icons.Rounded.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                  )
                }
              }
            }
          }
        }

        item(key = "company_eligibility") {
          EligibilityStripCard(
            eligibleOnly = uiState.eligibleOnly,
            profile = uiState.candidateProfile,
            openCount = openJobs.size,
            onToggleEligibleOnly = onToggleEligibleOnly,
            onEditProfileClick = onEditProfileClick,
          )
        }

        if (openJobs.isEmpty()) {
          item(key = "company_empty") {
            ZeroAlertsBentoCard(
              onEditProfileClick = onEditProfileClick,
              secondaryActionLabel = if (uiState.eligibleOnly) "Show All Open" else null,
              onSecondaryActionClick = if (uiState.eligibleOnly) onToggleEligibleOnly else null,
            )
          }
        } else {
          items(openJobs, key = { it.job.id }) { item ->
            JobAlertCard(
              job = item.job,
              isEligible = item.isEligible,
              isSaved = item.isSaved,
              isApplied = item.isApplied,
              registrationNumber = item.registrationNumber,
              onToggleBookmark = { onToggleBookmark(item.job.id) },
              onMarkAppliedClick = { onMarkAppliedClick(item) },
              onApplyNowLaunched = { onApplyNowLaunched(item) },
              onCardClick = {
                onOpenJobDetail(item.job.id, "Hub → Private & MNC → $companyTitle")
              },
            )
          }
        }
      }
    }
  }
}
