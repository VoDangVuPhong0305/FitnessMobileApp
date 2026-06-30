package com.example.fitnessmobileapp.ui.profile

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.fitnessmobileapp.R
import java.util.Calendar

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        private const val REMINDER_REQUEST_CODE = 200
    }

    // Hàm được gọi tự động khi đến giờ nhắc nhở
    override fun onReceive(context: Context, intent: Intent?) {
        showNotification(context)
        scheduleNextReminder(context)
    }

    // Hàm tạo và hiển thị thông báo nhắc nhở tập luyện
    private fun showNotification(context: Context) {
        val channelId = "workout_reminder_channel"
        val notificationId = 1

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Nhắc nhở luyện tập",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Thông báo nhắc nhở tập luyện mỗi ngày"
            }

            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Giảm cân trong 30 ngày")
            .setContentText("Thời gian tập luyện, bắt đầu thôi!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    // Hàm tự đặt lại nhắc nhở cho ngày tiếp theo
    private fun scheduleNextReminder(context: Context) {
        val prefs = context.getSharedPreferences("reminder_data", Context.MODE_PRIVATE)

        val enabled = prefs.getBoolean("enabled", true)
        val hour = prefs.getInt("hour", 19)
        val minute = prefs.getInt("minute", 30)

        if (!enabled) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Android 12 trở lên cần kiểm tra quyền đặt báo thức chính xác
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                return
            }
        }

        val intent = Intent(context, ReminderReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
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
}