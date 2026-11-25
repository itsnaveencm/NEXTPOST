package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ApplicationLifecycleStage
import com.example.data.model.PersonalApplicationStatus

@Entity(tableName = "applied_jobs")
data class AppliedJobEntity(
  @PrimaryKey val jobId: String,
  val registrationNumber: String,
  val appliedDateDisplay: String,
  val appliedAtMillis: Long,
  val stageName: String = ApplicationLifecycleStage.APPLIED.name,
  val personalStatusName: String = PersonalApplicationStatus.APPLIED.name,
  val statusNote: String? = null,
)
