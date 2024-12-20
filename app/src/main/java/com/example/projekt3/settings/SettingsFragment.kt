package com.example.projekt3.settings

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.Fragment
import com.example.projekt3.aglobalApk.Baseactivity
import com.example.projekt3.aglobalApk.DatabaseHelper
import com.example.projekt3.R
import com.example.projekt3.powiadomienia.Notifications
import java.util.Calendar

class SettingsFragment : Fragment() {
    private lateinit var db: DatabaseHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = DatabaseHelper(requireContext())
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as Baseactivity).setViewMode("Personalizacja")

        val offpowiadomienia = view.findViewById<Switch>(R.id.switch1)
        val powiadomienia_status = db.baza_pobierz("ustawienia", "powiadomieniaonoff")

        val buttonnazwa = view.findViewById<Button>(R.id.button9)
        val buttongatunek = view.findViewById<Button>(R.id.button6)
        val buttonkarmienie = view.findViewById<Button>(R.id.button8)
        val buttonposiadanie = view.findViewById<Button>(R.id.button7)

        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)
        val radio2 = view.findViewById<RadioButton>(R.id.radioButton2)
        val radio3 = view.findViewById<RadioButton>(R.id.radioButton3)
        val radio4 = view.findViewById<RadioButton>(R.id.radioButton4)


        val description = view.findViewById<TextView>(R.id.description)

        val buttonMotyw = view.findViewById<Button>(R.id.button90)

        val powd_1 = view.findViewById<Switch>(R.id.switch2)
        val powd_2 = view.findViewById<Switch>(R.id.switch3)


// ---------- Pobierz wartości z bazy i zaimplementuj w aktywności --------------------------------------------------------------------------------------------------------------------

        val nazwa = db.baza_pobierz("turtle", "nazwa")

        val gatunek = db.baza_pobierz("turtle", "gatunek")
        val karmienie = db.baza_pobierz("turtle", "karmienie")
        val posiadanie = db.baza_pobierz("turtle", "posiadanie")

        val powiadomienia = db.baza_pobierz("ustawienia", "powiadomienia")
        val description_status = db.baza_pobierz("ustawienia", "description")


        val powd_1_status = db.baza_pobierz("ustawienia", "powiadomienia1")
        val powd_2_status = db.baza_pobierz("ustawienia", "powiadomienia2")

        buttonMotyw.text = db.baza_pobierz("ustawienia", "motyw")


        buttonMotyw.setOnClickListener {
            showMotywDialog(buttonMotyw)
        }

        db.baza_wstaw("ustawienia", "current", "4")


        buttonkarmienie.text = karmienie
        buttonnazwa.text = nazwa
        buttonposiadanie.text = posiadanie
        buttongatunek.text = gatunek
        description.text = description_status

        radioGroup.check(
            when (powiadomienia) {
                "2" -> radio2.id
                "3" -> radio3.id
                "4" -> radio4.id
                else -> radio2.id
            }
        )
        description.text = db.baza_pobierz("ustawienia", "description")

        powd_1.isChecked = (powd_1_status == "12")
        powd_2.isChecked = (powd_2_status == "13")


        when (powiadomienia_status) {
            "1" -> {
                offpowiadomienia.isChecked = true
                radio2.isEnabled = false
                radio3.isEnabled = false
                radio4.isEnabled = false
                powd_1.isEnabled = false
                powd_2.isEnabled = false
                description.text = "Powiadomienia i powiadomienia dodatkowe są wyłączone."
                buttonkarmienie.isEnabled = false
                buttonkarmienie.text = "Wyłączone"
            }

            else -> {
                offpowiadomienia.isChecked = false
                radio2.isEnabled = true
                radio3.isEnabled = true
                radio4.isEnabled = true
                powd_1.isEnabled = true
                powd_2.isEnabled = true

                buttonkarmienie.isEnabled = true
                val selectedTime = db.baza_pobierz("turtle", "karmienie")
                buttonkarmienie.text = selectedTime
            }
        }

