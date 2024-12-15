package com.example.myapplication

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.LinearLayout
import android.widget.RemoteViews
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Widget_Task : AppWidgetProvider() {

    private val taskList: MutableList<Task> = mutableListOf()

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // Update each widget instance
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        // Retrieve tasks from SharedPreferences or any other source
        loadTasks(context)

        val views = RemoteViews(context.packageName, R.layout.widget__task) // Use your layout file name

        // Clear previous views and update with new tasks
        views.removeAllViews(R.id.widget_task_container)

        for (task in taskList) {
            val taskView = RemoteViews(context.packageName, R.layout.widget__task) // Use your task item layout
            taskView.setTextViewText(R.id.title, task.title)
            taskView.setTextViewText(R.id.date, "Date: ${task.date}")
            taskView.setTextViewText(R.id.time, "Time: ${task.time}")

            views.addView(R.id.widget_task_container, taskView)
        }

        // Tell the AppWidgetManager to perform an update on the current widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun loadTasks(context: Context) {
        val prefs = context.getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE)
        val json = prefs.getString("tasks", null)
        val type = object : TypeToken<MutableList<Task>>() {}.type
        taskList.clear()

        json?.let {
            taskList.addAll(Gson().fromJson(it, type))
        }
    }
}