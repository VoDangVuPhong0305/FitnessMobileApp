package com.example.fitnessmobileapp.ui.profile

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessmobileapp.R
import java.util.Locale

class ReminderActivity : AppCompatActivity() {

    private lateinit var switchReminder: Switch
    private lateinit var txtReminderTime: TextView

    private var reminderEnabled = true
    private var reminderHour = 19
    private var reminderMinute = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        findViewById<ImageView>(R.id.btnBackReminder).setOnClickListener {
            finish()
        }

        switchReminder = findViewById(R.id.switchReminder)
        txtReminderTime = findViewById(R.id.txtReminderTime)

        loadReminder()
        updateUI()

        switchReminder.setOnCheckedChangeListener { _, isChecked ->
            reminderEnabled = isChecked
            saveReminder()
            updateUI()
        }

        findViewById<TextView>(R.id.layoutReminderTime).setOnClickListener {
            showTimePicker()
        }

        findViewById<TextView>(R.id.btnSaveReminder).setOnClickListener {
            saveReminder()
            Toast.makeText(this, "Đã lưu nhắc nhở", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun updateUI() {
        switchReminder.isChecked = reminderEnabled
        txtReminderTime.text = String.format(Locale.US, "%02d:%02d", reminderHour, reminderMinute)
    }

    private fun showTimePicker() {
        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                reminderHour = hourOfDay
                reminderMinute = minute
                updateUI()
                saveReminder()
            },
            reminderHour,
            reminderMinute,
            true
        ).show()
    }

    private fun saveReminder() {
        getSharedPreferences("reminder_data", MODE_PRIVATE)
            .edit()
            .putBoolean("enabled", reminderEnabled)
            .putInt("hour", reminderHour)
            .putInt("minute", reminderMinute)
            .apply()
    }

    private fun loadReminder() {
        val prefs = getSharedPreferences("reminder_data", MODE_PRIVATE)
        reminderEnabled = prefs.getBoolean("enabled", true)
        reminderHour = prefs.getInt("hour", 19)
        reminderMinute = prefs.getInt("minute", 30)
    }
}
