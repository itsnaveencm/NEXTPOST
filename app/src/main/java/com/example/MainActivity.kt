package com.example

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AlertPulseDatabase
import com.example.data.local.CandidateProfileStore
import com.example.data.remote.OfficialFeedApiService
import com.example.data.repository.LiveVerifiedJobRepository
import com.example.navigation.AlertPulseApp
import com.example.ui.theme.AlertPulseTheme
import com.example.ui.viewmodel.AlertsViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.dark(Color.BLACK),
      navigationBarStyle = SystemBarStyle.dark(Color.BLACK),
    )

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
    val factory = AlertsViewModel.Factory(repository, profileStore)

    setContent {
      AlertPulseTheme {
        val alertsViewModel: AlertsViewModel = viewModel(factory = factory)
        AlertPulseApp(viewModel = alertsViewModel)
      }
    }
  }
}
