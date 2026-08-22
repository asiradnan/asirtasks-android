package com.asiradnan.asirtasks

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.asiradnan.asirtasks.data.AppContainer
import com.asiradnan.asirtasks.data.AppDataContainer
import com.asiradnan.asirtasks.worker.SyncWorker
import java.util.concurrent.TimeUnit


class AsirTasksApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)

        createNotificationChannel()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Only sync when online
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .addTag("SyncTag")
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "PeriodicSync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }

    private fun createNotificationChannel() {
        val name = "Task Reminders"
        val descriptionText = "Notifications for due tasks"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("TASK_REMINDERS", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}