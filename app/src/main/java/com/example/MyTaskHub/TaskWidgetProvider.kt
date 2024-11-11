package com.example.MyTaskHub

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TaskWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_task)

            // Load tasks from SharedPreferences
            val sharedPreferences = context.getSharedPreferences("task_prefs", Context.MODE_PRIVATE)
            val json = sharedPreferences.getString("task_list", null)

            // Deserialize task list
            val taskList: List<Task> = if (json != null) {
                val type = object : TypeToken<List<Task>>() {}.type
                Gson().fromJson(json, type)
            } else {
                emptyList()
            }

            // Display tasks in the widget
            views.setTextViewText(R.id.taskItem1, taskList.getOrNull(0)?.title ?: "No Task")
            views.setTextViewText(R.id.taskItem2, taskList.getOrNull(1)?.title ?: "")
            views.setTextViewText(R.id.taskItem3, taskList.getOrNull(2)?.title ?: "")

            // Launch activity on click
            val intent = Intent(context, SecondMainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widgetTitle, pendingIntent)

            // Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        // Manually trigger widget update
        fun updateAllWidgets(context: Context) {
            val widgetManager = AppWidgetManager.getInstance(context)
            val ids = widgetManager.getAppWidgetIds(ComponentName(context, TaskWidgetProvider::class.java))
            for (id in ids) {
                updateAppWidget(context, widgetManager, id)
            }
        }
    }
}
