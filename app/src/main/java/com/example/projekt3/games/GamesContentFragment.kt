package com.example.projekt3.games

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import com.example.projekt3.aglobalApk.Baseactivity
import com.example.projekt3.aglobalApk.DatabaseHelper
import com.example.projekt3.R
import org.json.JSONArray
import org.json.JSONObject

class GamesContentFragment : Fragment() {
    val allQuestions: MutableList<Question> = mutableListOf()
    val questionsList: MutableList<String> = mutableListOf()
    val answersA: MutableList<String> = mutableListOf()
    val answersB: MutableList<String> = mutableListOf()
    val answersC: MutableList<String> = mutableListOf()
    val answersD: MutableList<String> = mutableListOf()
    val categories: MutableList<String> = mutableListOf()
    val correctAnswers: MutableList<String> = mutableListOf()
    var currentQuestionIndex: Int = 0
    private lateinit var buttonA: Button
    private lateinit var buttonB: Button
    private lateinit var buttonC: Button
    private lateinit var buttonD: Button
    private lateinit var pytanie: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var points_textview: TextView
    private lateinit var question_number: TextView
    private lateinit var points: TextView
    private var game_type: Int? = 6
    private var time: Int = 0
    private var timer: CountDownTimer? = null
    private var isPaused = false
    private var timeRemainingInMillis: Long = 0
    private var next = true
    private var givepoints = true
    private var givepoints_normal = true
    private var currentValue: String = ""
    private var currentBar: Int = 0
    private var mainView: View? = null
    private var correct: String = ""
    private var end = false
    private lateinit var db: DatabaseHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = DatabaseHelper(requireContext())
        return inflater.inflate(R.layout.fragment_games_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as Baseactivity).setViewMode("Quiz - gra",1,false,false)

//--------------------
        question_number = view.findViewById(R.id.textView14)
        points_textview = view.findViewById(R.id.textView17)
        progressBar = view.findViewById(R.id.progressBar)

        mainView = view.findViewById(R.id.main)


        val title = view.findViewById<TextView>(R.id.textView15)
        points = view.findViewById(R.id.textView18)
        pytanie = view.findViewById(R.id.textView19)
        val image = view.findViewById<ImageView>(R.id.imageView7)
        val back = view.findViewById<TextView>(R.id.textView16)
        back.setOnClickListener {
            if (!end) {
                endGame(true)
            }

        }
        buttonA = view.findViewById(R.id.button)
        buttonB = view.findViewById(R.id.button2)
        buttonC = view.findViewById(R.id.button3)
        buttonD = view.findViewById(R.id.button4)

//---------------------------------------------------
        points.text = "0"

