package com.example.fitnessmobileapp.ui.report

import android.app.AlertDialog
import android.widget.ScrollView
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.example.fitnessmobileapp.R
import com.example.fitnessmobileapp.data.repository.WorkoutReportManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.LinearLayout
import com.example.fitnessmobileapp.data.repository.WorkoutReportRecord
import android.annotation.SuppressLint
import android.view.MotionEvent
import android.widget.GridLayout
import android.os.Handler
import android.os.Looper


class ReportFragment : Fragment() {

    private lateinit var scrollReport: NestedScrollView
    private lateinit var cardCalendar: View
    private lateinit var cardWeight: View

    private lateinit var tabCalendar: View
    private lateinit var tabWeight: View
    private lateinit var txtTabCalendar: TextView
    private lateinit var txtTabWeight: TextView
    private lateinit var lineCalendar: View
    private lateinit var lineWeight: View

    private lateinit var txtTotalDays: TextView
    private lateinit var txtTotalCalories: TextView
    private lateinit var txtTotalMinutes: TextView

    private lateinit var txtCalendarMonth: TextView
    private lateinit var gridReportCalendar: GridLayout
    private lateinit var txtSelectedDate: TextView
    private lateinit var layoutWorkoutHistory: LinearLayout
    private lateinit var scrollWorkoutHistory: ScrollView

    private lateinit var btnEditWeight: TextView
    private lateinit var btnAddWeight: TextView
    private lateinit var txtCurrentWeight: TextView
    private lateinit var txtLast30Days: TextView
    private lateinit var txtAverageWeight: TextView
    private lateinit var layoutWeightLineChart: FrameLayout

    private lateinit var txtBMIValue: TextView
    private lateinit var txtBMIStatus: TextView
    private lateinit var txtBMIMarker: TextView
    private lateinit var bmiScaleContainer: View

    private lateinit var txtCaloriesToday: TextView

    private lateinit var barCal1: View
    private lateinit var barCal2: View
    private lateinit var barCal3: View
    private lateinit var barCal4: View
    private lateinit var barCal5: View
    private lateinit var barCal6: View
    private lateinit var barCal7: View

    private lateinit var txtCalValue1: TextView
    private lateinit var txtCalValue2: TextView
    private lateinit var txtCalValue3: TextView
    private lateinit var txtCalValue4: TextView
    private lateinit var txtCalValue5: TextView
    private lateinit var txtCalValue6: TextView
    private lateinit var txtCalValue7: TextView

    private lateinit var txtCalDay1: TextView
    private lateinit var txtCalDay2: TextView
    private lateinit var txtCalDay3: TextView
    private lateinit var txtCalDay4: TextView
    private lateinit var txtCalDay5: TextView
    private lateinit var txtCalDay6: TextView
    private lateinit var txtCalDay7: TextView

    private lateinit var weightLineChartView: WeightLineChartView

    private var heightM = 1.65
    private val reportCalendar = Calendar.getInstance()
    private val selectedCalendar = Calendar.getInstance()
    private val reportDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val weightList = mutableListOf<WeightRecord>()

    data class WeightRecord(
        val date: String,
        val weight: Double
    )

    // Chức năng: lấy tên tài khoản hiện tại để lưu cân nặng riêng cho từng người dùng.
    private fun getCurrentUsername(): String {
        val loginPrefs = requireContext().getSharedPreferences("login_data", Context.MODE_PRIVATE)
        return loginPrefs.getString("current_user", "guest") ?: "guest"
    }

    // Chức năng: lấy SharedPreferences hồ sơ người dùng đã tạo ở Onboarding.
    private fun getProfilePrefs() =
        requireContext().getSharedPreferences("user_${getCurrentUsername()}_profile", Context.MODE_PRIVATE)

    // Chức năng: lấy SharedPreferences riêng để lưu lịch sử cân nặng.
    private fun getWeightPrefs() =
        requireContext().getSharedPreferences("user_${getCurrentUsername()}_weight_report", Context.MODE_PRIVATE)

    // Chức năng: đọc số Float trong hồ sơ, tránh lỗi nếu dữ liệu cũ từng lưu dạng Int.
    private fun getProfileFloat(
        key: String,
        defaultValue: Float
    ): Float {
        val prefs = getProfilePrefs()

        return try {
            prefs.getFloat(key, defaultValue)
        } catch (e: ClassCastException) {
            prefs.getInt(key, defaultValue.toInt()).toFloat()
        }
    }

