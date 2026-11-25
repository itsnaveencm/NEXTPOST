package com.example.ui.components

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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.rounded.AutoAwesome
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.JobAlert
import com.example.data.model.JobStatusTag
import com.example.data.model.OfficialSourceVerifier
import com.example.ui.theme.AlertPulseTypography
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.ElevatedBarSurface
import com.example.ui.theme.ElevatedCardSurface
import com.example.ui.theme.ElevatedCardSurfaceAlt
import com.example.ui.theme.LavenderBadgeContainer
import com.example.ui.theme.MutedText
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SkyAccent
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.StatusRed
import com.example.ui.theme.SubtleBorder
import com.example.ui.theme.SubtleBorderHighlight
import com.example.ui.theme.TitaniumSilver
import com.example.ui.theme.TitaniumSilverMuted

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JobAlertCard(
  job: JobAlert,
  isEligible: Boolean,
  isSaved: Boolean,
  onToggleBookmark: () -> Unit,
  modifier: Modifier = Modifier,
  isApplied: Boolean = false,
  registrationNumber: String? = null,
  onMarkAppliedClick: () -> Unit = {},
  onApplyNowLaunched: () -> Unit = {},
  onCardClick: (() -> Unit)? = null,
  reminderEnabled: Boolean? = null,
  onToggleReminder: ((Boolean) -> Unit)? = null,
) {
  val context = LocalContext.current
  var isBriefExpanded by rememberSaveable(job.id) { mutableStateOf(false) }
  val isSourceVerified = OfficialSourceVerifier.verifyJob(job).isVerified
  val statusTag = job.resolveStatusTag(isApplied = isApplied)
  val isOpenNow = job.isApplicationOpenNow()
  val isUpcoming = job.isUpcoming()
  val daysLeft = job.daysRemaining()
  val isUrgent = isOpenNow && daysLeft in 0..7

  val cardShape = RoundedCornerShape(18.dp)
  val cardModifier =
    if (onCardClick != null) {
      modifier.fillMaxWidth().clip(cardShape).clickable(onClick = onCardClick).testTag("job_card_${job.id}")
    } else {
      modifier.fillMaxWidth().testTag("job_card_${job.id}")
    }

  Surface(
    color = ElevatedCardSurface,
    shape = cardShape,
    border = BorderStroke(1.dp, SubtleBorder),
    modifier = cardModifier,
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f),
        ) {
          Text(
            text = job.organization,
            style = MaterialTheme.typography.labelLarge,
            color = SkyAccent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            color = if (isEligible) LavenderBadgeContainer else ElevatedCardSurfaceAlt,
            shape = RoundedCornerShape(24.dp),
            border =
              BorderStroke(
                1.dp,
                if (isEligible) SoftLavender.copy(alpha = 0.35f) else SubtleBorder,
              ),
          ) {
            Text(
              text = if (isEligible) "ELIGIBLE" else "CHECK REQ",
              style = AlertPulseTypography.BadgeMono,
              color = if (isEligible) SoftLavender else MutedText,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
              maxLines = 1,
            )
          }
        }

        IconButton(
          onClick = onToggleBookmark,
          modifier = Modifier.minimumInteractiveComponentSize().testTag("bookmark_btn_${job.id}"),
        ) {
          Icon(
            imageVector = if (isSaved) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription =
              stringResource(
                if (isSaved) R.string.cd_bookmark_remove else R.string.cd_bookmark_save
              ),
            tint = if (isSaved) SoftLavender else TitaniumSilverMuted,
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      VerifiedSourceBadge(
        officialDomain = job.officialDomain,
        isVerified = isSourceVerified,
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = job.postTitle,
        style =
          MaterialTheme.typography.titleMedium.copy(
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
          ),
        color = PrimaryText,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
      )

      Spacer(modifier = Modifier.height(10.dp))

      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        CompactBentoPill(text = job.qualificationTag, isMonospace = false)
        CompactBentoPill(text = job.vacancyCountLabel, isMonospace = true)
        CompactBentoPill(text = job.locationLabel, isMonospace = false)
      }

      Spacer(modifier = Modifier.height(14.dp))
      HorizontalDivider(color = SubtleBorder, thickness = 1.dp)
      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        when {
          isOpenNow -> ApplicationOpenBadge()
          isUpcoming ->
            Text(
              text = "UPCOMING • OPENS ${job.startDateDisplay}",
              style = AlertPulseTypography.BadgeMono,
              color = SoftLavender,
            )
          else ->
            Text(
              text = "EXPIRED",
              style = AlertPulseTypography.BadgeMono,
              color = StatusRed,
            )
        }

        DotMatrixCountdownBadge(
          text = job.countdownBadgeText(),
          isUrgent = isUrgent,
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text =
            if (isUpcoming) "OPENS: ${job.startDateDisplay}"
            else "LAST DATE: ${job.lastDateDisplay}",
          style = AlertPulseTypography.DateMono,
          color = TitaniumSilver,
          maxLines = 1,
        )

        if (!isUpcoming && isSourceVerified) {
          Surface(
            color = if (isApplied) LavenderBadgeContainer else ElevatedCardSurfaceAlt,
            shape = RoundedCornerShape(24.dp),
            border =
              BorderStroke(
                1.dp,
                if (isApplied) SoftLavender else SubtleBorder,
              ),
            modifier =
              Modifier.clip(RoundedCornerShape(24.dp))
                .clickable(onClick = onMarkAppliedClick)
                .testTag("mark_applied_btn_${job.id}"),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
              Icon(
                imageVector =
                  if (isApplied) Icons.Rounded.CheckCircle else Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = if (isApplied) SoftLavender else TitaniumSilverMuted,
                modifier = Modifier.size(14.dp),
              )
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                text =
                  if (isApplied) {
                    if (!registrationNumber.isNullOrBlank()) "APPLIED • $registrationNumber"
                    else "APPLIED"
                  } else {
                    "Mark as Applied"
                  },
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isApplied) FontWeight.Bold else FontWeight.Medium,
                color = if (isApplied) SoftLavender else TitaniumSilver,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
            }
          }
        }
      }

      if (reminderEnabled != null && onToggleReminder != null) {
        Spacer(modifier = Modifier.height(12.dp))
        Row(
          modifier =
            Modifier.fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(ElevatedBarSurface)
              .border(1.dp, SubtleBorder, RoundedCornerShape(14.dp))
              .padding(horizontal = 12.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
              imageVector =
                if (reminderEnabled) {
                  Icons.Outlined.NotificationsActive
                } else {
                  Icons.Outlined.NotificationsOff
                },
              contentDescription = null,
              tint = if (reminderEnabled) SoftLavender else MutedText,
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Deadline Reminder",
                style = MaterialTheme.typography.titleSmall,
                color = PrimaryText,
              )
              Text(
                text = if (reminderEnabled) "ALERT ACTIVE • 3D PRIOR" else "REMINDER PAUSED",
                style = AlertPulseTypography.BadgeMono,
                color = if (reminderEnabled) SoftLavender else MutedText,
              )
            }
          }
          Switch(
            checked = reminderEnabled,
            onCheckedChange = onToggleReminder,
            colors =
              SwitchDefaults.colors(
                checkedThumbColor = AmoledBlack,
                checkedTrackColor = SoftLavender,
                uncheckedThumbColor = TitaniumSilverMuted,
                uncheckedTrackColor = ElevatedCardSurfaceAlt,
                uncheckedBorderColor = SubtleBorder,
              ),
            modifier = Modifier.testTag("reminder_switch_${job.id}"),
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Column(
        modifier =
          Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isBriefExpanded) LavenderBadgeContainer.copy(alpha = 0.7f) else ElevatedBarSurface)
            .border(
              1.dp,
              if (isBriefExpanded) SubtleBorderHighlight else SubtleBorder,
              RoundedCornerShape(14.dp),
            )
            .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
      ) {
        Row(
          modifier =
            Modifier.fillMaxWidth()
              .clickable { isBriefExpanded = !isBriefExpanded }
              .padding(horizontal = 12.dp, vertical = 10.dp)
              .testTag("ai_brief_toggle_${job.id}"),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
              imageVector = Icons.Rounded.AutoAwesome,
              contentDescription = null,
              tint = SoftLavender,
              modifier = Modifier.size(15.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "AI BRIEF",
              style = AlertPulseTypography.BadgeMono,
              color = SoftLavender,
            )
            Text(
              text = " • Eligibility, Age & Selection",
              style = MaterialTheme.typography.bodySmall,
              color = SecondaryText,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
          Icon(
            imageVector =
              if (isBriefExpanded) {
                Icons.Rounded.KeyboardArrowUp
              } else {
                Icons.Rounded.KeyboardArrowDown
              },
            contentDescription = if (isBriefExpanded) "Collapse AI Brief" else "Expand AI Brief",
            tint = SoftLavender,
            modifier = Modifier.size(20.dp),
          )
        }

        if (isBriefExpanded) {
          HorizontalDivider(color = SubtleBorder, thickness = 1.dp)
          Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            AiBriefBulletRow(label = "ELIGIBILITY", value = job.aiBrief.eligibility)
            AiBriefBulletRow(label = "AGE LIMIT", value = job.aiBrief.ageLimit)
            AiBriefBulletRow(label = "SELECTION & FEE", value = job.aiBrief.selectionAndFee)
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        OutlinedButton(
          onClick = {
            if (isSourceVerified) BrowserLauncher.openOfficialUrl(context, job.officialPdfUrl)
          },
          enabled = isSourceVerified,
          shape = RoundedCornerShape(24.dp),
          border = BorderStroke(1.dp, SubtleBorder),
          colors =
            ButtonDefaults.outlinedButtonColors(
              containerColor = ElevatedBarSurface,
              contentColor = TitaniumSilver,
            ),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
          modifier =
            Modifier.weight(1f)
              .height(42.dp)
              .testTag("pdf_button_${job.id}"),
        ) {
          Icon(
            imageVector = Icons.Outlined.Description,
            contentDescription = null,
            modifier = Modifier.size(15.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = stringResource(R.string.action_official_pdf),
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
          )
        }

        if (isOpenNow && isSourceVerified) {
          Button(
            onClick = {
              onApplyNowLaunched()
              BrowserLauncher.openOfficialUrl(context, job.directApplyUrl)
            },
            shape = RoundedCornerShape(24.dp),
            colors =
              ButtonDefaults.buttonColors(
                containerColor = SoftLavender,
                contentColor = AmoledBlack,
              ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
            modifier =
              Modifier.weight(1f)
                .height(42.dp)
                .testTag("apply_button_${job.id}"),
          ) {
            Text(
              text = stringResource(R.string.action_apply_now),
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
        } else {
          OutlinedButton(
            onClick = { onCardClick?.invoke() },
            enabled = false,
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, SubtleBorder),
            colors =
              ButtonDefaults.outlinedButtonColors(
                disabledContainerColor = ElevatedCardSurfaceAlt,
                disabledContentColor = MutedText,
              ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
            modifier = Modifier.weight(1f).height(42.dp).testTag("apply_blocked_${job.id}"),
          ) {
            Text(
              text =
                when {
                  !isSourceVerified -> "Source Blocked"
                  statusTag == JobStatusTag.UPCOMING -> "Opens ${job.startDateDisplay}"
                  else -> "Window Closed"
                },
              style = MaterialTheme.typography.labelLarge,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun CompactBentoPill(
  text: String,
  isMonospace: Boolean,
) {
  Surface(
    color = ElevatedCardSurfaceAlt,
    shape = RoundedCornerShape(24.dp),
    border = BorderStroke(1.dp, SubtleBorder),
  ) {
    Text(
      text = text,
      style =
        if (isMonospace) {
          AlertPulseTypography.VacancyTabularMono
        } else {
          MaterialTheme.typography.bodySmall
        },
      color = TitaniumSilver,
      modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

@Composable
private fun AiBriefBulletRow(label: String, value: String) {
  Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
    Box(
      modifier =
        Modifier.padding(top = 5.dp)
          .size(5.dp)
          .clip(RoundedCornerShape(3.dp))
          .background(SoftLavender)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = label,
        style = AlertPulseTypography.BadgeMono,
        color = SoftLavender,
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.bodySmall,
        color = PrimaryText,
      )
    }
  }
}
