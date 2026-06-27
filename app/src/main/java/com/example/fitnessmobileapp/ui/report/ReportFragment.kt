package com.example.fitnessmobileapp.ui.report

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fitnessmobileapp.R
import com.example.fitnessmobileapp.data.repository.WorkoutReportManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class ReportFragment : Fragment() {

    private lateinit var tabCalendar: LinearLayout
    private lateinit var tabWeight: LinearLayout
    private lateinit var txtTabCalendar: TextView
    private lateinit var txtTabWeight: TextView
    private lateinit var lineCalendar: View
    private lateinit var lineWeight: View
    private lateinit var sectionCalendar: LinearLayout
    private lateinit var sectionData: LinearLayout

    private lateinit var txtTotalDays: TextView
    private lateinit var txtTotalCalories: TextView
    private lateinit var txtTotalMinutes: TextView
    private lateinit var calendarView: CalendarView
    private lateinit var txtSelectedDate: TextView
    private lateinit var txtWorkoutHistory: TextView

    private lateinit var btnEditWeight: TextView
    private lateinit var txtCurrentWeight: TextView
    private lateinit var txtLast30Days: TextView
    private lateinit var txtAverageWeight: TextView

    private lateinit var txtBMIValue: TextView
    private lateinit var txtBMIStatus: TextView
    private lateinit var txtBMIMarker: TextView
    private lateinit var bmiScaleContainer: View

    private lateinit var barWeight1: View
    private lateinit var barWeight2: View
    private lateinit var barWeight3: View
    private lateinit var barWeight4: View
    private lateinit var txtWeightDate1: TextView
    private lateinit var txtWeightDate2: TextView
    private lateinit var txtWeightDate3: TextView
    private lateinit var txtWeightDate4: TextView

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
        setupEvents()
        loadReportData()
        showCalendarSection()

        return view
    }

    override fun onResume() {
        super.onResume()

        if (::txtTotalDays.isInitialized) {
            loadReportData()
        }
    }

    private fun initViews(view: View) {
        tabCalendar = view.findViewById(R.id.tabCalendar)
        tabWeight = view.findViewById(R.id.tabWeight)
        txtTabCalendar = view.findViewById(R.id.txtTabCalendar)
        txtTabWeight = view.findViewById(R.id.txtTabWeight)
        lineCalendar = view.findViewById(R.id.lineCalendar)
        lineWeight = view.findViewById(R.id.lineWeight)

        sectionCalendar = view.findViewById(R.id.sectionCalendar)
        sectionData = view.findViewById(R.id.sectionData)

        txtTotalDays = view.findViewById(R.id.txtTotalDays)
        txtTotalCalories = view.findViewById(R.id.txtTotalCalories)
        txtTotalMinutes = view.findViewById(R.id.txtTotalMinutes)
        calendarView = view.findViewById(R.id.calendarView)
        txtSelectedDate = view.findViewById(R.id.txtSelectedDate)
        txtWorkoutHistory = view.findViewById(R.id.txtWorkoutHistory)

        btnEditWeight = view.findViewById(R.id.btnEditWeight)
        txtCurrentWeight = view.findViewById(R.id.txtCurrentWeight)
        txtLast30Days = view.findViewById(R.id.txtLast30Days)
        txtAverageWeight = view.findViewById(R.id.txtAverageWeight)

        txtBMIValue = view.findViewById(R.id.txtBMIValue)
        txtBMIStatus = view.findViewById(R.id.txtBMIStatus)
        txtBMIMarker = view.findViewById(R.id.txtBMIMarker)
        bmiScaleContainer = view.findViewById(R.id.bmiScaleContainer)

        barWeight1 = view.findViewById(R.id.barWeight1)
        barWeight2 = view.findViewById(R.id.barWeight2)
        barWeight3 = view.findViewById(R.id.barWeight3)
        barWeight4 = view.findViewById(R.id.barWeight4)

        txtWeightDate1 = view.findViewById(R.id.txtWeightDate1)
        txtWeightDate2 = view.findViewById(R.id.txtWeightDate2)
        txtWeightDate3 = view.findViewById(R.id.txtWeightDate3)
        txtWeightDate4 = view.findViewById(R.id.txtWeightDate4)

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
    }

    private fun setupEvents() {
        tabCalendar.setOnClickListener {
            showCalendarSection()
        }

        tabWeight.setOnClickListener {
            showDataSection()
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

    private fun showWorkoutHistory(date: String) {
        val records = WorkoutReportManager.getRecordsByDate(requireContext(), date)

        if (records.isEmpty()) {
            txtWorkoutHistory.text =
                "Ngày này chưa có dữ liệu tập luyện.\n\nKhi người dùng hoàn thành bài tập, dữ liệu sẽ tự lưu vào báo cáo."
            return
        }

        val result = StringBuilder()

        records.forEachIndexed { index, record ->
            result.append("Lần tập ${index + 1}\n")
            result.append("• Ngày tập: ${formatDateForDisplay(record.date)}\n")
            result.append("• Ngày thứ: ${record.dayNumber}\n")
            result.append("• Loại bài tập: ${record.exerciseType}\n")
            result.append("• Số bài: ${record.exerciseCount}\n")
            result.append("• Thời gian: ${formatDuration(record.durationSeconds)}\n")
            result.append("• Calo: ${record.calories} Kcal\n\n")
        }

        txtWorkoutHistory.text = result.toString()
    }

    private fun showAddWeightDialog() {
        val input = EditText(requireContext())
        input.hint = "Nhập cân nặng, ví dụ: 65.5"
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

        AlertDialog.Builder(requireContext())
            .setTitle("Cập nhật cân nặng")
            .setMessage("Nhập cân nặng hôm nay")
            .setView(input)
            .setPositiveButton("Lưu") { _, _ ->
                val weight = input.text.toString().trim().toDoubleOrNull()

                if (weight == null || weight <= 0) {
                    Toast.makeText(requireContext(), "Cân nặng không hợp lệ", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val today = SimpleDateFormat("d/M", Locale.getDefault()).format(Date())
                weightList.add(WeightRecord(today, weight))

                while (weightList.size > 4) {
                    weightList.removeAt(0)
                }

                updateWeightInfo()
                updateWeightChart()

                Toast.makeText(requireContext(), "Đã cập nhật cân nặng", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .show()
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
        val bars = listOf(barWeight1, barWeight2, barWeight3, barWeight4)
        val labels = listOf(txtWeightDate1, txtWeightDate2, txtWeightDate3, txtWeightDate4)

        if (weightList.isEmpty()) return

        val minWeight = weightList.minOf { it.weight }
        val maxWeight = weightList.maxOf { it.weight }
        val range = if (maxWeight - minWeight == 0.0) 1.0 else maxWeight - minWeight

        for (index in bars.indices) {
            if (index < weightList.size) {
                val item = weightList[index]

                val percent = (item.weight - minWeight) / range
                val height = 55 + (percent * 100).roundToInt()

                val params = bars[index].layoutParams
                params.height = height
                bars[index].layoutParams = params

                labels[index].text = item.date
                bars[index].visibility = View.VISIBLE
                labels[index].visibility = View.VISIBLE
            } else {
                bars[index].visibility = View.INVISIBLE
                labels[index].visibility = View.INVISIBLE
            }
        }
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

    private fun showCalendarSection() {
        sectionCalendar.visibility = View.VISIBLE
        sectionData.visibility = View.GONE

        txtTabCalendar.setTextColor(0xFF111111.toInt())
        txtTabWeight.setTextColor(0xFF999999.toInt())
        lineCalendar.setBackgroundColor(0xFF111111.toInt())
        lineWeight.setBackgroundColor(0x00FFFFFF)
    }

    private fun showDataSection() {
        sectionCalendar.visibility = View.GONE
        sectionData.visibility = View.VISIBLE

        txtTabCalendar.setTextColor(0xFF999999.toInt())
        txtTabWeight.setTextColor(0xFF111111.toInt())
        lineCalendar.setBackgroundColor(0x00FFFFFF)
        lineWeight.setBackgroundColor(0xFF111111.toInt())
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
            if (parsedDate != null) outputFormat.format(parsedDate) else date
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
}