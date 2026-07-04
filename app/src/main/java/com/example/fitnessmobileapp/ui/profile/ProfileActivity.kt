package com.example.fitnessmobileapp.ui.profile

import android.app.AlertDialog
import android.content.SharedPreferences
import android.graphics.Color
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
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import java.text.SimpleDateFormat
import java.util.Date

class ProfileActivity : AppCompatActivity() {

    private lateinit var btnMetric: TextView
    private lateinit var btnImperial: TextView

    private lateinit var txtHeight: TextView
    private lateinit var txtWeight: TextView
    private lateinit var txtTargetWeight: TextView
    private lateinit var txtGender: TextView
    private lateinit var txtBirthDate: TextView

    private var isMetric = true
    private var height = 165
    private var weight = 65.0
    private var targetWeight = 65.0
    private var gender = "Nữ"
    private var birthYear = 1995

    // Chức năng: khởi tạo màn hình Hồ sơ của tôi và gắn sự kiện bấm.
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

        setupClickEvents()

        loadProfile()
        updateUI()
    }

    // Chức năng: mỗi lần quay lại màn Hồ sơ thì load lại dữ liệu mới nhất.
    // Nhờ vậy sau khi đổi kg bên Báo cáo, quay lại Hồ sơ sẽ thấy dữ liệu mới.
    override fun onResume() {
        super.onResume()
        loadProfile()
        updateUI()
    }

    // Chức năng: gắn sự kiện bấm cho các dòng trong hồ sơ.
    private fun setupClickEvents() {
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
            showBirthYearDialog()
        }
    }

    // Chức năng: cập nhật dữ liệu lên giao diện theo đơn vị kg/cm hoặc lb/ft.
    private fun updateUI() {
        if (isMetric) {
            txtHeight.text = "$height cm"
            txtWeight.text = String.format(Locale.US, "%.1f kg", weight)
            txtTargetWeight.text = String.format(Locale.US, "%.1f kg", targetWeight)

            btnMetric.setBackgroundResource(R.drawable.bg_unit_selected)
            btnImperial.setBackgroundResource(R.drawable.bg_unit_unselected)

            btnMetric.setTextColor(Color.parseColor("#111111"))
            btnImperial.setTextColor(Color.parseColor("#777777"))
        } else {
            txtHeight.text = convertCmToFtIn(height)
            txtWeight.text = String.format(Locale.US, "%.1f lb", kgToLb(weight))
            txtTargetWeight.text = String.format(Locale.US, "%.1f lb", kgToLb(targetWeight))

            btnMetric.setBackgroundResource(R.drawable.bg_unit_unselected)
            btnImperial.setBackgroundResource(R.drawable.bg_unit_selected)

            btnMetric.setTextColor(Color.parseColor("#777777"))
            btnImperial.setTextColor(Color.parseColor("#111111"))
        }

        txtGender.text = gender
        txtBirthDate.text = birthYear.toString()
    }

    // Chức năng: lấy tên tài khoản hiện tại để đọc/lưu hồ sơ riêng theo từng user.
    private fun getCurrentUsername(): String {
        val loginPrefs = getSharedPreferences("login_data", MODE_PRIVATE)
        return loginPrefs.getString("current_user", "guest") ?: "guest"
    }

    // Chức năng: lấy SharedPreferences hồ sơ theo từng tài khoản.
    private fun getUserProfilePrefs(): SharedPreferences {
        return getSharedPreferences("user_${getCurrentUsername()}_profile", MODE_PRIVATE)
    }

    // Chức năng: lưu hồ sơ cá nhân.
    // Khi đổi cân nặng hoặc chiều cao ở Hồ sơ thì đồng bộ sang Báo cáo và lộ trình tập.
    private fun saveProfile(recalculatePlan: Boolean = false) {
        val legacyPrefs = getSharedPreferences("profile_data", MODE_PRIVATE)
        val userPrefs = getUserProfilePrefs()

        val heightM = height / 100.0
        val currentBMI = if (heightM > 0) {
            weight / (heightM * heightM)
        } else {
            0.0
        }

        val planLevel = getPlanLevelByBodyData(currentBMI)

        val legacyEditor = legacyPrefs.edit()
            .putBoolean("isMetric", isMetric)
            .putFloat("height", height.toFloat())
            .putFloat("weight", weight.toFloat())
            .putFloat("currentWeight", weight.toFloat())
            .putFloat("targetWeight", targetWeight.toFloat())
            .putFloat("currentBMI", currentBMI.toFloat())
            .putString("planLevel", planLevel)
            .putString("gender", gender)
            .putInt("birthYear", birthYear)
            .putString("birthDate", "$birthYear-01-01")

        val userEditor = userPrefs.edit()
            .putBoolean("isMetric", isMetric)
            .putFloat("height", height.toFloat())
            .putFloat("weight", weight.toFloat())
            .putFloat("currentWeight", weight.toFloat())
            .putFloat("targetWeight", targetWeight.toFloat())
            .putFloat("currentBMI", currentBMI.toFloat())
            .putString("planLevel", planLevel)
            .putString("gender", gender)
            .putInt("birthYear", birthYear)
            .putString("birthDate", "$birthYear-01-01")

        if (recalculatePlan) {
            val now = System.currentTimeMillis()

            legacyEditor.putLong("planRecalculatedAt", now)
            userEditor.putLong("planRecalculatedAt", now)
        }

        legacyEditor.apply()
        userEditor.apply()

        // Chức năng: nếu đổi cân nặng/chiều cao thì lưu thêm vào dữ liệu cân nặng của Báo cáo.
        if (recalculatePlan) {
            saveWeightReportRecord()
        }
    }

    // Chức năng: lấy hồ sơ đã lưu.
    // Đọc an toàn cả Int, Float, String để tránh crash khi dữ liệu từ Báo cáo lưu khác kiểu.
    private fun loadProfile() {
        val legacyPrefs = getSharedPreferences("profile_data", MODE_PRIVATE)
        val userPrefs = getUserProfilePrefs()

        isMetric = if (userPrefs.contains("isMetric")) {
            userPrefs.getBoolean("isMetric", true)
        } else {
            legacyPrefs.getBoolean("isMetric", true)
        }

        height = if (userPrefs.contains("height")) {
            getDoubleCompat(userPrefs, "height", 165.0).roundToInt()
        } else {
            getDoubleCompat(legacyPrefs, "height", 165.0).roundToInt()
        }

        weight = when {
            userPrefs.contains("currentWeight") -> {
                getDoubleCompat(userPrefs, "currentWeight", 65.0)
            }

            userPrefs.contains("weight") -> {
                getDoubleCompat(userPrefs, "weight", 65.0)
            }

            legacyPrefs.contains("currentWeight") -> {
                getDoubleCompat(legacyPrefs, "currentWeight", 65.0)
            }

            else -> {
                getDoubleCompat(legacyPrefs, "weight", 65.0)
            }
        }

        targetWeight = if (userPrefs.contains("targetWeight")) {
            getDoubleCompat(userPrefs, "targetWeight", 65.0)
        } else {
            getDoubleCompat(legacyPrefs, "targetWeight", 65.0)
        }

        gender = if (userPrefs.contains("gender")) {
            getStringCompat(userPrefs, "gender", "Nữ")
        } else {
            getStringCompat(legacyPrefs, "gender", "Nữ")
        }

        birthYear = when {
            userPrefs.contains("birthYear") -> {
                getIntCompat(userPrefs, "birthYear", 1995)
            }

            userPrefs.contains("birthDate") -> {
                getYearFromBirthDate(getStringCompat(userPrefs, "birthDate", "1995-01-01"))
            }

            legacyPrefs.contains("birthYear") -> {
                getIntCompat(legacyPrefs, "birthYear", 1995)
            }

            legacyPrefs.contains("birthDate") -> {
                getYearFromBirthDate(getStringCompat(legacyPrefs, "birthDate", "1995-01-01"))
            }

            else -> {
                1995
            }
        }
    }

    // Chức năng: đọc số dạng Double an toàn từ SharedPreferences.
    // Sửa lỗi crash khi cùng một key lúc thì lưu Int, lúc thì lưu Float.
    private fun getDoubleCompat(
        prefs: SharedPreferences,
        key: String,
        defaultValue: Double
    ): Double {
        if (!prefs.contains(key)) {
            return defaultValue
        }

        return try {
            prefs.getFloat(key, defaultValue.toFloat()).toDouble()
        } catch (firstException: ClassCastException) {
            try {
                prefs.getInt(key, defaultValue.toInt()).toDouble()
            } catch (secondException: ClassCastException) {
                try {
                    prefs.getString(key, defaultValue.toString())?.toDoubleOrNull()
                        ?: defaultValue
                } catch (thirdException: ClassCastException) {
                    defaultValue
                }
            }
        }
    }

    // Chức năng: đọc số Int an toàn từ SharedPreferences.
    private fun getIntCompat(
        prefs: SharedPreferences,
        key: String,
        defaultValue: Int
    ): Int {
        if (!prefs.contains(key)) {
            return defaultValue
        }

        return try {
            prefs.getInt(key, defaultValue)
        } catch (firstException: ClassCastException) {
            try {
                prefs.getFloat(key, defaultValue.toFloat()).roundToInt()
            } catch (secondException: ClassCastException) {
                try {
                    prefs.getString(key, defaultValue.toString())?.take(4)?.toIntOrNull()
                        ?: defaultValue
                } catch (thirdException: ClassCastException) {
                    defaultValue
                }
            }
        }
    }

    // Chức năng: đọc chuỗi an toàn từ SharedPreferences.
    private fun getStringCompat(
        prefs: SharedPreferences,
        key: String,
        defaultValue: String
    ): String {
        return try {
            prefs.getString(key, defaultValue) ?: defaultValue
        } catch (exception: ClassCastException) {
            defaultValue
        }
    }

    // Chức năng: lấy năm từ chuỗi ngày sinh cũ dạng yyyy-MM-dd.
    private fun getYearFromBirthDate(birthDate: String): Int {
        return birthDate.take(4).toIntOrNull() ?: 1995
    }

    // Chức năng: hiện hộp thoại chỉnh chiều cao.
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
                saveProfile(recalculatePlan = true)

                showRecalculatePlanDialog {
                    Toast.makeText(
                        this,
                        "Đã cập nhật BMI và tính lại lộ trình tập",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .show()
    }

    // Chức năng: hiện hộp thoại chỉnh cân nặng hiện tại.
    private fun showWeightDialog() {
        showDecimalPicker("Cân nặng", weight) {
            weight = it
            updateUI()
            saveProfile(recalculatePlan = true)

            showRecalculatePlanDialog {
                Toast.makeText(
                    this,
                    "Đã cập nhật BMI và tính lại lộ trình tập",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Chức năng: hiện hộp thoại chỉnh cân nặng mục tiêu.
    private fun showTargetWeightDialog() {
        showDecimalPicker("Cân nặng mục tiêu", targetWeight) {
            targetWeight = it
            updateUI()
            saveProfile()

            Toast.makeText(
                this,
                "Đã lưu cân nặng mục tiêu",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Chức năng: hiện NumberPicker cho các giá trị có phần thập phân như kg/lb.
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

    // Chức năng: hiện hộp thoại chọn giới tính.
    private fun showGenderDialog() {
        val options = arrayOf("Nam", "Nữ")
        val checkedIndex = options.indexOf(gender).coerceAtLeast(0)

        AlertDialog.Builder(this)
            .setTitle("Giới tính")
            .setSingleChoiceItems(options, checkedIndex) { dialog, which ->
                gender = options[which]
                updateUI()
                saveProfile()

                Toast.makeText(
                    this,
                    "Đã lưu giới tính",
                    Toast.LENGTH_SHORT
                ).show()

                dialog.dismiss()
            }
            .show()
    }

    // Chức năng: hiện hộp thoại chọn năm sinh.
    // Dữ liệu này lấy từ birthYear ban đầu của Onboarding.
    private fun showBirthYearDialog() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val yearPicker = NumberPicker(this).apply {
            minValue = 1900
            maxValue = currentYear
            value = birthYear.coerceIn(minValue, maxValue)
        }

        AlertDialog.Builder(this)
            .setTitle("Năm sinh")
            .setView(yearPicker)
            .setNegativeButton("HỦY", null)
            .setPositiveButton("LƯU") { _, _ ->
                birthYear = yearPicker.value
                updateUI()
                saveProfile()

                Toast.makeText(
                    this,
                    "Đã lưu năm sinh",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }

        // Chức năng: lấy SharedPreferences lưu lịch sử cân nặng bên màn Báo cáo.
        private fun getWeightReportPrefs() =
            getSharedPreferences("user_${getCurrentUsername()}_weight_report", MODE_PRIVATE)

        // Chức năng: khi đổi cân nặng ở Hồ sơ thì cập nhật luôn dữ liệu cân nặng bên Báo cáo.
        private fun saveWeightReportRecord() {
            val today = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date())
            val prefs = getWeightReportPrefs()
            val oldData = prefs.getString("records", "") ?: ""

            val records = oldData
                .split(";")
                .filter { it.isNotBlank() }
                .filterNot { item ->
                    item.substringBefore("|") == today
                }
                .toMutableList()

            records.add("$today|$weight")

            while (records.size > 7) {
                records.removeAt(0)
            }

            prefs.edit()
                .putString("records", records.joinToString(";"))
                .apply()
        }

        // Chức năng: dựa vào BMI để chọn nhóm lộ trình tập giống ReportFragment.
        private fun getPlanLevelByBodyData(bmi: Double): String {
            return when {
                bmi < 18.5 -> "underweight"
                bmi < 25.0 -> "normal"
                bmi < 30.0 -> "overweight"
                else -> "obese"
            }
        }

        // Chức năng: hiện loading tính lại lộ trình giống màn Báo cáo.
        private fun showRecalculatePlanDialog(onFinish: () -> Unit) {
            val normalFont = Typeface.create("sans-serif", Typeface.NORMAL)
            val mediumFont = Typeface.create("sans-serif-medium", Typeface.NORMAL)

            val container = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
                setPadding(dp(24), dp(30), dp(24), dp(28))
                background = GradientDrawable().apply {
                    setColor(Color.WHITE)
                    cornerRadius = dp(28).toFloat()
                }
            }

            val titleText = TextView(this).apply {
                text = "Đang tạo kế hoạch cá nhân"
                gravity = Gravity.CENTER
                setTextColor(Color.parseColor("#111111"))
                textSize = 22f
                typeface = mediumFont
                includeFontPadding = false
            }

            val descriptionText = TextView(this).apply {
                text = "Ứng dụng đang phân tích chỉ số cơ thể mới của bạn..."
                gravity = Gravity.CENTER
                setTextColor(Color.parseColor("#777777"))
                textSize = 15f
                typeface = normalFont
                includeFontPadding = false
                setPadding(0, dp(10), 0, dp(24))
            }

            val circleContainer = FrameLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    dp(170),
                    dp(170)
                )
            }

            val circleProgressView = RecalculateCircleProgressView(this)

            circleContainer.addView(
                circleProgressView,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    Gravity.CENTER
                )
            )

            val percentText = TextView(this).apply {
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

            val bottomText = TextView(this).apply {
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

            val dialog = AlertDialog.Builder(this)
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
            }

            dialog.show()
        }

        // Chức năng: đổi dp sang pixel.
        private fun dp(value: Int): Int {
            return (value * resources.displayMetrics.density).toInt()
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

    // Chức năng: đổi kg sang pound.
    private fun kgToLb(kg: Double): Double {
        return kg * 2.20462
    }

    // Chức năng: đổi pound sang kg.
    private fun lbToKg(lb: Double): Double {
        return lb / 2.20462
    }

    // Chức năng: đổi cm sang feet + inch.
    private fun convertCmToFtIn(cm: Int): String {
        val totalInches = cm / 2.54
        val feet = (totalInches / 12).toInt()
        val inches = (totalInches % 12).toInt()

        return "${feet}ft ${inches}in"
    }
}