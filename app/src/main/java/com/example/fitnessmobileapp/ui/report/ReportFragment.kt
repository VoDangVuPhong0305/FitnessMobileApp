package com.example.fitnessmobileapp.ui.report

import android.app.AlertDialog
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
import android.widget.CalendarView
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
import android.widget.ScrollView

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

    private lateinit var calendarView: CalendarView
    private lateinit var txtSelectedDate: TextView
    private lateinit var layoutWorkoutHistory: LinearLayout
    private lateinit var scrollWorkoutHistory: ScrollView

    private lateinit var btnEditWeight: TextView
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

    private lateinit var weightLineChartView: WeightLineChartView

    private val heightM = 1.65

    private val weightList = mutableListOf(
        WeightRecord("10/6", 77.5),
        WeightRecord("12/6", 76.8),
        WeightRecord("16/6", 76.0),
        WeightRecord("19/6", 75.5)
    )

    data class WeightRecord(
        val date: String,
        val weight: Double
    )

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

        calendarView = view.findViewById(R.id.calendarView)
        txtSelectedDate = view.findViewById(R.id.txtSelectedDate)
        layoutWorkoutHistory = view.findViewById(R.id.layoutWorkoutHistory)
        scrollWorkoutHistory = view.findViewById(R.id.scrollWorkoutHistory)

        btnEditWeight = view.findViewById(R.id.btnEditWeight)
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

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format(
                Locale.getDefault(),
                "%04d-%02d-%02d",
                year,
                month + 1,
                dayOfMonth
            )

            val displayDate = "$dayOfMonth/${month + 1}/$year"
            txtSelectedDate.text = displayDate
            showWorkoutHistory(selectedDate)
        }

        btnEditWeight.setOnClickListener {
            showAddWeightDialog()
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

    private fun showTodayHistory() {
        val todayQuery = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val todayDisplay = SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(Date())

        txtSelectedDate.text = todayDisplay
        showWorkoutHistory(todayQuery)
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

    private fun showAddWeightDialog() {
        val input = EditText(requireContext()).apply {
            hint = "Nhập cân nặng, ví dụ: 65.5"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setPadding(34, 20, 34, 20)
            textSize = 15f
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Cập nhật cân nặng")
            .setMessage("Nhập cân nặng hôm nay")
            .setView(input)
            .setPositiveButton("Lưu", null)
            .setNegativeButton("Hủy", null)
            .create()

        dialog.setOnShowListener {
            dialog.window?.setBackgroundDrawableResource(R.drawable.bg_dialog_rounded)

            val btnSave = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val btnCancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            btnSave.setTextColor(0xFF18C27A.toInt())
            btnCancel.setTextColor(0xFF777777.toInt())

            btnSave.setOnClickListener {
                val weight = input.text.toString().trim().toDoubleOrNull()

                if (weight == null || weight <= 0) {
                    Toast.makeText(
                        requireContext(),
                        "Cân nặng không hợp lệ",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val today = SimpleDateFormat("d/M", Locale.getDefault()).format(Date())

                weightList.add(
                    WeightRecord(
                        date = today,
                        weight = weight
                    )
                )

                while (weightList.size > 6) {
                    weightList.removeAt(0)
                }

                updateWeightInfo()
                updateWeightChart()

                Toast.makeText(
                    requireContext(),
                    "Đã cập nhật cân nặng",
                    Toast.LENGTH_SHORT
                ).show()

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun updateWeightInfo() {
        val currentWeight = weightList.lastOrNull()?.weight ?: 0.0
        val oldWeight = weightList.firstOrNull()?.weight ?: currentWeight

        val last30Days = oldWeight - currentWeight
        val averageWeight = weightList.map { it.weight }.average()

        txtCurrentWeight.text = "%.1f".format(currentWeight)
        txtLast30Days.text = "%.1f".format(last30Days)
        txtAverageWeight.text = "%.1f".format(averageWeight)

        val bmi = currentWeight / (heightM * heightM)

        txtBMIValue.text = "%.1f".format(bmi)
        txtBMIStatus.text = getBMIStatus(bmi)

        val statusColor = when {
            bmi < 18.5 -> 0xFF5F93FF.toInt()
            bmi < 25 -> 0xFF63D44B.toInt()
            bmi < 30 -> 0xFFF2AB45.toInt()
            else -> 0xFFF05066.toInt()
        }

        txtBMIStatus.setTextColor(statusColor)
        txtBMIMarker.setTextColor(statusColor)

        updateBMIPosition(bmi)
    }

    private fun updateBMIPosition(bmi: Double) {
        bmiScaleContainer.post {
            val minBMI = 15.0
            val maxBMI = 40.0

            val fixedBMI = bmi.coerceIn(minBMI, maxBMI)
            val percent = (fixedBMI - minBMI) / (maxBMI - minBMI)

            val maxMove = bmiScaleContainer.width - txtBMIMarker.width
            txtBMIMarker.translationX = (percent * maxMove).toFloat()
        }
    }

    private fun updateWeightChart() {
        weightLineChartView.setData(weightList)
    }

    private fun updateCaloriesChart() {
        val allRecords = WorkoutReportManager.getAllRecords(requireContext())
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val caloriesByDay = mutableListOf<Int>()

        for (i in 6 downTo 0) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)

            val date = dateFormat.format(calendar.time)
            val calories = allRecords
                .filter { it.date == date }
                .sumOf { it.calories }

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

        val maxCalories = caloriesByDay.maxOrNull() ?: 0
        val safeMax = if (maxCalories == 0) 1 else maxCalories

        for (i in bars.indices) {
            val calories = caloriesByDay[i]
            val percent = calories.toDouble() / safeMax.toDouble()
            val height = 35 + (percent * 130).roundToInt()

            val params = bars[i].layoutParams
            params.height = height
            bars[i].layoutParams = params

            values[i].text = calories.toString()
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
            bmi < 25 -> "Bình thường"
            bmi < 30 -> "Thừa cân"
            else -> "Béo phì"
        }
    }

    class WeightLineChartView(context: Context) : View(context) {

        private var data: List<WeightRecord> = emptyList()

        private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#E6E6E6")
            strokeWidth = 2f
        }

        private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#9E9E9E")
            textSize = 28f
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

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            if (data.isEmpty()) {
                canvas.drawText("Chưa có dữ liệu cân nặng", 30f, height / 2f, textPaint)
                return
            }

            val left = 58f
            val top = 28f
            val right = width - 24f
            val bottom = height - 48f
            val chartWidth = right - left
            val chartHeight = bottom - top

            val weights = data.map { it.weight }
            var minWeight = weights.minOrNull() ?: 0.0
            var maxWeight = weights.maxOrNull() ?: 1.0

            if (maxWeight - minWeight < 1.0) {
                maxWeight += 1.0
                minWeight -= 1.0
            }

            val range = maxWeight - minWeight

            for (i in 0..4) {
                val y = top + chartHeight * i / 4f
                canvas.drawLine(left, y, right, y, gridPaint)

                val labelValue = maxWeight - range * i / 4.0
                canvas.drawText("%.0f".format(labelValue), 4f, y + 8f, textPaint)
            }

            val points = data.mapIndexed { index, record ->
                val x = if (data.size == 1) {
                    left + chartWidth / 2f
                } else {
                    left + chartWidth * index / (data.size - 1).toFloat()
                }

                val yPercent = ((record.weight - minWeight) / range).toFloat()
                val y = bottom - chartHeight * yPercent

                Pair(x, y)
            }

            val fillPath = Path()
            fillPath.moveTo(points.first().first, bottom)
            points.forEach { point ->
                fillPath.lineTo(point.first, point.second)
            }
            fillPath.lineTo(points.last().first, bottom)
            fillPath.close()
            canvas.drawPath(fillPath, fillPaint)

            val linePath = Path()
            linePath.moveTo(points.first().first, points.first().second)
            for (i in 1 until points.size) {
                linePath.lineTo(points[i].first, points[i].second)
            }
            canvas.drawPath(linePath, linePaint)

            points.forEachIndexed { index, point ->
                canvas.drawCircle(point.first, point.second, 12f, pointBorderPaint)
                canvas.drawCircle(point.first, point.second, 8f, pointPaint)

                val label = data[index].date
                val textWidth = textPaint.measureText(label)
                canvas.drawText(label, point.first - textWidth / 2f, height - 10f, textPaint)
            }
        }
    }
}