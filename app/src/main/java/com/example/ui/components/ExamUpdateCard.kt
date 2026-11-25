package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Login
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.ExamUpdate
import com.example.data.model.ExamUpdateType
import com.example.data.model.OfficialSourceVerifier
import com.example.ui.theme.AlertPulseTypography
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.ElevatedBarSurface
import com.example.ui.theme.ElevatedCardSurface
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
import com.example.ui.theme.SubtleBorder
import com.example.ui.theme.TitaniumSilver
import com.example.ui.theme.TitaniumSilverMuted
import com.example.ui.theme.UrgentRose
import com.example.ui.theme.UrgentRoseContainer

@Composable
fun ExamUpdateCard(
  update: ExamUpdate,
  modifier: Modifier = Modifier,
  isSaved: Boolean = false,
  onToggleSaved: (() -> Unit)? = null,
  onCardClick: (() -> Unit)? = null,
) {
  val context = LocalContext.current
  val isSourceVerified = OfficialSourceVerifier.verifyExamUpdate(update).isVerified

  val (badgeContainer, badgeText) =
    when (update.type) {
      ExamUpdateType.ADMIT_CARD -> UrgentRoseContainer to UrgentRose
      ExamUpdateType.CITY_SLIP -> LavenderBadgeContainer to SoftLavender
      ExamUpdateType.ANSWER_KEY -> SkyBadgeContainer to SkyAccent
      ExamUpdateType.RESULT -> EmeraldContainer to EmeraldSoft
      ExamUpdateType.EXAM_DATE -> SkyBadgeContainer to SkyAccent
      ExamUpdateType.NOTIFICATION -> LavenderBadgeContainer to SoftLavender
      ExamUpdateType.DEADLINE -> StatusAmberContainer to StatusAmber
    }

  val cardShape = RoundedCornerShape(18.dp)
  val cardModifier =
    if (onCardClick != null) {
      modifier.fillMaxWidth().clip(cardShape).clickable(onClick = onCardClick).testTag("exam_update_card_${update.id}")
    } else {
      modifier.fillMaxWidth().testTag("exam_update_card_${update.id}")
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
        Text(
          text = update.organization,
          style = MaterialTheme.typography.labelLarge,
          color = SkyAccent,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
          color = badgeContainer,
          shape = RoundedCornerShape(24.dp),
          border = BorderStroke(1.dp, badgeText.copy(alpha = 0.35f)),
        ) {
          Text(
            text = update.type.badgeLabel,
            style = AlertPulseTypography.BadgeMono,
            color = badgeText,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            maxLines = 1,
          )
        }
        if (onToggleSaved != null) {
          IconButton(
            onClick = onToggleSaved,
            modifier = Modifier.minimumInteractiveComponentSize().testTag("save_notice_btn_${update.id}"),
          ) {
            Icon(
              imageVector = if (isSaved) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
              contentDescription = if (isSaved) "Remove saved notice" else "Save notice",
              tint = if (isSaved) SoftLavender else TitaniumSilverMuted,
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))
      VerifiedSourceBadge(
        officialDomain = update.officialDomain,
        isVerified = isSourceVerified,
      )

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = update.examTitle,
        style = MaterialTheme.typography.titleMedium,
        color = PrimaryText,
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = update.summaryNote,
        style = MaterialTheme.typography.bodyMedium,
        color = SecondaryText,
      )

      Spacer(modifier = Modifier.height(12.dp))
      HorizontalDivider(color = SubtleBorder, thickness = 1.dp)
      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Column {
          Text(
            text = "PUBLISHED",
            style = AlertPulseTypography.BadgeMono,
            color = MutedText,
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = update.releasedDateDisplay,
            style = AlertPulseTypography.DateMono,
            color = TitaniumSilver,
          )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "STATUS",
            style = AlertPulseTypography.BadgeMono,
            color = MutedText,
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = update.statusLabel,
            style = AlertPulseTypography.DateMono,
            color = badgeText,
          )
        }
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "EXAM DATE",
            style = AlertPulseTypography.BadgeMono,
            color = MutedText,
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = update.examDateDisplay.ifBlank { "Not Announced" },
            style = AlertPulseTypography.DateMono,
            color = SoftLavender,
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        if (!update.noticePdfUrl.isNullOrBlank()) {
          OutlinedButton(
            onClick = {
              if (isSourceVerified) BrowserLauncher.openOfficialUrl(context, update.noticePdfUrl)
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
                .testTag("notice_pdf_${update.id}"),
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
        }

        Button(
          onClick = {
            if (isSourceVerified) BrowserLauncher.openOfficialUrl(context, update.portalLoginUrl)
          },
          enabled = isSourceVerified,
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
              .testTag("portal_login_${update.id}"),
        ) {
          Icon(
            imageVector = Icons.Rounded.Login,
            contentDescription = null,
            modifier = Modifier.size(15.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isSourceVerified) "Official Source Link ↗" else "Source Blocked",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
          )
        }
      }
    }
  }
}
