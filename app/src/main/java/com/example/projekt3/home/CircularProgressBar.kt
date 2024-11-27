package com.example.projekt3.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.projekt3.powiadomienia.Notifications
import kotlin.math.min

class CircularProgressBar(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var progress: Float = 0f // Początkowy postęp ustawiony na 100%
    private val paint: Paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 30f
        isAntiAlias = true
    }

    private val backgroundPaint: Paint = Paint().apply {
        color = Color.TRANSPARENT
        style = Paint.Style.STROKE
        strokeWidth = 31f
        isAntiAlias = true
    }

    // Handler i Runnable do odliczania 1% co sekundę
    private val handler = Handler(Looper.getMainLooper())
    private val decreaseRunnable = object : Runnable {
        override fun run() {

            setProgress(progress + 0.2f) // Odejmowanie 1%
            handler.postDelayed(this, 10) // Uruchamianie co sekundę

        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Środek widoku
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(centerX, centerY) - paint.strokeWidth

        // Rysuj tło okręgu
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)

        // Obliczanie kąta (procent postępu)
        val angle = 360 * progress / 100

        // Ustaw kolor w zależności od wartości progresu
        paint.color = when {
            progress <= 30 -> Color.RED
            progress <= 50 -> Color.rgb(255, 165, 0) // Pomarańczowy
            progress <= 70 -> Color.YELLOW
            else -> Color.GREEN
        }

        // Rysowanie wypełnienia
        canvas.drawArc(
            centerX - radius, centerY - radius, centerX + radius, centerY + radius,
            -90f, angle, false, paint
        )
    }

    // Ustawienie nowego postępu
    fun setProgress(newProgress: Float) {
        if (newProgress >= 100){


            stopDecreasing()
        } else{
            progress = newProgress
            invalidate() 
        }

    }

    fun startDecreasing() {
        handler.post(decreaseRunnable)
    }

    fun stopDecreasing() {
        handler.removeCallbacks(decreaseRunnable)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredSize = 300 // Domyślny minimalny rozmiar w px (możesz dostosować)
        val width = resolveSize(desiredSize, widthMeasureSpec)
        val height = resolveSize(desiredSize, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }
}
