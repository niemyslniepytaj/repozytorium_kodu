package com.example.projekt3.powiadomienia

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.projekt3.R
import com.example.projekt3.home.MainFragment


class NotificationWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        // Pobierz dane z inputData
        val title = inputData.getString("title") ?: "Powiadomienie"
        val message = inputData.getString("message") ?: "Masz nowe powiadomienie"
        val isPersistent = inputData.getBoolean("isPersistent", false)

        sendNotification(title, message, isPersistent)
        return Result.success()
    }

    private fun sendNotification(title: String, message: String, isPersistent: Boolean) {
        val channelId = "my_channel_id"

        // Tworzenie intencji powiązanej z powiadomieniem
        val intent = Intent(applicationContext, MainFragment::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Tworzenie powiadomienia
        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.baseline_twotone_dinner_dining_24)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(!isPersistent)

        if (isPersistent) {
            notificationBuilder.setOngoing(true) // Trwałe powiadomienie
        }

        // Wyślij powiadomienie
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notificationBuilder.build())
    }
}