    // Chức năng: load chiều cao và lịch sử cân nặng đã lưu.
// Nếu chưa có lịch sử cân nặng thì lấy cân nặng ban đầu từ Onboarding.
    private fun loadWeightData() {
        val heightCm = getProfileFloat("height", 165f)
        heightM = (heightCm / 100.0).coerceIn(1.0, 2.3)

        val prefs = getWeightPrefs()
        val savedRecords = prefs.getString("records", "") ?: ""

        weightList.clear()

        if (savedRecords.isNotBlank()) {
            savedRecords.split(";").forEach { item ->
                val parts = item.split("|")

                if (parts.size == 2) {
                    val date = parts[0]
                    val weight = parts[1].toDoubleOrNull()

                    if (weight != null && weight > 0) {
                        weightList.add(
                            WeightRecord(
                                date = date,
                                weight = weight
                            )
                        )
                    }
                }
            }
        }

        if (weightList.isEmpty()) {
            val currentWeight = getProfileFloat("currentWeight", 70f).toDouble()
            val today = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date())

            weightList.add(
                WeightRecord(
                    date = today,
                    weight = currentWeight
                )
            )

            saveWeightData()
        }
    }

    // Chức năng: lưu lịch sử cân nặng vào SharedPreferences.
    private fun saveWeightData() {
        val data = weightList.joinToString(";") { record ->
            "${record.date}|${record.weight}"
        }

        getWeightPrefs()
            .edit()
            .putString("records", data)
            .apply()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_report, container, false)

        initViews(view)
        setupWorkoutHistoryScroll()
        setupEvents()
        loadReportData()
        selectCalendarTab()

        return view
    }

    override fun onResume() {
        super.onResume()

        if (::txtTotalDays.isInitialized) {
            loadReportData()
        }
    }

    private fun initViews(view: View) {
        scrollReport = view.findViewById(R.id.scrollReport)
        cardCalendar = view.findViewById(R.id.cardCalendar)
        cardWeight = view.findViewById(R.id.cardWeight)

        tabCalendar = view.findViewById(R.id.tabCalendar)
        tabWeight = view.findViewById(R.id.tabWeight)
        txtTabCalendar = view.findViewById(R.id.txtTabCalendar)
        txtTabWeight = view.findViewById(R.id.txtTabWeight)
        lineCalendar = view.findViewById(R.id.lineCalendar)
        lineWeight = view.findViewById(R.id.lineWeight)

        txtTotalDays = view.findViewById(R.id.txtTotalDays)
        txtTotalCalories = view.findViewById(R.id.txtTotalCalories)
        txtTotalMinutes = view.findViewById(R.id.txtTotalMinutes)

        txtCalendarMonth = view.findViewById(R.id.txtCalendarMonth)
        gridReportCalendar = view.findViewById(R.id.gridReportCalendar)
        txtSelectedDate = view.findViewById(R.id.txtSelectedDate)
        layoutWorkoutHistory = view.findViewById(R.id.layoutWorkoutHistory)
        scrollWorkoutHistory = view.findViewById(R.id.scrollWorkoutHistory)

        btnEditWeight = view.findViewById(R.id.btnEditWeight)
        btnAddWeight = view.findViewById(R.id.btnAddWeight)
        txtCurrentWeight = view.findViewById(R.id.txtCurrentWeight)
        txtLast30Days = view.findViewById(R.id.txtLast30Days)
        txtAverageWeight = view.findViewById(R.id.txtAverageWeight)
        layoutWeightLineChart = view.findViewById(R.id.layoutWeightLineChart)

        txtBMIValue = view.findViewById(R.id.txtBMIValue)
        txtBMIStatus = view.findViewById(R.id.txtBMIStatus)
        txtBMIMarker = view.findViewById(R.id.txtBMIMarker)
        bmiScaleContainer = view.findViewById(R.id.bmiScaleContainer)

        txtCaloriesToday = view.findViewById(R.id.txtCaloriesToday)

        barCal1 = view.findViewById(R.id.barCal1)
        barCal2 = view.findViewById(R.id.barCal2)
        barCal3 = view.findViewById(R.id.barCal3)
        barCal4 = view.findViewById(R.id.barCal4)
        barCal5 = view.findViewById(R.id.barCal5)
        barCal6 = view.findViewById(R.id.barCal6)
        barCal7 = view.findViewById(R.id.barCal7)

        txtCalValue1 = view.findViewById(R.id.txtCalValue1)
        txtCalValue2 = view.findViewById(R.id.txtCalValue2)
        txtCalValue3 = view.findViewById(R.id.txtCalValue3)
        txtCalValue4 = view.findViewById(R.id.txtCalValue4)
        txtCalValue5 = view.findViewById(R.id.txtCalValue5)
        txtCalValue6 = view.findViewById(R.id.txtCalValue6)
        txtCalValue7 = view.findViewById(R.id.txtCalValue7)

        txtCalDay1 = view.findViewById(R.id.txtCalDay1)
        txtCalDay2 = view.findViewById(R.id.txtCalDay2)
        txtCalDay3 = view.findViewById(R.id.txtCalDay3)
        txtCalDay4 = view.findViewById(R.id.txtCalDay4)
        txtCalDay5 = view.findViewById(R.id.txtCalDay5)
        txtCalDay6 = view.findViewById(R.id.txtCalDay6)
        txtCalDay7 = view.findViewById(R.id.txtCalDay7)

        weightLineChartView = WeightLineChartView(requireContext())
        layoutWeightLineChart.removeAllViews()
        layoutWeightLineChart.addView(
            weightLineChartView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
    }

    // Chức năng: cho vùng Lịch sử tập luyện tự cuộn bên trong,
    // không để ScrollView lớn bên ngoài giành quyền kéo.
    @SuppressLint("ClickableViewAccessibility")
    private fun setupWorkoutHistoryScroll() {
        scrollWorkoutHistory.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_MOVE -> {
                    view.parent.requestDisallowInterceptTouchEvent(true)
                }

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    view.parent.requestDisallowInterceptTouchEvent(false)
                }
            }

            false
        }
    }

    private fun setupEvents() {
        tabCalendar.setOnClickListener {
            selectCalendarTab()
            scrollToView(cardCalendar)
        }

        tabWeight.setOnClickListener {
            selectWeightTab()
            scrollToView(cardWeight)
        }

        btnEditWeight.setOnClickListener {
            showEditBodyDialog()
        }

        btnAddWeight.setOnClickListener {
            showAddWeightOnlyDialog()
        }
    }

    private fun scrollToView(targetView: View) {
        scrollReport.post {
            scrollReport.smoothScrollTo(0, targetView.top - 12)
        }
    }

    private fun selectCalendarTab() {
        txtTabCalendar.setTextColor(0xFF111111.toInt())
        txtTabWeight.setTextColor(0xFF999999.toInt())
        lineCalendar.setBackgroundColor(0xFF111111.toInt())
        lineWeight.setBackgroundColor(0x00FFFFFF)
    }

    private fun selectWeightTab() {
        txtTabCalendar.setTextColor(0xFF999999.toInt())
        txtTabWeight.setTextColor(0xFF111111.toInt())
        lineCalendar.setBackgroundColor(0x00FFFFFF)
        lineWeight.setBackgroundColor(0xFF111111.toInt())
    }

    private fun loadReportData() {
        showSummaryData()
        showTodayHistory()
        loadWeightData()
        updateWeightInfo()
        updateWeightChart()
        updateCaloriesChart()
    }

    private fun showSummaryData() {
        val summary = WorkoutReportManager.getTotalSummary(requireContext())

        txtTotalDays.text = summary.totalWorkouts.toString()
        txtTotalCalories.text = summary.totalCalories.toString()
        txtTotalMinutes.text = secondsToMinutes(summary.totalDurationSeconds).toString()
    }

    // Chức năng: khi mở Báo cáo, tự hiển thị lịch sử của ngày có buổi tập gần nhất.
