package com.example.projekt3.aglobalApk

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.projekt3.R
import com.example.projekt3.home.MainFragment
import com.example.projekt3.settings.SettingsFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StartFragment : Fragment() {
    private lateinit var db: DatabaseHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        db = DatabaseHelper(requireContext())
        return inflater.inflate(R.layout.start_fragment, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? Baseactivity)?.setViewMode("Wprowadzenie")
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        val toolbarlevel = requireActivity().findViewById<TextView>(R.id.toolbarpoints)
        val avatar = requireActivity().findViewById<ImageView>(R.id.avatar_image)
        val avatar_background = requireActivity().findViewById<ImageView>(R.id.avatar_background)

        var iteration = 0
        val message= view.findViewById<TextView>(R.id.textView21)
        val fab = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)

        val navigation = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        toolbar.visibility=View.INVISIBLE
        toolbarlevel.visibility=View.INVISIBLE
        navigation.visibility=View.INVISIBLE
        navigation.selectedItemId = R.id.navigation_none
        message.text ="Witaj w ŻółwiK-u! \n Oto krótki przewodnik po aplikacji. \n *kliknij przycisk aby rozpocząć*"

        fab.setOnClickListener{
            iteration++
            when(iteration){
                1 -> {
                    toolbar.visibility=View.VISIBLE
                    toolbarlevel.visibility=View.INVISIBLE


                    message.text="Zacznijmy od góry.\n Po lewej stronie znajduje się napis który wskazuje w której części aplikacji jesteś."
                }
                2 -> {

                    avatar.visibility=View.VISIBLE
                    avatar_background.visibility=View.VISIBLE

                    message.text="Po prawej stronie znajduje się avatar twojego żółwia.\n Po kliknięciu na niego możesz go dowolnie modyfikować."
                }
                3 -> {
                    toolbarlevel.visibility=View.VISIBLE
                    toolbar.visibility=View.INVISIBLE
                    message.text="To jest twój aktualny poziom.\n Z każdym poziomem zyskujesz nowe elementy dla twojego avatara!"



                }

                4 -> {
                    toolbarlevel.text="1000 do poziomu 1"
                    message.text="Jeśli znajdujesz się w sekcji quizu, w tym miejscu wyświetli Ci się ile punktów brakuje do kolejnego poziomu."
                }
                5 -> {
                    toolbarlevel.text="0"
                    toolbar.visibility=View.VISIBLE

                    message.text="A więc tak się prezentuje górna część aplikacji."

                }
                6 -> {
                    toolbar.visibility=View.INVISIBLE
                    toolbarlevel.visibility=View.INVISIBLE

                    navigation.visibility=View.VISIBLE


                    navigation.menu.getItem(0).isEnabled = false
                    navigation.menu.getItem(1).isEnabled = false
                    navigation.menu.getItem(2).isEnabled = false
                    navigation.menu.getItem(3).isEnabled = false
                    message.text="To jest menu nawigacyjne, dzięki któremu możesz się poruszać po aplikacji."
                }
                7 -> {
                    message.text="Sekcja: Mój Żółw.\n Tutaj możesz nakarmić lub umyć swojego wirtualnego żółwia. Żółwie są mało rozmowne, ale jak na niego klikniesz to coś Ci odpowie :)."
                    navigation.selectedItemId = R.id.navigation_home
                }
                8 -> {
                    message.text="Sekcja: Mini-Gry.\n Tutaj możesz zdobyć punkty w 3 rodzajach quizu. Zapoznaj się z zasadami klikając *info* w tej sekcji."
                    navigation.selectedItemId = R.id.navigation_games
                }
                9 -> {
                    message.text="Sekcja: Info-Żółw.\n Tutaj możesz poznać więcej faktów i informacji o różnych gatunkach żółwi."
                    navigation.selectedItemId = R.id.navigation_info
                }
                10 -> {
                    message.text="Sekcja: Ustawienia.\n Tutaj możesz dostosować ustawienia aplikacji, powiadomień oraz informacje o twoim żółwiu."
                    navigation.selectedItemId = R.id.navigation_settings

                }
                11->{
                    toolbar.visibility=View.VISIBLE
                    toolbarlevel.visibility=View.VISIBLE
                    message.text="A więc to już wszystko.\n Baw się dobrze i bądź jak żółw. Nie spiesz się :). \n*kliknij przycisk aby przenieść się do ustawień*"
                }
                12 -> {
                    navigation.menu.getItem(0).isEnabled = true
                    navigation.menu.getItem(1).isEnabled = true
                    navigation.menu.getItem(2).isEnabled = true
                    navigation.menu.getItem(3).isEnabled = true
                    navigation.selectedItemId = R.id.navigation_settings
                    (activity as Baseactivity).loadFragment(SettingsFragment())
                    db.baza_wstaw("ustawienia", "firsttime", "false")

                }
            }
        }
    }
}