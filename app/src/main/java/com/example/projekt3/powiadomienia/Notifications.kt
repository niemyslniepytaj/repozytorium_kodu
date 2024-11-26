package com.example.projekt3.powiadomienia

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
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
        createNotificationChannel("feeding_channel")
        createNotificationChannel("cleaning_channel")
        createNotificationChannel("house_cleaning_channel")
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




        powiadomienia?.let { scheduleNotifications(this, it.toInt(), 24) }
        powiadomienia1?.let { scheduleNotifications(this, it.toInt(), 730) }
        powiadomienia2?.let { scheduleNotifications(this, it.toInt(), 336) }

    }
    fun cancelA(){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)
    }
    fun cancelB(){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(2)
    }
    fun cancelC(){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(3)
    }
    fun disablebNotifications() {
        // Tutaj wstrzymaj wszystkie powiadomienia
        val workManager = WorkManager.getInstance(this)
        workManager.cancelUniqueWork("bWork")
    }
    fun disablecNotifications() {
        // Tutaj wstrzymaj wszystkie powiadomienia
        val workManager = WorkManager.getInstance(this)
        workManager.cancelUniqueWork("cWork")

    }
    fun disableNotifications() {
        // Tutaj wstrzymaj wszystkie powiadomienia
        val workManager = WorkManager.getInstance(this)
        workManager.cancelUniqueWork("aWork")
        workManager.cancelUniqueWork("bWork")
        workManager.cancelUniqueWork("cWork")
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
    private fun createNotificationChannel(channelID: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channelName = "My Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelID, channelName, importance).apply {
                description = "Opis mojego kanału powiadomień"
            }

            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
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

        when (option) {
            2, 3, 4 -> {
                val titlesAndMessages = listOf(
                    "Nakarm mnie!" to "Twój żółw jest głodny.",
                    "Czas na obiad!" to "Twój żółw czeka na posiłek.",
                    "Nie zapomnij o mnie!" to "Twój żółw prosi o jedzenie.",
                    "Hej, głodny jestem!" to "Twój żółw ma pusty żołądek.",
                    "Czas karmienia!" to "Twój żółw czeka na smakołyk.",
                    "Pomóż mi, głód mnie dopadł!" to "Nakarm swojego żółwia już teraz.",
                    "Czas na przekąskę!" to "Twój żółw prosi o jedzenie.",
                    "Gdzie moje jedzenie?" to "Twój żółw jest głodny, zajmij się nim.",
                    "Nie zapominaj o karmieniu!" to "Twój żółw czeka z utęsknieniem na posiłek.",
                    "Czas uzupełnić energię!" to "Twój żółw potrzebuje jedzenia.",
                    "Zadbaj o mnie!" to "Twój żółw prosi o posiłek.",
                    "Posiłek czeka!" to "Twój żółw czeka na smaczny kąsek."
                )
                val randomData = titlesAndMessages.random()

                val inputData = workDataOf(
                    "title" to randomData.first,
                    "message" to randomData.second,
                    "channelID" to "feeding_channel", // Kanał dla opcji 2, 3, 4
                    "isPersistent" to (option != 2)
                )
                scheduleDailyNotification(context, workManager, inputData)
            }

            13 -> {
                val titlesAndMessages = listOf(
                    "Czas na kąpiel!" to "Twój żółw potrzebuje czystej skorupy.",
                    "Czysty żółw to szczęśliwy żółw!" to "Umyj swojego żółwia, by był zdrowy.",
                    "Zadbaj o czystość skorupy!" to "Twój żółw wymaga kąpieli dla zdrowia.",
                    "Nie zapomnij o czystości!" to "Twój żółw potrzebuje pielęgnacji."
                )
                val randomData = titlesAndMessages.random()

                val inputData = workDataOf(
                    "title" to randomData.first,
                    "message" to randomData.second,
                    "channelID" to "cleaning_channel", // Kanał dla opcji 12
                    "isPersistent" to true
                )
                scheduleMonthlyNotification(context, workManager, inputData)
            }

            12 -> {
                val titlesAndMessages = listOf(
                    "Posprzątaj domek!" to "Twój żółw prosi o czysty dom.",
                    "Porządki czas zacząć!" to "Twój żółw czeka na czystość w swoim domu.",
                    "Utrzymaj domek w porządku!" to "Twój żółw prosi o posprzątanie.",
                    "Czystość to zdrowie!" to "Twój żółw potrzebuje czystego domu."
                )
                val randomData = titlesAndMessages.random()

                val inputData = workDataOf(
                    "title" to randomData.first,
                    "message" to randomData.second,
                    "channelID" to "house_cleaning_channel", // Kanał dla opcji 1
                    "isPersistent" to true
                )
                scheduleWeeklyNotification(context, workManager, inputData)
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
            "aWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
    private fun scheduleWeeklyNotification(
        context: Context,
        workManager: WorkManager,
        inputData: Data
    ) {
        // Odczytaj zapisaną godzinę z SharedPreferences
        val hour = sharedPreferences.getInt("notification_hour", 9) // Domyślnie 9:00
        var minute = sharedPreferences.getInt("notification_minute", 0) // Domyślnie 00 minut
        minute+=2
        // Oblicz opóźnienie dla pierwszego powiadomienia
        val delayInMillis = calculateInitialDelay(hour, minute)

        // Zlecenie pracy powiadomienia z codzienną powtarzalnością
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(360, TimeUnit.HOURS)
            .setInputData(inputData)
            .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "bWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
    private fun scheduleMonthlyNotification(
        context: Context,
        workManager: WorkManager,
        inputData: Data
    ) {
        // Odczytaj zapisaną godzinę z SharedPreferences
        val hour = sharedPreferences.getInt("notification_hour", 9) // Domyślnie 9:00
        var minute = sharedPreferences.getInt("notification_minute", 0) // Domyślnie 00 minut
        minute+=4
        // Oblicz opóźnienie dla pierwszego powiadomienia
        val delayInMillis = calculateInitialDelay(hour, minute)

        // Zlecenie pracy powiadomienia z codzienną powtarzalnością
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(720, TimeUnit.HOURS)
            .setInputData(inputData)
            .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "cWork",
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