//----------------------------------------------------------------------------------------------------------------------------------------
        offpowiadomienia.setOnClickListener {
            if (offpowiadomienia.isChecked) {
                (activity as Notifications).disableNotifications()
                radio2.isEnabled = false
                radio3.isEnabled = false
                radio4.isEnabled = false
                powd_1.isEnabled = false
                powd_2.isEnabled = false

                buttonkarmienie.isEnabled = false
                buttonkarmienie.text = "Wyłączone"
                description.text = "Powiadomienia i powiadomienia dodatkowe są wyłączone."
                db.baza_wstaw("ustawienia", "powiadomieniaonoff", "1")
            } else {
                if((activity as Baseactivity).areNotificationsEnabled(requireContext())){
                    (activity as Notifications).enableNotifications()
                    radio2.isEnabled = true
                    radio3.isEnabled = true
                    radio4.isEnabled = true
                    powd_1.isEnabled = true
                    powd_2.isEnabled = true

                    buttonkarmienie.isEnabled = true
                    val selectedTime = db.baza_pobierz("turtle", "karmienie")
                    buttonkarmienie.text = selectedTime
                    description.text = db.baza_pobierz("ustawienia", "description")
                    db.baza_wstaw("ustawienia", "powiadomieniaonoff", "-1")
                }else{
                    db.baza_wstaw("ustawienia", "powiadomieniaonoff", "1")
                    Toast.makeText(context, "Powiadomienia są wyłączone, włącz powiadomienia w ustawieniach telefonu", Toast.LENGTH_SHORT).show()
                    offpowiadomienia.isChecked = true
                }

            }
        }


        powd_1.setOnClickListener {
            db.baza_wstaw("ustawienia", "powiadomienia1", if (powd_1.isChecked) "12" else "-1")
            if (powd_2.isChecked) {
                (activity as Notifications).scheduleNotifications(requireContext(), 12, 720)
            } else {
                (activity as Notifications).disablebNotifications()
            }
        }

        powd_2.setOnClickListener {
            db.baza_wstaw("ustawienia", "powiadomienia2", if (powd_2.isChecked) "13" else "-1")
            if (powd_2.isChecked) {
                (activity as Notifications).scheduleNotifications(requireContext(), 13, 720)
            } else {
                (activity as Notifications).disablecNotifications()
            }
        }

        buttonkarmienie.setOnClickListener {
            showTimePickerDialog(buttonkarmienie)
        }
        buttonnazwa.setOnClickListener {
            showInputDialog(buttonnazwa)
        }
        buttongatunek.setOnClickListener {
            showOptionsDialog(buttongatunek)
        }
        buttonposiadanie.setOnClickListener {
            val a = buttonposiadanie.text.toString()
            if (a == "Tak") {
                buttonposiadanie.text = "Nie"
                db.baza_wstaw("turtle", "posiadanie", "Nie")
            } else {
                buttonposiadanie.text = "Tak"
                db.baza_wstaw("turtle", "posiadanie", "Tak")
            }
        }


        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = group.findViewById<RadioButton>(checkedId)

            when (selectedRadioButton) {

                radio2 -> {
                    (activity as Notifications).scheduleNotifications(requireContext(), 2, 24)
                    description.text =
                        "Powiadomienia przypominające o karmieniu żółwia o ustalonej godzinie."
                    db.baza_wstaw("ustawienia", "powiadomienia", "2")
                    db.baza_wstaw("ustawienia", "description", description.text.toString())
                }

                radio3 -> {
                    (activity as Notifications).scheduleNotifications(requireContext(), 3, 24)
                    description.text =
                        "Powiadomienia są nieusuwalne i będą się powtarzać codziennie o ustalonej godzinie. Aby je usunąć, wystarczy nakarmić żółwia w aplikacji."
                    db.baza_wstaw("ustawienia", "powiadomienia", "3")
                    db.baza_wstaw("ustawienia", "description", description.text.toString())
                }

                radio4 -> {
                    (activity as Notifications).scheduleNotifications(requireContext(), 4, 24)
                    description.text =
                        "Powiadomienia nieusuwalne, powtarzane co 24 godziny. Dopóki nie nakarmisz żółwia w aplikacji będą się ponawiać co 15 minut."
                    db.baza_wstaw("ustawienia", "powiadomienia", "4")
                    db.baza_wstaw("ustawienia", "description", description.text.toString())
                }
            }

        }

    }

    // Funkcja do wyświetlenia TimePicker i zaktualizowania powiadomienia
    private fun showTimePickerDialog(button: Button) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(context, { _, hourOfDay, minute ->
            // Zapisz wybraną godzinę w przycisku (lub gdziekolwiek indziej)
            val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
            button.text = selectedTime
            db.baza_wstaw("turtle", "karmienie", selectedTime)
            // Oblicz czas w godzinach, który przekażesz do powiadomienia
            val timeInHours = hourOfDay
            (activity as Notifications).saveNotificationTime(hourOfDay, minute)
            Toast.makeText(context, "Powiadomienie o $selectedTime", Toast.LENGTH_SHORT).show()

            val selectedOption = db.baza_pobierz("ustawienia", "powiadomienia")
            // Pytamy użytkownika, którą opcję powiadomienia wybrał (1-4)

            if (selectedOption != null) {
                (activity as Notifications).enableNotifications()
            }
            // Sprawdź, którą opcję wybrał użytkownik i odpowiednio zaplanuj powiadomienie

        }, currentHour, currentMinute, true)

        timePickerDialog.show()
    }


    private fun showInputDialog(button: Button, title: String = "Imię / Nazwa żółwia") {
        // Stwórz EditText do wpisania tekstu
        val builder = AlertDialog.Builder(requireContext())
        val inputEditText = EditText(requireContext())
        inputEditText.gravity = Gravity.CENTER
        val titleView = TextView(requireContext()).apply {
            text = title
            textSize = 24f
            setTextColor(Color.BLACK)
            setBackgroundResource(R.drawable.x_dialog_header)
            setPadding(40, 40, 40, 40)
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
        }
        builder.setCustomTitle(titleView)
        builder.setView(inputEditText)
        builder.setPositiveButton("OK") { _, _ ->
            // Pobierz tekst wpisany przez użytkownika i zmień tekst przycisku
            val userInput = inputEditText.text.toString()
            button.text = userInput
            db.baza_wstaw("turtle", "nazwa", userInput)

        }
        builder.setNegativeButton("Anuluj", null)
        // Stwórz AlertDialog
        val dialog = builder.create()
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeButton.setBackgroundColor(Color.BLACK)
            negativeButton.setTextColor(Color.WHITE) // Zmien kolor na pożądany
            positiveButton.setBackgroundColor(Color.BLACK)
            positiveButton.setTextColor(Color.WHITE) // Zmien kolor na pożądany
            dialog.window?.setBackgroundDrawableResource(R.drawable.x_dialog_body)
        }
        dialog.show()

    }

    @SuppressLint("SetTextI18n")
    private fun showOptionsDialog(button: Button) {
        // Zdefiniowane opcje

        var options2 = (activity as Baseactivity).tab_all.toTypedArray()
        val titleView = TextView(requireContext()).apply {
            text = "Wybierz swój gatunek"
            textSize = 24f
            setTextColor(Color.BLACK)
            setBackgroundResource(R.drawable.x_dialog_header)
            setPadding(40, 40, 40, 40)
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
        }
        // Stwórz AlertDialog z opcjami do wyboru
        val dialog = AlertDialog.Builder(requireContext())
            .setCustomTitle(titleView)
            .setItems(options2) { _, which ->
                // Kiedy użytkownik wybierze opcję, zmień tekst przycisku na wybraną opcję
                val selectedOption = options2[which]
                button.text = selectedOption
                db.baza_wstaw("turtle", "gatunek", selectedOption)
            }
            .setNegativeButton("Anuluj", null) // Przycisk anulowania bez dodatkowych akcji
            .create()
        dialog.setOnShowListener {
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeButton.setBackgroundColor(Color.BLACK)
            negativeButton.setTextColor(Color.WHITE) // Zmien kolor na pożądany
            dialog.window?.setBackgroundDrawableResource(R.drawable.x_dialog_body)
        }
        // Pokaż dialog
        dialog.show()
    }
    fun showMotywDialog(button: Button) {
        val titleView = TextView(requireContext()).apply {
            text = "Wybierz motyw"
            textSize = 24f
            setTextColor(Color.BLACK)
            setBackgroundResource(R.drawable.x_dialog_header)
            setPadding(40, 40, 40, 40)
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
        }
        val options = arrayOf("Zielony", "Fioletowy","Jasnoniebieski", "Pomarańczowy","Niebieski","Dostępny")
        val dialog = AlertDialog.Builder(requireContext())
            .setCustomTitle(titleView)
            .setItems(options) { _, which ->
                val selectedOption = options[which]
                val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                when (selectedOption) {
                    "Zielony" -> {
                        editor.putString("selectedTheme", "A")
                    }
                    "Fioletowy" -> {
                        editor.putString("selectedTheme", "B")
                    }
                    "Jasnoniebieski" -> {
                        editor.putString("selectedTheme", "C")
                    }
                    "Pomarańczowy" -> {
                        editor.putString("selectedTheme", "D")
                    }
                    "Niebieski" -> {
                        editor.putString("selectedTheme", "E")
                    }
                    "Dostępny" ->{
                        editor.putString("selectedTheme", "F")
                    }
                }
                editor.apply()
                db.baza_wstaw("ustawienia", "motyw", selectedOption)
                (activity as Baseactivity).applyTheme()
                (activity as Baseactivity).navigation.selectedItemId = R.id.navigation_home

                button.text = selectedOption
                requireActivity().recreate()
            }
            .setNegativeButton("Anuluj", null)
            .create()


        dialog.setOnShowListener {
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeButton.setBackgroundColor(Color.BLACK)
            negativeButton.setTextColor(Color.WHITE) // Zmien kolor na pożądany
            dialog.window?.setBackgroundDrawableResource(R.drawable.x_dialog_body)
        }
        dialog.show()
    }

}
