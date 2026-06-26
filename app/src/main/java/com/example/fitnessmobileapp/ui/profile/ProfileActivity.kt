package com.example.fitnessmobileapp.ui.profile

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessmobileapp.R
import java.util.Locale

class ProfileActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        btnMetric = findViewById(R.id.btnMetric)
        btnImperial = findViewById(R.id.btnImperial)

        txtHeight = findViewById(R.id.txtHeight)
        txtWeight = findViewById(R.id.txtWeight)
        txtTargetWeight = findViewById(R.id.txtTargetWeight)
        txtGender = findViewById(R.id.txtGender)
        txtBirthDate = findViewById(R.id.txtBirthDate)
        txtTargetPart = findViewById(R.id.txtTargetPart)
        txtLevel = findViewById(R.id.txtLevel)
        txtTrainingDays = findViewById(R.id.txtTrainingDays)

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

        findViewById<RelativeLayout>(R.id.layoutHeight).setOnClickListener {
            showHeightDialog()
        }

        findViewById<RelativeLayout>(R.id.layoutWeight).setOnClickListener {
            showWeightDialog()
        }

        findViewById<RelativeLayout>(R.id.layoutTargetWeight).setOnClickListener {
            showTargetWeightDialog()
        }

        findViewById<RelativeLayout>(R.id.layoutGender).setOnClickListener {
            showGenderDialog()
        }

        findViewById<RelativeLayout>(R.id.layoutBirthDate).setOnClickListener {
            showBirthDateDialog()
        }

        findViewById<RelativeLayout>(R.id.layoutTargetPart).setOnClickListener {
            showTargetPartDialog()
        }

        findViewById<RelativeLayout>(R.id.layoutLevel).setOnClickListener {
            showLevelDialog()
        }

        findViewById<RelativeLayout>(R.id.layoutTrainingDays).setOnClickListener {
            showTrainingDaysDialog()
        }
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
    }

    private fun saveProfile() {
        val prefs = getSharedPreferences("profile_data", MODE_PRIVATE)

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
        val prefs = getSharedPreferences("profile_data", MODE_PRIVATE)

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
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(40, 30, 40, 10)
        }

        val heightPicker = NumberPicker(this).apply {
            minValue = 100
            maxValue = 220
            value = height.coerceIn(minValue, maxValue)
        }

        val unitPicker = NumberPicker(this).apply {
            minValue = 0
            maxValue = 1
            displayedValues = arrayOf("cm", "ft+in")
            value = if (isMetric) 0 else 1
        }

        container.addView(heightPicker)
        container.addView(unitPicker)

        AlertDialog.Builder(this)
            .setTitle("Chiều cao")
            .setView(container)
            .setNegativeButton("HỦY", null)
            .setPositiveButton("LƯU") { _, _ ->
                height = heightPicker.value
                isMetric = unitPicker.value == 0
                updateUI()
                saveProfile()
                Toast.makeText(this, "Đã lưu chiều cao", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun showWeightDialog() {
        showDecimalPicker("Cân nặng", weight) {
            weight = it
            updateUI()
            saveProfile()
            Toast.makeText(this, "Đã lưu cân nặng", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showTargetWeightDialog() {
        showDecimalPicker("Cân nặng mục tiêu", targetWeight) {
            targetWeight = it
            updateUI()
            saveProfile()
            Toast.makeText(this, "Đã lưu cân nặng mục tiêu", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDecimalPicker(
        title: String,
        currentValue: Double,
        onSave: (Double) -> Unit
    ) {
        val displayValue = if (isMetric) currentValue else kgToLb(currentValue)

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(40, 30, 40, 10)
        }

        val numberPicker = NumberPicker(this).apply {
            minValue = 30
            maxValue = 250
            value = displayValue.toInt().coerceIn(minValue, maxValue)
        }

        val decimalPicker = NumberPicker(this).apply {
            minValue = 0
            maxValue = 9
            value = (((displayValue - displayValue.toInt()) * 10).toInt()).coerceIn(0, 9)
        }

        val unitPicker = NumberPicker(this).apply {
            minValue = 0
            maxValue = 1
            displayedValues = arrayOf("kg", "lb")
            value = if (isMetric) 0 else 1
        }

        container.addView(numberPicker)
        container.addView(decimalPicker)
        container.addView(unitPicker)

        AlertDialog.Builder(this)
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
        val options = arrayOf("Nam", "Nữ")
        val checkedIndex = options.indexOf(gender).coerceAtLeast(0)

        AlertDialog.Builder(this)
            .setTitle("Giới tính")
            .setSingleChoiceItems(options, checkedIndex) { dialog, which ->
                gender = options[which]
                updateUI()
                saveProfile()
                Toast.makeText(this, "Đã lưu giới tính", Toast.LENGTH_SHORT).show()
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
            this,
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
                Toast.makeText(this, "Đã lưu ngày sinh", Toast.LENGTH_SHORT).show()
            },
            year,
            month,
            day
        ).show()
    }

    private fun showTargetPartDialog() {
        val options = arrayOf("Cánh tay", "Bụng", "Mông", "Chân")
        val checkedIndex = options.indexOf(targetPart).coerceAtLeast(0)

        AlertDialog.Builder(this)
            .setTitle("Bộ phận mục tiêu")
            .setSingleChoiceItems(options, checkedIndex) { dialog, which ->
                targetPart = options[which]
                updateUI()
                saveProfile()
                Toast.makeText(this, "Đã lưu bộ phận mục tiêu", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .show()
    }

    private fun showLevelDialog() {
        val options = arrayOf("Người bắt đầu", "Trung bình", "Nâng cao")
        val checkedIndex = options.indexOf(level).coerceAtLeast(0)

        AlertDialog.Builder(this)
            .setTitle("Mức tập luyện")
            .setSingleChoiceItems(options, checkedIndex) { dialog, which ->
                level = options[which]
                updateUI()
                saveProfile()
                Toast.makeText(this, "Đã lưu mức tập luyện", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .show()
    }

    private fun showTrainingDaysDialog() {
        val options = arrayOf("1/7", "2/7", "3/7", "4/7", "5/7", "6/7", "7/7")
        val checkedIndex = (trainingDays - 1).coerceIn(0, options.lastIndex)

        AlertDialog.Builder(this)
            .setTitle("Ngày tập luyện")
            .setSingleChoiceItems(options, checkedIndex) { dialog, which ->
                trainingDays = which + 1
                updateUI()
                saveProfile()
                Toast.makeText(this, "Đã lưu ngày tập luyện", Toast.LENGTH_SHORT).show()
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
