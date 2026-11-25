package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ApplicationLifecycleStage
import com.example.data.model.AppliedJobRecord
import com.example.data.model.PersonalApplicationStatus
import com.example.ui.theme.AlertPulseTypography
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.ElevatedBarSurface
import com.example.ui.theme.ElevatedCardSurface
import com.example.ui.theme.ElevatedCardSurfaceAlt
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.LavenderBadgeContainer
import com.example.ui.theme.MutedText
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SkyAccent
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.SubtleBorder
import com.example.ui.theme.TitaniumSilver
import com.example.ui.theme.TitaniumSilverMuted

@Composable
fun AppliedJobBentoCard(
  record: AppliedJobRecord,
  onEditRegistrationClick: () -> Unit,
  onStageSelected: (ApplicationLifecycleStage) -> Unit,
  onRemoveAppliedClick: () -> Unit,
  modifier: Modifier = Modifier,
  onPersonalStatusSelected: ((PersonalApplicationStatus) -> Unit)? = null,
  onCardClick: (() -> Unit)? = null,
) {
  val context = LocalContext.current
  val hasRegId = record.registrationNumber.isNotBlank()

  val (bannerContainer, bannerBorder, bannerContent) =
    when (record.stage) {
      ApplicationLifecycleStage.ADMIT_CARD,
      ApplicationLifecycleStage.RESULT ->
        Triple(EmeraldContainer, EmeraldSoft.copy(alpha = 0.35f), EmeraldSoft)
      ApplicationLifecycleStage.CITY_SLIP,
      ApplicationLifecycleStage.ANSWER_KEY ->
        Triple(LavenderBadgeContainer, SoftLavender.copy(alpha = 0.35f), SoftLavender)
      ApplicationLifecycleStage.APPLIED ->
        Triple(ElevatedCardSurfaceAlt, SubtleBorder, TitaniumSilver)
    }

  val cardShape = RoundedCornerShape(18.dp)
  val cardModifier =
    if (onCardClick != null) {
      modifier.fillMaxWidth().clip(cardShape).clickable(onClick = onCardClick).testTag("applied_card_${record.job.id}")
    } else {
      modifier.fillMaxWidth().testTag("applied_card_${record.job.id}")
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = record.job.organization,
            style = MaterialTheme.typography.labelLarge,
            color = SkyAccent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "APPLIED ${record.appliedDateDisplay}",
            style = AlertPulseTypography.DateMono,
            color = SecondaryText,
            maxLines = 1,
          )
        }

        IconButton(
          onClick = onRemoveAppliedClick,
          modifier =
            Modifier.minimumInteractiveComponentSize().testTag("remove_applied_${record.job.id}"),
        ) {
          Icon(
            imageVector = Icons.Outlined.DeleteOutline,
            contentDescription = "Remove from applied tracker",
            tint = TitaniumSilverMuted,
            modifier = Modifier.size(19.dp),
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = record.job.postTitle,
        style =
          MaterialTheme.typography.titleMedium.copy(
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
          ),
        color = PrimaryText,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
      )

      if (onPersonalStatusSelected != null) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          listOf(
            PersonalApplicationStatus.APPLIED,
            PersonalApplicationStatus.IN_PROGRESS,
            PersonalApplicationStatus.COMPLETED,
          ).forEach { status ->
            val selected = record.personalStatus == status
            Surface(
              color = if (selected) SoftLavender else ElevatedCardSurfaceAlt,
              shape = RoundedCornerShape(24.dp),
              border = BorderStroke(1.dp, if (selected) SoftLavender else SubtleBorder),
              modifier =
                Modifier.clip(RoundedCornerShape(24.dp))
                  .clickable { onPersonalStatusSelected(status) }
                  .testTag("personal_status_${record.job.id}_${status.name}"),
            ) {
              Text(
                text = status.label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) AmoledBlack else TitaniumSilver,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                maxLines = 1,
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier =
          Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ElevatedBarSurface)
            .border(1.dp, SubtleBorder, RoundedCornerShape(14.dp))
            .padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "REGISTRATION / APPLICATION ID",
            style = AlertPulseTypography.BadgeMono,
            color = MutedText,
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = if (hasRegId) record.registrationNumber else "NOT ADDED — TAP EDIT",
            style = AlertPulseTypography.DateMono,
            color = if (hasRegId) PrimaryText else SecondaryText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (hasRegId) {
            IconButton(
              onClick = {
                val clipboard =
                  context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(
                  ClipData.newPlainText("Registration ID", record.registrationNumber)
                )
                Toast.makeText(
                    context,
                    "Copied ${record.registrationNumber}",
                    Toast.LENGTH_SHORT,
                  )
                  .show()
              },
              modifier =
                Modifier.minimumInteractiveComponentSize().testTag("copy_reg_${record.job.id}"),
            ) {
              Icon(
                imageVector = Icons.Rounded.ContentCopy,
                contentDescription = "Copy Registration ID",
                tint = SoftLavender,
                modifier = Modifier.size(17.dp),
              )
            }
          }

          IconButton(
            onClick = onEditRegistrationClick,
            modifier =
              Modifier.minimumInteractiveComponentSize().testTag("edit_reg_${record.job.id}"),
          ) {
            Icon(
              imageVector = Icons.Outlined.Edit,
              contentDescription = "Edit Registration ID",
              tint = TitaniumSilver,
              modifier = Modifier.size(17.dp),
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))
      HorizontalDivider(color = SubtleBorder, thickness = 1.dp)
      Spacer(modifier = Modifier.height(14.dp))

      LifecycleFiveStageStepper(
        currentStage = record.stage,
        onStageClick = onStageSelected,
      )

      Spacer(modifier = Modifier.height(14.dp))

      Surface(
        color = bannerContainer,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, bannerBorder),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Canvas(modifier = Modifier.size(6.dp)) { drawCircle(color = bannerContent) }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = record.stage.liveBannerText,
              style = AlertPulseTypography.BadgeMono,
              color = bannerContent,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
          if (!record.statusNote.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = record.statusNote,
              style = MaterialTheme.typography.bodySmall,
              color = TitaniumSilver,
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Button(
        onClick = { BrowserLauncher.openOfficialUrl(context, record.portalUrl) },
        shape = RoundedCornerShape(24.dp),
        colors =
          ButtonDefaults.buttonColors(
            containerColor = SoftLavender,
            contentColor = AmoledBlack,
          ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        modifier =
          Modifier.fillMaxWidth()
            .height(42.dp)
            .testTag("applied_portal_btn_${record.job.id}"),
      ) {
        Text(
          text = record.stage.actionButtonLabel,
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
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

@Composable
private fun LifecycleFiveStageStepper(
  currentStage: ApplicationLifecycleStage,
  onStageClick: (ApplicationLifecycleStage) -> Unit,
) {
  val stages = ApplicationLifecycleStage.entries

  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      stages.forEachIndexed { index, stage ->
        val isReached = stage.stepNumber <= currentStage.stepNumber
        val isCurrent = stage == currentStage

        Box(
          modifier =
            Modifier.size(26.dp)
              .clip(CircleShape)
              .background(
                when {
                  isCurrent -> SoftLavender
                  isReached -> LavenderBadgeContainer
                  else -> ElevatedCardSurfaceAlt
                }
              )
              .border(
                width = if (isCurrent) 1.5.dp else 1.dp,
                color = if (isReached) SoftLavender else SubtleBorder,
                shape = CircleShape,
              )
              .clickable { onStageClick(stage) }
              .testTag("stage_node_${stage.stepNumber}"),
          contentAlignment = Alignment.Center,
        ) {
          if (stage.stepNumber < currentStage.stepNumber) {
            Icon(
              imageVector = Icons.Rounded.Check,
              contentDescription = stage.stepLabel,
              tint = SoftLavender,
              modifier = Modifier.size(14.dp),
            )
          } else {
            Text(
              text = stage.stepNumber.toString(),
              style = AlertPulseTypography.BadgeMono,
              color =
                when {
                  isCurrent -> AmoledBlack
                  isReached -> SoftLavender
                  else -> MutedText
                },
            )
          }
        }

        if (index < stages.lastIndex) {
          val nextReached = stages[index + 1].stepNumber <= currentStage.stepNumber
          Box(
            modifier =
              Modifier.weight(1f)
                .height(2.dp)
                .background(if (nextReached) SoftLavender else SubtleBorder)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      stages.forEach { stage ->
        val isReached = stage.stepNumber <= currentStage.stepNumber
        Text(
          text = "${stage.stepNumber}.${stage.stepLabel.replace("Answer ", "Ans ")}",
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 0.2.sp),
          color = if (isReached) SoftLavender else MutedText,
          textAlign = TextAlign.Center,
          maxLines = 1,
          overflow = TextOverflow.Clip,
          modifier = Modifier.weight(1f),
        )
      }
    }
  }
}
