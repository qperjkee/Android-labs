package com.example.lab6

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var taskInput: EditText
    private lateinit var addButton: Button
    private lateinit var prioritySpinner: Spinner
    private lateinit var sortSpinner: Spinner
    private lateinit var taskListView: ListView
    private val taskList = mutableListOf<Task>()
    private lateinit var adapter: ArrayAdapter<Task>

    private val fileName = "todolist.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskInput = findViewById(R.id.taskInput)
        addButton = findViewById(R.id.addButton)
        prioritySpinner = findViewById(R.id.prioritySpinner)
        sortSpinner = findViewById(R.id.sortSpinner)
        taskListView = findViewById(R.id.taskListView)

        val priorityAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.priority_levels, android.R.layout.simple_spinner_item
        )
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        prioritySpinner.adapter = priorityAdapter

        val sortAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.sort_options, android.R.layout.simple_spinner_item
        )
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortSpinner.adapter = sortAdapter

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, taskList)
        taskListView.adapter = adapter

        loadTasks()

        addButton.setOnClickListener {
            val taskText = taskInput.text.toString()
            if (taskText.isNotEmpty()) {
                val priority = prioritySpinner.selectedItem.toString()
                val task = Task(taskText, priority, System.currentTimeMillis())
                taskList.add(task)
                adapter.notifyDataSetChanged()
                taskInput.text.clear()
                saveTasks()
            } else {
                Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show()
            }
        }

        taskListView.setOnItemClickListener { _, _, position, _ ->
            val task = taskList[position]

            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to delete this task?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    taskList.removeAt(position)
                    adapter.notifyDataSetChanged()
                    saveTasks()
                    Toast.makeText(this, "Task removed", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No", null)
            builder.create().show()
        }

        // Handle sorting
        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> sortTasksByPriority()
                    1 -> sortTasksByDate()
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }
    }

    private fun saveTasks() {
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE)
            for (task in taskList) {
                val taskData = "${task.text},${task.priority},${task.dateCreated}\n"
                fileOutputStream.write(taskData.toByteArray())
            }
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadTasks() {
        val fileInputStream: FileInputStream
        try {
            fileInputStream = openFileInput(fileName)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = inputStreamReader.readLines()
            taskList.clear()
            for (line in bufferedReader) {
                val taskData = line.split(",")
                if (taskData.size == 3) {
                    val text = taskData[0]
                    val priority = taskData[1]
                    val dateCreated = taskData[2].toLong()
                    taskList.add(Task(text, priority, dateCreated))
                }
            }
            adapter.notifyDataSetChanged()
            fileInputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun sortTasksByPriority() {
        taskList.sortBy { it.priority }
        adapter.notifyDataSetChanged()
    }

    private fun sortTasksByDate() {
        taskList.sortBy { it.dateCreated }
        adapter.notifyDataSetChanged()
    }
}

data class Task(val text: String, val priority: String, val dateCreated: Long) {
    override fun toString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = dateFormat.format(Date(dateCreated))
        return "$text\nPriority: $priority\nCreated on: $date"
    }
}