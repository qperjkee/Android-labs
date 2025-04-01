package com.example.lab3

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader

class FileViewActivity : AppCompatActivity() {

    private lateinit var tvFileContent: TextView
    private lateinit var btnBack: Button
    private lateinit var btnClear: Button
    private val FILE_NAME = "selection_data.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_view)

        tvFileContent = findViewById(R.id.tvFileContent)
        btnBack = findViewById(R.id.btnBack)
        btnClear = findViewById(R.id.btnClear)

        loadFileContent()

        btnBack.setOnClickListener {
            finish()
        }

        btnClear.setOnClickListener {
            showConfirmationDialog()
        }
    }

    private fun loadFileContent() {
        try {
            val fileInputStream: FileInputStream = openFileInput(FILE_NAME)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder = StringBuilder()
            var line: String?

            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append('\n')
            }

            bufferedReader.close()
            inputStreamReader.close()
            fileInputStream.close()

            if (stringBuilder.isNotEmpty()) {
                tvFileContent.text = stringBuilder.toString()
            } else {
                tvFileContent.text = "No data found in the file."
            }

        } catch (e: Exception) {
            tvFileContent.text = "No data found in the file."
        }
    }

    private fun clearFile() {
        try {
            val fileOutputStream = openFileOutput(FILE_NAME, Context.MODE_PRIVATE)
            fileOutputStream.close()

            tvFileContent.text = "No data found in the file."

            Toast.makeText(this, "File cleared successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error clearing file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Clear File")
            .setMessage("Are you sure you want to clear all data from the file?")
            .setPositiveButton("Yes") { _, _ ->
                clearFile()
            }
            .setNegativeButton("No", null)
            .show()
    }
}