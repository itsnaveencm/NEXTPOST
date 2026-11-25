package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PersonOutline
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.CandidateProfile
import com.example.data.model.DateWindowUtils
import com.example.data.model.JobWindowFilter
import com.example.data.repository.FeedSyncMetadata
import com.example.ui.theme.AlertPulseTypography
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.ElevatedBarSurface
import com.example.ui.theme.ElevatedCardSurface
import com.example.ui.theme.ElevatedCardSurfaceAlt
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.GlassTintedSurface
import com.example.ui.theme.LavenderBadgeContainer
import com.example.ui.theme.MutedText
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SkyAccent
import com.example.ui.theme.SkyBadgeContainer
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedContainer
import com.example.ui.theme.SubtleBorder
import com.example.ui.theme.SubtleBorderHighlight
import com.example.ui.theme.TitaniumSilver
import com.example.ui.theme.TitaniumSilverMuted
import com.example.ui.theme.UrgentRose
import com.example.ui.theme.UrgentRoseContainer
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun NextPostTopBar(
  onProfileClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier =
      modifier
        .fillMaxWidth()
        .background(AmoledBlack)
        .padding(start = 16.dp, end = 12.dp, top = 8.dp, bottom = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      DotMatrixPulseMark(modifier = Modifier.size(16.dp))
      Spacer(modifier = Modifier.width(10.dp))
      Text(
        text = "NEXTPOST",
        style = AlertPulseTypography.BrandHeaderDot,
        color = PrimaryText,
        maxLines = 1,
      )
    }

    IconButton(
      onClick = onProfileClick,
      modifier = Modifier.minimumInteractiveComponentSize().testTag("profile_button"),
    ) {
      Surface(
        color = ElevatedCardSurface,
        shape = CircleShape,
        border = BorderStroke(1.dp, SubtleBorder),
        modifier = Modifier.size(40.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Rounded.PersonOutline,
            contentDescription = stringResource(R.string.cd_profile),
            tint = SkyAccent,
            modifier = Modifier.size(20.dp),
          )
        }
      }
    }
  }
}

@Composable
fun GlassyTopBarWithBreadcrumb(
  title: String,
  breadcrumbs: List<String>,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier,
  trailingContent: (@Composable () -> Unit)? = null,
) {
  Surface(
    color = ElevatedBarSurface,
    modifier = modifier.fillMaxWidth().testTag("glassy_top_bar"),
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth().padding(start = 6.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        IconButton(
          onClick = onBackClick,
          modifier = Modifier.minimumInteractiveComponentSize().testTag("top_bar_back_button"),
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = stringResource(R.string.cd_back),
            tint = SkyAccent,
          )
        }

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }

        if (trailingContent != null) {
          Spacer(modifier = Modifier.width(8.dp))
          trailingContent()
        }
      }

      if (breadcrumbs.isNotEmpty()) {
        Row(
          modifier =
            Modifier.fillMaxWidth()
              .horizontalScroll(rememberScrollState())
              .padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
              .testTag("breadcrumb_bar"),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          breadcrumbs.forEachIndexed { index, crumb ->
            val isLast = index == breadcrumbs.lastIndex
            Text(
              text = crumb,
              style = AlertPulseTypography.BadgeMono,
              color = if (isLast) SkyAccent else SecondaryText,
              maxLines = 1,
            )
            if (!isLast) {
              Text(
                text = "  →  ",
                style = AlertPulseTypography.BadgeMono,
                color = MutedText,
              )
            }
          }
        }
      }

      HorizontalDivider(color = SubtleBorder, thickness = 1.dp)
    }
  }
}

@Composable
fun GlassyCard(
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null,
  borderColor: Color = SubtleBorder,
  containerColor: Color = GlassTintedSurface,
  content: @Composable ColumnScope.() -> Unit,
) {
  val shape = RoundedCornerShape(18.dp)
  val cardModifier =
    if (onClick != null) {
      modifier.clip(shape).clickable(onClick = onClick)
    } else {
      modifier
    }

  Surface(
    color = containerColor,
    shape = shape,
    border = BorderStroke(1.dp, borderColor),
    modifier = cardModifier,
  ) {
    Column(content = content)
  }
}

