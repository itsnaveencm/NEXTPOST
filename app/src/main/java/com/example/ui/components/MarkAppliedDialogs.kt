package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DateWindowUtils
import com.example.data.model.JobAlert
import com.example.ui.theme.AlertPulseTypography
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.ElevatedBarSurface
import com.example.ui.theme.ElevatedCardSurface
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.SubtleBorder
import com.example.ui.theme.TitaniumSilver
import com.example.ui.theme.UrgentRose

@Composable
fun MarkAppliedDialog(
  job: JobAlert,
  isFromBrowserReturn: Boolean,
  isAlreadyApplied: Boolean,
  initialRegistrationNumber: String,
  onSave: (String) -> Unit,
  onRemoveApplied: (() -> Unit)? = null,
  onDismiss: () -> Unit,
) {
  var regNumber by remember(job.id, initialRegistrationNumber) {
    mutableStateOf(initialRegistrationNumber)
  }
  val todayDisplay = remember { DateWindowUtils.formatMillisToDisplay() }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      color = ElevatedCardSurface,
      shape = RoundedCornerShape(20.dp),
      border = BorderStroke(1.dp, SubtleBorder),
      modifier = Modifier.fillMaxWidth().testTag("mark_applied_dialog"),
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Text(
          text =
            when {
              isFromBrowserReturn -> "APPLICATION CHECK"
              isAlreadyApplied -> "UPDATE APPLIED ENTRY"
              else -> "MARK AS APPLIED"
            },
          style = AlertPulseTypography.SectionHeaderMono,
          color = SoftLavender,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text =
            if (isFromBrowserReturn) {
              "Did you complete your application for ${job.postTitle}?"
            } else {
              "${job.organization} — ${job.postTitle}"
            },
          style = MaterialTheme.typography.bodyMedium,
          color = PrimaryText,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "APPLIED DATE: $todayDisplay",
          style = AlertPulseTypography.DateMono,
          color = SecondaryText,
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = regNumber,
          onValueChange = { regNumber = it },
          label = {
            Text(
              text = "Application / Registration Number (Optional)",
              style = MaterialTheme.typography.bodySmall,
            )
          },
          placeholder = {
            Text(
              text = "e.g. RRB-26-884120",
              style = AlertPulseTypography.DateMono,
              color = SecondaryText,
            )
          },
          singleLine = true,
          textStyle = AlertPulseTypography.DateMono.copy(color = PrimaryText),
          shape = RoundedCornerShape(14.dp),
          colors =
            OutlinedTextFieldDefaults.colors(
              focusedBorderColor = SoftLavender,
              unfocusedBorderColor = SubtleBorder,
              focusedContainerColor = ElevatedBarSurface,
              unfocusedContainerColor = ElevatedBarSurface,
              focusedLabelColor = SoftLavender,
              unfocusedLabelColor = SecondaryText,
              cursorColor = SoftLavender,
            ),
          modifier = Modifier.fillMaxWidth().testTag("registration_number_input"),
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          OutlinedButton(
            onClick = onDismiss,
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, SubtleBorder),
            colors =
              ButtonDefaults.outlinedButtonColors(
                containerColor = ElevatedBarSurface,
                contentColor = TitaniumSilver,
              ),
            modifier = Modifier.weight(1f).height(42.dp).testTag("dialog_cancel_button"),
          ) {
            Text(
              text = if (isFromBrowserReturn) "Not Yet" else "Cancel",
              style = MaterialTheme.typography.labelLarge,
              maxLines = 1,
            )
          }

          Button(
            onClick = { onSave(regNumber) },
            shape = RoundedCornerShape(24.dp),
            colors =
              ButtonDefaults.buttonColors(
                containerColor = SoftLavender,
                contentColor = AmoledBlack,
              ),
            modifier = Modifier.weight(1f).height(42.dp).testTag("dialog_save_applied_button"),
          ) {
            Text(
              text = if (isFromBrowserReturn) "Yes, Applied" else "Save Status",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
            )
          }
        }

        if (isAlreadyApplied && onRemoveApplied != null) {
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            TextButton(
              onClick = onRemoveApplied,
              modifier = Modifier.testTag("dialog_remove_applied_button"),
            ) {
              Text(
                text = "Remove from Applied Tracker",
                style = MaterialTheme.typography.labelMedium,
                color = UrgentRose,
              )
            }
          }
        }
      }
    }
  }
}