// Nếu chưa có buổi tập nào thì mới hiển thị ngày hôm nay.
    private fun showTodayHistory() {
        val allRecords = WorkoutReportManager.getAllRecords(requireContext())

        if (allRecords.isNotEmpty()) {
            val latestRecord = allRecords.maxByOrNull { record ->
                record.completedAtMillis
            }

            val latestDate = latestRecord?.date ?: reportDateFormat.format(Date())

            val parsedDate = try {
                reportDateFormat.parse(latestDate)
            } catch (e: Exception) {
                Date()
            } ?: Date()

            selectedCalendar.time = parsedDate
            reportCalendar.time = parsedDate

            txtSelectedDate.text = formatDateForDisplay(latestDate)

            showWorkoutHistory(latestDate)
            renderWorkoutCalendar()
        } else {
            selectedCalendar.time = Date()
            reportCalendar.time = Date()

            val todayQuery = reportDateFormat.format(Date())
            txtSelectedDate.text = formatDateForDisplay(todayQuery)

            showWorkoutHistory(todayQuery)
            renderWorkoutCalendar()
        }
    }

    // Chức năng: tự vẽ lịch tháng bằng GridLayout.
// Ngày nào có dữ liệu tập luyện thì tô xanh lá.
    private fun renderWorkoutCalendar() {
        gridReportCalendar.removeAllViews()

        val month = reportCalendar.get(Calendar.MONTH)
        val year = reportCalendar.get(Calendar.YEAR)

        txtCalendarMonth.text = "thg ${month + 1} $year"
        txtCalendarMonth.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
        txtCalendarMonth.includeFontPadding = false

        val completedDates = WorkoutReportManager.getAllRecords(requireContext())
            .map { record -> record.date }
            .toSet()

        addCalendarWeekHeader()

        val firstDayOfMonth = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
        }

        val daysInMonth = firstDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)

        val firstDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK)

        // Tính ô trống đầu hàng
        val emptyDaysBeforeMonth = firstDayOfWeek - 1

        repeat(emptyDaysBeforeMonth) {
            addEmptyCalendarCell()
        }

        for (day in 1..daysInMonth) {
            val dateString = String.format(
                Locale.getDefault(),
                "%04d-%02d-%02d",
                year,
                month + 1,
                day
            )

            addCalendarDayCell(
                day = day,
                dateString = dateString,
                hasWorkout = completedDates.contains(dateString)
            )
        }
    }

    // Chức năng: thêm hàng tiêu đề thứ trong tuần, chữ nhỏ gọn giống app mẫu.
    private fun addCalendarWeekHeader() {
        val weekDays = listOf("CN", "Th2", "Th3", "Th4", "Th5", "Th6", "Th7")

        weekDays.forEach { dayName ->
            val textView = TextView(requireContext()).apply {
                text = dayName
                gravity = Gravity.CENTER
                setTextColor(Color.parseColor("#8F8F8F"))
                textSize = 14f
                typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                includeFontPadding = false

                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = dp(28)
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                }
            }

            gridReportCalendar.addView(textView)
        }
    }

    // Chức năng: thêm ô trống trước ngày đầu tháng để giữ đúng bố cục lịch.
    private fun addEmptyCalendarCell() {
        val emptyContainer = FrameLayout(requireContext()).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = dp(48)
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }
        }

        gridReportCalendar.addView(emptyContainer)
    }

    // Chức năng: thêm một ô ngày vào lịch với kích thước nhỏ gọn.
    // Ngày đã tập là vòng tròn xanh, ngày chưa tập là vòng tròn xám nhạt.
    private fun addCalendarDayCell(
        day: Int,
        dateString: String,
        hasWorkout: Boolean
    ) {
        val cellContainer = FrameLayout(requireContext()).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = dp(48)
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }
        }

        val textColor = if (hasWorkout) {
            Color.WHITE
        } else {
            Color.parseColor("#666666")
        }

        val dayView = TextView(requireContext()).apply {
            text = day.toString()
            gravity = Gravity.CENTER
            setTextColor(textColor)
            textSize = 15f
            typeface = Typeface.create("sans-serif", Typeface.NORMAL)
            includeFontPadding = false
            background = createCircleDayBackground(hasWorkout)

            layoutParams = FrameLayout.LayoutParams(
                dp(38),
                dp(38),
                Gravity.CENTER
            )

            setOnClickListener {
                selectedCalendar.set(
                    reportCalendar.get(Calendar.YEAR),
                    reportCalendar.get(Calendar.MONTH),
                    day
                )

                txtSelectedDate.text =
                    "$day/${reportCalendar.get(Calendar.MONTH) + 1}/${reportCalendar.get(Calendar.YEAR)}"

                showWorkoutHistory(dateString)
                renderWorkoutCalendar()
            }
        }

        cellContainer.addView(dayView)
        gridReportCalendar.addView(cellContainer)
    }

    // Chức năng: tạo nền hình tròn cho ngày trong lịch.
    private fun createCircleDayBackground(hasWorkout: Boolean): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL

            if (hasWorkout) {
                setColor(Color.parseColor("#16D878"))
                setStroke(dp(1), Color.parseColor("#16D878"))
            } else {
                setColor(Color.parseColor("#F7F7F7"))
                setStroke(dp(1), Color.parseColor("#F7F7F7"))
            }
        }
    }

    // Chức năng: hiển thị lịch sử tập luyện của ngày được chọn.
    // Mỗi lần tập sẽ được hiển thị thành một card nhỏ, dễ nhìn hơn dạng chữ dài.
    private fun showWorkoutHistory(date: String) {
        val records = WorkoutReportManager.getRecordsByDate(requireContext(), date)
            .sortedByDescending { record ->
                record.completedAtMillis
            }

        layoutWorkoutHistory.removeAllViews()

        if (records.isEmpty()) {
            addEmptyHistoryView()
            return
        }

        records.forEachIndexed { index, record ->
            addWorkoutHistoryItem(
                index = index,
                record = record
            )
        }
    }

    // Chức năng: hiển thị thông báo khi ngày được chọn chưa có lịch sử tập luyện.
    private fun addEmptyHistoryView() {
        val emptyText = TextView(requireContext()).apply {
            text = "Ngày này chưa có dữ liệu tập luyện.\nHoàn thành một buổi tập để lưu lịch sử."
            setTextColor(Color.parseColor("#777777"))
            textSize = 15f
            setLineSpacing(4f, 1.0f)
        }

        layoutWorkoutHistory.addView(emptyText)
    }

    // Chức năng: tạo một item lịch sử tập luyện dạng card nhỏ, chữ nhẹ và gọn giống app mẫu.
    private fun addWorkoutHistoryItem(
        index: Int,
        record: WorkoutReportRecord
    ) {
        if (index > 0) {
            addHistoryDivider()
        }

        val normalFont = Typeface.create("sans-serif", Typeface.NORMAL)
        val mediumFont = Typeface.create("sans-serif-medium", Typeface.NORMAL)

        val itemLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(8), dp(12), dp(8))
            background = createRoundedBackground("#FFFFFF", 18f)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            if (index > 0) {
                params.topMargin = dp(14)
            }

            layoutParams = params
        }

        val titleText = TextView(requireContext()).apply {
            text = "${getExerciseTypeDisplayName(record.exerciseType)} - Ngày ${record.dayNumber}"
            setTextColor(Color.parseColor("#111111"))
            textSize = 17f
            typeface = Typeface.DEFAULT_BOLD
            paint.isFakeBoldText = true
        }

        val dateText = TextView(requireContext()).apply {
            text = "${formatTimeOfDay(record.completedAtMillis)}  •  ${formatDateForDisplay(record.date)}"
            setTextColor(Color.parseColor("#888888"))
            textSize = 14f
            typeface = normalFont
            setPadding(0, dp(4), 0, dp(6))
        }

        val statRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        statRow.addView(
            createHistoryStatText(
                value = "${record.exerciseCount}",
                label = "Bài tập"
            )
        )

        statRow.addView(
            createHistoryStatText(
                value = formatDuration(record.durationSeconds),
                label = "Thời lượng"
            )
        )

        statRow.addView(
            createHistoryStatText(
                value = "${record.calories}",
                label = "Kcal"
            )
        )

        itemLayout.addView(titleText)
        itemLayout.addView(dateText)
        itemLayout.addView(statRow)

        layoutWorkoutHistory.addView(itemLayout)
    }

    // Chức năng: tạo đường kẻ ngang để phân chia các lần tập trong lịch sử.
    private fun addHistoryDivider() {
        val divider = View(requireContext()).apply {
            setBackgroundColor(Color.parseColor("#DDDDDD"))

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(1)
            ).apply {
                topMargin = dp(8)
                bottomMargin = dp(8)
                marginStart = dp(4)
                marginEnd = dp(4)
            }
        }
        layoutWorkoutHistory.addView(divider)
    }

    // Chức năng: tạo một cột thông số nhỏ trong item lịch sử.
    // Chức năng: tạo một cột thông số nhỏ trong item lịch sử với chữ mảnh, dễ nhìn hơn.
    private fun createHistoryStatText(
        value: String,
        label: String
    ): LinearLayout {
        val normalFont = Typeface.create("sans-serif", Typeface.NORMAL)
        val mediumFont = Typeface.create("sans-serif-medium", Typeface.NORMAL)

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER

            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val valueText = TextView(requireContext()).apply {
            text = value
            setTextColor(Color.parseColor("#222222"))
            textSize = 16f
            gravity = Gravity.CENTER
            typeface = mediumFont
        }

        val labelText = TextView(requireContext()).apply {
            text = label
            setTextColor(Color.parseColor("#999999"))
            textSize = 13f
            gravity = Gravity.CENTER
            typeface = normalFont
        }

        layout.addView(valueText)
        layout.addView(labelText)

        return layout
    }

    // Chức năng: đổi mã loại bài tập thành tên tiếng Việt dễ đọc.
    private fun getExerciseTypeDisplayName(exerciseType: String): String {
        return when (exerciseType) {
            "full_body" -> "Tập Toàn Thân"
            "abs" -> "Cơ Bụng"
            "arms_chest" -> "Tay & Ngực"
            "legs" -> "Chân"
            else -> "Bài tập"
        }
    }

    // Chức năng: định dạng giờ hoàn thành buổi tập.
    private fun formatTimeOfDay(timeMillis: Long): String {
        return try {
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            outputFormat.format(Date(timeMillis))
        } catch (e: Exception) {
            "--:--"
        }
    }

    // Chức năng: tạo nền bo góc cho item lịch sử.
    private fun createRoundedBackground(
        color: String,
        radiusDp: Float
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(Color.parseColor(color))
            cornerRadius = radiusDp * resources.displayMetrics.density
        }
    }

    // Chức năng: đổi dp sang pixel để tạo giao diện bằng Kotlin.
    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    // Chức năng: mở hộp thoại chỉnh sửa cân nặng và chiều cao với giao diện gọn đẹp.
    // Sau khi lưu sẽ cập nhật lại BMI, biểu đồ cân nặng và lưu vào hồ sơ người dùng.
    private fun showEditBodyDialog() {
        val currentWeight = weightList.lastOrNull()?.weight ?: getProfileFloat("currentWeight", 70f).toDouble()
        val currentHeightCm = (heightM * 100).roundToInt()

        val normalFont = Typeface.create("sans-serif", Typeface.NORMAL)
        val mediumFont = Typeface.create("sans-serif-medium", Typeface.NORMAL)

        val dialogContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(24), dp(24), dp(18))
            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = dp(22).toFloat()
            }
        }

        val titleText = TextView(requireContext()).apply {
            text = "Chỉnh sửa chỉ số"
            setTextColor(Color.parseColor("#111111"))
            textSize = 22f
            typeface = mediumFont
            includeFontPadding = false
        }

        val subTitleText = TextView(requireContext()).apply {
            text = "Cập nhật cân nặng và chiều cao hiện tại"
            setTextColor(Color.parseColor("#888888"))
            textSize = 14f
            typeface = normalFont
            includeFontPadding = false
            setPadding(0, dp(8), 0, dp(20))
        }

        val weightLabel = TextView(requireContext()).apply {
            text = "Cân nặng"
            setTextColor(Color.parseColor("#333333"))
            textSize = 16f
            typeface = mediumFont
            includeFontPadding = false
        }

        val inputWeight = EditText(requireContext()).apply {
            setText("%.1f".format(currentWeight))
            hint = "75.0"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setTextColor(Color.parseColor("#222222"))
            setHintTextColor(Color.parseColor("#BBBBBB"))
            textSize = 18f
            typeface = normalFont
            setSingleLine(true)
            backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#DDDDDD"))
            setPadding(0, 0, 0, dp(6))
        }

        val weightUnit = TextView(requireContext()).apply {
            text = "kg"
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            textSize = 16f
            typeface = mediumFont
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#18C27A"))
                cornerRadius = dp(6).toFloat()
            }
            layoutParams = LinearLayout.LayoutParams(dp(48), dp(40)).apply {
                marginStart = dp(14)
            }
        }

        val weightInputRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(10), 0, dp(24))

            addView(
                inputWeight,
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            )

            addView(weightUnit)
        }

        val heightLabel = TextView(requireContext()).apply {
            text = "Chiều cao"
            setTextColor(Color.parseColor("#333333"))
            textSize = 16f
            typeface = mediumFont
            includeFontPadding = false
        }

        val inputHeight = EditText(requireContext()).apply {
            setText(currentHeightCm.toString())
            hint = "170"
            inputType = InputType.TYPE_CLASS_NUMBER
            setTextColor(Color.parseColor("#222222"))
            setHintTextColor(Color.parseColor("#BBBBBB"))
            textSize = 18f
            typeface = normalFont
            setSingleLine(true)
            backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#DDDDDD"))
            setPadding(0, 0, 0, dp(6))
        }

        val heightUnit = TextView(requireContext()).apply {
            text = "cm"
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            textSize = 16f
            typeface = mediumFont
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#18C27A"))
                cornerRadius = dp(6).toFloat()
            }
            layoutParams = LinearLayout.LayoutParams(dp(48), dp(40)).apply {
                marginStart = dp(14)
            }
        }

        val heightInputRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(10), 0, dp(26))

            addView(
                inputHeight,
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            )

            addView(heightUnit)
        }

        val buttonRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END or Gravity.CENTER_VERTICAL
        }

        val btnCancel = TextView(requireContext()).apply {
            text = "HỦY"
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#777777"))
            textSize = 15f
            typeface = mediumFont
            setPadding(dp(18), dp(12), dp(18), dp(12))
        }

        val btnSave = TextView(requireContext()).apply {
            text = "LƯU"
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#18C27A"))
            textSize = 15f
            typeface = mediumFont
            setPadding(dp(18), dp(12), dp(4), dp(12))
        }

        buttonRow.addView(btnCancel)
        buttonRow.addView(btnSave)

        dialogContainer.addView(titleText)
        dialogContainer.addView(subTitleText)
        dialogContainer.addView(weightLabel)
        dialogContainer.addView(weightInputRow)
        dialogContainer.addView(heightLabel)
        dialogContainer.addView(heightInputRow)
        dialogContainer.addView(buttonRow)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogContainer)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            val newWeight = inputWeight.text.toString().trim().replace(",", ".").toDoubleOrNull()
            val newHeightCm = inputHeight.text.toString().trim().toDoubleOrNull()

            if (newWeight == null || newWeight < 30.0 || newWeight > 250.0) {
                Toast.makeText(
                    requireContext(),
                    "Cân nặng không hợp lệ",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (newHeightCm == null || newHeightCm < 100.0 || newHeightCm > 230.0) {
                Toast.makeText(
                    requireContext(),
                    "Chiều cao không hợp lệ",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            dialog.dismiss()
            saveBodyDataAndRecalculatePlan(newWeight, newHeightCm)
        }

        dialog.setOnShowListener {
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }

        dialog.show()
    }

    // Chức năng: lưu cân nặng, chiều cao, BMI mới và chạy hiệu ứng tính lại lộ trình tập.
    private fun saveBodyDataAndRecalculatePlan(
        newWeight: Double,
        newHeightCm: Double
    ) {
        heightM = newHeightCm / 100.0

        val today = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date())

        weightList.removeAll { record ->
            record.date == today
        }

        weightList.add(
            WeightRecord(
                date = today,
                weight = newWeight
            )
        )

        while (weightList.size > 7) {
            weightList.removeAt(0)
        }

        val newBMI = newWeight / (heightM * heightM)
        val planLevel = getPlanLevelByBodyData(newBMI)

        getProfilePrefs()
            .edit()
            .putFloat("height", newHeightCm.toFloat())
            .putFloat("currentWeight", newWeight.toFloat())
            .putFloat("currentBMI", newBMI.toFloat())
            .putString("planLevel", planLevel)
            .putLong("planRecalculatedAt", System.currentTimeMillis())
            .apply()

        saveWeightData()
        updateWeightInfo()
        updateWeightChart()

        showRecalculatePlanDialog {
            Toast.makeText(
                requireContext(),
                "Đã cập nhật BMI và tính lại lộ trình tập",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Chức năng: dựa vào BMI để chọn nhóm lộ trình tập phù hợp.
    private fun getPlanLevelByBodyData(bmi: Double): String {
        return when {
            bmi < 18.5 -> "underweight"
            bmi < 25.0 -> "normal"
            bmi < 30.0 -> "overweight"
            else -> "obese"
        }
    }

    // Chức năng: hiển thị loading tính lại lộ trình dạng vòng tròn 0% đến 100%.
    private fun showRecalculatePlanDialog(onFinish: () -> Unit) {
        val normalFont = Typeface.create("sans-serif", Typeface.NORMAL)
        val mediumFont = Typeface.create("sans-serif-medium", Typeface.NORMAL)

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24), dp(30), dp(24), dp(28))
            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = dp(28).toFloat()
            }
        }

        val titleText = TextView(requireContext()).apply {
            text = "Đang tạo kế hoạch cá nhân"
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#111111"))
            textSize = 22f
            typeface = mediumFont
            includeFontPadding = false
        }

        val descriptionText = TextView(requireContext()).apply {
            text = "Ứng dụng đang phân tích chỉ số cơ thể mới của bạn..."
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#777777"))
            textSize = 15f
            typeface = normalFont
            includeFontPadding = false
            setPadding(0, dp(10), 0, dp(24))
        }

        val circleContainer = FrameLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                dp(170),
                dp(170)
            )
        }

        val circleProgressView = RecalculateCircleProgressView(requireContext())

        circleContainer.addView(
            circleProgressView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER
            )
        )

        val percentText = TextView(requireContext()).apply {
            text = "0%"
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#111111"))
            textSize = 42f
            typeface = mediumFont
            includeFontPadding = false
        }

        circleContainer.addView(
            percentText,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER
            )
        )

        val bottomText = TextView(requireContext()).apply {
            text = "Bắt đầu xây dựng lại lộ trình tập trong 30 ngày"
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#666666"))
            textSize = 15f
            typeface = normalFont
            includeFontPadding = false
            setPadding(0, dp(22), 0, 0)
        }

        container.addView(titleText)
        container.addView(descriptionText)
        container.addView(circleContainer)
        container.addView(bottomText)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(container)
            .setCancelable(false)
            .create()

        dialog.setOnShowListener {
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val handler = Handler(Looper.getMainLooper())
            var progress = 0

            val runnable = object : Runnable {
                override fun run() {
                    progress += 1

                    if (progress > 100) {
                        progress = 100
                    }

                    percentText.text = "$progress%"
                    circleProgressView.setProgress(progress)

                    when (progress) {
                        25 -> {
                            descriptionText.text = "Đang cập nhật BMI mới..."
                        }

                        55 -> {
                            descriptionText.text = "Đang chọn bài tập phù hợp..."
                        }

                        80 -> {
                            descriptionText.text = "Đang hoàn tất lộ trình 30 ngày..."
                        }
                    }

                    if (progress < 100) {
                        handler.postDelayed(this, 45)
                    } else {
                        handler.postDelayed({
                            if (dialog.isShowing) {
                                dialog.dismiss()
                            }

                            onFinish()
                        }, 500)
                    }
                }
            }

            handler.postDelayed(runnable, 300)

            handler.post(runnable)
        }

        dialog.show()
    }

    // Chức năng: mở hộp thoại chỉ cập nhật cân nặng hôm nay.
    // Dùng cho nút + trong card Cân nặng, không chỉnh chiều cao.
    private fun showAddWeightOnlyDialog() {
        val currentWeight = weightList.lastOrNull()?.weight
            ?: getProfileFloat("currentWeight", 70f).toDouble()

        val normalFont = Typeface.create("sans-serif", Typeface.NORMAL)
        val mediumFont = Typeface.create("sans-serif-medium", Typeface.NORMAL)

        val dialogContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(24), dp(24), dp(18))
            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = dp(22).toFloat()
            }
        }

        val titleText = TextView(requireContext()).apply {
            text = "Cập nhật cân nặng"
            setTextColor(Color.parseColor("#111111"))
            textSize = 22f
            typeface = mediumFont
            includeFontPadding = false
        }

        val subTitleText = TextView(requireContext()).apply {
            text = "Nhập cân nặng hiện tại của hôm nay"
            setTextColor(Color.parseColor("#888888"))
            textSize = 14f
            typeface = normalFont
            includeFontPadding = false
            setPadding(0, dp(8), 0, dp(22))
        }

        val inputWeight = EditText(requireContext()).apply {
            setText("%.1f".format(currentWeight))
            hint = "76.0"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setTextColor(Color.parseColor("#222222"))
            setHintTextColor(Color.parseColor("#BBBBBB"))
            textSize = 22f
            typeface = normalFont
            setSingleLine(true)
            backgroundTintList =
                android.content.res.ColorStateList.valueOf(Color.parseColor("#18C27A"))
            setPadding(0, 0, 0, dp(6))
        }

        val unitText = TextView(requireContext()).apply {
            text = "kg"
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            textSize = 16f
            typeface = mediumFont
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#18C27A"))
                cornerRadius = dp(6).toFloat()
            }
            layoutParams = LinearLayout.LayoutParams(dp(50), dp(40)).apply {
                marginStart = dp(14)
            }
        }

        val inputRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, dp(28))

            addView(
                inputWeight,
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            )

            addView(unitText)
        }

        val noteText = TextView(requireContext()).apply {
            text = "Gợi ý: chỉ cần cập nhật khi bạn cân lại, ví dụ mỗi tuần 1 lần."
            setTextColor(Color.parseColor("#999999"))
            textSize = 13f
            typeface = normalFont
            includeFontPadding = false
            setPadding(0, 0, 0, dp(18))
        }

        val buttonRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END or Gravity.CENTER_VERTICAL
        }

        val btnCancel = TextView(requireContext()).apply {
            text = "HỦY"
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#777777"))
            textSize = 15f
            typeface = mediumFont
            setPadding(dp(18), dp(12), dp(18), dp(12))
        }

        val btnSave = TextView(requireContext()).apply {
            text = "LƯU"
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#18C27A"))
            textSize = 15f
            typeface = mediumFont
            setPadding(dp(18), dp(12), dp(4), dp(12))
        }

        buttonRow.addView(btnCancel)
        buttonRow.addView(btnSave)

        dialogContainer.addView(titleText)
        dialogContainer.addView(subTitleText)
        dialogContainer.addView(inputRow)
        dialogContainer.addView(noteText)
        dialogContainer.addView(buttonRow)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogContainer)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            val newWeight = inputWeight.text.toString().trim()
                .replace(",", ".")
                .toDoubleOrNull()

            if (newWeight == null || newWeight < 30.0 || newWeight > 250.0) {
                Toast.makeText(
                    requireContext(),
                    "Cân nặng không hợp lệ",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val currentHeightCm = heightM * 100.0

            dialog.dismiss()
            saveBodyDataAndRecalculatePlan(newWeight, currentHeightCm)
        }

        dialog.setOnShowListener {
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }

        dialog.show()
    }

    private fun updateWeightInfo() {
        val currentWeight = weightList.lastOrNull()?.weight ?: 0.0
        val oldWeight = weightList.firstOrNull()?.weight ?: currentWeight

        val last30Days = oldWeight - currentWeight
        val averageWeight = if (weightList.isNotEmpty()) {
            weightList.map { it.weight }.average()
        } else {
            currentWeight
        }

        txtCurrentWeight.text = "%.1f".format(currentWeight)
        txtLast30Days.text = "%.1f".format(last30Days)
        txtAverageWeight.text = "%.1f".format(averageWeight)

        val bmi = if (heightM > 0) {
            currentWeight / (heightM * heightM)
        } else {
            0.0
        }

        txtBMIValue.text = "%.1f".format(bmi)
        txtBMIStatus.text = getBMIStatus(bmi)

        txtBMIMarker.text = "%.1f".format(bmi)
        txtBMIMarker.setTextColor(Color.WHITE)
        txtBMIMarker.background = createRoundedBackground("#555A60", 14f)
        txtBMIMarker.typeface = Typeface.create("sans-serif", Typeface.NORMAL)

        val statusColor = when {
            bmi < 18.5 -> Color.parseColor("#75BDE3")
            bmi < 25.0 -> Color.parseColor("#63D44B")
            bmi < 30.0 -> Color.parseColor("#F2D22E")
            bmi < 35.0 -> Color.parseColor("#E89A2E")
            else -> Color.parseColor("#D9231F")
        }

        txtBMIStatus.setTextColor(statusColor)

        updateBMIPosition(bmi)
    }

    private fun updateBMIPosition(bmi: Double) {
        bmiScaleContainer.post {
            val minBMI = 15.0
            val maxBMI = 40.0

            val fixedBMI = bmi.coerceIn(minBMI, maxBMI)
            val percent = (fixedBMI - minBMI) / (maxBMI - minBMI)

            val barWidth = bmiScaleContainer.width
            val markerWidth = txtBMIMarker.width

            val rawX = ((barWidth * percent) - (markerWidth / 2.0)).toFloat()

            txtBMIMarker.translationX =
                rawX.coerceIn(
                    0f,
                    (barWidth - markerWidth).toFloat()
                )
        }
    }

    private fun updateWeightChart() {
        weightLineChartView.setData(weightList)
    }

    /// Chức năng: cập nhật biểu đồ calo theo tuần hiện tại.
