package com.example

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AlertPulseDatabase
import com.example.data.local.CandidateProfileStore
import com.example.data.remote.OfficialFeedApiService
import com.example.data.repository.LiveVerifiedJobRepository
import com.example.navigation.AlertPulseApp
import com.example.notifications.NextPostNotificationHelper
import com.example.ui.theme.AlertPulseTheme
import com.example.ui.viewmodel.AlertsViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
  private val deepLinkJobId = MutableStateFlow<String?>(null)
  private val deepLinkUpdateId = MutableStateFlow<String?>(null)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.dark(Color.BLACK),
      navigationBarStyle = SystemBarStyle.dark(Color.BLACK),
    )

    NextPostNotificationHelper.ensureChannels(applicationContext)
    consumeIntentDeepLinks(intent)

    val database = AlertPulseDatabase.getInstance(applicationContext)
    val profileStore = CandidateProfileStore(applicationContext)
    val apiService = OfficialFeedApiService.create()
    val repository =
      LiveVerifiedJobRepository(
        savedJobDao = database.savedJobDao(),
        appliedJobDao = database.appliedJobDao(),
        cachedFeedDao = database.cachedFeedDao(),
        apiService = apiService,
        defaultFeedEndpointUrl = BuildConfig.NEXTPOST_FEED_API_URL,
      )
    val factory = AlertsViewModel.Factory(repository, profileStore, applicationContext)

    setContent {
      AlertPulseTheme {
        val alertsViewModel: AlertsViewModel = viewModel(factory = factory)
        val pendingJobId by deepLinkJobId.collectAsStateWithLifecycle()
        val pendingUpdateId by deepLinkUpdateId.collectAsStateWithLifecycle()
        AlertPulseApp(
          viewModel = alertsViewModel,
          deepLinkJobId = pendingJobId,
          onConsumeDeepLinkJob = { deepLinkJobId.value = null },
          deepLinkUpdateId = pendingUpdateId,
          onConsumeDeepLinkUpdate = { deepLinkUpdateId.value = null },
        )
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    consumeIntentDeepLinks(intent)
  }

  private fun consumeIntentDeepLinks(intent: Intent?) {
    val jobId = intent?.getStringExtra(NextPostNotificationHelper.EXTRA_JOB_ID)?.takeIf { it.isNotBlank() }
    val updateId =
      intent?.getStringExtra(NextPostNotificationHelper.EXTRA_UPDATE_ID)?.takeIf { it.isNotBlank() }
    if (jobId != null) {
      deepLinkJobId.value = jobId
      intent?.removeExtra(NextPostNotificationHelper.EXTRA_JOB_ID)
    }
    if (updateId != null) {
      deepLinkUpdateId.value = updateId
      intent?.removeExtra(NextPostNotificationHelper.EXTRA_UPDATE_ID)
    }
  }
}
