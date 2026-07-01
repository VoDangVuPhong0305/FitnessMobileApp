package com.example.fitnessmobileapp.ui.profile

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fitnessmobileapp.R
import java.util.Calendar
import java.util.Locale

class ReminderActivity : AppCompatActivity() {

    private lateinit var switchReminder: Switch
    private lateinit var txtReminderTime: TextView

    private var reminderEnabled = true
    private var reminderHour = 19
    private var reminderMinute = 30

    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 100
        private const val REMINDER_REQUEST_CODE = 200
    }

    // Hàm khởi tạo màn hình nhắc nhở và xử lý các sự kiện bấm
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

            if (isChecked) {
                requestNotificationPermission()
                scheduleReminder()
            } else {
                cancelReminder()
            }

            updateUI()
        }

        findViewById<TextView>(R.id.layoutReminderTime).setOnClickListener {
            showTimePicker()
        }

        findViewById<TextView>(R.id.btnSaveReminder).setOnClickListener {
            saveReminder()

            if (reminderEnabled) {
                requestNotificationPermission()
                scheduleReminder()
                Toast.makeText(this, "Đã lưu nhắc nhở", Toast.LENGTH_SHORT).show()
            } else {
                cancelReminder()
                Toast.makeText(this, "Đã tắt nhắc nhở", Toast.LENGTH_SHORT).show()
            }

            finish()
        }
    }

    // Hàm cập nhật giờ và trạng thái bật/tắt lên giao diện
    private fun updateUI() {
        switchReminder.isChecked = reminderEnabled
        txtReminderTime.text = String.format(
            Locale.US,
            "%02d:%02d",
            reminderHour,
            reminderMinute
        )
    }

    // Hàm mở hộp thoại chọn giờ nhắc nhở
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

    // Hàm lưu trạng thái nhắc nhở và giờ nhắc nhở
    private fun saveReminder() {
        getSharedPreferences("reminder_data", MODE_PRIVATE)
            .edit()
            .putBoolean("enabled", reminderEnabled)
            .putInt("hour", reminderHour)
            .putInt("minute", reminderMinute)
            .apply()
    }

    // Hàm đọc lại dữ liệu nhắc nhở đã lưu
    private fun loadReminder() {
        val prefs = getSharedPreferences("reminder_data", MODE_PRIVATE)

        reminderEnabled = prefs.getBoolean("enabled", true)
        reminderHour = prefs.getInt("hour", 19)
        reminderMinute = prefs.getInt("minute", 30)
    }

    // Hàm đặt thông báo chính xác theo giờ người dùng chọn
    private fun scheduleReminder() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Android 12 trở lên cần kiểm tra quyền đặt báo thức chính xác
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(
                    this,
                    "Bạn cần cấp quyền Báo thức chính xác cho app",
                    Toast.LENGTH_LONG
                ).show()

                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
                return
            }
        }

        val intent = Intent(this, ReminderReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, reminderHour)
            set(Calendar.MINUTE, reminderMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    // Hàm hủy nhắc nhở khi người dùng tắt switch
    private fun cancelReminder() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, ReminderReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    // Hàm xin quyền hiện thông báo trên Android 13 trở lên
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS

            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            }
        }
    }
}