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
import android.view.MotionEvent
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fitnessmobileapp.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar
import java.util.Locale
import android.graphics.Paint
import android.widget.EditText
import android.widget.FrameLayout
import android.graphics.drawable.ColorDrawable

class ReminderActivity : AppCompatActivity() {

    private lateinit var layoutReminderList: LinearLayout
    private lateinit var btnDeleteMode: TextView
    private lateinit var btnAddReminder: View

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

        updateDeleteModeButton()
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
        updateDeleteModeButton()

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
            val deleteButton = ImageView(this).apply {
                setImageResource(R.drawable.ic_trash_clean)
                setColorFilter(Color.WHITE)
                background = roundedBackground("#EC5A70", 18f)
                scaleType = ImageView.ScaleType.CENTER
                setPadding(dp(26), dp(26), dp(26), dp(26))
                contentDescription = "Xóa nhắc nhở"

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

    // Chức năng: hiện bottom sheet để thêm hoặc chỉnh sửa nhắc nhở.
    private fun showReminderDialog(editingReminder: ReminderItem?) {
        val dialog = BottomSheetDialog(this)

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        var selectedHour = editingReminder?.hour ?: 20
        var selectedMinute = editingReminder?.minute ?: 0
        val selectedDays = editingReminder?.days?.toMutableSet() ?: allDays.toMutableSet()

        // Root container dùng FrameLayout để dễ dàng đè nút X lên góc
        val rootFrame = FrameLayout(this).apply {
            background = roundedBackground("#FFFFFF", 32f)
        }

        val contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(28), dp(52), dp(28), dp(32))
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val btnClose = TextView(this).apply {
            text = "✕"
            textSize = 22f
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#888888"))
            includeFontPadding = false
            typeface = Typeface.DEFAULT_BOLD

            layoutParams = FrameLayout.LayoutParams(dp(54), dp(54)).apply {
                gravity = Gravity.END or Gravity.TOP
                topMargin = dp(14)
                rightMargin = dp(14)
            }

            setOnClickListener {
                dialog.dismiss()
            }
        }

        val title = TextView(this).apply {
            text = "Hãy đặt lịch nhắc nhở"
            textSize = 28f
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.BLACK)
            includeFontPadding = false

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val subtitle = TextView(this).apply {
            text = "Hãy phấn chấn lên, nhắc bạn phải tập hằng ngày."
            textSize = 17f
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#9CA3AF"))
            includeFontPadding = false
            setLineSpacing(dp(2).toFloat(), 1.1f)

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(14)
            }
        }

