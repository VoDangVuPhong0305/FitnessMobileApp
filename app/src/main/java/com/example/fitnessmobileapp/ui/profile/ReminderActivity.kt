package com.example.fitnessmobileapp.ui.profile

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fitnessmobileapp.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar
import java.util.Locale

class ReminderActivity : AppCompatActivity() {

    private lateinit var layoutReminderList: LinearLayout
    private lateinit var btnDeleteMode: TextView
    private lateinit var btnAddReminder: TextView

    private val reminders = mutableListOf<ReminderItem>()
    private var deleteMode = false

    private val allDays = setOf(
        Calendar.SUNDAY,
        Calendar.MONDAY,
        Calendar.TUESDAY,
        Calendar.WEDNESDAY,
        Calendar.THURSDAY,
        Calendar.FRIDAY,
        Calendar.SATURDAY
    )

    private val dayShortOptions = listOf(
        Calendar.SUNDAY to "C",
        Calendar.MONDAY to "2",
        Calendar.TUESDAY to "3",
        Calendar.WEDNESDAY to "4",
        Calendar.THURSDAY to "5",
        Calendar.FRIDAY to "6",
        Calendar.SATURDAY to "7"
    )

    private val dayFullOptions = listOf(
        Calendar.SUNDAY to "CN",
        Calendar.MONDAY to "Th2",
        Calendar.TUESDAY to "Th3",
        Calendar.WEDNESDAY to "Th4",
        Calendar.THURSDAY to "Th5",
        Calendar.FRIDAY to "Th6",
        Calendar.SATURDAY to "Th7"
    )

    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 100
        private const val BASE_REQUEST_CODE = 2000
        private const val LEGACY_REQUEST_CODE = 200

        private const val PREFS_NAME = "reminder_data"
        private const val KEY_ITEMS = "items"