@Composable
fun GlassySectionHeader(
  eyebrow: String,
  title: String,
  modifier: Modifier = Modifier,
  trailingBadge: String? = null,
) {
  Row(
    modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = eyebrow.uppercase(),
        style = AlertPulseTypography.BadgeMono,
        color = SkyAccent,
        maxLines = 1,
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = PrimaryText,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
    if (!trailingBadge.isNullOrBlank()) {
      Spacer(modifier = Modifier.width(8.dp))
      Surface(
        color = SkyBadgeContainer,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, SkyAccent.copy(alpha = 0.35f)),
      ) {
        Text(
          text = trailingBadge.uppercase(),
          style = AlertPulseTypography.BadgeMono,
          color = SkyAccent,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
          maxLines = 1,
        )
      }
    }
  }
}

@Composable
fun VerifiedSourceBadge(
  officialDomain: String,
  isVerified: Boolean = true,
  modifier: Modifier = Modifier,
) {
  val container = if (isVerified) EmeraldContainer else StatusRedContainer
  val content = if (isVerified) EmeraldSoft else StatusRed
  val label =
    if (isVerified) {
      "Verified: ${officialDomain.ifBlank { "official portal" }}"
    } else {
      "UNVERIFIED SOURCE BLOCKED"
    }

  Surface(
    color = container,
    shape = RoundedCornerShape(24.dp),
    border = BorderStroke(1.dp, content.copy(alpha = 0.38f)),
    modifier = modifier.testTag("verified_source_badge"),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
    ) {
      Icon(
        imageVector = if (isVerified) Icons.Rounded.VerifiedUser else Icons.Rounded.WarningAmber,
        contentDescription = null,
        tint = content,
        modifier = Modifier.size(12.dp),
      )
      Spacer(modifier = Modifier.width(5.dp))
      Text(
        text = label,
        style = AlertPulseTypography.BadgeMono,
        color = content,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

@Composable
fun WindowFilterStrip(
  selected: JobWindowFilter,
  onSelect: (JobWindowFilter) -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    JobWindowFilter.entries.forEach { filter ->
      StadiumPill(
        label = filter.label,
        selected = filter == selected,
        onClick = { onSelect(filter) },
        testTag = "window_filter_${filter.name}",
      )
    }
  }
}

@Composable
fun FeedSyncStatusBanner(
  syncMetadata: FeedSyncMetadata,
  isSyncing: Boolean,
  onRefreshClick: () -> Unit,
  onConfigureClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val statusLabel =
    when (syncMetadata.syncStatus) {
      "VERIFIED_SYNC_OK" -> "SYNCED • ${DateWindowUtils.formatSyncTimestamp(syncMetadata.lastSyncedAtMillis)}"
      "SYNC_ERROR" -> "SYNC RETRY • OFFICIAL PORTALS"
      else -> "CONNECTED TO OFFICIAL PORTALS"
    }

  GlassyCard(
    onClick = onConfigureClick,
    modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp).testTag("feed_sync_banner"),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = statusLabel,
          style = AlertPulseTypography.BadgeMono,
          color = if (syncMetadata.syncStatus == "SYNC_ERROR") StatusRed else EmeraldSoft,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text =
            if (syncMetadata.endpointUrl.isBlank()) {
              "Live API not configured • Zero dummy records policy active"
            } else {
              "${syncMetadata.verifiedJobsCount} verified jobs • ${syncMetadata.blockedRecordsCount} unverified blocked"
            },
          style = MaterialTheme.typography.bodySmall,
          color = SecondaryText,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }

      IconButton(
        onClick = onRefreshClick,
        enabled = !isSyncing,
        modifier = Modifier.minimumInteractiveComponentSize().testTag("sync_refresh_button"),
      ) {
        if (isSyncing) {
          CircularProgressIndicator(
            color = SkyAccent,
            strokeWidth = 2.dp,
            modifier = Modifier.size(18.dp),
          )
        } else {
          Icon(
            imageVector = Icons.Rounded.Refresh,
            contentDescription = "Refresh verified feeds",
            tint = SkyAccent,
            modifier = Modifier.size(20.dp),
          )
        }
      }
    }
  }
}

