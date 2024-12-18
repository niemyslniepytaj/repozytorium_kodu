package com.example.projekt3.avatar

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.projekt3.R
import com.example.projekt3.aglobalApk.Baseactivity
import com.example.projekt3.aglobalApk.DatabaseHelper
import com.example.projekt3.games.GamesFragment
import com.example.projekt3.home.MainFragment
import com.example.projekt3.info.InfoturtleFragment
import com.example.projekt3.settings.SettingsFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton


class AvatarFragment : Fragment() {

    private lateinit var db: DatabaseHelper
    private lateinit var fab: FloatingActionButton
    private var level: Int = 0
    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        db = DatabaseHelper(requireContext())
        return inflater.inflate(R.layout.fragment_avatar, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (activity as Baseactivity).setViewMode("Edytuj avatar", 0, true, false)


        level = db.baza_pobierz("gry", "level").toString().toInt()
        val avatar_image = view.findViewById<ImageView>(R.id.avatar_image_pre)
        val avatar_background =
            view.findViewById<View>(R.id.avatar_background_pre).background as LayerDrawable
        val final_image = requireActivity().findViewById<ImageView>(R.id.avatar_image)
        val final_background =
            requireActivity().findViewById<View>(R.id.avatar_background).background as LayerDrawable

        val modelbutton = view.findViewById<Button>(R.id.button4)
        val kolorbutton = view.findViewById<Button>(R.id.button10)
        val strokebutton = view.findViewById<Button>(R.id.button5)
        val tlobutton = view.findViewById<Button>(R.id.button11)
        val submitbutton = view.findViewById<Button>(R.id.button13)

        fab = view.findViewById(R.id.floatingActionButton3)
        val which = db.baza_pobierz("ustawienia", "current")
        fab.setOnClickListener {
            when (which) {
                "1" -> (activity as Baseactivity).loadFragment(MainFragment(), 3)
                "2" -> (activity as Baseactivity).loadFragment(GamesFragment(), 3)
                "3" -> (activity as Baseactivity).loadFragment(InfoturtleFragment(), 3)
                "4" -> (activity as Baseactivity).loadFragment(SettingsFragment(), 3)
            }

        }
//        db.baza_wstaw("turtle", "stroke", "Czarny")
//        db.baza_wstaw("turtle", "avatar", "z_a_fioletowy")
//        db.baza_wstaw("avatar", "kolor", "fioletowy")
//        db.baza_wstaw("avatar", "model", "A")
//        db.baza_wstaw("turtle", "tlo", "z_morski")


        modelbutton.text = db.baza_pobierz("turtle", "button1").toString()
        kolorbutton.text = db.baza_pobierz("turtle", "button2").toString()
        strokebutton.text = db.baza_pobierz("turtle", "button3").toString()
        tlobutton.text = db.baza_pobierz("turtle", "button4").toString()


        // Warstwa gradientu

        modelbutton.setOnClickListener {
            avatar_preview_image(modelbutton, avatar_image, "model")
        }
        kolorbutton.setOnClickListener {
            avatar_preview_image(kolorbutton, avatar_image, "kolor")
        }

        strokebutton.setOnClickListener {
            avatar_preview_color(strokebutton, avatar_background)
        }

        tlobutton.setOnClickListener {
            avatar_preview_background(tlobutton, avatar_image)
        }
        (activity as Baseactivity).setup_avatar(avatar_image, avatar_background,true)
        submitbutton.setOnClickListener {
            val a = db.baza_pobierz("avatar", "avatar")
            val b = db.baza_pobierz("avatar", "stroke")
            val c = db.baza_pobierz("avatar", "tlo")
            val b1 = db.baza_pobierz("avatar", "button1")
            val b2 = db.baza_pobierz("avatar", "button2")
            val b3 = db.baza_pobierz("avatar", "button3")
            val b4 = db.baza_pobierz("avatar", "button4")
            db.baza_wstaw("turtle", "button1", b1.toString())
            db.baza_wstaw("turtle", "button2", b2.toString())
            db.baza_wstaw("turtle", "button3", b3.toString())
            db.baza_wstaw("turtle", "button4", b4.toString())
            db.baza_wstaw("turtle", "avatar", a.toString())
            db.baza_wstaw("turtle", "stroke", b.toString())
            db.baza_wstaw("turtle", "tlo", c.toString())

            (activity as Baseactivity).setup_avatar(final_image, final_background)
            tlobutton.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.button_default_text
                )
            )
            kolorbutton.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.button_default_text
                )
            )
            modelbutton.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.button_default_text
                )
            )
            strokebutton.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.button_default_text
                )
            )

        }

    }


    fun avatar_preview_color(button: Button, avatar: LayerDrawable?) {
        val strokeOptions = arrayListOf("Czarny", "Fioletowy")
//        if(level>=2) strokeOptions.add("3")
//        if(level>=6) strokeOptions.add("4")
//        if(level>=11) strokeOptions.add("5")
//        if(level>=15) strokeOptions.add("6")
//        if(level>=19) strokeOptions.add("7")
        if (level >= 0) strokeOptions.add("Różowy")
        if (level >= 0) strokeOptions.add("Niebieski")
        if (level >= 0) strokeOptions.add("Czerwony")
        if (level >= 0) strokeOptions.add("Pomarańczowy")
        if (level >= 0) strokeOptions.add("Złoty")


        // Wyświetlenie dialogu i oczekiwanie na wybór użytkownika
        showOptionsDialog(
            "Wybierz kolor ramki(${strokeOptions.size}/7)",
            strokeOptions.toTypedArray()
        ) { selectedOption ->
            val strokeColor = when (selectedOption) {
                "Czarny" -> ContextCompat.getColor(requireContext(), R.color.black)
                "Fioletowy" -> ContextCompat.getColor(requireContext(), R.color.purple)
                "Różowy" -> ContextCompat.getColor(requireContext(), R.color.pink)
                "Niebieski" -> ContextCompat.getColor(requireContext(), R.color.niebieski)
                "Czerwony" -> ContextCompat.getColor(requireContext(), R.color.czerwony)
                "Pomarańczowy" -> ContextCompat.getColor(requireContext(), R.color.orange)
                "Złoty" -> ContextCompat.getColor(requireContext(), R.color.golden_color)
                else -> ContextCompat.getColor(requireContext(), R.color.purple) // Domyślny kolor
            }

            // Zastosowanie obramowania (stroke) do avatara po wyborze użytkownika
            avatar?.let {
                val strokeWidth = resources.getDimensionPixelSize(R.dimen.avatar_stroke_width)
                val strokeLayer = it.getDrawable(1) as? GradientDrawable
                strokeLayer?.setStroke(strokeWidth, strokeColor)
            }
            button.text = selectedOption
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.golden_color))
            db.baza_wstaw("avatar", "button3", selectedOption)
            db.baza_wstaw("avatar", "stroke", selectedOption)
        }
    }

    fun avatar_preview_image(button: Button, avatar: ImageView, choose: String) {
        val modelOptions = arrayListOf("A")
        val colorOptions = arrayListOf("Niebieski", "Różowy")
        var title = ""
        var finaloptions: ArrayList<String>
        if (choose == "model") {
            finaloptions = modelOptions

//            if(level>=3) finaloptions.add("2")
//            if(level>=8) finaloptions.add("3")
//            if(level>=14) finaloptions.add("4")
//            if(level>=18) finaloptions.add("5")
            if (level >= 0) finaloptions.add("B")
            if (level >= 0) finaloptions.add("C")
            if (level >= 0) finaloptions.add("D")
            if (level >= 0) finaloptions.add("E")
            title = "Wybierz model (${finaloptions.size}/5)"
        } else {
            finaloptions = colorOptions
//            if(level>=1) finaloptions.add("3")
//            if(level>=5) finaloptions.add("4")
//            if(level>=9) finaloptions.add("5")
//            if(level>=12) finaloptions.add("6")
//            if(level>=17) finaloptions.add("7")
            if (level >= 0) finaloptions.add("Pomarańczowy")
            if (level >= 0) finaloptions.add("Fioletowy")
            if (level >= 0) finaloptions.add("Żółty")
            if (level >= 0) finaloptions.add("Czerwony")
            if (level >= 0) finaloptions.add("Jasnoniebieski")
            title = "Wybierz kolor (${finaloptions.size}/7)"
        }
        val model = db.baza_pobierz("avatar", "model")?.lowercase()
        val color = db.baza_pobierz("avatar", "kolor")?.lowercase()
        // Wyświetlenie dialogu i oczekiwanie na wybór użytkownika
        showOptionsDialog(title, finaloptions.toTypedArray()) { selectedOption ->

            val option_selected =
                (activity as Baseactivity).removePolishChars(selectedOption.lowercase())

            var bnd = ""
            if (choose == "model") {
                bnd = "z_${option_selected}_${color}"
                db.baza_wstaw("avatar", "model", option_selected)
                db.baza_wstaw("avatar", "button1", selectedOption)
                button.text = selectedOption
            } else {
                bnd = "z_${model}_${option_selected}"
                db.baza_wstaw("avatar", "kolor", option_selected)
                db.baza_wstaw("avatar", "button2", selectedOption)
                button.text = selectedOption
            }


            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.golden_color))
            val resId = resources.getIdentifier(bnd, "drawable", requireContext().packageName)
            avatar.setImageResource(resId)
            db.baza_wstaw("avatar", "avatar", bnd)
        }
    }

    fun avatar_preview_background(button: Button, avatar: ImageView) {
        val tloOptions = arrayListOf("Morski", "Pomarańczowy")
//        if(level>=4) tloOptions.add("Fioletowy")
//        if(level>=7) tloOptions.add("Wielokolorowy 1")
//        if(level>=10) tloOptions.add("Niebieski")
//        if(level>=13) tloOptions.add("Żółty")
//        if(level>=16) tloOptions.add("Różowy")
//        if(level>=20) tloOptions.add("Wielokolorowy 2")
        if (level >= 0) tloOptions.add("Fioletowy")
        if (level >= 0) tloOptions.add("Wielokolorowy")
        if (level >= 0) tloOptions.add("Niebieski")
        if (level >= 0) tloOptions.add("Żółty")
        if (level >= 0) tloOptions.add("Różowy")
        if (level >= 0) tloOptions.add("Magiczny")

        // Wyświetlenie dialogu i oczekiwanie na wybór użytkownika
        showOptionsDialog(
            "Wybierz tło (${tloOptions.size}/8)",
            tloOptions.toTypedArray()
        ) { selectedOption ->

            val option_selected =
                (activity as Baseactivity).removePolishChars(selectedOption.lowercase())

            val resourceId = resources.getIdentifier(
                "z_tlo_$option_selected",
                "drawable",
                requireContext().packageName
            )
            avatar.setBackgroundResource(resourceId)

            button.text = selectedOption
            db.baza_wstaw("avatar", "tlo", "z_tlo_$option_selected")
            db.baza_wstaw("avatar", "button4", selectedOption)
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.golden_color))

        }
    }


    fun showOptionsDialog(
        title: String,
        options: Array<String>,
        callback: (selectedOption: String) -> Unit
    ) {
        val titleView = TextView(requireContext()).apply {
            text = title
            textSize = 24f
            setTextColor(Color.BLACK)
            setBackgroundResource(R.drawable.x_dialog_header)
            setPadding(40, 40, 40, 40)
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setCustomTitle(titleView)
            .setItems(options) { _, which ->
                callback(options[which]) // Wywołanie callbacku z wybraną opcją
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