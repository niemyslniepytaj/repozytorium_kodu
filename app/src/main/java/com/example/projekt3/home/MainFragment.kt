package com.example.projekt3.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.projekt3.aglobalApk.Baseactivity
import com.example.projekt3.aglobalApk.DatabaseHelper
import com.example.projekt3.R
import com.example.projekt3.aglobalApk.StartFragment

class MainFragment : Fragment() {
    private lateinit var circularProgressBar: CircularProgressBar
    private lateinit var circularProgressBar2: CircularProgressBar
    private lateinit var circularProgressBar3: CircularProgressBar
    private lateinit var db: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        db = DatabaseHelper(requireContext())
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nazwa = db.baza_pobierz("turtle", "nazwa").toString()

        (activity as? Baseactivity)?.setViewMode(nazwa)

        db.baza_wstaw("ustawienia", "current", "1")

        val turtlemessage = view.findViewById<TextView>(R.id.textView20)
        val turtle = view.findViewById<ImageView>(R.id.imageView2)
        turtlemessage.visibility = View.INVISIBLE
        // Opóźnienie dla uruchomienia animacji


        turtle.setOnClickListener {
            animateTextViewFromImageView(turtlemessage, turtle)
            setMessage(turtlemessage)
        }


        // Ustawienia komponentów
        circularProgressBar = view.findViewById(R.id.circularProgressBar)
        circularProgressBar2 = view.findViewById(R.id.circularProgressBar2)
        circularProgressBar3 = view.findViewById(R.id.circularProgressBar3)

        // Rozpocznij animacje
        circularProgressBar.startDecreasing()
        circularProgressBar2.startDecreasing()
        circularProgressBar3.startDecreasing()
    }

    fun animateTextViewFromImageView(textView: TextView, imageView: ImageView) {
        // Ustawienie początkowych parametrów TextView (np. początkowy rozmiar)
//        textView.scaleX = 0.01f
//        textView.scaleY = 0.01f
//
        // Pozycje startowe i docelowe dla przesunięcia
        val imageViewCenterX = imageView.x + imageView.width / 2 - textView.width / 2
        val imageViewCenterY = imageView.y + imageView.height / 2 - textView.height / 2
        val textViewFinalX = textView.x
        val textViewFinalY = textView.y

        // Tworzenie animacji skalowania
        val scaleAnimation = ScaleAnimation(
            0.0f, 1.0f,  // skalowanie od 0.01 do pełnej skali (1.0)
            0.0f, 1.0f,  // skalowanie od 0.01 do pełnej skali (1.0)
            Animation.RELATIVE_TO_SELF, 0.5f,  // skalowanie od środka (poziomo)
            Animation.RELATIVE_TO_SELF, 0.5f   // skalowanie od środka (pionowo)
        )
        scaleAnimation.duration = 1000 // czas trwania animacji (ms)

        // Tworzenie animacji przesunięcia
        val translateAnimation = TranslateAnimation(
            Animation.ABSOLUTE, imageViewCenterX - textViewFinalX,
            Animation.ABSOLUTE, 0f,
            Animation.ABSOLUTE, imageViewCenterY - textViewFinalY,
            Animation.ABSOLUTE, 0f
        )
        translateAnimation.duration = 500 // czas trwania animacji (ms)

        // Tworzenie zestawu animacji
        val animationSet = AnimationSet(true)
        animationSet.addAnimation(scaleAnimation)
        animationSet.addAnimation(translateAnimation)

        // Uruchomienie animacji
        textView.startAnimation(animationSet)
        textView.visibility = View.VISIBLE
    }

    fun setMessage(textView: TextView) {

        textView.text = "Wyślij wiadomość"
        val liczba = (1..60).random()
        textView.text = when (liczba) {
            1 -> "Spiesz się... powoli!"
            2 -> "Jestem mistrzem cierpliwości."
            3 -> "Mam czas, a ty?"
            4 -> "Biegam w snach."
            5 -> "Wolno, ale z klasą."
            6 -> "Zawsze na czas... kiedyś."
            7 -> "Życie na niskich obrotach."
            8 -> "Jutro też jest dzień."
            9 -> "Może pośpię trochę dłużej."
            10 -> "Moje tempo? Epickie!"
            11 -> "Nigdy nie spieszę się."
            12 -> "Cierpliwość to sztuka."
            13 -> "Zacznij bez mnie."
            14 -> "Droga na skróty? Nie dziękuję."
            15 -> "Będę tam, kiedy przyjdzie czas."
            16 -> "Czuję się jak rakieta... powolna rakieta."
            17 -> "Po co biec, skoro można iść?"
            18 -> "Żółwi maraton? Każdy dzień!"
            19 -> "Nigdy nie ścigaj się z królikiem."
            20 -> "Mogę poczekać, a ty?"
            21 -> "Lubię toczyć się przez życie."
            22 -> "Lepiej wolno niż wcale!"
            23 -> "Zawsze warto odpocząć."
            24 -> "Czekaj... jeszcze chwilę."
            25 -> "Świat mnie nie dogoni!"
            26 -> "Czas to złoto... wolno zbierane."
            27 -> "Wolniej... jeszcze wolniej."
            28 -> "Szybcy i wściekli? To nie dla mnie."
            29 -> "Moje tempo to filozofia."
            30 -> "Życie w ślimaczym tempie... ale jestem żółwiem!"
            31 -> "Polećmy rakietą!"
            32 -> "Zróbmy coś szalonego!"
            33 -> "Czas na przygodę!"
            34 -> "Kto chce tańczyć?"
            35 -> "Gdzie jest impreza?"
            36 -> "Zróbmy coś epickiego!"
            37 -> "To czas na karaoke!"
            38 -> "Skok na bungee? Dlaczego nie!"
            39 -> "Życie to impreza!"
            40 -> "Kto jest gotowy na wyścig?"
            41 -> "Czas na zmianę fryzury!"
            42 -> "Pora na pizzę!"
            43 -> "Może skok na spadochronie?"
            44 -> "Kto jest gotowy na przygodę?"
            45 -> "Chodźmy na koncert!"
            46 -> "Zróbmy coś szalonego!"
            47 -> "Kto jest gotowy na niespodzianki?"
            48 -> "Włączmy muzykę na cały regulator!"
            49 -> "Pora na wspinaczkę!"
            50 -> "Chodźmy oglądać gwiazdy!"
            51 -> "Zróbmy spontaniczny wyjazd!"
            52 -> "Czas na wycieczkę rowerową!"
            53 -> "Zagrajmy w coś szalonego!"
            54 -> "Chodźmy na paintball!"
            55 -> "Chodż nakarmimy rybki! Albo mnie rybkami :)"
            56 -> "Czas na piknik!"
            57 -> "Pora na nocne pływanie!"
            58 -> "Zróbmy maraton filmowy!"
            59 -> "Spróbujmy czegoś nowego!"
            60 -> "Kto jest gotowy na coś wyjątkowego?"
            else -> {
                "Nie znam tej liczby"
            }
        }

    }
}
