package com.example.fitnessmobileapp.ui.profile

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.fitnessmobileapp.R
import java.util.Locale

class ProfileFragment : Fragment() {

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

    private lateinit var txtProfileBMI: TextView
    private lateinit var txtProfileGoal: TextView
    private lateinit var txtProfileAdvice: TextView

    private var isMetric = true
    private var height = 165
    private var weight = 65.0
    private var targetWeight = 65.0
    private var gender = "Nữ"
    private var birthDate = "1995-01-01"
    private var targetPart = "Mông"
    private var level = "Trung bình"
    private var trainingDays = 3

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val btnBack = view.findViewById<TextView>(R.id.btnBackProfile)
        btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        btnMetric = view.findViewById(R.id.btnMetric)
        btnImperial = view.findViewById(R.id.btnImperial)

        txtHeight = view.findViewById(R.id.txtHeight)
        txtWeight = view.findViewById(R.id.txtWeight)
        txtTargetWeight = view.findViewById(R.id.txtTargetWeight)
        txtGender = view.findViewById(R.id.txtGender)
        txtBirthDate = view.findViewById(R.id.txtBirthday)
        txtTargetPart = view.findViewById(R.id.txtTargetPart)
        txtLevel = view.findViewById(R.id.txtWorkoutLevel)
        txtTrainingDays = view.findViewById(R.id.txtWorkoutDays)

        txtProfileBMI = view.findViewById(R.id.txtProfileBMI)
        txtProfileGoal = view.findViewById(R.id.txtProfileGoal)
        txtProfileAdvice = view.findViewById(R.id.txtProfileAdvice)

        loadProfile()
        updateUI()

        btnMetric.setOnClickListener {
            isMetric = true
            updateUI()
            saveProfile()
        }

        btnImperial.setOnClickListener {
            isMetric = false
            updateUI()
            saveProfile()
        }

        view.findViewById<View>(R.id.layoutHeight).setOnClickListener {
            showHeightDialog()
        }

        view.findViewById<View>(R.id.layoutWeight).setOnClickListener {
            showWeightDialog()
        }

        view.findViewById<View>(R.id.layoutTargetWeight).setOnClickListener {
            showTargetWeightDialog()
        }

        view.findViewById<View>(R.id.layoutGender).setOnClickListener {
            showGenderDialog()
        }

        view.findViewById<View>(R.id.layoutBirthDate).setOnClickListener {
            showBirthDateDialog()
        }

        view.findViewById<View>(R.id.layoutTargetPart).setOnClickListener {
            showTargetPartDialog()
        }

        view.findViewById<View>(R.id.layoutLevel).setOnClickListener {
            showLevelDialog()
        }

        view.findViewById<View>(R.id.layoutTrainingDays).setOnClickListener {
            showTrainingDaysDialog()
        }

        return view
    }

    private fun updateUI() {
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

        val heightM = height / 100.0
        val bmi = weight / (heightM * heightM)

        val bmiStatus = when {
            bmi < 18.5 -> "Gầy"
            bmi < 25 -> "Bình thường"
            bmi < 30 -> "Thừa cân"
            else -> "Béo phì"
        }

        txtProfileBMI.text = "BMI hiện tại: %.1f - %s".format(Locale.US, bmi, bmiStatus)

        val goalDiff = weight - targetWeight
        txtProfileGoal.text = when {
            goalDiff > 0 -> "Mục tiêu: Giảm %.1f kg".format(Locale.US, goalDiff)
            goalDiff < 0 -> "Mục tiêu: Tăng %.1f kg".format(Locale.US, -goalDiff)
            else -> "Mục tiêu: Duy trì cân nặng"
        }

        val nextTrainingDay = (trainingDays + 1).coerceAtMost(7)
        txtProfileAdvice.text =
            "Gợi ý: Duy trì tập luyện $trainingDays - $nextTrainingDay buổi mỗi tuần."
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
            }
            .show()
    }

    private fun showWeightDialog() {
        showDecimalPicker("Cân nặng", weight) {
            weight = it
            updateUI()
            saveProfile()
        }
    }

    private fun showTargetWeightDialog() {
        showDecimalPicker("Cân nặng mục tiêu", targetWeight) {
            targetWeight = it
            updateUI()
            saveProfile()
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
        val checkedIndex = options.indexOf(gender)

        AlertDialog.Builder(context)
            .setTitle("Giới tính")
            .setSingleChoiceItems(options, checkedIndex) { dialog, which ->
                gender = options[which]
                updateUI()
                saveProfile()
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
            },
            year,
            month,
            day
        ).show()
    }

    private fun showTargetPartDialog() {
        val context = requireContext()
        val options = arrayOf("Cánh tay", "Bụng", "Mông", "Chân")
        val checkedIndex = options.indexOf(targetPart)

        AlertDialog.Builder(context)
            .setTitle("Bộ phận mục tiêu")
            .setSingleChoiceItems(options, checkedIndex) { dialog, which ->
                targetPart = options[which]
                updateUI()
                saveProfile()
                dialog.dismiss()
            }
            .show()
    }

    private fun showLevelDialog() {
        val context = requireContext()
        val options = arrayOf("Người bắt đầu", "Trung bình", "Nâng cao")
        val checkedIndex = options.indexOf(level)

        AlertDialog.Builder(context)
            .setTitle("Mức tập luyện")
            .setSingleChoiceItems(options, checkedIndex) { dialog, which ->
                level = options[which]
                updateUI()
                saveProfile()
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
                dialog.dismiss()
            }
            .show()
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