package com.example.projekt3.info

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.projekt3.aglobalApk.Baseactivity
import com.example.projekt3.aglobalApk.DatabaseHelper
import com.example.projekt3.R
import com.google.android.material.tabs.TabLayout

class InfoturtleFragment : Fragment() {
    private lateinit var buttonContainer: LinearLayout
    private lateinit var db: DatabaseHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_infoturtle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as Baseactivity).setViewMode("Poznaj Gatunki")


        buttonContainer = view.findViewById(R.id.buttonContainer)
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)

        db = DatabaseHelper(requireContext())
        db.baza_wstaw("ustawienia", "current", "3")
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                buttonContainer.removeAllViews()  // Usuń stare przyciski
                when (tab?.position) {
                    0 -> {
                        turtle_tab_container((activity as Baseactivity).tab_ladowe)
                        db.baza_wstaw("ustawienia", "turtletab", "1")
                    }
                    1 -> {
                        turtle_tab_container((activity as Baseactivity).tab_wodne)
                        db.baza_wstaw("ustawienia", "turtletab", "2")
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        val currenttab=db.baza_pobierz("ustawienia", "turtletab").toString().toInt()
        if(currenttab==1){
            turtle_tab_container((activity as Baseactivity).tab_ladowe)
            tabLayout.getTabAt(0)?.select()
        }
        else{
            turtle_tab_container((activity as Baseactivity).tab_wodne)
            tabLayout.getTabAt(1)?.select()
        }
    }

    private fun turtle_tab_container(kat: MutableList<String>) {
        kat.forEach { buttonText ->
            val button = Button(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 24, 0, 24)
                }
                text = buttonText
                setTextAppearance(R.style.ButtonTextStyle)
                setBackgroundResource(R.drawable.x_info_buttons)
                setOnClickListener {
                    info(buttonText)
                }
            }
            buttonContainer.addView(button)
        }
    }

    @SuppressLint("DiscouragedApi")
    fun info(turtle_name: String) {

        val res=(activity as Baseactivity).removePolishChars(turtle_name.lowercase().substring(turtle_name.lastIndexOf(' ') + 1))
        val resourceId = resources.getIdentifier("z_turtles_$res", "drawable", requireContext().packageName)
        val bundle = Bundle()
        bundle.putString("image_resource", resourceId.toString())
        bundle.putString("nazwa", turtle_name)

        (activity as Baseactivity).loadFragment(
            InfoturtleContentFragment(),
            4,
            bundle
        )
    }
}
