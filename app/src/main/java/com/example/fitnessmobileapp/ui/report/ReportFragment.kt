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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class ReportFragment : Fragment() {

    private lateinit var tabCalendar: LinearLayout
    private lateinit var tabWeight: LinearLayout

    private lateinit var lineCalendar: View
    private lateinit var lineWeight: View

    private lateinit var sectionCalendar: LinearLayout
    private lateinit var sectionWeight: LinearLayout

    private lateinit var txtTotalDays: TextView
    private lateinit var txtTotalCalories: TextView
    private lateinit var txtTotalMinutes: TextView

    private lateinit var calendarView: CalendarView
    private lateinit var txtSelectedDate: TextView
    private lateinit var txtWorkoutHistory: TextView

    private lateinit var btnAddWeight: TextView
    private lateinit var txtWeightInfo: TextView
    private lateinit var txtBMIValue: TextView
    private lateinit var txtBMIStatus: TextView
    private lateinit var bmiScaleContainer: View

    private lateinit var barWeight1: View
    private lateinit var barWeight2: View
    private lateinit var barWeight3: View
    private lateinit var barWeight4: View

    private lateinit var txtWeightDate1: TextView
    private lateinit var txtWeightDate2: TextView
    private lateinit var txtWeightDate3: TextView
    private lateinit var txtWeightDate4: TextView

    private val height = 1.70

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_report, container, false)

        tabCalendar = view.findViewById(R.id.tabCalendar)
        tabWeight = view.findViewById(R.id.tabWeight)

        lineCalendar = view.findViewById(R.id.lineCalendar)
        lineWeight = view.findViewById(R.id.lineWeight)

        sectionCalendar = view.findViewById(R.id.sectionCalendar)
        sectionWeight = view.findViewById(R.id.sectionWeight)

        txtTotalDays = view.findViewById(R.id.txtTotalDays)
        txtTotalCalories = view.findViewById(R.id.txtTotalCalories)
        txtTotalMinutes = view.findViewById(R.id.txtTotalMinutes)

        calendarView = view.findViewById(R.id.calendarView)
        txtSelectedDate = view.findViewById(R.id.txtSelectedDate)
        txtWorkoutHistory = view.findViewById(R.id.txtWorkoutHistory)

        btnAddWeight = view.findViewById(R.id.btnAddWeight)
        txtWeightInfo = view.findViewById(R.id.txtWeightInfo)
        txtBMIValue = view.findViewById(R.id.txtBMIValue)
        txtBMIStatus = view.findViewById(R.id.txtBMIStatus)
        bmiScaleContainer = view.findViewById(R.id.bmiScaleContainer)

        barWeight1 = view.findViewById(R.id.barWeight1)
        barWeight2 = view.findViewById(R.id.barWeight2)
        barWeight3 = view.findViewById(R.id.barWeight3)
        barWeight4 = view.findViewById(R.id.barWeight4)

        txtWeightDate1 = view.findViewById(R.id.txtWeightDate1)
        txtWeightDate2 = view.findViewById(R.id.txtWeightDate2)
        txtWeightDate3 = view.findViewById(R.id.txtWeightDate3)
        txtWeightDate4 = view.findViewById(R.id.txtWeightDate4)

        showSummaryData()
        showCalendarSection()
        showTodayHistory()
        updateWeightInfo()
        updateWeightChart()

        tabCalendar.setOnClickListener {
            showCalendarSection()
        }

        tabWeight.setOnClickListener {
            showWeightSection()
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            txtSelectedDate.text = "Lịch tập ngày $selectedDate"
            showWorkoutHistory(selectedDate)
        }

        btnAddWeight.setOnClickListener {
            showAddWeightDialog()
        }

        return view
    }

    private fun showSummaryData() {
        txtTotalDays.text = ReportData.getTotalWorkoutDays().toString()
        txtTotalCalories.text = "%.1f".format(ReportData.getTotalCalories())
        txtTotalMinutes.text = ReportData.getWorkoutMinutes().toString()
    }

    private fun showTodayHistory() {
        val today = SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(Date())
        txtSelectedDate.text = "Lịch tập ngày $today"
        showWorkoutHistory(today)
    }

    private fun showWorkoutHistory(date: String) {
        val historyList = ReportData.getWorkoutHistoryByDate(date)

        if (historyList.isEmpty()) {
            txtWorkoutHistory.text = "Ngày này chưa có dữ liệu tập luyện."
            return
        }

        val result = StringBuilder()

        for (item in historyList) {
            result.append("• ${item.exerciseName}\n")
            result.append("  Thời gian: ${item.time}\n")
            result.append("  Thời lượng: ${item.duration}\n")
            result.append("  Calo: ${item.calories} Calo\n\n")
        }

        txtWorkoutHistory.text = result.toString()
    }

    private fun showAddWeightDialog() {
        val input = EditText(requireContext())
        input.hint = "Nhập cân nặng, ví dụ: 75.5"
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

        AlertDialog.Builder(requireContext())
            .setTitle("Thêm cân nặng mới")
            .setMessage("Nhập cân nặng hôm nay")
            .setView(input)
            .setPositiveButton("Lưu") { _, _ ->
                val weightText = input.text.toString().trim()

                if (weightText.isEmpty()) {
                    Toast.makeText(requireContext(), "Vui lòng nhập cân nặng", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val weight = weightText.toDoubleOrNull()

                if (weight == null || weight <= 0) {
                    Toast.makeText(requireContext(), "Cân nặng không hợp lệ", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                ReportData.addNewWeight(weight)

                updateWeightInfo()
                updateWeightChart()

                Toast.makeText(requireContext(), "Đã thêm cân nặng mới", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun updateWeightInfo() {
        val currentWeight = ReportData.getCurrentWeight()
        val oldWeight = ReportData.getOldWeight()
        val changeWeight = ReportData.getWeightChange()

        val changeText = when {
            changeWeight < 0 -> "Đã giảm: %.1f kg".format(-changeWeight)
            changeWeight > 0 -> "Đã tăng: %.1f kg".format(changeWeight)
            else -> "Không thay đổi"
        }

        txtWeightInfo.text =
            "Hiện tại: %.1f kg\n30 ngày trước: %.1f kg\n%s"
                .format(currentWeight, oldWeight, changeText)

        val bmi = currentWeight / (height * height)
        txtBMIValue.text = "%.1f".format(bmi)
        updateBMIPosition(bmi)

        val status = when {
            bmi < 18.5 -> "●  Gầy"
            bmi < 25 -> "●  Bình thường"
            bmi < 30 -> "●  Thừa cân"
            else -> "●  Béo phì"
        }

        txtBMIStatus.text = status
    }

    private fun updateBMIPosition(bmi: Double) {
        bmiScaleContainer.post {
            val minBMI = 15.0
            val maxBMI = 40.0

            val fixedBMI = bmi.coerceIn(minBMI, maxBMI)
            val percent = (fixedBMI - minBMI) / (maxBMI - minBMI)

            val maxMove = bmiScaleContainer.width - txtBMIValue.width
            val positionX = percent * maxMove

            txtBMIValue.translationX = positionX.toFloat()
        }
    }

    private fun updateWeightChart() {
        val weightRecords = ReportData.getWeightRecords()

        val bars = listOf(barWeight1, barWeight2, barWeight3, barWeight4)
        val labels = listOf(txtWeightDate1, txtWeightDate2, txtWeightDate3, txtWeightDate4)

        if (weightRecords.isEmpty()) return

        val minWeight = weightRecords.minOf { it.weight }
        val maxWeight = weightRecords.maxOf { it.weight }
        val range = if (maxWeight - minWeight == 0.0) 1.0 else maxWeight - minWeight

        for (i in bars.indices) {
            if (i < weightRecords.size) {
                val item = weightRecords[i]

                val percent = (item.weight - minWeight) / range
                val barHeight = 70 + (percent * 80).roundToInt()

                val params = bars[i].layoutParams
                params.height = barHeight
                bars[i].layoutParams = params

                labels[i].text = item.date

                bars[i].visibility = View.VISIBLE
                labels[i].visibility = View.VISIBLE
            } else {
                bars[i].visibility = View.INVISIBLE
                labels[i].visibility = View.INVISIBLE
            }
        }
    }

    private fun showCalendarSection() {
        sectionCalendar.visibility = View.VISIBLE
        sectionWeight.visibility = View.GONE

        lineCalendar.setBackgroundColor(0xFFF6E96B.toInt())
        lineWeight.setBackgroundColor(0x00FFFFFF)
    }

    private fun showWeightSection() {
        sectionCalendar.visibility = View.GONE
        sectionWeight.visibility = View.VISIBLE

        lineCalendar.setBackgroundColor(0x00FFFFFF)
        lineWeight.setBackgroundColor(0xFFF6E96B.toInt())
    }
}