@Composable
fun EligibilityStripCard(
  eligibleOnly: Boolean,
  profile: CandidateProfile,
  openCount: Int,
  onToggleEligibleOnly: () -> Unit,
  onEditProfileClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  GlassyCard(
    borderColor = if (eligibleOnly) SubtleBorderHighlight else SubtleBorder,
    modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(modifier = Modifier.weight(1f).clickable(onClick = onEditProfileClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Rounded.Tune,
            contentDescription = null,
            tint = SkyAccent,
            modifier = Modifier.size(14.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text =
              if (eligibleOnly) {
                "ELIGIBLE ONLY: ON • $openCount VERIFIED"
              } else {
                "SHOWING ALL VERIFIED • $openCount"
              },
            style = AlertPulseTypography.BadgeMono,
            color = if (eligibleOnly) SkyAccent else TitaniumSilver,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "${profile.qualification.label} • ${profile.stream.label} • ${profile.age} yrs • ${profile.homeStateCode}",
          style = MaterialTheme.typography.bodySmall,
          color = SecondaryText,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Surface(
        color = if (eligibleOnly) SoftLavender else ElevatedCardSurfaceAlt,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, if (eligibleOnly) SoftLavender else SubtleBorder),
        modifier =
          Modifier.clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onToggleEligibleOnly)
            .testTag("eligible_only_chip"),
      ) {
        Text(
          text = if (eligibleOnly) "ELIGIBLE" else "ALL OPEN",
          style = AlertPulseTypography.BadgeMono,
          color = if (eligibleOnly) AmoledBlack else TitaniumSilver,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
          maxLines = 1,
        )
      }
    }
  }
}

@Composable
fun GlassyDirectoryRowCard(
  title: String,
  subtitle: String,
  badgeText: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  eyebrow: String? = null,
  icon: ImageVector? = null,
  accentColor: Color = SkyAccent,
  trailingAction: (@Composable () -> Unit)? = null,
  testTag: String? = null,
) {
  val baseModifier =
    if (testTag != null) modifier.fillMaxWidth().testTag(testTag) else modifier.fillMaxWidth()

  GlassyCard(
    onClick = onClick,
    modifier = baseModifier,
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        if (icon != null) {
          Surface(
            color = SkyBadgeContainer,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f)),
            modifier = Modifier.size(42.dp),
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp),
              )
            }
          }
          Spacer(modifier = Modifier.width(12.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
          if (!eyebrow.isNullOrBlank()) {
            Text(
              text = eyebrow.uppercase(),
              style = AlertPulseTypography.BadgeMono,
              color = accentColor,
              maxLines = 1,
            )
            Spacer(modifier = Modifier.height(2.dp))
          }
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = PrimaryText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          Spacer(modifier = Modifier.height(3.dp))
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = SecondaryText,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
          )
        }
      }

      Spacer(modifier = Modifier.width(10.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
          color = LavenderBadgeContainer,
          shape = RoundedCornerShape(24.dp),
          border = BorderStroke(1.dp, SoftLavender.copy(alpha = 0.35f)),
        ) {
          Text(
            text = badgeText,
            style = AlertPulseTypography.BadgeMono,
            color = SoftLavender,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            maxLines = 1,
          )
        }
        if (trailingAction != null) {
          trailingAction()
        } else {
          Spacer(modifier = Modifier.width(6.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = null,
            tint = TitaniumSilverMuted,
            modifier = Modifier.size(16.dp),
          )
        }
      }
    }
  }
}

@Composable
fun DotMatrixPulseMark(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier) {
    val radius = size.minDimension * 0.13f
    val stepX = size.width / 3f
    val stepY = size.height / 3f
    for (row in 0..2) {
      for (col in 0..2) {
        val isAccent = (row == 1 && col == 1) || (row == 0 && col == 2) || (row == 2 && col == 0)
        drawCircle(
          color = if (isAccent) SkyAccent else SubtleBorder,
          radius = radius,
          center = Offset(x = stepX * (col + 0.5f), y = stepY * (row + 0.5f)),
        )
      }
    }
  }
}

