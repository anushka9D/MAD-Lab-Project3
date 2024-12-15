package com.example.myapplication


import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class View_Task_Page : AppCompatActivity() {

    private lateinit var title: TextView
    private lateinit var note: TextView
    private lateinit var date: TextView
    private lateinit var time: TextView

    private var taskIndex: Int? = null
    private var taskList: MutableList<Task> = mutableListOf()

    private lateinit var startTimer: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_task_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val back: ImageButton = findViewById(R.id.back)
        back.setOnClickListener {
            finish() // Go back to the previous activity
        }

        title = findViewById(R.id.title)
        note = findViewById(R.id.note)
        date = findViewById(R.id.date)
        time = findViewById(R.id.time)

        startTimer = findViewById(R.id.btnStartTimer)
        startTimer.setOnClickListener {
            val intent = Intent(this, Timer_Set_Page::class.java)
            startActivity(intent)
        }

        // Load existing tasks
        loadTasks()

        // Get the task index from the intent
        taskIndex = intent.getIntExtra("task_index", -1)


        // Display task data if available
        if (taskIndex != -1) {
            val task = taskList[taskIndex!!]
            date.setText(task.date)
            note.setText(task.note)
            time.setText(task.time)
            title.setText(task.title)
        }


    }

    private fun loadTasks() {
        val sharedPreferences = getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("tasks", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<Task>>() {}.type
            taskList = Gson().fromJson(json, type)
        }
    }

}
