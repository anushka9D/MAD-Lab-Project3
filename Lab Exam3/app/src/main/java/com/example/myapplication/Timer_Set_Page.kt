package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class Timer_Set_Page : AppCompatActivity() {

    private lateinit var npHours: NumberPicker
    private lateinit var npMinutes: NumberPicker
    private lateinit var npSeconds: NumberPicker
    private lateinit var btnStartTimer: Button
    //private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_timer_set_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val back: ImageButton = findViewById(R.id.back)
        back.setOnClickListener {
            finish() // Go back to the previous activity
        }

        // Initialize NumberPickers and Button
        npHours = findViewById(R.id.npHours)
        npMinutes = findViewById(R.id.npMinutes)
        npSeconds = findViewById(R.id.npSeconds)
        btnStartTimer = findViewById(R.id.startTimer)

        // Set min and max values for hours, minutes, and seconds
        npHours.minValue = 0
        npHours.maxValue = 23
        npMinutes.minValue = 0
        npMinutes.maxValue = 59
        npSeconds.minValue = 0
        npSeconds.maxValue = 59

        // Start the Timer_Page when the button is clicked
        btnStartTimer.setOnClickListener {
            val hours = npHours.value
            val minutes = npMinutes.value
            val seconds = npSeconds.value

            // Calculate total milliseconds for the countdown
            val totalMillis = (hours * 3600 + minutes * 60 + seconds) * 1000L

            // Create an Intent to navigate to Timer_Page
            val intent = Intent(this@Timer_Set_Page, Timer_Page::class.java).apply {
                putExtra("EXTRA_HOURS", hours)
                putExtra("EXTRA_MINUTES", minutes)
                putExtra("EXTRA_SECONDS", seconds)
            }
            startActivity(intent)
            finish() // Optionally finish this activity
        }
    }

}