@Composable
fun DotMatrixRadarBellIcon(modifier: Modifier = Modifier) {
  Box(modifier = modifier.size(72.dp), contentAlignment = Alignment.Center) {
    Canvas(modifier = Modifier.matchParentSize()) {
      val cx = size.width / 2f
      val cy = size.height / 2f
      val outerRadius = size.minDimension * 0.45f
      val innerRadius = size.minDimension * 0.31f
      val outerDots = 16
      val innerDots = 10

      for (i in 0 until outerDots) {
        val angle = (2.0 * Math.PI * i / outerDots)
        val dotX = cx + (outerRadius * cos(angle)).toFloat()
        val dotY = cy + (outerRadius * sin(angle)).toFloat()
        drawCircle(
          color = SkyAccent.copy(alpha = if (i % 2 == 0) 0.55f else 0.25f),
          radius = 2.dp.toPx(),
          center = Offset(dotX, dotY),
        )
      }
      for (i in 0 until innerDots) {
        val angle = (2.0 * Math.PI * i / innerDots) + 0.3
        val dotX = cx + (innerRadius * cos(angle)).toFloat()
        val dotY = cy + (innerRadius * sin(angle)).toFloat()
        drawCircle(
          color = SoftLavender.copy(alpha = 0.7f),
          radius = 1.6.dp.toPx(),
          center = Offset(dotX, dotY),
        )
      }
    }
    Surface(
      color = LavenderBadgeContainer,
      shape = CircleShape,
      border = BorderStroke(1.dp, SoftLavender.copy(alpha = 0.35f)),
      modifier = Modifier.size(38.dp),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = Icons.Outlined.NotificationsActive,
          contentDescription = null,
          tint = SoftLavender,
          modifier = Modifier.size(20.dp),
        )
      }
    }
  }
}

@Composable
fun ZeroAlertsBentoCard(
  modifier: Modifier = Modifier,
  headline: String = "NO VERIFIED ACTIVE OPENINGS",
  subtitle: String = "No verified active openings right now — Connected to official portals",
  badgeText: String = "OFFICIAL PORTAL VERIFICATION ACTIVE",
  onEditProfileClick: (() -> Unit)? = null,
  secondaryActionLabel: String? = null,
  onSecondaryActionClick: (() -> Unit)? = null,
) {
  Box(
    modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 18.dp),
    contentAlignment = Alignment.Center,
  ) {
    Surface(
      color = ElevatedCardSurface,
      shape = RoundedCornerShape(18.dp),
      border = BorderStroke(1.dp, SubtleBorder),
      modifier = Modifier.fillMaxWidth().testTag("zero_alerts_bento_card"),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        DotMatrixRadarBellIcon()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = headline.uppercase(),
          style = AlertPulseTypography.SectionHeaderMono,
          color = PrimaryText,
          textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodyMedium,
          color = SecondaryText,
          textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
          color = LavenderBadgeContainer,
          shape = RoundedCornerShape(24.dp),
          border = BorderStroke(1.dp, SoftLavender.copy(alpha = 0.35f)),
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
          ) {
            Canvas(modifier = Modifier.size(6.dp)) { drawCircle(color = SoftLavender) }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = badgeText.uppercase(),
              style = AlertPulseTypography.BadgeMono,
              color = SoftLavender,
              maxLines = 1,
            )
          }
        }

        if (onEditProfileClick != null || (secondaryActionLabel != null && onSecondaryActionClick != null)) {
          Spacer(modifier = Modifier.height(18.dp))
          Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            if (onEditProfileClick != null) {
              OutlinedButton(
                onClick = onEditProfileClick,
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, SubtleBorderHighlight),
                colors =
                  ButtonDefaults.outlinedButtonColors(
                    containerColor = ElevatedBarSurface,
                    contentColor = SoftLavender,
                  ),
                modifier = Modifier.height(42.dp).testTag("empty_edit_profile_button"),
              ) {
                Text(
                  text = "Qualification Profile",
                  style = MaterialTheme.typography.labelLarge,
                  maxLines = 1,
                )
              }
            }

            if (secondaryActionLabel != null && onSecondaryActionClick != null) {
              OutlinedButton(
                onClick = onSecondaryActionClick,
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, SubtleBorder),
                colors =
                  ButtonDefaults.outlinedButtonColors(
                    containerColor = ElevatedBarSurface,
                    contentColor = TitaniumSilver,
                  ),
                modifier = Modifier.height(42.dp).testTag("empty_secondary_action_button"),
              ) {
                Text(
                  text = secondaryActionLabel,
                  style = MaterialTheme.typography.labelLarge,
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
fun ApplicationOpenBadge(modifier: Modifier = Modifier) {
  Surface(
    color = EmeraldContainer,
    shape = RoundedCornerShape(24.dp),
    border = BorderStroke(1.dp, EmeraldSoft.copy(alpha = 0.35f)),
    modifier = modifier,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
    ) {
      Canvas(modifier = Modifier.size(5.dp)) { drawCircle(color = EmeraldSoft) }
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "APPLICATION OPEN",
        style = AlertPulseTypography.BadgeMono,
        color = EmeraldSoft,
        maxLines = 1,
      )
    }
  }
}

