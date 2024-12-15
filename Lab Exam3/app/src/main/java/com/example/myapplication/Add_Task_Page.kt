package com.example.myapplication

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

data class Task(val title: String, val date: String, val time: String, val note: String)

class Add_Task_Page : AppCompatActivity() {

    private lateinit var title: EditText
    private lateinit var note: EditText
    private lateinit var date: EditText
    private lateinit var time: EditText
    private lateinit var save: Button

    private var taskIndex: Int? = null
    private var taskList: MutableList<Task> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_task_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val back: ImageButton = findViewById(R.id.back)
        back.setOnClickListener {
            finish() // Go back to the previous activity
        }

        // Initialize views
        title = findViewById(R.id.title)
        note = findViewById(R.id.note)
        date = findViewById(R.id.date)
        time = findViewById(R.id.time)
        save = findViewById(R.id.save)

        // Load existing tasks
        loadTasks()

        // Check if editing an existing task
        taskIndex = intent.getIntExtra("task_index", -1)
        if (taskIndex != -1) {
            populateTaskData(taskIndex!!)
        }

        // Set listeners for date and time pickers
        date.setOnClickListener { showDatePicker() }
        time.setOnClickListener { showTimePicker() }

        // Save button listener
        save.setOnClickListener { saveTask() }



    }

    private fun loadTasks() {
        val sharedPreferences = getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("tasks", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<Task>>() {}.type
            taskList = Gson().fromJson(json, type)
        }
    }

    private fun saveTasks() {
        val sharedPreferences = getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(taskList)
        editor.putString("tasks", json)
        editor.apply()
    }

    private fun populateTaskData(index: Int) {
        val task = taskList[index]
        title.setText(task.title)
        note.setText(task.note)
        date.setText(task.date)
        time.setText(task.time)
    }

    private fun saveTask() {
        val titleText = title.text.toString().trim()
        val noteText = note.text.toString().trim()
        val dateText = date.text.toString().trim()
        val timeText = time.text.toString().trim()

        if (titleText.isEmpty()) {
            Toast.makeText(this, "Task title cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val task = Task(titleText, dateText, timeText, noteText)

        if (taskIndex != null && taskIndex != -1) {
            // Update existing task
            taskList[taskIndex!!] = task
            Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show()
        } else {
            // Add new task
            taskList.add(task)
            Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show()
        }

        saveTasks()
        setResult(RESULT_OK)
        finish() // Close the activity after saving
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedMonth = (selectedMonth + 1).toString().padStart(2, '0')
            val formattedDay = selectedDay.toString().padStart(2, '0')
            date.setText("$selectedYear-$formattedMonth-$formattedDay")
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val formattedHour = selectedHour.toString().padStart(2, '0')
            val formattedMinute = selectedMinute.toString().padStart(2, '0')
            time.setText("$formattedHour:$formattedMinute")
        }, hour, minute, true)

        timePickerDialog.show()
    }



}