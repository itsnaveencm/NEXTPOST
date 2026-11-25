package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AlertPulseTypography
import com.example.ui.theme.ElevatedCardSurface
import com.example.ui.theme.ElevatedCardSurfaceAlt
import com.example.ui.theme.LavenderBadgeContainer
import com.example.ui.theme.MutedText
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.SubtleBorder
import com.example.ui.viewmodel.StateDirectoryItem

@Composable
fun StateDirectoryView(
  items: List<StateDirectoryItem>,
  isPrivateMode: Boolean,
  onStateClick: (StateDirectoryItem) -> Unit,
  emptyLabel: String,
  modifier: Modifier = Modifier,
) {
  if (items.isEmpty()) {
    ZeroAlertsBentoCard(
      headline = "NO MATCHING REGIONS",
      subtitle = emptyLabel,
      badgeText = "28 STATES & 8 UTS READY",
      modifier = modifier.fillMaxSize(),
    )
  } else {
    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 24.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = modifier.fillMaxSize().testTag("state_directory_grid"),
    ) {
      items(items, key = { it.state.code }) { item ->
        StateBentoCard(
          item = item,
          isPrivateMode = isPrivateMode,
          onClick = { onStateClick(item) },
        )
      }
    }
  }
}

@Composable
private fun StateBentoCard(
  item: StateDirectoryItem,
  isPrivateMode: Boolean,
  onClick: () -> Unit,
) {
  val hasAlerts = item.activeAlertCount > 0
  val countFormatted = item.activeAlertCount.toString().padStart(2, '0')
  val subtitle = if (isPrivateMode) item.state.primaryHub else item.state.boardShort
  val fillFraction =
    if (item.activeAlertCount == 0) 0f
    else (item.activeAlertCount.toFloat() / item.maxSectorAlertCount.toFloat()).coerceIn(0.2f, 1f)

  Surface(
    color = ElevatedCardSurface,
    shape = RoundedCornerShape(18.dp),
    border =
      BorderStroke(
        width = 1.dp,
        color = if (item.isHomeState) SoftLavender else SubtleBorder,
      ),
    modifier =
      Modifier.fillMaxWidth()
        .clip(RoundedCornerShape(18.dp))
        .clickable(onClick = onClick)
        .testTag("state_card_${item.state.code}"),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = item.state.code,
            style = AlertPulseTypography.SectionHeaderMono,
            color = if (item.isHomeState) SoftLavender else PrimaryText,
            maxLines = 1,
          )
          if (item.isHomeState) {
            Spacer(modifier = Modifier.width(5.dp))
            Icon(
              imageVector = Icons.Rounded.PushPin,
              contentDescription = "Home State",
              tint = SoftLavender,
              modifier = Modifier.size(12.dp),
            )
          }
        }

        Surface(
          color = if (hasAlerts) LavenderBadgeContainer else ElevatedCardSurfaceAlt,
          shape = RoundedCornerShape(24.dp),
          border =
            BorderStroke(
              1.dp,
              if (hasAlerts) SoftLavender.copy(alpha = 0.35f) else SubtleBorder,
            ),
        ) {
          Text(
            text = "$countFormatted LIVE",
            style = AlertPulseTypography.BadgeMono,
            color = if (hasAlerts) SoftLavender else MutedText,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            maxLines = 1,
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = item.state.name,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = PrimaryText,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )

      Spacer(modifier = Modifier.height(3.dp))

      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = SecondaryText,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Subtle activity progress bar (#B8A9E8 over #24262D)
      Box(
        modifier =
          Modifier.fillMaxWidth()
            .height(3.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(SubtleBorder)
      ) {
        if (fillFraction > 0f) {
          Box(
            modifier =
              Modifier.fillMaxWidth(fillFraction)
                .fillMaxHeight()
                .clip(RoundedCornerShape(2.dp))
                .background(SoftLavender)
          )
        }
      }
    }
  }
}