@Composable
fun StadiumPill(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  testTag: String,
  modifier: Modifier = Modifier,
) {
  Surface(
    color = if (selected) SoftLavender else ElevatedCardSurface,
    shape = RoundedCornerShape(24.dp),
    border = BorderStroke(1.dp, if (selected) SoftLavender else SubtleBorder),
    modifier =
      modifier
        .clip(RoundedCornerShape(24.dp))
        .clickable(onClick = onClick)
        .testTag(testTag),
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelLarge,
      fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
      color = if (selected) AmoledBlack else TitaniumSilver,
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
      maxLines = 1,
    )
  }
}

@Composable
fun FloatingSearchPill(
  query: String,
  onQueryChange: (String) -> Unit,
  placeholder: String,
  modifier: Modifier = Modifier,
) {
  Surface(
    color = ElevatedCardSurface,
    shape = RoundedCornerShape(18.dp),
    border = BorderStroke(1.dp, SubtleBorder),
    modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        imageVector = Icons.Rounded.Search,
        contentDescription = stringResource(R.string.cd_search),
        tint = SkyAccent,
        modifier = Modifier.size(18.dp),
      )
      Spacer(modifier = Modifier.width(10.dp))
      Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
        if (query.isEmpty()) {
          Text(
            text = placeholder,
            style = MaterialTheme.typography.bodyMedium,
            color = SecondaryText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
        BasicTextField(
          value = query,
          onValueChange = onQueryChange,
          singleLine = true,
          textStyle = MaterialTheme.typography.bodyMedium.copy(color = PrimaryText),
          cursorBrush = SolidColor(SkyAccent),
          modifier = Modifier.fillMaxWidth().testTag("floating_search_input"),
        )
      }
      if (query.isNotEmpty()) {
        IconButton(
          onClick = { onQueryChange("") },
          modifier = Modifier.minimumInteractiveComponentSize().testTag("floating_search_clear"),
        ) {
          Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = stringResource(R.string.cd_clear_search),
            tint = TitaniumSilver,
            modifier = Modifier.size(16.dp),
          )
        }
      }
    }
  }
}

@Composable
fun DotMatrixCountdownBadge(
  text: String,
  isUrgent: Boolean,
  modifier: Modifier = Modifier,
) {
  val containerColor = if (isUrgent) UrgentRoseContainer else LavenderBadgeContainer
  val contentColor = if (isUrgent) UrgentRose else SoftLavender
  val borderColor = if (isUrgent) UrgentRose.copy(alpha = 0.35f) else SubtleBorderHighlight

  Surface(
    color = containerColor,
    shape = RoundedCornerShape(24.dp),
    border = BorderStroke(1.dp, borderColor),
    modifier = modifier,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
    ) {
      Canvas(modifier = Modifier.size(5.dp)) { drawCircle(color = contentColor) }
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = text.uppercase(),
        style = AlertPulseTypography.BadgeMono,
        color = contentColor,
        maxLines = 1,
      )
    }
  }
}
