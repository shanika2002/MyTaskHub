package com.example.MyTaskHub

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TaskFragment : Fragment() {

    private lateinit var taskAdapter: TaskAdapter
    private var taskList = mutableListOf<Task>()
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TaskFragment", "TaskAdapter initialized")

        sharedPreferences = requireContext().getSharedPreferences("task_prefs", Context.MODE_PRIVATE)

        loadTasks()

        val view = inflater.inflate(R.layout.activity_task_fragment, container, false)

        setupRecyclerView(view)
        setupAddTaskButton(view)

        return view
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewTasks)
        recyclerView.layoutManager = LinearLayoutManager(context)
        taskAdapter = TaskAdapter(
            taskList,
            { task -> onTaskClick(task) },
            { task -> showEditTaskDialog(task) },
            { task -> deleteTask(task) }  // Pass the delete function
        )
        recyclerView.adapter = taskAdapter
    }

    private fun setupAddTaskButton(view: View) {
        val fabAddTask: FloatingActionButton = view.findViewById(R.id.fabAddTask)
        fabAddTask.setOnClickListener {
            Log.d("TaskFragment", "Floating Action Button clicked")
            showAddTaskDialog()
        }
    }

    private fun showAddTaskDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add New Task")

        val input = EditText(context).apply { hint = "Enter Task Note" }
        builder.setView(input)

        builder.setPositiveButton("Add") { dialog, _ ->
            val taskTitle = input.text.toString().trim()
            if (taskTitle.isNotEmpty()) {
                addTask(taskTitle)
                dialog.dismiss()
            } else {
                showToast("Task title cannot be empty")
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun addTask(title: String) {
        taskList.add(Task(title))
        taskAdapter.notifyDataSetChanged()
        saveTasks()
        showToast("Task added successfully")
    }

    private fun showEditTaskDialog(task: Task) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Edit Task")

        val input = EditText(context).apply { setText(task.title) }
        builder.setView(input)

        builder.setPositiveButton("Save") { dialog, _ ->
            val updatedTitle = input.text.toString().trim()
            if (updatedTitle.isNotEmpty()) {
                updateTask(task, updatedTitle)
                dialog.dismiss()
            } else {
                showToast("Task title cannot be empty")
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun updateTask(task: Task, title: String) {
        task.title = title
        taskAdapter.notifyDataSetChanged()
        saveTasks()
        showToast("Task updated successfully")
    }

    private fun deleteTask(task: Task) {
        taskList.remove(task)  // Remove the task from the list
        taskAdapter.notifyDataSetChanged()  // Notify the adapter
        saveTasks()  // Save the updated task list to SharedPreferences
        showToast("Task deleted successfully")
    }

    private fun onTaskClick(task: Task) {
        // You can handle task click here, or leave it as is
    }

    private fun saveTasks() {
        sharedPreferences.edit().apply {
            val json = gson.toJson(taskList)
            putString("task_list", json)
            apply()
        }
        updateWidget()
    }

    private fun loadTasks() {
        val json = sharedPreferences.getString("task_list", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<Task>>() {}.type
            taskList = gson.fromJson(json, type)
        }
    }

    private fun updateWidget() {
        val intent = Intent(requireContext(), TaskWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(requireContext())
                .getAppWidgetIds(ComponentName(requireContext(), TaskWidgetProvider::class.java))
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }
        requireContext().sendBroadcast(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
