package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.AcademicStream
import com.example.data.model.CandidateProfile
import com.example.data.model.IndianStatesCatalog
import com.example.data.model.NotificationPreferences
import com.example.data.model.QualificationLevel
import com.example.notifications.NextPostNotificationHelper
import com.example.ui.components.StadiumPill
import com.example.ui.theme.AlertPulseTypography
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.ElevatedBarSurface
import com.example.ui.theme.ElevatedCardSurface
import com.example.ui.theme.ElevatedCardSurfaceAlt
import com.example.ui.theme.LavenderBadgeContainer
import com.example.ui.theme.MutedText
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.SubtleBorder
import com.example.ui.theme.SubtleBorderHighlight
import com.example.ui.theme.TitaniumSilverMuted
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CandidateProfileScreen(
  currentProfile: CandidateProfile,
  currentNotificationPrefs: NotificationPreferences,
  onSavePreferences: (CandidateProfile, NotificationPreferences) -> Unit,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  var selectedQual by remember(currentProfile) { mutableStateOf(currentProfile.qualification) }
  var selectedStream by remember(currentProfile) { mutableStateOf(currentProfile.stream) }
  var selectedAge by remember(currentProfile) { mutableIntStateOf(currentProfile.age) }
  var selectedStateCode by remember(currentProfile) { mutableStateOf(currentProfile.homeStateCode) }
  var isStatePickerExpanded by remember { mutableStateOf(false) }

  var notifEligibleJobs by remember(currentNotificationPrefs) {
    mutableStateOf(currentNotificationPrefs.newEligibleJobsEnabled)
  }
  var notifDeadlineReminders by remember(currentNotificationPrefs) {
    mutableStateOf(currentNotificationPrefs.deadlineRemindersEnabled)
  }
  var notifExamUpdates by remember(currentNotificationPrefs) {
    mutableStateOf(currentNotificationPrefs.examUpdatesEnabled)
  }

  val permissionLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }

  val ensureNotificationPermission: () -> Unit = {
    if (
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        !NextPostNotificationHelper.canPostNotifications(context)
    ) {
      permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
  }

  val selectedStateObj = IndianStatesCatalog.findByCode(selectedStateCode)

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    Column(modifier = Modifier.widthIn(max = 720.dp).fillMaxSize()) {
      Surface(color = ElevatedBarSurface, modifier = Modifier.fillMaxWidth()) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            IconButton(
              onClick = onNavigateBack,
              modifier = Modifier.minimumInteractiveComponentSize().testTag("profile_back_button"),
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.cd_back),
                tint = SoftLavender,
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "PROFILE",
                style = AlertPulseTypography.SectionHeaderMono,
                color = SoftLavender,
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Candidate Eligibility & Home State",
                style = MaterialTheme.typography.titleMedium,
                color = PrimaryText,
              )
            }
          }
          HorizontalDivider(color = SubtleBorder, thickness = 1.dp)
        }
      }

      Column(
        modifier =
          Modifier.weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 18.dp)
      ) {
        Text(
          text = "HIGHEST QUALIFICATION",
          style = AlertPulseTypography.BadgeMono,
          color = SoftLavender,
        )
        Spacer(modifier = Modifier.height(10.dp))
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          QualificationLevel.entries.forEach { level ->
            StadiumPill(
              label = level.label,
              selected = level == selectedQual,
              onClick = { selectedQual = level },
              testTag = "qual_chip_${level.name}",
            )
          }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
          text = "STREAM / SPECIALIZATION",
          style = AlertPulseTypography.BadgeMono,
          color = SoftLavender,
        )
        Spacer(modifier = Modifier.height(10.dp))
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          AcademicStream.entries.forEach { stream ->
            StadiumPill(
              label = stream.label,
              selected = stream == selectedStream,
              onClick = { selectedStream = stream },
              testTag = "stream_chip_${stream.name}",
            )
          }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Surface(
          color = ElevatedCardSurface,
          shape = RoundedCornerShape(18.dp),
          border = BorderStroke(1.dp, SubtleBorder),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = "AGE (18–45)",
                style = AlertPulseTypography.BadgeMono,
                color = SoftLavender,
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                  onClick = { if (selectedAge > 18) selectedAge-- },
                  modifier = Modifier.minimumInteractiveComponentSize(),
                ) {
                  Icon(
                    imageVector = Icons.Rounded.Remove,
                    contentDescription = "Decrease age",
                    tint = SoftLavender,
                  )
                }
                Surface(
                  color = LavenderBadgeContainer,
                  shape = RoundedCornerShape(24.dp),
                  border = BorderStroke(1.dp, SubtleBorderHighlight),
                ) {
                  Text(
                    text = "$selectedAge YRS",
                    style = AlertPulseTypography.CounterMono,
                    color = SoftLavender,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                  )
                }
                IconButton(
                  onClick = { if (selectedAge < 45) selectedAge++ },
                  modifier = Modifier.minimumInteractiveComponentSize(),
                ) {
                  Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Increase age",
                    tint = SoftLavender,
                  )
                }
              }
            }

            Slider(
              value = selectedAge.toFloat(),
              onValueChange = { selectedAge = it.roundToInt().coerceIn(18, 45) },
              valueRange = 18f..45f,
              colors =
                SliderDefaults.colors(
                  thumbColor = SoftLavender,
                  activeTrackColor = SoftLavender,
                  inactiveTrackColor = ElevatedCardSurfaceAlt,
                ),
            )
          }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
          text = "HOME STATE / UT (PINNED TO TOP)",
          style = AlertPulseTypography.BadgeMono,
          color = SoftLavender,
        )
        Spacer(modifier = Modifier.height(10.dp))

        Box(
          modifier =
            Modifier.fillMaxWidth()
              .clip(RoundedCornerShape(18.dp))
              .background(ElevatedCardSurface)
              .border(1.dp, SubtleBorder, RoundedCornerShape(18.dp))
              .clickable { isStatePickerExpanded = !isStatePickerExpanded }
              .padding(horizontal = 16.dp, vertical = 14.dp)
              .testTag("home_state_selector")
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Rounded.LocationOn,
                contentDescription = null,
                tint = SoftLavender,
                modifier = Modifier.size(18.dp),
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "${selectedStateObj?.name ?: selectedStateCode} ($selectedStateCode)",
                style = MaterialTheme.typography.titleSmall,
                color = PrimaryText,
              )
            }
            Icon(
              imageVector =
                if (isStatePickerExpanded) {
                  Icons.Rounded.KeyboardArrowUp
                } else {
                  Icons.Rounded.KeyboardArrowDown
                },
              contentDescription = "Toggle state list",
              tint = SoftLavender,
            )
          }
        }

        if (isStatePickerExpanded) {
          Spacer(modifier = Modifier.height(8.dp))
          Surface(
            color = ElevatedCardSurface,
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, SubtleBorder),
            modifier = Modifier.fillMaxWidth().height(230.dp),
          ) {
            LazyColumn {
              items(IndianStatesCatalog.allRegions, key = { it.code }) { region ->
                val isSelected = region.code == selectedStateCode
                Row(
                  modifier =
                    Modifier.fillMaxWidth()
                      .clickable {
                        selectedStateCode = region.code
                        isStatePickerExpanded = false
                      }
                      .padding(horizontal = 16.dp, vertical = 12.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Text(
                    text = "${region.name} (${region.code})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) SoftLavender else PrimaryText,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                  )
                  if (isSelected) {
                    Icon(
                      imageVector = Icons.Rounded.Check,
                      contentDescription = null,
                      tint = SoftLavender,
                      modifier = Modifier.size(16.dp),
                    )
                  } else {
                    Text(
                      text = region.type.label,
                      style = AlertPulseTypography.BadgeMono,
                      color = MutedText,
                    )
                  }
                }
                HorizontalDivider(color = SubtleBorder, thickness = 0.5.dp)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
          text = "NOTIFICATIONS & DEADLINE ALERTS",
          style = AlertPulseTypography.BadgeMono,
          color = SoftLavender,
        )
        Spacer(modifier = Modifier.height(10.dp))

        Surface(
          color = ElevatedCardSurface,
          shape = RoundedCornerShape(18.dp),
          border = BorderStroke(1.dp, SubtleBorder),
          modifier = Modifier.fillMaxWidth().testTag("notification_preferences_card"),
        ) {
          Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            NotificationPreferenceRow(
              title = "New Eligible Job Alerts",
              subtitle = "Notify when new verified jobs match your profile & state",
              checked = notifEligibleJobs,
              onCheckedChange = { checked ->
                notifEligibleJobs = checked
                if (checked) ensureNotificationPermission()
              },
              testTag = "switch_notif_eligible_jobs",
            )
            HorizontalDivider(color = SubtleBorder, thickness = 1.dp)
            NotificationPreferenceRow(
              title = "Closing Deadline Reminders (48h & 6h)",
              subtitle = "Auto-schedule alerts for Saved & In-Progress jobs",
              checked = notifDeadlineReminders,
              onCheckedChange = { checked ->
                notifDeadlineReminders = checked
                if (checked) ensureNotificationPermission()
              },
              testTag = "switch_notif_deadlines",
            )
            HorizontalDivider(color = SubtleBorder, thickness = 1.dp)
            NotificationPreferenceRow(
              title = "Exam Updates (Admit Cards & Results)",
              subtitle = "Official admit card, city slip & result alerts",
              checked = notifExamUpdates,
              onCheckedChange = { checked ->
                notifExamUpdates = checked
                if (checked) ensureNotificationPermission()
              },
              testTag = "switch_notif_exam_updates",
            )
          }
        }
      }

      Surface(color = ElevatedBarSurface, modifier = Modifier.fillMaxWidth()) {
        Column {
          HorizontalDivider(color = SubtleBorder, thickness = 1.dp)
          Button(
            onClick = {
              onSavePreferences(
                CandidateProfile(
                  qualification = selectedQual,
                  stream = selectedStream,
                  age = selectedAge,
                  homeStateCode = selectedStateCode,
                  hasCompletedOnboarding = true,
                ),
                NotificationPreferences(
                  newEligibleJobsEnabled = notifEligibleJobs,
                  deadlineRemindersEnabled = notifDeadlineReminders,
                  examUpdatesEnabled = notifExamUpdates,
                ),
              )
            },
            shape = RoundedCornerShape(24.dp),
            colors =
              ButtonDefaults.buttonColors(
                containerColor = SoftLavender,
                contentColor = AmoledBlack,
              ),
            modifier =
              Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .height(48.dp)
                .testTag("save_preferences_button"),
          ) {
            Text(
              text = "Save & Apply Filter",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun NotificationPreferenceRow(
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  testTag: String,
) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = PrimaryText,
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = SecondaryText,
      )
    }
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors =
        SwitchDefaults.colors(
          checkedThumbColor = AmoledBlack,
          checkedTrackColor = SoftLavender,
          uncheckedThumbColor = TitaniumSilverMuted,
          uncheckedTrackColor = ElevatedCardSurfaceAlt,
          uncheckedBorderColor = SubtleBorder,
        ),
      modifier = Modifier.testTag(testTag),
    )
  }
}
