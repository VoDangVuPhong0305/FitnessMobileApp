package com.example.fitnessmobileapp.ui.profile

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.fitnessmobileapp.R
import org.json.JSONArray
import java.util.Calendar

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        private const val PREFS_NAME = "reminder_data"
        private const val KEY_ITEMS = "items"

        private const val EXTRA_REMINDER_ID = "extra_reminder_id"
        private const val BASE_REQUEST_CODE = 2000

        private const val CHANNEL_ID = "workout_reminder_channel"
    }

    data class ReminderItem(
        val id: Int,
        val hour: Int,
        val minute: Int,
        val days: MutableSet<Int>,
        val enabled: Boolean
    )

    override fun onReceive(context: Context, intent: Intent?) {
        val reminderId = intent?.getIntExtra(EXTRA_REMINDER_ID, -1) ?: -1
        val reminders = loadReminders(context)

        val reminder = if (reminderId == -1) {
            reminders.firstOrNull { it.enabled }
        } else {
            reminders.firstOrNull { it.id == reminderId && it.enabled }
        }

        if (reminder == null) return

        showNotification(context, reminder)
        scheduleNextReminder(context, reminder)
    }

    private fun showNotification(context: Context, reminder: ReminderItem) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) return
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Nhắc nhở luyện tập",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Thông báo nhắc nhở tập luyện"
            }

            notificationManager.createNotificationChannel(channel)
        }

        val openIntent = Intent(context, ReminderActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val openPendingIntent = PendingIntent.getActivity(
            context,
            BASE_REQUEST_CODE + reminder.id,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Giảm cân trong 30 ngày")
            .setContentText("Đến giờ tập luyện rồi, bắt đầu thôi!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(openPendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(reminder.id, notification)
    }

    private fun scheduleNextReminder(context: Context, reminder: ReminderItem) {
        if (!reminder.enabled) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            return
        }

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(EXTRA_REMINDER_ID, reminder.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            BASE_REQUEST_CODE + reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = getNextTriggerMillis(reminder)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    private fun getNextTriggerMillis(reminder: ReminderItem): Long {
        val now = System.currentTimeMillis()
        var bestTime: Long? = null
        val selectedDays = if (reminder.days.isEmpty()) {
            setOf(
                Calendar.SUNDAY,
                Calendar.MONDAY,
                Calendar.TUESDAY,
                Calendar.WEDNESDAY,
                Calendar.THURSDAY,
                Calendar.FRIDAY,
                Calendar.SATURDAY
            )
        } else {
            reminder.days
        }

        for (offset in 0..7) {
            val calendar = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_MONTH, offset)
                set(Calendar.HOUR_OF_DAY, reminder.hour)
                set(Calendar.MINUTE, reminder.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

            if (selectedDays.contains(dayOfWeek) && calendar.timeInMillis > now) {
                if (bestTime == null || calendar.timeInMillis < bestTime!!) {
                    bestTime = calendar.timeInMillis
                }
            }
        }

        return bestTime ?: Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, reminder.hour)
            set(Calendar.MINUTE, reminder.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun loadReminders(context: Context): List<ReminderItem> {
        val result = mutableListOf<ReminderItem>()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val rawItems = prefs.getString(KEY_ITEMS, null)

        if (!rawItems.isNullOrBlank()) {
            try {
                val array = JSONArray(rawItems)

                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val daysArray = obj.optJSONArray("days")
                    val days = mutableSetOf<Int>()

                    if (daysArray != null) {
                        for (j in 0 until daysArray.length()) {
                            days.add(daysArray.getInt(j))
                        }
                    }

                    if (days.isEmpty()) {
                        days.addAll(
                            listOf(
                                Calendar.SUNDAY,
                                Calendar.MONDAY,
                                Calendar.TUESDAY,
                                Calendar.WEDNESDAY,
                                Calendar.THURSDAY,
                                Calendar.FRIDAY,
                                Calendar.SATURDAY
                            )
                        )
                    }

                    result.add(
                        ReminderItem(
                            id = obj.optInt("id", i + 1),
                            hour = obj.optInt("hour", 20),
                            minute = obj.optInt("minute", 0),
                            days = days,
                            enabled = obj.optBoolean("enabled", true)
                        )
                    )
                }
            } catch (_: Exception) {
                result.clear()
            }
        }

        if (result.isEmpty()) {
            result.add(
                ReminderItem(
                    id = 1,
                    hour = prefs.getInt("hour", 20),
                    minute = prefs.getInt("minute", 0),
                    days = mutableSetOf(
                        Calendar.SUNDAY,
                        Calendar.MONDAY,
                        Calendar.TUESDAY,
                        Calendar.WEDNESDAY,
                        Calendar.THURSDAY,
                        Calendar.FRIDAY,
                        Calendar.SATURDAY
                    ),
                    enabled = prefs.getBoolean("enabled", true)
                )
            )
        }

        return result
    }
}