// Thứ tự luôn cố định: CN, T2, T3, T4, T5, T6, T7.
    private fun updateCaloriesChart() {
        val allRecords = WorkoutReportManager.getAllRecords(requireContext())
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val caloriesByDay = mutableListOf<Int>()
        val dayLabels = listOf("CN", "T2", "T3", "T4", "T5", "T6", "T7")

        val startOfWeek = Calendar.getInstance().apply {
            val currentDayOfWeek = get(Calendar.DAY_OF_WEEK)

            // Calendar.SUNDAY = 1, MONDAY = 2...
            // Lùi về Chủ nhật đầu tuần.
            add(Calendar.DAY_OF_YEAR, -(currentDayOfWeek - Calendar.SUNDAY))
        }

        for (i in 0..6) {
            val dayCalendar = Calendar.getInstance().apply {
                time = startOfWeek.time
                add(Calendar.DAY_OF_YEAR, i)
            }

            val date = dateFormat.format(dayCalendar.time)

            val calories = allRecords
                .filter { record -> record.date == date }
                .sumOf { record -> record.calories }

            caloriesByDay.add(calories)
        }

        val bars = listOf(barCal1, barCal2, barCal3, barCal4, barCal5, barCal6, barCal7)

        val values = listOf(
            txtCalValue1,
            txtCalValue2,
            txtCalValue3,
            txtCalValue4,
            txtCalValue5,
            txtCalValue6,
            txtCalValue7
        )

        val labels = listOf(
            txtCalDay1,
            txtCalDay2,
            txtCalDay3,
            txtCalDay4,
            txtCalDay5,
            txtCalDay6,
            txtCalDay7
        )

        val maxCalories = caloriesByDay.maxOrNull() ?: 0
        val safeMax = if (maxCalories == 0) 1 else maxCalories

        for (i in bars.indices) {
            val calories = caloriesByDay[i]
            val percent = calories.toDouble() / safeMax.toDouble()
            val height = 32 + (percent * 120).roundToInt()

            val params = bars[i].layoutParams
            params.height = height
            bars[i].layoutParams = params

            bars[i].background = createRoundedBackground("#18C27A", 9f)

            values[i].text = calories.toString()
            labels[i].text = dayLabels[i]
        }

        val todayCalories = WorkoutReportManager.getTodaySummary(requireContext()).totalCalories
        txtCaloriesToday.text = "Hôm nay: $todayCalories Kcal"
    }

    private fun secondsToMinutes(seconds: Int): Int {
        return seconds / 60
    }

    private fun formatDuration(seconds: Int): String {
        val minutes = seconds / 60
        val remainSeconds = seconds % 60
        return "%02d:%02d".format(minutes, remainSeconds)
    }

    private fun formatDateForDisplay(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
            val parsedDate = inputFormat.parse(date)

            if (parsedDate != null) {
                outputFormat.format(parsedDate)
            } else {
                date
            }
        } catch (e: Exception) {
            date
        }
    }

    private fun getBMIStatus(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Gầy"
            bmi < 25.0 -> "Bình thường"
            bmi < 30.0 -> "Thừa cân"
            bmi < 35.0 -> "Béo phì"
            else -> "Béo phì nặng"
        }
    }

    // Chức năng: vẽ biểu đồ cân nặng có thể kéo ngang.
    // Mặc định ngày hôm nay nằm ở giữa, hai bên mỗi bên 3 ngày, tổng cộng 7 ngày.
    class WeightLineChartView(context: Context) : View(context) {

        private var data: List<WeightRecord> = emptyList()

        private var centerDateOffsetDays = 0
        private var lastTouchX = 0f
        private var dragDistanceX = 0f

        private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#E6E6E6")
            strokeWidth = 2f
        }

        private val dashPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#18C27A")
            strokeWidth = 2.5f
            style = Paint.Style.STROKE
            pathEffect = android.graphics.DashPathEffect(floatArrayOf(14f, 12f), 0f)
        }

        private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#9E9E9E")
            textSize = 28f
            textAlign = Paint.Align.LEFT
        }

        private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#18C27A")
            strokeWidth = 6f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#3318C27A")
            style = Paint.Style.FILL
        }

        private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#18C27A")
            style = Paint.Style.FILL
        }

        private val pointBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }

        fun setData(newData: List<WeightRecord>) {
            data = newData
            invalidate()
        }

        override fun performClick(): Boolean {
            super.performClick()
            return true
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    parent.requestDisallowInterceptTouchEvent(true)
                    lastTouchX = event.x
                    dragDistanceX = 0f
                    return true
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = event.x - lastTouchX
                    lastTouchX = event.x
                    dragDistanceX += dx

                    val oneDayWidth = width / 7f

                    if (kotlin.math.abs(dragDistanceX) >= oneDayWidth) {
                        val dayMove = (dragDistanceX / oneDayWidth).toInt()

                        // Kéo sang phải: xem ngày cũ hơn.
                        // Kéo sang trái: xem ngày mới hơn.
                        centerDateOffsetDays -= dayMove

                        dragDistanceX -= dayMove * oneDayWidth
                        invalidate()
                    }

                    return true
                }

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                    performClick()
                    return true
                }
            }

            return true
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            if (data.isEmpty()) {
                canvas.drawText("Chưa có dữ liệu cân nặng", 30f, height / 2f, textPaint)
                return
            }

            val left = 120f      // chừa nhiều chỗ hơn cho số trục Y
            val top = 50f
            val right = width - 36f
            val bottom = height - 70f   // chừa thêm chỗ cho ngày ở trục X
            val chartWidth = right - left
            val chartHeight = bottom - top

            val visibleDays = getVisibleSevenDays()
            val visibleKeys = visibleDays.map { it.first }.toSet()

            val visibleRecords = data.filter { record ->
                visibleKeys.contains(record.date)
            }

            val weightsForScale = if (visibleRecords.isNotEmpty()) {
                visibleRecords.map { it.weight }
            } else {
                data.map { it.weight }
            }

            var minWeight = weightsForScale.minOrNull() ?: 0.0
            var maxWeight = weightsForScale.maxOrNull() ?: 1.0

            if (maxWeight - minWeight < 1.0) {
                maxWeight += 0.5
                minWeight -= 0.5
            }

            val range = maxWeight - minWeight

            drawGridAndYAxis(
                canvas = canvas,
                left = left,
                top = top,
                right = right,
                bottom = bottom,
                chartHeight = chartHeight,
                minWeight = minWeight,
                maxWeight = maxWeight,
                range = range
            )

            val points = mutableListOf<Triple<Float, Float, Double>>()

            visibleDays.forEachIndexed { index, dayItem ->
                val x = left + chartWidth * index / 6f
                val dateKey = dayItem.first
                val dateLabel = dayItem.second

                val record = data.findLast { weightRecord ->
                    weightRecord.date == dateKey
                }

                if (record != null) {
                    val yPercent = ((record.weight - minWeight) / range).toFloat()
                    val y = bottom - chartHeight * yPercent
                    points.add(Triple(x, y, record.weight))
                }

                drawDateLabel(canvas, x, dateLabel)
            }

            drawWeightLineAndPoints(canvas, points, bottom)
        }

        // Chức năng: tạo danh sách 7 ngày hiển thị trên trục X.
        // Ngày ở giữa là hôm nay cộng/trừ số ngày người dùng đã kéo.
        private fun getVisibleSevenDays(): List<Pair<String, String>> {
            val keyFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
            val labelFormat = SimpleDateFormat("dd", Locale.getDefault())

            val result = mutableListOf<Pair<String, String>>()

            for (i in -3..3) {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, centerDateOffsetDays + i)

                val key = keyFormat.format(calendar.time)
                val label = labelFormat.format(calendar.time)

                result.add(Pair(key, label))
            }

            return result
        }

        // Chức năng: vẽ lưới ngang và số cân nặng bên trái.
        private fun drawGridAndYAxis(
            canvas: Canvas,
            left: Float,
            top: Float,
            right: Float,
            bottom: Float,
            chartHeight: Float,
            minWeight: Double,
            maxWeight: Double,
            range: Double
        ) {
            for (i in 0..4) {
                val y = top + chartHeight * i / 4f
                canvas.drawLine(left, y, right, y, gridPaint)

                val labelValue = maxWeight - range * i / 4.0
                canvas.drawText("%.1f".format(labelValue), 4f, y + 8f, textPaint)
            }

            val centerWeight = data.lastOrNull()?.weight ?: return
            val centerPercent = ((centerWeight - minWeight) / range).toFloat()
            val centerY = bottom - chartHeight * centerPercent

            canvas.drawLine(left, centerY, right, centerY, dashPaint)
        }

        // Chức năng: vẽ nhãn ngày bên dưới trục X.
        private fun drawDateLabel(
            canvas: Canvas,
            x: Float,
            label: String
        ) {
            val oldAlign = textPaint.textAlign
            textPaint.textAlign = Paint.Align.CENTER
            canvas.drawText(label, x, height - 10f, textPaint)
            textPaint.textAlign = oldAlign
        }

        // Chức năng: vẽ đường biểu đồ, điểm cân nặng và bong bóng số cân nặng.
        private fun drawWeightLineAndPoints(
            canvas: Canvas,
            points: List<Triple<Float, Float, Double>>,
            bottom: Float
        ) {
            if (points.isEmpty()) {
                return
            }

            val fillPath = Path()
            fillPath.moveTo(points.first().first, bottom)

            points.forEach { point ->
                fillPath.lineTo(point.first, point.second)
            }

            fillPath.lineTo(points.last().first, bottom)
            fillPath.close()
            canvas.drawPath(fillPath, fillPaint)

            if (points.size >= 2) {
                val linePath = Path()
                linePath.moveTo(points.first().first, points.first().second)

                for (i in 1 until points.size) {
                    linePath.lineTo(points[i].first, points[i].second)
                }

                canvas.drawPath(linePath, linePaint)
            }

            points.forEach { point ->
                canvas.drawCircle(point.first, point.second, 10f, pointBorderPaint)
                canvas.drawCircle(point.first, point.second, 6f, pointPaint)
            }

            val lastPoint = points.last()
            val bubbleText = "%.1f".format(lastPoint.third)

            val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK
                style = Paint.Style.FILL
            }

            val bubbleTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 30f
                textAlign = Paint.Align.CENTER
            }

            val bubbleWidth = 88f
            val bubbleHeight = 54f
            val bubbleLeft = (lastPoint.first - bubbleWidth / 2f)
                .coerceIn(8f, width - bubbleWidth - 8f)
            val bubbleTop = (lastPoint.second - bubbleHeight - 18f)
                .coerceAtLeast(6f)

            val bubbleRect = android.graphics.RectF(
                bubbleLeft,
                bubbleTop,
                bubbleLeft + bubbleWidth,
                bubbleTop + bubbleHeight
            )

            canvas.drawRoundRect(bubbleRect, 24f, 24f, bubblePaint)
            canvas.drawText(
                bubbleText,
                bubbleRect.centerX(),
                bubbleRect.centerY() + 10f,
                bubbleTextPaint
            )
        }
    }

    // Chức năng: vẽ vòng tròn tiến trình khi tính lại lộ trình tập.
    private class RecalculateCircleProgressView(context: Context) : View(context) {

        private var progressPercent = 0

        private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#E8F5EE")
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }

        private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#18C27A")
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }

        fun setProgress(value: Int) {
            progressPercent = value.coerceIn(0, 100)
            invalidate()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val strokeWidth = 14f * resources.displayMetrics.density

            backgroundPaint.strokeWidth = strokeWidth
            progressPaint.strokeWidth = strokeWidth

            val padding = strokeWidth / 2f + 6f

            val rect = android.graphics.RectF(
                padding,
                padding,
                width - padding,
                height - padding
            )

            canvas.drawArc(
                rect,
                -90f,
                360f,
                false,
                backgroundPaint
            )

            canvas.drawArc(
                rect,
                -90f,
                360f * progressPercent / 100f,
                false,
                progressPaint
            )
        }
    }
}