        game_type = arguments?.getInt("game_type")
        when (game_type) {
            1 -> {
                title.text = "Tryb: Zwykły Quiz"
                image.setImageResource(R.drawable.z_quiz_1)
                time = 20
                gra1(3, 3, 2, true)
            }

            2 -> {
                title.text = "Tryb: Teleturniej"
                image.setImageResource(R.drawable.z_quiz_2)
                time = 30
                gra1(2, 4, 4, false)
            }

            3 -> {
                points.text = "20000"
                title.text = "Tryb: Gra o Wszystko"
                image.setImageResource(R.drawable.z_quiz_3)
                time = 15
                gra1(0, 0, 10, true)
            }
        }
    }

    fun endGame(user_end: Boolean) {
        end = true
        stopCountdown()
        next = false
        clean()
        buttonA.isEnabled = false
        buttonB.isEnabled = false
        buttonC.isEnabled = false
        buttonD.isEnabled = false
        val title: String
        val message: String


        if (user_end) {
            title = "Gra przerwana"
            if (game_type == 3) points.text = "0"
        } else {
            title = "Koniec gry"
        }
        var pointsend = points.text.toString()
        if (pointsend.toInt() > 0) {
            message = "Gratulacje! Twój wynik to: $pointsend."

            (activity as Baseactivity).updatePoints(pointsend)
        } else {
            message =
                "Było blisko! Niestety nie udało Ci się zdobyć żadnych punktów. Ale to nic, spróbuj jeszcze raz!"
        }


        showDialog(title, message)


        points_textview.text = ""
    }
    // Globalna lista z pytaniami

    // Funkcja do obsługi wyświetlania pytań
    fun displayQuestion() {

        if (currentQuestionIndex >= questionsList.size) {
            // Zakończ grę jeśli wszystkie pytania się skończyły
            endGame(false)
            return
        }
        resetCountdown(progressBar)
        when (game_type) {
            1 -> {
                when (categories[currentQuestionIndex]) {
                    "easy" -> currentValue = "100"
                    "normal" -> currentValue = "200"
                    "hard" -> currentValue = "300"
                }
            }

            2 -> {
                when (currentQuestionIndex) {
                    0 -> currentValue = "100"
                    1 -> currentValue = "200"
                    2 -> currentValue = "500"
                    3 -> currentValue = "750"
                    4 -> currentValue = "1250"
                    5 -> currentValue = "2500"
                    6 -> currentValue = "3750"
                    7 -> currentValue = "5000"
                    8 -> currentValue = "7500"
                    9 -> currentValue = "10000"
                }
                currentBar = currentQuestionIndex
            }

            3 -> {
                if (currentQuestionIndex in 0..9)  currentValue = "2500"

            }
        }
        question_number.text = (currentQuestionIndex + 1).toString() + "/" + questionsList.size.toString()
        points_textview.text = currentValue


        // Ustaw pytanie i odpowiedzi
        pytanie.text = questionsList[currentQuestionIndex]
        buttonA.text = answersA[currentQuestionIndex]
        buttonB.text = answersB[currentQuestionIndex]
        buttonC.text = answersC[currentQuestionIndex]
        buttonD.text = answersD[currentQuestionIndex]

        // Zresetuj wygląd przycisków
        buttonA.isEnabled = true
        buttonB.isEnabled = true
        buttonC.isEnabled = true
        buttonD.isEnabled = true

        next = true

        correct = correctAnswers[currentQuestionIndex]
        // Przechwytywanie kliknięć na przyciski
        buttonA.setOnClickListener { checkAnswer(buttonA, "A") }
        buttonB.setOnClickListener { checkAnswer(buttonB, "B") }
        buttonC.setOnClickListener { checkAnswer(buttonC, "C") }
        buttonD.setOnClickListener { checkAnswer(buttonD, "D") }

    }

    // Funkcja do sprawdzania odpowiedzi
    fun checkAnswer(
        button: Button,
        selectedAnswer: String,
    ) {
        stopCountdown()




        val currentPoints = points.text

        // Wyłącz przyciski po odpowiedzi
        buttonA.isEnabled = false
        buttonB.isEnabled = false
        buttonC.isEnabled = false
        buttonD.isEnabled = false


        if (selectedAnswer == correct) {

            button.foreground = getDrawable(requireContext(), R.drawable.x_quiz_answer_true)
            givepoints_normal = true

        } else {
            button.foreground = getDrawable(requireContext(), R.drawable.x_quiz_answer_false)
            givepoints_normal = false
            when (correct) {
                "A" -> buttonA.foreground =
                    getDrawable(requireContext(), R.drawable.x_quiz_answer_true)

                "B" -> buttonB.foreground =
                    getDrawable(requireContext(), R.drawable.x_quiz_answer_true)

                "C" -> buttonC.foreground =
                    getDrawable(requireContext(), R.drawable.x_quiz_answer_true)

                "D" -> buttonD.foreground =
                    getDrawable(requireContext(), R.drawable.x_quiz_answer_true)
            }
            if (next && game_type == 2) {
                points.text = "0"
                endGame(false)
                return
            }
            if (game_type == 3) {
                points.text = (currentPoints.toString().toInt() - currentValue.toInt()).toString()
                if (points.text.toString().toInt() == 0) {
                    endGame(false)
                    return
                }
            }
        }
        if (givepoints && givepoints_normal) {
            when (game_type) {
                1 -> points.text =(currentPoints.toString().toInt() + currentValue.toInt()).toString()
                2 -> points.text = currentValue.toInt().toString()
            }
        }
        if (next) {
            nextQuestion()
        }
    }

    fun nextQuestion() {
        Handler(Looper.getMainLooper()).postDelayed({
            buttonA.foreground = null
            buttonB.foreground = null
            buttonC.foreground = null
            buttonD.foreground = null
            currentQuestionIndex++
            displayQuestion()
        }, 2500)
    }

    // Funkcja główna do uruchamiania gry
    fun gra1(
        easyCount: Int,
        normalCount: Int,
        hardCount: Int,
        shuffle: Boolean,

        ) {
        // 1. Ładowanie pytań z pliku JSON do allQuestions

        if (allQuestions.isEmpty()) {
            loadQuestionsFromJson()
        }
        // 2. Losowanie pytań zgodnie z ustawieniami
        getRandomQuestions(easyCount, normalCount, hardCount, shuffle)

        // 3. Wyświetlanie pierwszego pytania
        currentQuestionIndex = 0
        displayQuestion()
    }

    fun loadQuestionsFromJson() {
        // Ładuje pytania z JSONa do allQuestions (w osobnej funkcji)
        // Zakładam, że struktura JSON jest zgodna ze strukturą pytania (Question)
        val jsonString =
            requireContext().assets.open("questions.json").bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonString)

        val jsonArray: JSONArray = jsonObject.getJSONArray("questions")

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val id = jsonObject.getInt("id")
            val category = jsonObject.getString("category")
            val question = jsonObject.getString("question")
            val correctAnswer = jsonObject.getString("correct_answer")
            val answers = jsonObject.getJSONObject("answers")

            val a = answers.getString("A")
            val b = answers.getString("B")
            val c = answers.getString("C")
            val d = answers.getString("D")

            // Dodajemy pytanie do listy
            allQuestions.add(Question(id, category, question, correctAnswer, a, b, c, d))
        }
    }

    fun getRandomQuestions(
        easyCount: Int,
        normalCount: Int,
        hardCount: Int,
        shuffle: Boolean
    ) {
        clean()
        val easyQuestions = allQuestions.filter { it.category == "easy" }.shuffled().take(easyCount)
        val normalQuestions =
            allQuestions.filter { it.category == "normal" }.shuffled().take(normalCount)
        val hardQuestions = allQuestions.filter { it.category == "hard" }.shuffled().take(hardCount)

        val selectedQuestions = mutableListOf<Question>()

        if (!shuffle) {
            selectedQuestions.addAll(easyQuestions)
            selectedQuestions.addAll(normalQuestions)
            selectedQuestions.addAll(hardQuestions)
        } else {
            selectedQuestions.addAll(easyQuestions + normalQuestions + hardQuestions)
            selectedQuestions.shuffle()
        }

        // Ładowanie pytań do list
        for (question in selectedQuestions) {
            questionsList.add(question.question)
            categories.add(question.category)
            answersA.add(question.a)
            answersB.add(question.b)
            answersC.add(question.c)
            answersD.add(question.d)
            correctAnswers.add(question.correctAnswer)
        }
    }

    fun clean() {
        questionsList.clear()
        answersA.clear()
        answersB.clear()
        answersC.clear()
        answersD.clear()
        categories.clear()
        correctAnswers.clear()
    }

    // Struktura pytania
    data class Question(
        val id: Int,
        val category: String,
        val question: String,
        val correctAnswer: String,
        val a: String,
        val b: String,
        val c: String,
        val d: String
    )


    fun startCountdown(progressBar: ProgressBar, totalTimeInSeconds: Int) {
        // Jeśli odliczanie jest już w toku, nie rób nic
        if (timer != null && !isPaused) return

        val initialProgress = 100 // Całkowita wartość procentowa dla ProgressBar
        progressBar.max = initialProgress
        progressBar.progress = initialProgress

        // Jeśli odliczanie jest wstrzymane, wznów z pozostałym czasem
        val startTimeInMillis = if (isPaused) timeRemainingInMillis else totalTimeInSeconds * 1000L

        timer = object : CountDownTimer(startTimeInMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                // Przechowaj pozostały czas
                timeRemainingInMillis = millisUntilFinished

                // Obliczenie pozostałego czasu w sekundach
                val secondsRemaining = (millisUntilFinished / 1000).toInt()

                // Oblicz procentowy postęp
                val progress = (secondsRemaining * 100) / totalTimeInSeconds
                progressBar.progress = progress

                // Zmiana koloru ProgressBar w zależności od postępu
                when {
                    progress > 50 -> progressBar.progressDrawable.setColorFilter(Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN)
                    progress > 25 -> progressBar.progressDrawable.setColorFilter(Color.YELLOW, android.graphics.PorterDuff.Mode.SRC_IN )

                    progress <=0 -> {
                        stopCountdown()
                        next = false
                        givepoints = false
                        buttonA.isEnabled = false
                        buttonB.isEnabled = false
                        buttonC.isEnabled = false
                        buttonD.isEnabled = false
                        checkAnswer(buttonA, "A")
                        checkAnswer(buttonB, "B")
                        checkAnswer(buttonC, "C")
                        next = true
                        checkAnswer(buttonD, "D")
                        givepoints = true
                    }
                    else -> {
                        progressBar.progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN)
                    }
                }
            }

            override fun onFinish() {
            }
        }

        timer?.start()
        isPaused = false
    }

    // Funkcja do zatrzymania odliczania
    fun stopCountdown() {
        if (timer != null) {
            timer?.cancel()
            isPaused = true
        }
    }

    // Funkcja do zresetowania odliczania
    fun resetCountdown(progressBar: ProgressBar) {
        if (timer != null) {
            timer?.cancel()
            timer = null
            isPaused = false
            timeRemainingInMillis = 0
        }
        startCountdown(progressBar, time)
    }

    fun showDialog(title: String, message: String) {
        mainView?.foreground = getDrawable(requireContext(), R.drawable.x_dialog_background)

        val builder = AlertDialog.Builder(requireContext())
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

        builder.setMessage(message)

        builder.setNegativeButton("OK") { _, _ ->
            (activity as Baseactivity).loadFragment(GamesFragment(), 5)
        }
        val dialog = builder.create()
        dialog.setOnShowListener {
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeButton.setBackgroundColor(Color.BLACK)
            negativeButton.setTextColor(Color.WHITE) // Zmien kolor na pożądany
            dialog.window?.setBackgroundDrawableResource(R.drawable.x_dialog_body)
        }

        dialog.show()


    }


}