package com.example.projekt3.powiadomienia

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.projekt3.aglobalApk.DatabaseHelper
import java.util.Calendar
import java.util.concurrent.TimeUnit

open class Notifications : AppCompatActivity() {
    private val REQUEST_NOTIFICATION_PERMISSION = 1
    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = DatabaseHelper(this)
        createNotificationChannel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkNotificationPermission()
        }
    }

    fun enableNotifications() {
        // Tutaj zaplanuj powiadomienia
        // Na przykład: scheduleNotifications(context, option, timeInHours)
        val powiadomienia = db.baza_pobierz("ustawienia", "powiadomienia")
        val powiadomienia1 = db.baza_pobierz("ustawienia", "powiadomienia1")
        val powiadomienia2 = db.baza_pobierz("ustawienia", "powiadomienia2")
        val powiadomienia3 = db.baza_pobierz("ustawienia", "powiadomienia3")



        powiadomienia?.let { scheduleNotifications(this, it.toInt(), 24) }
        powiadomienia1?.let { scheduleNotifications(this, it.toInt(), 730) }
        powiadomienia2?.let { scheduleNotifications(this, it.toInt(), 336) }

    }

    fun disableNotifications() {
        // Tutaj wstrzymaj wszystkie powiadomienia
        val workManager = WorkManager.getInstance(this)
        workManager.cancelUniqueWork("notificationWork")
        workManager.cancelUniqueWork("repeatingNotificationWork")
        workManager.cancelAllWork()
    }

    private fun checkNotificationPermission() {
        // Sprawdź, czy aplikacja ma już uprawnienie
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Jeśli nie, poproś o uprawnienie
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_NOTIFICATION_PERMISSION
            )
        }
    }

    // Tworzenie kanału powiadomień
    // Tworzenie kanału powiadomień dla Androida 8.0 i wyższych
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "my_channel_id"
            val channelName = "My Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Opis mojego kanału powiadomień"
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    // Zapisuje ustawiony czas powiadomienia do SharedPreferences
    fun saveNotificationTime(hour: Int, minute: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("notification_hour", hour)
        editor.putInt("notification_minute", minute)
        editor.apply()
    }
// Harmonogram powiadomień w zależności od wybranej opcji

    fun scheduleNotifications(context: Context, option: Int, timeInHours: Int = 0) {

        val workManager = WorkManager.getInstance(context)
        val inputData: Data

        when (option) {
            // 1. Powiadomienia wyłączone

            // 2. Powiadomienia co 24 godziny (normalne)
            92 -> {
                inputData = workDataOf(
                    "title" to "Twój żółw jest brudny!",
                    "message" to "Umyj go zanim zacznie śmierdzieć :).",
                    "isPersistent" to false
                )
                scheduleDailyNotification(context, workManager, inputData)
            }

            93 -> {
                inputData = workDataOf(
                    "title" to "Twój żółw ma brudny domek",
                    "message" to "Zajmij się nim, ty też czasem sprzątasz :).",
                    "isPersistent" to false
                )
                scheduleDailyNotification(context, workManager, inputData)
            }

            2 -> {
                inputData = workDataOf(
                    "title" to "Twój żółw jest już głodny!",
                    "message" to "Nakarm go. Ty też nie lubisz głodować :).",
                    "isPersistent" to false
                )
                scheduleDailyNotification(context, workManager, inputData)
            }

            // 3. Powiadomienia co 24 godziny (trwałe, wyłączane przyciskiem)
            3 -> {
                inputData = workDataOf(
                    "title" to "Tylko nie głodówka!",
                    "message" to "Wpadnij do aplikacji nakarmić swojego żółwia.",
                    "isPersistent" to true
                )
                scheduleDailyNotification(context, workManager, inputData)
            }

            // 4. Powiadomienia co 24 godziny, powtarzane co 15 minut, jeśli użytkownik nie zareaguje
            4 -> {
                inputData = workDataOf(
                    "title" to "Hej, to ja twój żółw!",
                    "message" to "Wpadnij do aplikacji mnie nakarmić, inaczej będę brzęczał do skutku.",
                    "isPersistent" to true
                )

                // Oblicz opóźnienie dla pierwszego powiadomienia
                val delayInMillis = calculateInitialDelay(
                    sharedPreferences.getInt("notification_hour", 9),
                    sharedPreferences.getInt("notification_minute", 0)
                )

                scheduleDailyNotification(context, workManager, inputData)

                // Zaplanuj powiadomienia co 15 minut z opóźnieniem
                val repeatInputData = workDataOf(
                    "title" to "Powiadomienie z Ponowieniem",
                    "message" to "Powiadomienie będzie się powtarzać co 15 minut, dopóki nie zareagujesz.",
                    "isPersistent" to true
                )
                scheduleRepeatingNotification(
                    context,
                    workManager,
                    repeatInputData,
                    15,
                    delayInMillis
                )
            }
        }
    }

    // Funkcja planująca codzienne powiadomienia
    private fun scheduleDailyNotification(
        context: Context,
        workManager: WorkManager,
        inputData: Data
    ) {
        // Odczytaj zapisaną godzinę z SharedPreferences
        val hour = sharedPreferences.getInt("notification_hour", 9) // Domyślnie 9:00
        val minute = sharedPreferences.getInt("notification_minute", 0) // Domyślnie 00 minut

        // Oblicz opóźnienie dla pierwszego powiadomienia
        val delayInMillis = calculateInitialDelay(hour, minute)

        // Zlecenie pracy powiadomienia z codzienną powtarzalnością
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
            .setInputData(inputData)
            .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "notificationWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    // Funkcja planująca powtarzające się powiadomienia co określoną ilość minut
    private fun scheduleRepeatingNotification(
        context: Context,
        workManager: WorkManager,
        inputData: Data,
        intervalMinutes: Long,
        initialDelay: Long
    ) {
        val workRequest =
            PeriodicWorkRequestBuilder<NotificationWorker>(intervalMinutes, TimeUnit.MINUTES)
                .setInputData(inputData)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build()

        workManager.enqueueUniquePeriodicWork(
            "repeatingNotificationWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    // Oblicza początkowe opóźnienie, aby powiadomienia wyświetlały się o wybranej godzinie
    private fun calculateInitialDelay(hour: Int, minute: Int): Long {
        val currentTime = Calendar.getInstance()
        val selectedTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        // Jeśli wybrana godzina jest przed bieżącą, zaplanuj powiadomienie na następny dzień
        if (selectedTime.before(currentTime)) {
            selectedTime.add(Calendar.DAY_OF_MONTH, 1)
        }

        return selectedTime.timeInMillis - currentTime.timeInMillis
    }


}