        const val EXTRA_REMINDER_ID = "extra_reminder_id"
    }

    data class ReminderItem(
        val id: Int,
        var hour: Int,
        var minute: Int,
        val days: MutableSet<Int>,
        var enabled: Boolean
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        findViewById<ImageView>(R.id.btnBackReminder).setOnClickListener {
            finish()
        }

        layoutReminderList = findViewById(R.id.layoutReminderList)
        btnDeleteMode = findViewById(R.id.btnDeleteMode)
        btnAddReminder = findViewById(R.id.btnAddReminder)

        btnAddReminder.background = roundedBackground("#7FE082", 100f)
        btnDeleteMode.setOnClickListener {
            if (reminders.isEmpty()) return@setOnClickListener
            deleteMode = !deleteMode
            renderReminders()
        }

        btnAddReminder.setOnClickListener {
            showReminderDialog(null)
        }

        cancelLegacyReminderAlarm()
        loadReminders()
        renderReminders()
    }

    private fun renderReminders() {
        layoutReminderList.removeAllViews()

        btnDeleteMode.visibility = if (reminders.isEmpty()) View.INVISIBLE else View.VISIBLE
        btnDeleteMode.text = if (deleteMode) "HỦY" else "🗑"
        btnDeleteMode.textSize = if (deleteMode) 16f else 24f
        btnDeleteMode.setTextColor(if (deleteMode) Color.parseColor("#9CA3AF") else Color.parseColor("#EC5A70"))

        reminders
            .sortedWith(compareBy<ReminderItem> { it.hour }.thenBy { it.minute })
            .forEach { reminder ->
                layoutReminderList.addView(createReminderRow(reminder))
            }

        if (reminders.isEmpty()) {
            layoutReminderList.addView(createEmptyView())
        }
    }

    private fun createReminderRow(reminder: ReminderItem): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(16)
            }
        }

        if (deleteMode) {
            val deleteButton = TextView(this).apply {
                text = "🗑"
                textSize = 28f
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
                background = roundedBackground("#EC5A70", 18f)
                layoutParams = LinearLayout.LayoutParams(dp(88), dp(112)).apply {
                    rightMargin = dp(12)
                }
                setOnClickListener {
                    deleteReminder(reminder)
                }
            }
            row.addView(deleteButton)
        }

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(22), dp(18), dp(18), dp(18))
            background = roundedBackground("#FFFFFF", 22f)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                elevation = dp(2).toFloat()
            }
            layoutParams = LinearLayout.LayoutParams(0, dp(112), 1f)
            setOnClickListener {
                showReminderDialog(reminder)
            }
        }

        val infoLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
        }

        val txtTime = TextView(this).apply {
            text = String.format(Locale.US, "%02d:%02d", reminder.hour, reminder.minute)
            textSize = 32f
            setTextColor(Color.BLACK)
            typeface = Typeface.DEFAULT_BOLD
        }

        val txtDays = TextView(this).apply {
            text = daysToText(reminder.days)
            textSize = 17f
            setTextColor(Color.parseColor("#56B982"))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(6)
            }
        }

        val switchReminder = Switch(this).apply {
            isChecked = reminder.enabled
            setOnCheckedChangeListener { _, isChecked ->
                reminder.enabled = isChecked
                saveReminders()

                if (isChecked) {
                    requestNotificationPermission()
                    scheduleReminder(reminder)
                } else {
                    cancelReminder(reminder.id)
                }

                Toast.makeText(
                    this@ReminderActivity,
                    if (isChecked) "Đã bật nhắc nhở" else "Đã tắt nhắc nhở",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        infoLayout.addView(txtTime)
        infoLayout.addView(txtDays)

        card.addView(infoLayout)
        card.addView(switchReminder)
        row.addView(card)

        return row
    }

    private fun createEmptyView(): View {
        return TextView(this).apply {
            text = "Chưa có nhắc nhở nào.\nBấm dấu + để thêm nhắc nhở mới."
            textSize = 17f
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#9CA3AF"))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(180)
            )
        }
    }

    private fun showReminderDialog(editingReminder: ReminderItem?) {
        val dialog = BottomSheetDialog(this)

        var selectedHour = editingReminder?.hour ?: 20
        var selectedMinute = editingReminder?.minute ?: 0
        val selectedDays = editingReminder?.days?.toMutableSet() ?: allDays.toMutableSet()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(28), dp(24), dp(28), dp(28))
            background = roundedBackground("#FFFFFF", 28f)
        }

        val btnClose = TextView(this).apply {
            text = "×"
            textSize = 42f
            gravity = Gravity.END
            setTextColor(Color.parseColor("#A3A3A3"))
            setOnClickListener { dialog.dismiss() }
        }

        val title = TextView(this).apply {
            text = "Hãy đặt lịch nhắc nhở"
            textSize = 28f
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.BLACK)
        }

        val subtitle = TextView(this).apply {
            text = "Hãy phấn chấn lên, nhắc bạn phải tập hằng ngày."
            textSize = 20f
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#A0A7B5"))
            setPadding(0, dp(10), 0, dp(22))
        }

        val pickerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, dp(28))
        }

        val hourPicker = NumberPicker(this).apply {
            minValue = 0
            maxValue = 23
            value = selectedHour
            setFormatter { value -> String.format(Locale.US, "%02d", value) }
            setOnValueChangedListener { _, _, newValue ->
                selectedHour = newValue
            }
        }

        val minutePicker = NumberPicker(this).apply {
            minValue = 0
            maxValue = 59
            value = selectedMinute
            setFormatter { value -> String.format(Locale.US, "%02d", value) }
            setOnValueChangedListener { _, _, newValue ->
                selectedMinute = newValue
            }
        }

        val colon = TextView(this).apply {
            text = ":"
            textSize = 42f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
            setPadding(dp(14), 0, dp(14), 0)
        }

        pickerLayout.addView(hourPicker)
        pickerLayout.addView(colon)
        pickerLayout.addView(minutePicker)

        val repeatTitle = TextView(this).apply {
            text = "Lặp lại"
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#777777"))
            setPadding(0, dp(8), 0, dp(14))
        }

        val dayLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val dayViews = mutableMapOf<Int, TextView>()

        fun refreshDayViews() {
            dayViews.forEach { (day, view) ->
                val selected = selectedDays.contains(day)
                view.background = roundedBackground(
                    if (selected) "#7FE082" else "#EEF1F4",
                    100f
                )
                view.setTextColor(Color.BLACK)
            }
        }

        dayShortOptions.forEach { (day, label) ->
            val dayView = TextView(this).apply {
                text = label
                textSize = 18f
                gravity = Gravity.CENTER
                typeface = Typeface.DEFAULT_BOLD
                layoutParams = LinearLayout.LayoutParams(0, dp(54), 1f).apply {
                    leftMargin = dp(4)
                    rightMargin = dp(4)
                }
                setOnClickListener {
                    if (selectedDays.contains(day)) {
                        selectedDays.remove(day)
                    } else {
                        selectedDays.add(day)
                    }
                    refreshDayViews()
                }
            }
            dayViews[day] = dayView
            dayLayout.addView(dayView)
        }

        refreshDayViews()

        val btnDone = TextView(this).apply {
            text = "XONG"
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
            background = roundedBackground("#7FE082", 40f)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(64)
            ).apply {
                topMargin = dp(34)
            }

            setOnClickListener {
                if (selectedDays.isEmpty()) {
                    Toast.makeText(
                        this@ReminderActivity,
                        "Bạn cần chọn ít nhất 1 ngày lặp lại",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val reminder = if (editingReminder == null) {
                    ReminderItem(
                        id = nextReminderId(),
                        hour = selectedHour,
                        minute = selectedMinute,
                        days = selectedDays,
                        enabled = true
                    ).also {
                        reminders.add(it)
                    }
                } else {
                    editingReminder.hour = selectedHour
                    editingReminder.minute = selectedMinute
                    editingReminder.days.clear()
                    editingReminder.days.addAll(selectedDays)
                    editingReminder
                }

                saveReminders()
                cancelReminder(reminder.id)

                if (reminder.enabled) {
                    requestNotificationPermission()
                    scheduleReminder(reminder)
                }

                renderReminders()
                dialog.dismiss()
                Toast.makeText(this@ReminderActivity, "Đã lưu nhắc nhở", Toast.LENGTH_SHORT).show()
            }
        }

        root.addView(btnClose)
        root.addView(title)
        root.addView(subtitle)
        root.addView(pickerLayout)
        root.addView(repeatTitle)
        root.addView(dayLayout)
        root.addView(btnDone)

        dialog.setContentView(root)
        dialog.show()
    }

    private fun nextReminderId(): Int {
        return (reminders.maxOfOrNull { it.id } ?: 0) + 1
    }

    private fun deleteReminder(reminder: ReminderItem) {
        cancelReminder(reminder.id)
        reminders.removeAll { it.id == reminder.id }
        saveReminders()

        if (reminders.isEmpty()) {
            deleteMode = false
        }

        renderReminders()
        Toast.makeText(this, "Đã xoá nhắc nhở", Toast.LENGTH_SHORT).show()
    }

    private fun loadReminders() {
        reminders.clear()

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
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
                        days.addAll(allDays)
                    }

                    reminders.add(
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
                reminders.clear()
            }
        }

        if (reminders.isEmpty()) {
            val oldHour = prefs.getInt("hour", 20)
            val oldMinute = prefs.getInt("minute", 0)
            val oldEnabled = prefs.getBoolean("enabled", true)

            reminders.add(
                ReminderItem(
                    id = 1,
                    hour = oldHour,
                    minute = oldMinute,
                    days = allDays.toMutableSet(),
                    enabled = oldEnabled
                )
            )
        }

        saveReminders()
    }

    private fun saveReminders() {
        val array = JSONArray()

        reminders.forEach { reminder ->
            val daysArray = JSONArray()
            reminder.days.sorted().forEach { day ->
                daysArray.put(day)
            }

            val obj = JSONObject()
            obj.put("id", reminder.id)
            obj.put("hour", reminder.hour)
            obj.put("minute", reminder.minute)
            obj.put("enabled", reminder.enabled)
            obj.put("days", daysArray)

            array.put(obj)
        }

        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putString(KEY_ITEMS, array.toString())
            .apply()
    }

    private fun scheduleReminder(reminder: ReminderItem) {
        if (!reminder.enabled) return

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
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

        val intent = Intent(this, ReminderReceiver::class.java).apply {
            putExtra(EXTRA_REMINDER_ID, reminder.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
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

    private fun cancelReminder(reminderId: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            BASE_REQUEST_CODE + reminderId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    private fun cancelLegacyReminderAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            LEGACY_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    private fun getNextTriggerMillis(reminder: ReminderItem): Long {
        val now = System.currentTimeMillis()
        var bestTime: Long? = null
        val selectedDays = if (reminder.days.isEmpty()) allDays else reminder.days

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

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS

            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            }
        }
    }

    private fun daysToText(days: Set<Int>): String {
        return dayFullOptions
            .filter { days.contains(it.first) }
            .joinToString(", ") { it.second }
    }

    private fun roundedBackground(color: String, radius: Float): GradientDrawable {
        return GradientDrawable().apply {
            setColor(Color.parseColor(color))
            cornerRadius = dp(radius.toInt()).toFloat()
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}