        // Chức năng: khu vực chọn giờ phút giống app mẫu.
        // Không dùng NumberPicker để không có lằn ngang và không bị bấm mới phóng to.
        val pickerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(190)
            ).apply {
                topMargin = dp(34)
                bottomMargin = dp(20)
            }
        }

        val hourWheel = createTimeWheelColumn(
            currentValue = selectedHour,
            maxValue = 23,
            alignToEnd = true
        ) { newValue ->
            selectedHour = newValue
        }

        val minuteWheel = createTimeWheelColumn(
            currentValue = selectedMinute,
            maxValue = 59,
            alignToEnd = false
        ) { newValue ->
            selectedMinute = newValue
        }

        val colon = TextView(this).apply {
            text = ":"
            textSize = 42f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#222222"))
            gravity = Gravity.CENTER
            includeFontPadding = false

            layoutParams = LinearLayout.LayoutParams(
                dp(28),
                dp(174)
            )
        }

        pickerLayout.addView(hourWheel)
        pickerLayout.addView(colon)
        pickerLayout.addView(minuteWheel)

        val repeatTitle = TextView(this).apply {
            text = "Lặp lại"
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#777777"))
            includeFontPadding = false

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(16)
                bottomMargin = dp(18)
            }
        }

        val dayLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(52)
            )
        }

        val dayViews = mutableMapOf<Int, TextView>()

        fun refreshDayViews() {
            dayViews.forEach { (day, view) ->
                val selected = selectedDays.contains(day)
                view.background = roundedBackground(if (selected) "#70E181" else "#EEF1F4", 100f)
                view.setTextColor(Color.BLACK)
            }
        }

        dayShortOptions.forEach { (day, label) ->
            val dayView = TextView(this).apply {
                text = label
                textSize = 17f
                gravity = Gravity.CENTER
                typeface = Typeface.DEFAULT_BOLD
                includeFontPadding = false

                layoutParams = LinearLayout.LayoutParams(dp(42), dp(42)).apply {
                    leftMargin = dp(4)
                    rightMargin = dp(4)
                }

                setOnClickListener {
                    if (selectedDays.contains(day)) selectedDays.remove(day)
                    else selectedDays.add(day)
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
            includeFontPadding = false
            background = roundedBackground("#70E181", 40f)

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(68)
            ).apply {
                topMargin = dp(42)
            }

            setOnClickListener {
                if (selectedDays.isEmpty()) {
                    Toast.makeText(
                        this@ReminderActivity,
                        "Bạn cần chọn ít nhất 1 ngày",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val reminder = if (editingReminder == null) {
                    ReminderItem(
                        id = nextReminderId(),
                        hour = selectedHour,
                        minute = selectedMinute,
                        days = selectedDays.toMutableSet(),
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

                // Chức năng: hủy lịch cũ rồi đặt lại lịch mới.
                cancelReminder(reminder.id)

                if (reminder.enabled) {
                    requestNotificationPermission()
                    scheduleReminder(reminder)
                }

                renderReminders()
                dialog.dismiss()

                Toast.makeText(
                    this@ReminderActivity,
                    "Đã lưu và đặt lịch nhắc nhở",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        contentLayout.addView(title)
        contentLayout.addView(subtitle)
        contentLayout.addView(pickerLayout)
        contentLayout.addView(repeatTitle)
        contentLayout.addView(dayLayout)
        contentLayout.addView(btnDone)

        rootFrame.addView(contentLayout)
        rootFrame.addView(btnClose)

        dialog.setContentView(rootFrame)
        dialog.setOnShowListener {
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val bottomSheet = dialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            )

            bottomSheet?.background = null

            if (bottomSheet != null) {
                val behavior = BottomSheetBehavior.from(bottomSheet)

                // Chức năng: không cho kéo cả popup xuống để đóng.
                behavior.isDraggable = false
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
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

    private fun sp(value: Float): Float {
        return value * resources.displayMetrics.scaledDensity
    }

    // Chức năng: cập nhật giao diện nút xóa ở góc phải.
    // Bình thường chỉ hiện icon thùng rác bằng background.
    // Khi vào chế độ xóa thì bỏ icon và chỉ hiện chữ HỦY.
    private fun updateDeleteModeButton() {
        if (deleteMode) {
            btnDeleteMode.text = "HỦY"
            btnDeleteMode.textSize = 16f
            btnDeleteMode.setTextColor(Color.parseColor("#9CA3AF"))
            btnDeleteMode.background = null
            btnDeleteMode.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            btnDeleteMode.contentDescription = "Hủy xóa nhắc nhở"
        } else {
            btnDeleteMode.text = ""
            btnDeleteMode.textSize = 16f
            btnDeleteMode.setTextColor(Color.parseColor("#9CA3AF"))
            btnDeleteMode.setBackgroundResource(R.drawable.ic_trash_clean)
            btnDeleteMode.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            btnDeleteMode.contentDescription = "Xóa nhắc nhở"
        }
    }

    // Chức năng: tạo cột chọn giờ/phút giống app mẫu.
    // Vùng chạm rộng hơn để kéo dễ hơn, nhưng số vẫn nằm gần dấu ":".
    private fun createTimeWheelColumn(
        currentValue: Int,
        maxValue: Int,
        alignToEnd: Boolean,
        onValueChanged: (Int) -> Unit
    ): FrameLayout {
        var value = currentValue.coerceIn(0, maxValue)
        var lastY = 0f
        var dragDistance = 0f

        fun formatNumber(number: Int): String {
            return String.format(Locale.US, "%02d", number)
        }

        fun previousValue(number: Int): Int {
            return if (number - 1 < 0) maxValue else number - 1
        }

        fun nextValue(number: Int): Int {
            return if (number + 1 > maxValue) 0 else number + 1
        }

        val topText = TextView(this)
        val centerText = TextView(this)
        val bottomText = TextView(this)

        fun setupText(
            textView: TextView,
            textSizeValue: Float,
            textColorValue: String,
            heightValue: Int
        ) {
            textView.gravity = Gravity.CENTER
            textView.includeFontPadding = false
            textView.textSize = textSizeValue
            textView.typeface = Typeface.DEFAULT_BOLD
            textView.setTextColor(Color.parseColor(textColorValue))
            textView.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(heightValue)
            )
        }

        setupText(topText, 34f, "#AEB5C2", 54)
        setupText(centerText, 44f, "#222222", 66)
        setupText(bottomText, 34f, "#AEB5C2", 54)

        fun refreshWheel() {
            topText.text = formatNumber(previousValue(value))
            centerText.text = formatNumber(value)
            bottomText.text = formatNumber(nextValue(value))
            onValueChanged(value)
        }

        fun increaseValue() {
            value = nextValue(value)
            refreshWheel()
        }

        fun decreaseValue() {
            value = previousValue(value)
            refreshWheel()
        }

        val touchListener = View.OnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.parent?.requestDisallowInterceptTouchEvent(true)
                    lastY = event.y
                    dragDistance = 0f
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    view.parent?.requestDisallowInterceptTouchEvent(true)

                    val diffY = event.y - lastY
                    dragDistance += diffY
                    lastY = event.y

                    while (dragDistance >= dp(18)) {
                        decreaseValue()
                        dragDistance -= dp(18)
                    }

                    while (dragDistance <= -dp(18)) {
                        increaseValue()
                        dragDistance += dp(18)
                    }

                    true
                }

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    view.parent?.requestDisallowInterceptTouchEvent(false)
                    dragDistance = 0f
                    true
                }

                else -> true
            }
        }

        val numberColumn = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER

            layoutParams = FrameLayout.LayoutParams(
                dp(88),
                dp(174),
                if (alignToEnd) Gravity.END or Gravity.CENTER_VERTICAL
                else Gravity.START or Gravity.CENTER_VERTICAL
            )

            addView(topText)
            addView(centerText)
            addView(bottomText)

            setOnTouchListener(touchListener)
        }

        topText.setOnTouchListener(touchListener)
        centerText.setOnTouchListener(touchListener)
        bottomText.setOnTouchListener(touchListener)

        val touchArea = FrameLayout(this).apply {
            // Chức năng: vùng chạm rộng hơn, kéo mé ngoài vẫn ăn.
            layoutParams = LinearLayout.LayoutParams(
                dp(132),
                dp(190)
            )

            setOnTouchListener(touchListener)
            addView(numberColumn)
        }

        refreshWheel()
        return touchArea
    }
}