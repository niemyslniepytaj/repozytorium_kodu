package com.example.projekt3.info

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.projekt3.aglobalApk.Baseactivity
import com.example.projekt3.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import org.json.JSONArray
import org.json.JSONObject

class InfoturtleContentFragment : Fragment() {
    private val tab0Headers = mutableListOf<String>()
    private val tab1Headers = mutableListOf<String>()
    private val tab2Headers = mutableListOf<String>()
    private val tab0Descriptions = mutableListOf<String>()
    private val tab1Descriptions = mutableListOf<String>()
    private val tab2Descriptions = mutableListOf<String>()
    private lateinit var fab: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_infoturtle_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tab0Headers.clear()
        tab1Headers.clear()
        tab2Headers.clear()

        tab0Descriptions.clear()
        tab1Descriptions.clear()
        tab2Descriptions.clear()
        val imageResource = arguments?.getString("image_resource")
        val turtleName = arguments?.getString("nazwa").toString()
        fab = view.findViewById(R.id.floatingActionButton2)

        fab.setOnClickListener {
            (activity as? Baseactivity)?.loadFragment(InfoturtleFragment(), 5)
        }


        val contenta = view.findViewById<LinearLayout>(R.id.content)
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout3)


        val imageView = view.findViewById<ImageView>(R.id.imageView)
        (activity as? Baseactivity)?.setViewMode(turtleName,0,true,false) // Ustaw nazwę żółwia jako setTitle

        imageView.setImageResource(
            resources.getIdentifier(
                imageResource, "drawable", requireContext().packageName
            )
        )

        loadTurtleData(requireContext(), turtleName)


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> loadTabContent(tab0Headers, tab0Descriptions, contenta)
                    1 -> loadTabContent(tab1Headers, tab1Descriptions, contenta)
                    2 -> loadTabContent(tab2Headers, tab2Descriptions, contenta)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Załaduj domyślnie pierwszą zakładkę
        loadTabContent(tab0Headers, tab0Descriptions, contenta)

    }

    // Funkcja ładująca nagłówki i opisy do ScrollView
    private fun loadTabContent(
        headers: List<String>, descriptions: List<String>, content: LinearLayout
    ) {
        content.removeAllViews() // Czyścimy stare widoki

        // Tworzymy zestawy nagłówek-opis
        for (i in headers.indices) {
            val headerView = TextView(requireContext())
            headerView.text = headers[i]
            headerView.textSize = 18f // Styl nagłówka
            headerView.setPadding(16, 16, 0, 8) // Dodajemy margines
            headerView.setTypeface(null, Typeface.BOLD)
            val descriptionView = TextView(requireContext())
            descriptionView.text = descriptions[i]
            descriptionView.textSize = 16f // Styl opisu
            descriptionView.setPadding(48, 8, 48, 16) // Dodajemy margines


            // Dodajemy do kontenera
            content.addView(headerView)
            content.addView(descriptionView)
        }
    }

    private fun loadTurtleData(context: Context, turtleName: String) {
        // Wczytaj plik JSON
        val jsonString = context.assets.open("turtles.json").bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonString)
        val turtlesArray: JSONArray = jsonObject.getJSONArray("turtles")

        for (i in 0 until turtlesArray.length()) {
            val turtleObject = turtlesArray.getJSONObject(i)
            if (turtleObject.getString("name") == turtleName) {
                // Czyszczenie list przed wczytaniem nowych danych


                // Wypełnij nagłówki i opisy dla kategorii 'care'
                val care = turtleObject.getJSONObject("care")
                val careKeys = care.keys() // Pobiera dynamiczne klucze (nagłówki)
                while (careKeys.hasNext()) {
                    val key = careKeys.next()
                    tab0Headers.add(key)
                    tab0Descriptions.add(care.getString(key))
                }

                // Wypełnij nagłówki i opisy dla kategorii 'environment'
                val environment = turtleObject.getJSONObject("environment")
                val environmentKeys = environment.keys()
                while (environmentKeys.hasNext()) {
                    val key = environmentKeys.next()
                    tab1Headers.add(key)
                    tab1Descriptions.add(environment.getString(key))
                }

                // Wypełnij nagłówki i opisy dla kategorii 'diseases'
                val diseases = turtleObject.getJSONObject("diseases")
                val diseasesKeys = diseases.keys()
                while (diseasesKeys.hasNext()) {
                    val key = diseasesKeys.next()
                    tab2Headers.add(key)
                    tab2Descriptions.add(diseases.getString(key))
                }

                break // Zatrzymaj pętlę po znalezieniu
            }
        }

    }
}







