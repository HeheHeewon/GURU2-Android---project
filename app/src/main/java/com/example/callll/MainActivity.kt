package com.example.callll


import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var scheduleListLayout: LinearLayout
    private var selectedDate: String? = null
    private val scheduleList = mutableListOf<String>() // List<schedule>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendarView = findViewById(R.id.calendarView)
        scheduleListLayout = findViewById(R.id.scheduleListLayout)

        // Load schedules from SharedPreferences
        val sharedPreferences = getSharedPreferences("SchedulePreferences", Context.MODE_PRIVATE)
        val savedSchedules = sharedPreferences.getStringSet("schedules", setOf())
        savedSchedules?.forEach {
            scheduleList.add(it)
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = "$year/${month + 1}/$dayOfMonth"
            if (selectedDate == date) {
                showAddDialog(date)
            } else {
                selectedDate = date
            }
        }
    }

    private fun showAddDialog(date: String) {
        val addDialogBuilder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_edit, null)
        val scheduleInput = dialogView.findViewById<EditText>(R.id.scheduleInput)

        addDialogBuilder.setView(dialogView)
        addDialogBuilder.setTitle("Add Schedule for $date")
        addDialogBuilder.setPositiveButton("Add") { dialog, _ ->
            val schedule = scheduleInput.text.toString()
            scheduleList.add("$date - $schedule")
            updateScheduleList()
            saveSchedulesToSharedPreferences()
            dialog.dismiss()
        }
        addDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        val addDialog = addDialogBuilder.create()
        addDialog.show()
    }

    private fun updateScheduleList() {
        scheduleListLayout.removeAllViews()
        scheduleList.forEach { schedule ->
            val textView = TextView(this)
            textView.text = schedule
            textView.setOnClickListener {
                showEditDialog(schedule)
            }
            scheduleListLayout.addView(textView)
        }
    }

    private fun showEditDialog(schedule: String) {
        val editDialogBuilder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_edit, null)
        val scheduleInput = dialogView.findViewById<EditText>(R.id.scheduleInput)
        scheduleInput.setText(schedule.substringAfter("-").trim())

        editDialogBuilder.setView(dialogView)
        editDialogBuilder.setTitle("Edit Schedule")
        editDialogBuilder.setPositiveButton("Save") { dialog, _ ->
            val newSchedule = scheduleInput.text.toString()
            val index = scheduleList.indexOf(schedule)
            if (index != -1) {
                scheduleList[index] = newSchedule
                updateScheduleList()
                saveSchedulesToSharedPreferences()
            }
            dialog.dismiss()
        }
        editDialogBuilder.setNegativeButton("Delete") { dialog, _ ->
            scheduleList.remove(schedule)
            updateScheduleList()
            saveSchedulesToSharedPreferences()
            dialog.dismiss()
        }
        val editDialog = editDialogBuilder.create()
        editDialog.show()
    }

    private fun saveSchedulesToSharedPreferences() {
        val sharedPreferences = getSharedPreferences("SchedulePreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val schedulesSet = mutableSetOf<String>()
        scheduleList.forEach { schedule ->
            schedulesSet.add(schedule)
        }
        editor.putStringSet("schedules", schedulesSet)
        editor.apply()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
