package com.example.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.ExamUpdateType
import com.example.data.model.JobAlertUiModel
import com.example.data.model.JobStatusTag
import com.example.ui.components.BrowserLauncher
import com.example.ui.components.DotMatrixCountdownBadge
import com.example.ui.components.GlassyCard
import com.example.ui.components.GlassyTopBarWithBreadcrumb
import com.example.ui.components.ZeroAlertsBentoCard
import com.example.ui.theme.AlertPulseTypography
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.ElevatedBarSurface
import com.example.ui.theme.ElevatedCardSurfaceAlt
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.LavenderBadgeContainer
import com.example.ui.theme.MutedText
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SkyAccent
import com.example.ui.theme.SkyBadgeContainer
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusAmberContainer
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusBlueContainer
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedContainer
import com.example.ui.theme.SubtleBorder
import com.example.ui.theme.SubtleBorderHighlight
import com.example.ui.theme.TitaniumSilver
import com.example.ui.theme.TitaniumSilverMuted
import com.example.ui.theme.UrgentRose
import com.example.ui.theme.UrgentRoseContainer
import com.example.ui.viewmodel.AlertsUiState

@Composable
fun JobDetailScreen(
  jobId: String,
  breadcrumbPrefix: String,
  uiState: AlertsUiState,
  onNavigateBack: () -> Unit,
  onToggleBookmark: (String) -> Unit,
  onMarkAppliedClick: (JobAlertUiModel) -> Unit,
  onApplyNowLaunched: (JobAlertUiModel) -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val item = uiState.findJobUi(jobId)
  var isSelectionExpanded by rememberSaveable(jobId) { mutableStateOf(true) }

  val prefixList =
    breadcrumbPrefix.split("→").map { it.trim() }.filter { it.isNotEmpty() }.ifEmpty {
      listOf("Hub")
    }

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    Column(modifier = Modifier.widthIn(max = 780.dp).fillMaxSize()) {
      GlassyTopBarWithBreadcrumb(
        title = item?.job?.organization ?: "Recruitment Details",
        breadcrumbs = prefixList + (item?.job?.postTitle ?: jobId),
        onBackClick = onNavigateBack,
      )

      if (item == null) {
        ZeroAlertsBentoCard()
      } else {
        val job = item.job
        val statusTag = job.resolveStatusTag(isApplied = item.isApplied)
        val (statusBg, statusFg) =
          when (statusTag) {
            JobStatusTag.ACTIVE -> EmeraldContainer to EmeraldSoft
            JobStatusTag.CLOSING_SOON -> StatusAmberContainer to StatusAmber
            JobStatusTag.EXPIRED -> StatusRedContainer to StatusRed
            JobStatusTag.APPLIED -> StatusBlueContainer to StatusBlue
          }

        Column(
          modifier =
            Modifier.weight(1f)
              .verticalScroll(rememberScrollState())
              .padding(horizontal = 16.dp, vertical = 14.dp)
              .testTag("job_detail_scroll"),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          // 1. Overview Card
          GlassyCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Surface(
                  color = statusBg,
                  shape = RoundedCornerShape(24.dp),
                  border = BorderStroke(1.dp, statusFg.copy(alpha = 0.45f)),
                ) {
                  Text(
                    text = statusTag.label,
                    style = AlertPulseTypography.BadgeMono,
                    color = statusFg,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                  )
                }

                DotMatrixCountdownBadge(
                  text = job.countdownBadgeText(),
                  isUrgent = job.daysRemaining() in 0..7,
                )
              }

              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = job.organization,
                style = MaterialTheme.typography.labelLarge,
                color = SkyAccent,
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = job.postTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
              )
              Spacer(modifier = Modifier.height(12.dp))
              HorizontalDivider(color = SubtleBorder, thickness = 1.dp)
              Spacer(modifier = Modifier.height(12.dp))

              DetailKeyValueRow("VACANCIES", job.vacancyCountLabel)
              Spacer(modifier = Modifier.height(8.dp))
              DetailKeyValueRow("LOCATION", job.locationLabel)
              Spacer(modifier = Modifier.height(8.dp))
              DetailKeyValueRow("EXPERIENCE", job.experienceRequired)
              if (!item.registrationNumber.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                DetailKeyValueRow("REGISTRATION ID", item.registrationNumber)
              }
            }
          }

          // 2. Eligibility & Pay Card
          GlassyCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = "ELIGIBILITY & PAY",
                style = AlertPulseTypography.BadgeMono,
                color = SkyAccent,
              )
              Spacer(modifier = Modifier.height(10.dp))
              DetailKeyValueRow(
                "QUALIFICATION",
                "${job.qualificationTag} (${if (item.isEligible) "Matches Your Profile" else "Check Criteria"})",
              )
              Spacer(modifier = Modifier.height(8.dp))
              DetailKeyValueRow("AGE LIMIT", job.aiBrief.ageLimit)
              Spacer(modifier = Modifier.height(8.dp))
              DetailKeyValueRow("SALARY / PAY SCALE", job.salaryPayScale)
            }
          }

          // 3. Important Dates Card
          GlassyCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = "IMPORTANT DATES",
                style = AlertPulseTypography.BadgeMono,
                color = SoftLavender,
              )
              Spacer(modifier = Modifier.height(10.dp))
              DetailKeyValueRow("APPLICATION START", job.startDateDisplay)
              Spacer(modifier = Modifier.height(8.dp))
              DetailKeyValueRow("LAST DATE TO APPLY", job.lastDateDisplay)
              Spacer(modifier = Modifier.height(8.dp))
              DetailKeyValueRow("EXAM DATE", job.examDateDisplay.ifBlank { "Not Announced" })
            }
          }

          // 4. Expandable Selection Process & Exam Details Panel
          Column(
            modifier =
              Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                  if (isSelectionExpanded) LavenderBadgeContainer.copy(alpha = 0.6f)
                  else ElevatedBarSurface
                )
                .border(
                  1.dp,
                  if (isSelectionExpanded) SubtleBorderHighlight else SubtleBorder,
                  RoundedCornerShape(18.dp),
                )
                .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
          ) {
            Row(
              modifier =
                Modifier.fillMaxWidth()
                  .clickable { isSelectionExpanded = !isSelectionExpanded }
                  .padding(horizontal = 16.dp, vertical = 14.dp)
                  .testTag("detail_selection_toggle"),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = "SELECTION PROCESS & EXAM DETAILS",
                style = AlertPulseTypography.BadgeMono,
                color = SoftLavender,
              )
              Icon(
                imageVector =
                  if (isSelectionExpanded) {
                    Icons.Rounded.KeyboardArrowUp
                  } else {
                    Icons.Rounded.KeyboardArrowDown
                  },
                contentDescription = "Toggle selection process",
                tint = SoftLavender,
              )
            }

            if (isSelectionExpanded) {
              HorizontalDivider(color = SubtleBorder, thickness = 1.dp)
              Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                DetailKeyValueRow("ELIGIBILITY BRIEF", job.aiBrief.eligibility)
                DetailKeyValueRow("AGE & RELAXATION", job.aiBrief.ageLimit)
                DetailKeyValueRow("SELECTION & FEE", job.aiBrief.selectionAndFee)
              }
            }
          }
        }

        // Bottom Sticky Action Bar
        Surface(color = ElevatedBarSurface, modifier = Modifier.fillMaxWidth()) {
          Column {
            HorizontalDivider(color = SubtleBorder, thickness = 1.dp)
            Row(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              OutlinedButton(
                onClick = { BrowserLauncher.openOfficialUrl(context, job.officialPdfUrl) },
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, SubtleBorder),
                colors =
                  ButtonDefaults.outlinedButtonColors(
                    containerColor = ElevatedCardSurfaceAlt,
                    contentColor = TitaniumSilver,
                  ),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                modifier = Modifier.height(44.dp).testTag("detail_pdf_btn"),
              ) {
                Icon(
                  imageVector = Icons.Outlined.Description,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("PDF", style = MaterialTheme.typography.labelLarge, maxLines = 1)
              }

              IconButton(
                onClick = { onToggleBookmark(job.id) },
                modifier =
                  Modifier.minimumInteractiveComponentSize().testTag("detail_save_btn"),
              ) {
                Icon(
                  imageVector =
                    if (item.isSaved) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                  contentDescription = if (item.isSaved) "Saved" else "Save",
                  tint = if (item.isSaved) SoftLavender else TitaniumSilverMuted,
                )
              }

              IconButton(
                onClick = { onMarkAppliedClick(item) },
                modifier =
                  Modifier.minimumInteractiveComponentSize().testTag("detail_mark_applied_btn"),
              ) {
                Icon(
                  imageVector =
                    if (item.isApplied) Icons.Rounded.CheckCircle else Icons.Outlined.CheckCircle,
                  contentDescription = "Mark as Applied",
                  tint = if (item.isApplied) EmeraldSoft else TitaniumSilverMuted,
                )
              }

              Button(
                onClick = {
                  onApplyNowLaunched(item)
                  BrowserLauncher.openOfficialUrl(context, job.directApplyUrl)
                },
                shape = RoundedCornerShape(24.dp),
                colors =
                  ButtonDefaults.buttonColors(
                    containerColor = SoftLavender,
                    contentColor = AmoledBlack,
                  ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                modifier = Modifier.weight(1f).height(44.dp).testTag("detail_apply_btn"),
              ) {
                Text(
                  text = "Apply Now ↗",
                  style = MaterialTheme.typography.labelLarge,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1,
                )
                Spacer(modifier = Modifier.width(4.dp))
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
    }
  }
}

@Composable
fun ExamUpdateDetailScreen(
  updateId: String,
  breadcrumbPrefix: String,
  uiState: AlertsUiState,
  onNavigateBack: () -> Unit,
  onToggleSavedNotice: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val update = uiState.findUpdate(updateId)
  val isSaved = updateId in uiState.savedNotificationIds
  val prefixList =
    breadcrumbPrefix.split("→").map { it.trim() }.filter { it.isNotEmpty() }.ifEmpty {
      listOf("Hub")
    }

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    Column(modifier = Modifier.widthIn(max = 780.dp).fillMaxSize()) {
      GlassyTopBarWithBreadcrumb(
        title = update?.organization ?: "Exam Update",
        breadcrumbs = prefixList + (update?.examTitle ?: updateId),
        onBackClick = onNavigateBack,
        trailingContent = {
          IconButton(
            onClick = { onToggleSavedNotice(updateId) },
            modifier =
              Modifier.minimumInteractiveComponentSize().testTag("update_detail_save_btn"),
          ) {
            Icon(
              imageVector = if (isSaved) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
              contentDescription = if (isSaved) "Remove saved notice" else "Save notice",
              tint = if (isSaved) SoftLavender else TitaniumSilverMuted,
            )
          }
        },
      )

      if (update == null) {
        ZeroAlertsBentoCard()
      } else {
        val (badgeContainer, badgeFg) =
          when (update.type) {
            ExamUpdateType.ADMIT_CARD -> UrgentRoseContainer to UrgentRose
            ExamUpdateType.CITY_SLIP,
            ExamUpdateType.NOTIFICATION -> LavenderBadgeContainer to SoftLavender
            ExamUpdateType.ANSWER_KEY,
            ExamUpdateType.EXAM_DATE -> SkyBadgeContainer to SkyAccent
            ExamUpdateType.RESULT -> EmeraldContainer to EmeraldSoft
            ExamUpdateType.DEADLINE -> StatusAmberContainer to StatusAmber
          }

        Column(
          modifier =
            Modifier.weight(1f)
              .verticalScroll(rememberScrollState())
              .padding(horizontal = 16.dp, vertical = 14.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          GlassyCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Surface(
                  color = badgeContainer,
                  shape = RoundedCornerShape(24.dp),
                  border = BorderStroke(1.dp, badgeFg.copy(alpha = 0.4f)),
                ) {
                  Text(
                    text = update.type.badgeLabel,
                    style = AlertPulseTypography.BadgeMono,
                    color = badgeFg,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                  )
                }
                Text(
                  text = update.statusLabel,
                  style = AlertPulseTypography.DateMono,
                  color = badgeFg,
                )
              }

              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = update.organization,
                style = MaterialTheme.typography.labelLarge,
                color = SkyAccent,
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = update.examTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
              )
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = update.summaryNote,
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText,
              )
              Spacer(modifier = Modifier.height(14.dp))
              HorizontalDivider(color = SubtleBorder, thickness = 1.dp)
              Spacer(modifier = Modifier.height(12.dp))

              DetailKeyValueRow("PUBLICATION DATE", update.releasedDateDisplay)
              Spacer(modifier = Modifier.height(8.dp))
              DetailKeyValueRow("EXAM DATE", update.examDateDisplay.ifBlank { "Not Announced" })
              Spacer(modifier = Modifier.height(8.dp))
              DetailKeyValueRow("STATUS", update.statusLabel)
            }
          }
        }

        Surface(color = ElevatedBarSurface, modifier = Modifier.fillMaxWidth()) {
          Column {
            HorizontalDivider(color = SubtleBorder, thickness = 1.dp)
            Row(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              if (!update.noticePdfUrl.isNullOrBlank()) {
                OutlinedButton(
                  onClick = { BrowserLauncher.openOfficialUrl(context, update.noticePdfUrl) },
                  shape = RoundedCornerShape(24.dp),
                  border = BorderStroke(1.dp, SubtleBorder),
                  colors =
                    ButtonDefaults.outlinedButtonColors(
                      containerColor = ElevatedCardSurfaceAlt,
                      contentColor = TitaniumSilver,
                    ),
                  modifier = Modifier.weight(1f).height(44.dp),
                ) {
                  Text("Official PDF", style = MaterialTheme.typography.labelLarge, maxLines = 1)
                }
              }

              Button(
                onClick = { BrowserLauncher.openOfficialUrl(context, update.portalLoginUrl) },
                shape = RoundedCornerShape(24.dp),
                colors =
                  ButtonDefaults.buttonColors(
                    containerColor = SoftLavender,
                    contentColor = AmoledBlack,
                  ),
                modifier = Modifier.weight(1f).height(44.dp),
              ) {
                Text(
                  text = "Official Source Link ↗",
                  style = MaterialTheme.typography.labelLarge,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1,
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
private fun DetailKeyValueRow(label: String, value: String) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = label,
      style = AlertPulseTypography.BadgeMono,
      color = MutedText,
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium,
      color = PrimaryText,
    )
  }
}
