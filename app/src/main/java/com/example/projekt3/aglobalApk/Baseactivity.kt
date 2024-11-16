package com.example.projekt3.aglobalApk

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.projekt3.R
import com.example.projekt3.avatar.AvatarFragment
import com.example.projekt3.games.GamesFragment
import com.example.projekt3.home.MainFragment
import com.example.projekt3.info.InfoturtleFragment
import com.example.projekt3.powiadomienia.Notifications
import com.example.projekt3.settings.SettingsFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import org.json.JSONObject


open class Baseactivity : Notifications() {

    val db = DatabaseHelper(this)

    val navigation: BottomNavigationView by lazy { findViewById(R.id.bottomNavigationView) }
    val toolbar: MaterialToolbar by lazy { findViewById(R.id.toolbar) }
    val titlemain: TextView by lazy { findViewById(R.id.toolbar_title) }
    val toolbarlevel: TextView by lazy { findViewById(R.id.toolbarpoints) }
    val tab_wodne = mutableListOf<String>()
    val tab_ladowe = mutableListOf<String>()
    val tab_all = mutableListOf<String>()
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.transparent)
        setContentView(R.layout.activity_base)

//        setSupportActionBar(toolbar)
//        db.baza_wstaw("gry", "punkty2", "0")
//        db.baza_wstaw("gry", "level", "0")
//        db.baza_wstaw("gry", "punktydolvl", "1000")

        if(db.baza_pobierz("ustawienia", "firsttime") != "false") loadFragment(StartFragment()) else loadFragment(MainFragment())


        navigation.selectedItemId = R.id.navigation_home


        val avatar = findViewById<ImageView>(R.id.avatar_image)
        val avatar2=findViewById<ImageView>(R.id.avatar_background)
        val avatar_background =
            findViewById<ImageView>(R.id.avatar_background).background as? LayerDrawable



        setup_avatar(avatar, avatar_background)

        avatar.setOnClickListener {
            loadFragment(AvatarFragment(), 2)
        }
        navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> if(R.id.navigation_home !=navigation.selectedItemId) loadFragment(MainFragment())
                R.id.navigation_games -> if(R.id.navigation_games !=navigation.selectedItemId) loadFragment(GamesFragment())
                R.id.navigation_info -> if(R.id.navigation_info !=navigation.selectedItemId) loadFragment(InfoturtleFragment())
                R.id.navigation_settings -> if(R.id.navigation_settings !=navigation.selectedItemId) loadFragment(SettingsFragment())
                else -> R.id.navigation_home
            }
            true
        }
        createTurtleArrays(this)
    }

    fun setViewMode(title: String, levelV: Int=0, toolbarV: Boolean = true, navigationV: Boolean = true) {
        titlemain.text = title
        val level = db.baza_pobierz("gry", "level").toString().toInt()
        val nextlvl = level + 1
        val pointstolvl = db.baza_pobierz("gry", "punktydolvl").toString()
        if (levelV == 0) {
            toolbarlevel.text = "$level"
        } else {
            if (level >= 20) {
                toolbarlevel.text = "Poziom MAX"
            } else {
                toolbarlevel.text = "$pointstolvl do poziomu $nextlvl"
            }
        }
        if(toolbarV) toolbar.visibility = View.VISIBLE else toolbar.visibility = View.GONE
        if(navigationV) navigation.visibility=View.VISIBLE else navigation.visibility=View.GONE
    }

    fun updatePoints(points: String) {
        val pointsupdate = db.baza_pobierz("gry", "punkty2").toString().toInt() + points.toInt()
        db.baza_wstaw("gry", "punkty2", pointsupdate.toString())
        val levelThresholds = listOf(
            1000,
            4000,
            10000,
            20000,
            30500,
            41500,
            53000,
            65000,
            77500,
            90500,
            104000,
            118000,
            132500,
            147500,
            163000,
            179000,
            195500,
            212500,
            230000,
            248000
        )
        var currentLevel = 0
        var pointsForNextLevel = 0

        for (i in levelThresholds.indices) {
            // Sprawdź czy suma punktów przekracza próg poziomu
            if (pointsupdate >= levelThresholds[i]) {
                currentLevel = i + 1 // Poziomy zaczynają się od 1
            } else {
                pointsForNextLevel = levelThresholds[i] - pointsupdate
                break
            }
        }

        // Jeśli przekroczono najwyższy poziom, nie ma kolejnego poziomu
        if (currentLevel == levelThresholds.size) pointsForNextLevel =
            0 // Użytkownik osiągnął maksymalny poziom

        val level = db.baza_pobierz("gry", "level").toString().toInt()

        if (currentLevel > level) {
            Toast.makeText(
                this,
                "Level up ($currentLevel)! Odblokowano nową zawartość dla avatara.",
                Toast.LENGTH_SHORT
            ).show()
        }
        db.baza_wstaw("gry", "level", currentLevel.toString())
        db.baza_wstaw("gry", "punktydolvl", pointsForNextLevel.toString())


    }

    fun loadFragment(fragment: Fragment, mode: Int = 1, data: Bundle? = null) {
        // Jeśli dane są przekazane, ustaw je w fragmencie
        if (data != null) fragment.arguments = data

        val transaction = supportFragmentManager.beginTransaction()
        when (mode) {
            1 -> transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            2 -> transaction.setCustomAnimations(R.anim.fade_down, R.anim.fade_up)
            3 -> transaction.setCustomAnimations(R.anim.fade_up2, R.anim.fade_down2)
            4 -> transaction.setCustomAnimations(R.anim.go_left, R.anim.go_left2)
            5 -> transaction.setCustomAnimations(R.anim.go_right2, R.anim.go_right)

        }
        transaction.replace(R.id.content_frame, fragment)
        transaction.addToBackStack(null) // Umożliwia cofnięcie do poprzedniego fragmentu
        transaction.commit()
    }

    fun setup_avatar(avatar: ImageView, avatar_background: LayerDrawable?) {
        val image = db.baza_pobierz("turtle", "avatar")
        val stroke = db.baza_pobierz("turtle", "stroke")
        val tlo = db.baza_pobierz("turtle", "tlo")



        avatar.setImageResource(resources.getIdentifier(image, "drawable", packageName))
        avatar.setBackgroundResource(resources.getIdentifier(tlo, "drawable", packageName))

        val strokeColor = when (stroke) {
            "Czarny" -> ContextCompat.getColor(this, R.color.black)
            "Fioletowy" -> ContextCompat.getColor(this, R.color.purple)
            "Różowy" -> ContextCompat.getColor(this, R.color.pink)
            "Niebieski" -> ContextCompat.getColor(this, R.color.niebieski)
            "Czerwony" -> ContextCompat.getColor(this, R.color.czerwony)
            "Pomarańczowy" -> ContextCompat.getColor(this, R.color.orange)
            "Złoty" -> ContextCompat.getColor(this, R.color.golden_color)
            else -> ContextCompat.getColor(this, R.color.purple)
        }
        val strokeWidth = resources.getDimensionPixelSize(R.dimen.avatar_stroke_width_2)

        avatar_background?.let {
            val strokeLayer = it.getDrawable(1) as GradientDrawable
            strokeLayer.setStroke(strokeWidth, strokeColor)
        }

        val layerDrawable = toolbarlevel.background as LayerDrawable

        layerDrawable.let {
            // Pobierz warstwę, która zawiera stroke (to jest druga warstwa w twoim przypadku, więc indeks 1)
            val strokeLayer = it.getDrawable(1) as GradientDrawable

            strokeLayer.setStroke(strokeWidth, strokeColor)
        }
        toolbar.background.let { background ->
            // Sprawdzenie czy tło toolbaru jest typu GradientDrawable
            val strokeLayer = background as GradientDrawable
            strokeLayer.setStroke(strokeWidth, strokeColor)
        }
    }


    fun createTurtleArrays(context: Context){
        // Wczytaj plik JSON
        val jsonString = context.assets.open("turtles.json").bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonString)
        val turtlesArray: JSONArray = jsonObject.getJSONArray("turtles")

        // Iteracja po żółwiach i dodawanie do odpowiednich list na podstawie atrybutu "live"
        for (i in 0 until turtlesArray.length()) {
            val turtleObject = turtlesArray.getJSONObject(i)
            val turtleName = turtleObject.getString("name")
            val turtleLive = turtleObject.getString("live")

            if (turtleLive == "W") {
                tab_wodne.add(turtleName)
            } else if (turtleLive == "L") {
                tab_ladowe.add(turtleName)
            }
            tab_all.add(turtleName)
        }
    }
    fun removePolishChars(text: String): String {
        val polishChars = "ąćęłńóśźż"
        val englishChars = "acelnoszz"

        return text.map { char ->
            val index = polishChars.indexOf(char)
            if (index != -1) englishChars[index] else char
        }.joinToString("")
    }
}

