package com.example.ui.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.graphics.toArgb
import com.example.data.model.JobAlert
import com.example.data.model.JobShareFormatter
import com.example.ui.theme.AmoledBlack

object BrowserLauncher {
  fun openOfficialUrl(context: Context, url: String) {
    val parsedUri = Uri.parse(url)
    try {
      val colorParams =
        CustomTabColorSchemeParams.Builder()
          .setToolbarColor(AmoledBlack.toArgb())
          .setNavigationBarColor(AmoledBlack.toArgb())
          .build()

      val customTabsIntent =
        CustomTabsIntent.Builder()
          .setDefaultColorSchemeParams(colorParams)
          .setColorScheme(CustomTabsIntent.COLOR_SCHEME_DARK)
          .setShowTitle(true)
          .setUrlBarHidingEnabled(true)
          .build()

      customTabsIntent.launchUrl(context, parsedUri)
    } catch (_: Exception) {
      try {
        val fallbackIntent =
          Intent(Intent.ACTION_VIEW, parsedUri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          }
        context.startActivity(fallbackIntent)
      } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, "No browser app found to open $url", Toast.LENGTH_SHORT).show()
      }
    }
  }

  fun shareJobSummary(context: Context, job: JobAlert) {
    val shareText = JobShareFormatter.format(job)
    val sendIntent =
      Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "${job.organization} — ${job.postTitle}")
        putExtra(Intent.EXTRA_TEXT, shareText)
      }
    val chooser =
      Intent.createChooser(sendIntent, "Share opportunity").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
    try {
      context.startActivity(chooser)
    } catch (_: ActivityNotFoundException) {
      Toast.makeText(context, "No sharing app available", Toast.LENGTH_SHORT).show()
    }
  }
}
