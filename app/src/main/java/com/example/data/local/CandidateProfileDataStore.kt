package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.AcademicStream
import com.example.data.model.CandidateProfile
import com.example.data.model.NotificationPreferences
import com.example.data.model.QualificationLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.candidateProfileDataStore: DataStore<Preferences> by
  preferencesDataStore(name = "candidate_profile_prefs")

class CandidateProfileStore(private val context: Context) {

  private object Keys {
    val QUALIFICATION = stringPreferencesKey("qualification")
    val STREAM = stringPreferencesKey("stream")
    val AGE = intPreferencesKey("age")
    val HOME_STATE_CODE = stringPreferencesKey("home_state_code")
    val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    val SAVED_NOTIFICATIONS = stringSetPreferencesKey("saved_notifications")
    val SAVED_ORGS = stringSetPreferencesKey("saved_orgs")
    val FEED_ENDPOINT_URL = stringPreferencesKey("feed_endpoint_url")
    val NOTIF_ELIGIBLE_JOBS = booleanPreferencesKey("notif_eligible_jobs")
    val NOTIF_DEADLINE_REMINDERS = booleanPreferencesKey("notif_deadline_reminders")
    val NOTIF_EXAM_UPDATES = booleanPreferencesKey("notif_exam_updates")
    val SEEN_ACTIVE_JOB_IDS = stringSetPreferencesKey("seen_active_job_ids")
    val SEEN_EXAM_UPDATE_IDS = stringSetPreferencesKey("seen_exam_update_ids")
    val NOTIFICATION_PROMPT_SHOWN = booleanPreferencesKey("notification_prompt_shown")
  }

  val profileFlow: Flow<CandidateProfile> =
    context.candidateProfileDataStore.data.map { prefs ->
      val qualification =
        prefs[Keys.QUALIFICATION]?.let { name ->
          QualificationLevel.entries.firstOrNull { it.name == name }
        } ?: QualificationLevel.BTECH

      val stream =
        prefs[Keys.STREAM]?.let { name ->
          AcademicStream.entries.firstOrNull { it.name == name }
        } ?: AcademicStream.ECE

      val age = (prefs[Keys.AGE] ?: 23).coerceIn(18, 45)
      val homeStateCode = prefs[Keys.HOME_STATE_CODE] ?: "KL"
      val completed = prefs[Keys.ONBOARDING_COMPLETED] ?: false

      CandidateProfile(
        qualification = qualification,
        stream = stream,
        age = age,
        homeStateCode = homeStateCode,
        hasCompletedOnboarding = completed,
      )
    }

  val notificationPrefsFlow: Flow<NotificationPreferences> =
    context.candidateProfileDataStore.data.map { prefs ->
      NotificationPreferences(
        newEligibleJobsEnabled = prefs[Keys.NOTIF_ELIGIBLE_JOBS] ?: true,
        deadlineRemindersEnabled = prefs[Keys.NOTIF_DEADLINE_REMINDERS] ?: true,
        examUpdatesEnabled = prefs[Keys.NOTIF_EXAM_UPDATES] ?: true,
      )
    }

  val savedNotificationIdsFlow: Flow<Set<String>> =
    context.candidateProfileDataStore.data.map { prefs ->
      prefs[Keys.SAVED_NOTIFICATIONS] ?: emptySet()
    }

  val savedOrgIdsFlow: Flow<Set<String>> =
    context.candidateProfileDataStore.data.map { prefs ->
      prefs[Keys.SAVED_ORGS] ?: emptySet()
    }

  val feedEndpointUrlFlow: Flow<String> =
    context.candidateProfileDataStore.data.map { prefs ->
      prefs[Keys.FEED_ENDPOINT_URL] ?: ""
    }

  val seenActiveJobIdsFlow: Flow<Set<String>> =
    context.candidateProfileDataStore.data.map { prefs ->
      prefs[Keys.SEEN_ACTIVE_JOB_IDS] ?: emptySet()
    }

  val seenExamUpdateIdsFlow: Flow<Set<String>> =
    context.candidateProfileDataStore.data.map { prefs ->
      prefs[Keys.SEEN_EXAM_UPDATE_IDS] ?: emptySet()
    }

  val notificationPromptShownFlow: Flow<Boolean> =
    context.candidateProfileDataStore.data.map { prefs ->
      prefs[Keys.NOTIFICATION_PROMPT_SHOWN] ?: false
    }

  suspend fun saveProfile(profile: CandidateProfile) {
    context.candidateProfileDataStore.edit { prefs ->
      prefs[Keys.QUALIFICATION] = profile.qualification.name
      prefs[Keys.STREAM] = profile.stream.name
      prefs[Keys.AGE] = profile.age.coerceIn(18, 45)
      prefs[Keys.HOME_STATE_CODE] = profile.homeStateCode
      prefs[Keys.ONBOARDING_COMPLETED] = true
    }
  }

  suspend fun saveNotificationPreferences(notifPrefs: NotificationPreferences) {
    context.candidateProfileDataStore.edit { prefs ->
      prefs[Keys.NOTIF_ELIGIBLE_JOBS] = notifPrefs.newEligibleJobsEnabled
      prefs[Keys.NOTIF_DEADLINE_REMINDERS] = notifPrefs.deadlineRemindersEnabled
      prefs[Keys.NOTIF_EXAM_UPDATES] = notifPrefs.examUpdatesEnabled
    }
  }

  suspend fun saveFeedEndpointUrl(url: String) {
    context.candidateProfileDataStore.edit { prefs ->
      prefs[Keys.FEED_ENDPOINT_URL] = url.trim()
    }
  }

  suspend fun markJobsSeen(jobIds: Collection<String>) {
    if (jobIds.isEmpty()) return
    context.candidateProfileDataStore.edit { prefs ->
      val current = prefs[Keys.SEEN_ACTIVE_JOB_IDS]?.toMutableSet() ?: mutableSetOf()
      current.addAll(jobIds)
      prefs[Keys.SEEN_ACTIVE_JOB_IDS] = current
    }
  }

  suspend fun markExamUpdatesSeen(updateIds: Collection<String>) {
    if (updateIds.isEmpty()) return
    context.candidateProfileDataStore.edit { prefs ->
      val current = prefs[Keys.SEEN_EXAM_UPDATE_IDS]?.toMutableSet() ?: mutableSetOf()
      current.addAll(updateIds)
      prefs[Keys.SEEN_EXAM_UPDATE_IDS] = current
    }
  }

  suspend fun markNotificationPromptShown() {
    context.candidateProfileDataStore.edit { prefs ->
      prefs[Keys.NOTIFICATION_PROMPT_SHOWN] = true
    }
  }

  suspend fun toggleSavedNotification(updateId: String) {
    context.candidateProfileDataStore.edit { prefs ->
      val current = prefs[Keys.SAVED_NOTIFICATIONS]?.toMutableSet() ?: mutableSetOf()
      if (!current.add(updateId)) {
        current.remove(updateId)
      }
      prefs[Keys.SAVED_NOTIFICATIONS] = current
    }
  }

  suspend fun toggleSavedOrg(orgOrCompanyId: String) {
    context.candidateProfileDataStore.edit { prefs ->
      val current = prefs[Keys.SAVED_ORGS]?.toMutableSet() ?: mutableSetOf()
      if (!current.add(orgOrCompanyId)) {
        current.remove(orgOrCompanyId)
      }
      prefs[Keys.SAVED_ORGS] = current
    }
  }
}
