package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Home_Page : AppCompatActivity() {

    private lateinit var taskContainer: LinearLayout
    private var taskList: MutableList<Task> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Check if permission is already granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is not granted, so request it
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    notificationPermissionRequestCode
                )
            }
        }

        val addTaskButton: ImageButton = findViewById(R.id.add)
        addTaskButton.setOnClickListener {
            val intent = Intent(this, Add_Task_Page::class.java)
            startActivity(intent)
        }

        val timerButton: ImageButton = findViewById(R.id.timer)
        timerButton.setOnClickListener {
            val intent = Intent(this, Timer_Set_Page::class.java)
            startActivity(intent)
        }


        taskContainer = findViewById(R.id.taskContainer)

        loadTasks()
        displayTasks()

    }

    // Handle the permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == notificationPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted - you can post notifications now
            } else {
                // Permission denied - handle the case (e.g., show a message to the user)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadTasks() // Reload tasks when returning to this activity
        displayTasks()
    }

    private fun loadTasks() {
        val sharedPreferences = getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("tasks", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<Task>>() {}.type
            taskList = Gson().fromJson(json, type)
        }
    }

    private fun displayTasks() {
        taskContainer.removeAllViews()
        for (task in taskList) {
            val taskView = LayoutInflater.from(this).inflate(R.layout.task_item, taskContainer, false)
            val titleTextView: TextView = taskView.findViewById(R.id.title1)
            val dateTextView: TextView = taskView.findViewById(R.id.date1)
            val timeTextView: TextView = taskView.findViewById(R.id.time1)
            val editButton: ImageButton = taskView.findViewById(R.id.edit)
            val deleteButton: ImageButton = taskView.findViewById(R.id.delete)
            val viewButton: ImageButton = taskView.findViewById(R.id.view)

            titleTextView.text = task.title
            dateTextView.text = "Date: ${task.date}"
            timeTextView.text = "Time: ${task.time}"

            editButton.setOnClickListener {
                val intent = Intent(this, Edit_Task_Page::class.java)
                intent.putExtra("task_index", taskList.indexOf(task)) // Pass the index of the task
                startActivity(intent)
            }

            // Set click the delete button
            deleteButton.setOnClickListener {
                // Show confirmation dialog
                AlertDialog.Builder(this)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Yes") { _, _ ->
                        taskList.remove(task)
                        saveTasks()
                        displayTasks()
                    }
                    .setNegativeButton("No", null)
                    .show()
            }

            viewButton.setOnClickListener {
                val intent = Intent(this, View_Task_Page::class.java)
                intent.putExtra("task_index", taskList.indexOf(task)) // Pass the index of the task
                startActivity(intent)
            }

            taskContainer.addView(taskView)
        }
    }


    private fun saveTasks() {
        val sharedPreferences = getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(taskList)
        editor.putString("tasks", json)
        editor.apply()

    }
}