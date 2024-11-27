package com.example.projekt3.games

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.example.projekt3.aglobalApk.Baseactivity
import com.example.projekt3.aglobalApk.DatabaseHelper
import com.example.projekt3.R

class GamesFragment : Fragment() {
    private lateinit var db: DatabaseHelper
    private var level: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_games, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as Baseactivity).setViewMode("Żółwio-Quiz", 1)


        db = DatabaseHelper(requireContext())
        db.baza_wstaw("ustawienia", "current", "2")
        level = db.baza_pobierz("gry", "level").toString().toInt()
        val info_gra1 = view.findViewById<Button>(R.id.button15)
        val info_gra2 = view.findViewById<Button>(R.id.button17)
        val info_gra3 = view.findViewById<Button>(R.id.button19)
        val button_gra1 = view.findViewById<Button>(R.id.button16)
        val button_gra2 = view.findViewById<Button>(R.id.button18)
        val button_gra3 = view.findViewById<Button>(R.id.button20)

        info_gra1.setOnClickListener {
            showGameDescriptionDialog(requireContext(), "Zwykły Quiz", 1)
        }
        info_gra2.setOnClickListener {
            showGameDescriptionDialog(requireContext(), "Teleturniej", 2)
        }
        info_gra3.setOnClickListener {
            showGameDescriptionDialog(requireContext(), "Gra o wszystko", 3)
        }
        button_gra1.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("game_type", 1)

            (activity as? Baseactivity)?.loadFragment(GamesContentFragment(), 4, bundle)
        }
        button_gra2.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("game_type", 2)

            (activity as? Baseactivity)?.loadFragment(GamesContentFragment(), 4, bundle)
        }
        button_gra3.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("game_type", 3)

            (activity as? Baseactivity)?.loadFragment(GamesContentFragment(), 4, bundle)
        }


    }

    fun showGameDescriptionDialog(context: Context, gameTitle: String, choose: Int) {
        var gameDescription = ""
        when (choose) {
            1 -> gameDescription = """
                                        <h4>Zasady gry:</h4>
                                        <ul>
                                            <li>&nbsp; 10 pytań - czas na odpowiedź: 20 sekund</li>
                                            <li>&nbsp; Trudność pytań: losowa</li>
                                            <li>&nbsp; Zdobyte punkty wyświetlane są po lewej stronie</li>
                                            <li>&nbsp; Kliknięcie przycisku "wyjdź" kończy grę, przyznając zdobyte już punkty</li>
                                        </ul>
                                        <h4>Punktacja:</h4>
                                        <ul>
                                            <li>&nbsp; Punkty, które możesz zdobyć za pytanie są wyświetlane po prawej stronie</li>
                                            <li>&nbsp; 100-300 punktów w zależności od stopnia trudności pytania</li>
                                         
                                        </ul>
                                    """.trimIndent()

            2 -> gameDescription = """
                                        <h4>Zasady gry:</h4>
                                        <ul>
                                            <li>&nbsp; 10 pytań - czas na odpowiedź: 30 sekund</li>
                                            <li>&nbsp; Trudność pytań: narasta z kolejnymi progami punktowymi</li>
                                            <li>&nbsp; Błędna odpowiedź powoduje zakończenie gry i punkty nie są przyznawane</li>
                                            <li>&nbsp; Kliknięcie przycisku "wyjdź" kończy grę, przyznając zdobyty już próg punktowy</li>
                                        </ul>
                                        <h4>Punktacja:</h4>
                                        <ul>
                                            <li>&nbsp; Próg punktowy o który grasz wyświetlany jest po prawej stronie</li>
                                            <li>&nbsp; Prógi punktowe 100-10000 punktów rosną z każdym pytaniem</li>
                                            
                                        </ul>
                                    """.trimIndent()

            3 -> gameDescription = """
                                        <h4>Zasady gry:</h4>
                                        <ul>
                                            <li>&nbsp; 10 pytań - czas na odpowiedź: 15 sekund</li>
                                            <li>&nbsp; Trudność pytań: trudne</li>
                                            <li>&nbsp; Błędna odpowiedź powoduje zmniejszenie ilości punktów</li>
                                            <li>&nbsp; Kliknięcie przycisku "wyjdź" kończy grę, oraz powoduje utratę wszystkich punktów</li>
                                        </ul>
                                        <h4>Punktacja:</h4>
                                        <ul>
                                            <li>&nbsp; Na start otrzymujesz 20000 punktów</li>
                                            <li>&nbsp; Za każdą błędną odpowiedź twoje punkty zmniejszają się o 2500</li>
                                        </ul>
                                    """.trimIndent()

        }
        val builder = AlertDialog.Builder(context)
        val formattedDescription =
            HtmlCompat.fromHtml(gameDescription, HtmlCompat.FROM_HTML_MODE_LEGACY)
        val titleView = TextView(requireContext()).apply {
            text = gameTitle
            textSize = 24f
            setTextColor(Color.BLACK)
            setBackgroundResource(R.drawable.x_dialog_header)
            setPadding(40, 40, 40, 40)
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
        }
        builder.setCustomTitle(titleView)
        val messageView = TextView(context).apply {
            text = formattedDescription
            setPadding(50, 20, 16, 16)
            textSize = 16f
        }
        builder.setView(messageView)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setBackgroundColor(resources.getColor(R.color.black))
            positiveButton.setTextColor(Color.WHITE) // Zmien kolor na pożądany
            dialog.window?.setBackgroundDrawableResource(R.drawable.x_dialog_body)
        }
        dialog.show()

    }

}