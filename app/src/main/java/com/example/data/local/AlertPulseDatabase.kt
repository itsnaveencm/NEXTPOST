package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
  entities =
    [
      SavedJobEntity::class,
      AppliedJobEntity::class,
      CachedJobEntity::class,
      CachedExamUpdateEntity::class,
      FeedSyncMetadataEntity::class,
    ],
  version = 5,
  exportSchema = false,
)
abstract class AlertPulseDatabase : RoomDatabase() {
  abstract fun savedJobDao(): SavedJobDao

  abstract fun appliedJobDao(): AppliedJobDao

  abstract fun cachedFeedDao(): CachedFeedDao

  companion object {
    @Volatile private var instance: AlertPulseDatabase? = null

    private val MIGRATION_1_2 =
      object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
          db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `applied_jobs` (
              `jobId` TEXT NOT NULL,
              `registrationNumber` TEXT NOT NULL,
              `appliedDateDisplay` TEXT NOT NULL,
              `appliedAtMillis` INTEGER NOT NULL,
              `stageName` TEXT NOT NULL,
              `statusNote` TEXT,
              PRIMARY KEY(`jobId`)
            )
            """.trimIndent()
          )
        }
      }

    private val MIGRATION_2_3 =
      object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
          db.execSQL(
            "ALTER TABLE `applied_jobs` ADD COLUMN `personalStatusName` TEXT NOT NULL DEFAULT 'APPLIED'"
          )
        }
      }

    private val MIGRATION_3_4 =
      object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
          db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `cached_jobs` (
              `id` TEXT NOT NULL,
              `organization` TEXT NOT NULL,
              `postTitle` TEXT NOT NULL,
              `sectorName` TEXT NOT NULL,
              `centralAgencyName` TEXT,
              `privateDomainName` TEXT,
              `stateCode` TEXT,
              `locationLabel` TEXT NOT NULL,
              `vacancyCountLabel` TEXT NOT NULL,
              `qualificationTag` TEXT NOT NULL,
              `minGeneralQualName` TEXT,
              `acceptedExactLevelsCsv` TEXT NOT NULL,
              `allowedStreamsCsv` TEXT NOT NULL,
              `minAge` INTEGER NOT NULL,
              `maxAge` INTEGER NOT NULL,
              `applicationStartDate` TEXT NOT NULL,
              `applicationEndDate` TEXT NOT NULL,
              `officialPdfUrl` TEXT NOT NULL,
              `directApplyUrl` TEXT NOT NULL,
              `officialDomain` TEXT NOT NULL,
              `lastVerifiedTimestamp` TEXT NOT NULL,
              `eligibilityBrief` TEXT NOT NULL,
              `ageLimitBrief` TEXT NOT NULL,
              `selectionAndFeeBrief` TEXT NOT NULL,
              `salaryPayScale` TEXT NOT NULL,
              `examDateDisplay` TEXT NOT NULL,
              `experienceRequired` TEXT NOT NULL,
              `orgTag` TEXT,
              `subCategoryTag` TEXT,
              `lastSyncedAtMillis` INTEGER NOT NULL,
              PRIMARY KEY(`id`)
            )
            """.trimIndent()
          )
          db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `cached_exam_updates` (
              `id` TEXT NOT NULL,
              `organization` TEXT NOT NULL,
              `examTitle` TEXT NOT NULL,
              `typeName` TEXT NOT NULL,
              `releasedDateDisplay` TEXT NOT NULL,
              `examDateDisplay` TEXT NOT NULL,
              `statusLabel` TEXT NOT NULL,
              `summaryNote` TEXT NOT NULL,
              `portalLoginUrl` TEXT NOT NULL,
              `noticePdfUrl` TEXT,
              `officialDomain` TEXT NOT NULL,
              `lastVerifiedTimestamp` TEXT NOT NULL,
              `lastSyncedAtMillis` INTEGER NOT NULL,
              PRIMARY KEY(`id`)
            )
            """.trimIndent()
          )
          db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `feed_sync_metadata` (
              `id` TEXT NOT NULL,
              `endpointUrl` TEXT NOT NULL,
              `lastSyncedAtMillis` INTEGER NOT NULL,
              `syncStatus` TEXT NOT NULL,
              `verifiedJobsCount` INTEGER NOT NULL,
              `verifiedUpdatesCount` INTEGER NOT NULL,
              `blockedRecordsCount` INTEGER NOT NULL,
              PRIMARY KEY(`id`)
            )
            """.trimIndent()
          )
        }
      }

    private val MIGRATION_4_5 =
      object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
          db.execSQL("ALTER TABLE `cached_jobs` ADD COLUMN `generalObcFee` TEXT")
          db.execSQL("ALTER TABLE `cached_jobs` ADD COLUMN `scStFemaleFee` TEXT")
        }
      }

    fun getInstance(context: Context): AlertPulseDatabase {
      return instance
        ?: synchronized(this) {
          instance
            ?: Room.databaseBuilder(
                context.applicationContext,
                AlertPulseDatabase::class.java,
                "alert_pulse_db",
              )
              .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
              .build()
              .also { instance = it }
        }
    }
  }
}
