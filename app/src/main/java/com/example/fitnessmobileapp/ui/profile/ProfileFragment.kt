package com.example.fitnessmobileapp.ui.profile

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fitnessmobileapp.R
import java.util.Locale

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var btnMetric: TextView
    private lateinit var btnImperial: TextView

    private lateinit var txtHeight: TextView
    private lateinit var txtWeight: TextView
    private lateinit var txtTargetWeight: TextView
    private lateinit var txtGender: TextView
    private lateinit var txtBirthDate: TextView
    private lateinit var txtTargetPart: TextView
    private lateinit var txtLevel: TextView
    private lateinit var txtTrainingDays: TextView

    private var isMetric = true
    private var height = 165
    private var weight = 65.0
    private var targetWeight = 65.0
    private var gender = "Nữ"
    private var birthDate = "1995-01-01"
    private var targetPart = "Mông"
    private var level = "Trung bình"
    private var trainingDays = 3

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfile()

        view.findViewById<View>(R.id.btnHelp).setOnClickListener {
            Toast.makeText(requireContext(), "Trợ giúp", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.itemMyProfile).setOnClickListener {
            showProfileMenuDialog()
        }

        view.findViewById<View>(R.id.itemReminder).setOnClickListener {
            Toast.makeText(requireContext(), "Nhắc nhở", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.itemSound).setOnClickListener {
            Toast.makeText(requireContext(), "Lựa chọn âm thanh", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.itemResetProgress).setOnClickListener {
            showResetProgressDialog()
        }

        view.findViewById<View>(R.id.itemDeleteAllData).setOnClickListener {
            showDeleteAllDataDialog()
        }

        view.findViewById<View>(R.id.itemFeedback).setOnClickListener {
            Toast.makeText(requireContext(), "Ý kiến phản hồi", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.itemFAQ).setOnClickListener {
            Toast.makeText(requireContext(), "Câu hỏi thường gặp", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showProfileMenuDialog() {
        val options = arrayOf(
            "Chiều cao: ${getHeightText()}",
            "Cân nặng: ${getWeightText()}",
            "Cân nặng mục tiêu: ${getTargetWeightText()}",
            "Giới tính: $gender",
            "Ngày sinh: $birthDate",
            "Bộ phận mục tiêu: $targetPart",
            "Mức tập luyện: $level",
            "Ngày tập luyện: $trainingDays/7"
        )

        AlertDialog.Builder(requireContext())
            .setTitle("Hồ sơ của tôi")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showHeightDialog()
                    1 -> showWeightDialog()
                    2 -> showTargetWeightDialog()
                    3 -> showGenderDialog()
                    4 -> showBirthDateDialog()
                    5 -> showTargetPartDialog()
                    6 -> showLevelDialog()
                    7 -> showTrainingDaysDialog()
                }
            }
            .setNegativeButton("ĐÓNG", null)
            .show()
    }

    private fun showResetProgressDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Đặt lại tiến độ")
            .setMessage("Bạn có chắc muốn đặt lại tiến độ tập luyện không?")
            .setNegativeButton("HỦY", null)
            .setPositiveButton("ĐỒNG Ý") { _, _ ->
                Toast.makeText(requireContext(), "Đã đặt lại tiến độ", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun showDeleteAllDataDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa tất cả dữ liệu")
            .setMessage("Bạn có chắc muốn xóa tất cả dữ liệu không?")
            .setNegativeButton("HỦY", null)
            .setPositiveButton("XÓA") { _, _ ->
                Toast.makeText(requireContext(), "Đã xóa dữ liệu", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun updateUI() {
        if (
            !::btnMetric.isInitialized ||
            !::btnImperial.isInitialized ||
            !::txtHeight.isInitialized ||
            !::txtWeight.isInitialized ||
            !::txtTargetWeight.isInitialized ||
            !::txtGender.isInitialized ||
            !::txtBirthDate.isInitialized ||
            !::txtTargetPart.isInitialized ||
            !::txtLevel.isInitialized ||
            !::txtTrainingDays.isInitialized
        ) {
            return
        }

        if (isMetric) {
            txtHeight.text = "$height cm"
            txtWeight.text = String.format(Locale.US, "%.1f kg", weight)
            txtTargetWeight.text = String.format(Locale.US, "%.1f kg", targetWeight)

            btnMetric.setBackgroundResource(R.drawable.bg_unit_selected)
            btnImperial.setBackgroundResource(R.drawable.bg_unit_unselected)
        } else {
            txtHeight.text = convertCmToFtIn(height)
            txtWeight.text = String.format(Locale.US, "%.1f lb", kgToLb(weight))
            txtTargetWeight.text = String.format(Locale.US, "%.1f lb", kgToLb(targetWeight))

            btnMetric.setBackgroundResource(R.drawable.bg_unit_unselected)
            btnImperial.setBackgroundResource(R.drawable.bg_unit_selected)
        }

        txtGender.text = gender
        txtBirthDate.text = birthDate
        txtTargetPart.text = targetPart
        txtLevel.text = level
        txtTrainingDays.text = "$trainingDays/7"
    }

    private fun saveProfile() {
        val prefs = requireActivity().getSharedPreferences("profile_data", Context.MODE_PRIVATE)

        prefs.edit()
            .putBoolean("isMetric", isMetric)
            .putInt("height", height)
            .putFloat("weight", weight.toFloat())
            .putFloat("targetWeight", targetWeight.toFloat())
            .putString("gender", gender)
            .putString("birthDate", birthDate)
            .putString("targetPart", targetPart)
            .putString("level", level)
            .putInt("trainingDays", trainingDays)
            .apply()
    }

    private fun loadProfile() {
        val prefs = requireActivity().getSharedPreferences("profile_data", Context.MODE_PRIVATE)

        isMetric = prefs.getBoolean("isMetric", true)
        height = prefs.getInt("height", 165)
        weight = prefs.getFloat("weight", 65f).toDouble()
        targetWeight = prefs.getFloat("targetWeight", 65f).toDouble()
        gender = prefs.getString("gender", "Nữ") ?: "Nữ"
        birthDate = prefs.getString("birthDate", "1995-01-01") ?: "1995-01-01"
        targetPart = prefs.getString("targetPart", "Mông") ?: "Mông"
        level = prefs.getString("level", "Trung bình") ?: "Trung bình"
        trainingDays = prefs.getInt("trainingDays", 3).coerceIn(1, 7)
    }

    private fun showHeightDialog() {
        val context = requireContext()

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(40, 30, 40, 10)
        }

        val heightPicker = NumberPicker(context).apply {
            minValue = 100
            maxValue = 220
            value = height
        }

        val unitPicker = NumberPicker(context).apply {
            minValue = 0
            maxValue = 1
            displayedValues = arrayOf("cm", "ft+in")
            value = if (isMetric) 0 else 1
        }

        container.addView(heightPicker)
        container.addView(unitPicker)

        AlertDialog.Builder(context)
            .setTitle("Chiều cao")
            .setView(container)
            .setNegativeButton("HỦY", null)
            .setPositiveButton("LƯU") { _, _ ->
                height = heightPicker.value
                isMetric = unitPicker.value == 0
                updateUI()
                saveProfile()
                Toast.makeText(requireContext(), "Đã lưu chiều cao", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun showWeightDialog() {
        showDecimalPicker("Cân nặng", weight) {
            weight = it
            updateUI()
            saveProfile()
            Toast.makeText(requireContext(), "Đã lưu cân nặng", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showTargetWeightDialog() {
        showDecimalPicker("Cân nặng mục tiêu", targetWeight) {
            targetWeight = it
            updateUI()
            saveProfile()
            Toast.makeText(requireContext(), "Đã lưu cân nặng mục tiêu", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDecimalPicker(
        title: String,
        currentValue: Double,
        onSave: (Double) -> Unit
    ) {
        val context = requireContext()

        val displayValue = if (isMetric) currentValue else kgToLb(currentValue)

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(40, 30, 40, 10)
        }

        val numberPicker = NumberPicker(context).apply {
            minValue = 30
            maxValue = 250
            value = displayValue.toInt().coerceIn(minValue, maxValue)
        }

        val decimalPicker = NumberPicker(context).apply {
            minValue = 0
            maxValue = 9
            value = (((displayValue - displayValue.toInt()) * 10).toInt()).coerceIn(0, 9)
        }

        val unitPicker = NumberPicker(context).apply {
            minValue = 0
            maxValue = 1
            displayedValues = arrayOf("kg", "lb")
            value = if (isMetric) 0 else 1
        }

        container.addView(numberPicker)
        container.addView(decimalPicker)
        container.addView(unitPicker)

        AlertDialog.Builder(context)
            .setTitle(title)
            .setView(container)
            .setNegativeButton("HỦY", null)
            .setPositiveButton("LƯU") { _, _ ->
                val value = numberPicker.value + decimalPicker.value / 10.0

                isMetric = unitPicker.value == 0

                val finalValue = if (isMetric) {
                    value
                } else {
                    lbToKg(value)
                }

                onSave(finalValue)
            }
            .show()
    }

    private fun showGenderDialog() {
        val context = requireContext()
        val options = arrayOf("Nam", "Nữ")
        val checkedIndex = options.indexOf(gender).coerceAtLeast(0)

        AlertDialog.Builder(context)
            .setTitle("Giới tính")
            .setSingleChoiceItems(options, checkedIndex) { dialog, which ->
                gender = options[which]
                updateUI()
                saveProfile()
                Toast.makeText(requireContext(), "Đã lưu giới tính", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .show()
    }

    private fun showBirthDateDialog() {
        val parts = birthDate.split("-")

        val year = parts.getOrNull(0)?.toIntOrNull() ?: 1995
        val month = parts.getOrNull(1)?.toIntOrNull()?.minus(1) ?: 0
        val day = parts.getOrNull(2)?.toIntOrNull() ?: 1

        DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                birthDate = String.format(
                    Locale.US,
                    "%04d-%02d-%02d",
                    selectedYear,
                    selectedMonth + 1,
                    selectedDay
                )

                updateUI()
                saveProfile()
                Toast.makeText(requireContext(), "Đã lưu ngày sinh", Toast.LENGTH_SHORT).show()
            },
            year,
            month,
            day
        ).show()
    }

    private fun showTargetPartDialog() {
        val context = requireContext()
        val options = arrayOf("Cánh tay", "Bụng", "Mông", "Chân")
        val checkedIndex = options.indexOf(targetPart).coerceAtLeast(0)

        AlertDialog.Builder(context)
            .setTitle("Bộ phận mục tiêu")
            .setSingleChoiceItems(options, checkedIndex) { dialog, which ->
                targetPart = options[which]
                updateUI()
                saveProfile()
                Toast.makeText(requireContext(), "Đã lưu bộ phận mục tiêu", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .show()
    }

    private fun showLevelDialog() {
        val context = requireContext()
        val options = arrayOf("Người bắt đầu", "Trung bình", "Nâng cao")
        val checkedIndex = options.indexOf(level).coerceAtLeast(0)

        AlertDialog.Builder(context)
            .setTitle("Mức tập luyện")
            .setSingleChoiceItems(options, checkedIndex) { dialog, which ->
                level = options[which]
                updateUI()
                saveProfile()
                Toast.makeText(requireContext(), "Đã lưu mức tập luyện", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .show()
    }

    private fun showTrainingDaysDialog() {
        val context = requireContext()
        val options = arrayOf("1/7", "2/7", "3/7", "4/7", "5/7", "6/7", "7/7")
        val checkedIndex = (trainingDays - 1).coerceIn(0, options.lastIndex)

        AlertDialog.Builder(context)
            .setTitle("Ngày tập luyện")
            .setSingleChoiceItems(options, checkedIndex) { dialog, which ->
                trainingDays = which + 1
                updateUI()
                saveProfile()
                Toast.makeText(requireContext(), "Đã lưu ngày tập luyện", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .show()
    }

    private fun getHeightText(): String {
        return if (isMetric) {
            "$height cm"
        } else {
            convertCmToFtIn(height)
        }
    }

    private fun getWeightText(): String {
        return if (isMetric) {
            String.format(Locale.US, "%.1f kg", weight)
        } else {
            String.format(Locale.US, "%.1f lb", kgToLb(weight))
        }
    }

    private fun getTargetWeightText(): String {
        return if (isMetric) {
            String.format(Locale.US, "%.1f kg", targetWeight)
        } else {
            String.format(Locale.US, "%.1f lb", kgToLb(targetWeight))
        }
    }

    private fun kgToLb(kg: Double): Double {
        return kg * 2.20462
    }

    private fun lbToKg(lb: Double): Double {
        return lb / 2.20462
    }

    private fun convertCmToFtIn(cm: Int): String {
        val totalInches = cm / 2.54
        val feet = (totalInches / 12).toInt()
        val inches = (totalInches % 12).toInt()

        return "${feet}ft ${inches}in"
    }
}