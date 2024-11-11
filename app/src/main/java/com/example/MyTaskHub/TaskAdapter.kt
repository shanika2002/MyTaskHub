package com.example.MyTaskHub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val tasks: List<Task>,
    private val onTaskClick: (Task) -> Unit,
    private val onEditClick: (Task) -> Unit,
    private val onDeleteClick: (Task) -> Unit // Add a new callback for delete
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task)
    }

    override fun getItemCount(): Int = tasks.size

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskTitle: TextView = itemView.findViewById(R.id.textViewTask)
        private val editTaskButton: ImageView = itemView.findViewById(R.id.btnEditTask)
        private val deleteTaskButton: ImageView = itemView.findViewById(R.id.btnDeleteTask)

        fun bind(task: Task) {
            taskTitle.text = task.title

            // Edit button click listener
            editTaskButton.setOnClickListener {
                onEditClick(task)
            }

            // Delete button click listener
            deleteTaskButton.setOnClickListener {
                onDeleteClick(task)
            }

            // Task click listener (if needed)
            itemView.setOnClickListener {
                onTaskClick(task)
            }
        }
    }
}
