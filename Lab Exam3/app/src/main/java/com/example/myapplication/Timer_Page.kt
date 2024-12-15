package com.example.myapplication

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


const val notificationID = 1
const val channelID = "timer_channel"
const val channelName = "Timer Notifications"
const val notificationPermissionRequestCode = 101

class Timer_Page : AppCompatActivity() {


    private lateinit var tvTimer: TextView
    private lateinit var tvTimeLeft: TextView
    private lateinit var tvCurrentTime: TextView // Add reference for tvCurrentTime
    private lateinit var btnLap: Button
    private lateinit var btnStartStopReset: Button

    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private var timerRunning: Boolean = false
    private var totalDuration: Long = 0  // Store total duration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_timer_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvTimer = findViewById(R.id.timer_text1)

        // Retrieve the data from the Intent
        val hours = intent.getIntExtra("EXTRA_HOURS", 0)
        val minutes = intent.getIntExtra("EXTRA_MINUTES", 0)
        val seconds = intent.getIntExtra("EXTRA_SECONDS", 0)

        // Display the timer data
        displayTimer(hours, minutes, seconds)

        timeLeftInMillis = (hours * 3600 + minutes * 60 + seconds) * 1000L


        // Initialize UI elements
        tvTimeLeft = findViewById(R.id.timer_text2)
        tvCurrentTime = findViewById(R.id.timer_text3) // Initialize tvCurrentTime
        btnLap = findViewById(R.id.delete_timer)
        btnStartStopReset = findViewById(R.id.pause_timer)
       // pbTimer = findViewById(R.id.pbTimer)


        // Get current time and set it to tvCurrentTime
        updateCurrentTime(hours, minutes)


        // Start the countdown timer
        startTimer()

        // Button click listeners
        btnStartStopReset.setOnClickListener {
            if (timerRunning) {
                pauseTimer()
            } else {
                resumeTimer()
            }
        }

        btnLap.setOnClickListener {
            // Clear the timer and navigate back to MainActivity
            resetTimer()
            navigateToTimerSetPage()
        }

    }


    // Function to display the current time
    private fun updateCurrentTime(hours: Int, minutes: Int) {
        val calendar = Calendar.getInstance()

        val hour = hours
        val min = minutes

        // Add 1 hour and 1 minute to the current time
        calendar.add(Calendar.HOUR_OF_DAY, hour)
        calendar.add(Calendar.MINUTE, min)

        // Format for current time
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val updatedTime = sdf.format(calendar.time)

        // Set updated time to tvCurrentTime
        tvCurrentTime.text = updatedTime // Set the updated time
    }


    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimer()

            }

            override fun onFinish() {
                timerRunning = false
                tvTimeLeft.text = "00:00:00"
                sendNotification()
                vibratePhone()
                playSound()
                navigateToTimerSetPage()
            }
        }.start()

        timerRunning = true
        btnStartStopReset.text = "Pause"
        btnStartStopReset.setBackgroundColor(resources.getColor(R.color.add_btn))
    }


    private fun pauseTimer() {
        countDownTimer?.cancel()
        timerRunning = false
        btnStartStopReset.text = "Resume"
        btnStartStopReset.setBackgroundColor(resources.getColor(R.color.add_btn))
    }

    private fun resumeTimer() {
        startTimer()
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        timerRunning = false
        timeLeftInMillis = totalDuration // Reset to original duration
        updateTimer()
        btnStartStopReset.text = "Start"
        btnStartStopReset.setBackgroundColor(resources.getColor(R.color.add_btn))
    }

    private fun updateTimer() {
        val hours = (timeLeftInMillis / 1000) / 3600
        val minutes = ((timeLeftInMillis / 1000) % 3600) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        tvTimeLeft.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    // Function to display the notification
    private fun sendNotification() {

        val notificationManager = NotificationManagerCompat.from(this)

        // Intent for when the notification is clicked
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelID)
            .setContentTitle("Timer Finished")
            .setContentText("Your timer has over.")
            .setSmallIcon(R.drawable.baseline_domain_verification_24)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Check for notification permission (if necessary)
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(notificationID, notification)
        }
    }

    // Vibrate the phone when the timer finishes
    private fun vibratePhone() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(500)
        }
    }

    // Play a sound when the timer finishes
    private fun playSound() {
        val notificationSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ringtone = RingtoneManager.getRingtone(applicationContext, notificationSound)
        ringtone.play()
    }

    // Function to navigate back to MainActivity
    private fun navigateToTimerSetPage() {
        val intent = Intent(this, Timer_Set_Page::class.java)
        startActivity(intent)
        finish()
    }

    private fun displayTimer(hours: Int, minutes: Int, seconds: Int) {
        val timerText = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        tvTimer.text = timerText
    }
}