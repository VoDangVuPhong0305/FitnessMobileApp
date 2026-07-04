package com.example.fitnessmobileapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import android.os.Handler
import android.os.Looper

class OnboardingActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnSkip: TextView
    private lateinit var btnNext: TextView
    private lateinit var progressFill: View

    private lateinit var txtQuestion: TextView
    private lateinit var txtDescription: TextView
    private lateinit var layoutContent: LinearLayout

    private lateinit var option1: TextView
    private lateinit var option2: TextView
    private lateinit var option3: TextView
    private lateinit var option4: TextView
    private lateinit var option5: TextView

    private lateinit var layoutCustomPicker: View
    private lateinit var txtPickerPrev: TextView
    private lateinit var txtPickerCurrent: TextView
    private lateinit var txtPickerNext: TextView

    private lateinit var txtUnit: TextView

    private lateinit var layoutBmi: View
    private lateinit var layoutBmiBar: View
    private lateinit var txtBmiValue: TextView
    private lateinit var txtBmiLabel: TextView
    private lateinit var txtBmiStatus: TextView
    private lateinit var viewBmiIndicator: View

    private lateinit var layoutSummary: View
    private lateinit var txtCurrentWeightSummary: TextView
    private lateinit var txtTargetWeightSummary: TextView
    private lateinit var txtLoadingPercent: TextView

    // Các view loading được tạo bằng Kotlin, không cần thêm XML
    private var layoutPlanLoading: LinearLayout? = null
    private var circlePlanProgress: PlanCircleProgressView? = null
    private var txtPlanPercent: TextView? = null
    private var txtPlanMessage: TextView? = null

    private var createPlanAnimator: ValueAnimator? = null
    private var createPlanFinished = false

    private var step = 0

    private var motivation = ""
    private var gender = ""
    private var birthYear = 1995
    private var height = 165
    private var currentWeight = 65.0
    private var targetWeight = 65.0

    private var pickerMin = 0
    private var pickerMax = 0
    private var pickerValue = 0
    private var isDecimalPicker = false
    private var onPickerChanged: ((Int) -> Unit)? = null

    private var lastTouchY = 0f
    private var scrollAccumulator = 0f

    // Hàm khởi tạo màn hình nhập thông tin ban đầu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        mapViews()
        setupButtons()
        showStep()
    }

    // Hàm ánh xạ các view từ XML sang Kotlin
    private fun mapViews() {
        btnBack = findViewById(R.id.btnBack)
        btnSkip = findViewById(R.id.btnSkip)
        btnNext = findViewById(R.id.btnNext)
        progressFill = findViewById(R.id.progressFill)

        txtQuestion = findViewById(R.id.txtQuestion)
        txtDescription = findViewById(R.id.txtDescription)
        layoutContent = findViewById(R.id.layoutContent)

        option1 = findViewById(R.id.option1)
        option2 = findViewById(R.id.option2)
        option3 = findViewById(R.id.option3)
        option4 = findViewById(R.id.option4)
        option5 = findViewById(R.id.option5)

        layoutCustomPicker = findViewById(R.id.layoutCustomPicker)
        txtPickerPrev = findViewById(R.id.txtPickerPrev)
        txtPickerCurrent = findViewById(R.id.txtPickerCurrent)
        txtPickerNext = findViewById(R.id.txtPickerNext)

        txtUnit = findViewById(R.id.txtUnit)

        layoutBmi = findViewById(R.id.layoutBmi)
        layoutBmiBar = findViewById(R.id.layoutBmiBar)
        txtBmiValue = findViewById(R.id.txtBmiValue)
        txtBmiLabel = findViewById(R.id.txtBmiLabel)
        txtBmiStatus = findViewById(R.id.txtBmiStatus)
        viewBmiIndicator = findViewById(R.id.viewBmiIndicator)

        layoutSummary = findViewById(R.id.layoutSummary)
        txtCurrentWeightSummary = findViewById(R.id.txtCurrentWeightSummary)
        txtTargetWeightSummary = findViewById(R.id.txtTargetWeightSummary)
        txtLoadingPercent = findViewById(R.id.txtLoadingPercent)
    }

    // Hàm xử lý nút quay lại, bỏ qua và tiếp theo
    private fun setupButtons() {
        btnBack.setOnClickListener {
            if (step > 0) {
                if (step == 7) {
                    cancelCreatePlanAnimation()
                }

                step--
                showStep()
            } else {
                finish()
            }
        }

        btnSkip.setOnClickListener {
            finishSetup()
        }

        btnNext.setOnClickListener {
            nextStep()
        }

        setupPickerSwipe()
    }

    // Hàm kiểm tra dữ liệu và chuyển sang bước tiếp theo
    private fun nextStep() {
        if (step == 0 && motivation.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn một động lực", Toast.LENGTH_SHORT).show()
            return
        }

        if (step == 1 && gender.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show()
            return
        }

        // Ở màn loading cuối, chỉ cho bấm Xong khi vòng tròn đã chạy đủ 100%
        if (step == 7) {
            if (!createPlanFinished) {
                return
            }

            finishSetup()
            return
        }

        if (step < 7) {
            step++
            showStep()
        }
    }

    // Hàm hiển thị giao diện tương ứng với từng bước
    private fun showStep() {
        if (step != 7) {
            cancelCreatePlanAnimation()
        }

        hideAllContent()
        resetOptionStyle()
        updateTopProgress()

        btnSkip.visibility = if (step == 7) View.GONE else View.VISIBLE
        btnBack.visibility = if (step == 7) View.GONE else View.VISIBLE
        btnNext.visibility = if (step == 7) View.INVISIBLE else View.VISIBLE

        btnNext.isEnabled = true
        btnNext.alpha = 1f
        btnNext.text = when (step) {
            6 -> "XONG"
            7 -> "Xong"
            else -> "Tiếp theo"
        }
        btnNext.setBackgroundResource(R.drawable.bg_green_button)
        btnNext.setTextColor(Color.parseColor("#111111"))

        when (step) {
            0 -> showMotivationStep()
            1 -> showGenderStep()
            2 -> showBirthYearStep()
            3 -> showHeightStep()
            4 -> showCurrentWeightStep()
            5 -> showTargetWeightStep()
            6 -> showSummaryStep()
            7 -> showLoadingStep()
        }
    }

    // Hàm ẩn toàn bộ nội dung cũ trước khi hiển thị bước mới
    private fun hideAllContent() {
        txtDescription.visibility = View.GONE

        option1.visibility = View.GONE
        option2.visibility = View.GONE
        option3.visibility = View.GONE
        option4.visibility = View.GONE
        option5.visibility = View.GONE

        layoutCustomPicker.visibility = View.GONE
        txtUnit.visibility = View.GONE
        layoutBmi.visibility = View.GONE
        layoutSummary.visibility = View.GONE
        txtLoadingPercent.visibility = View.GONE

        layoutPlanLoading?.visibility = View.GONE
    }

    // Hàm cập nhật thanh tiến trình nhỏ phía trên
    private fun updateTopProgress() {
        val totalSteps = 8
        val maxWidthDp = 150
        val fillWidthDp = maxWidthDp * (step + 1) / totalSteps

        val density = resources.displayMetrics.density
        val params = progressFill.layoutParams
        params.width = (fillWidthDp * density).toInt()
        progressFill.layoutParams = params
    }

    // Hàm hiển thị bước chọn động lực
    private fun showMotivationStep() {
        txtQuestion.text = "Điều gì thúc đẩy bạn nhiều nhất?"
        txtDescription.visibility = View.GONE

        showOptions(
            listOf(
                "💪  Có dáng đẹp",
                "⭐  Để trông tốt hơn trong ảnh",
                "😺  Cảm thấy tự tin",
                "🏃  Khả năng thể thao tốt hơn",
                "😊  Giải tỏa căng thẳng"
            )
        )

        option1.setOnClickListener { selectMotivation("Có dáng đẹp", option1) }
        option2.setOnClickListener { selectMotivation("Để trông tốt hơn trong ảnh", option2) }
        option3.setOnClickListener { selectMotivation("Cảm thấy tự tin", option3) }
        option4.setOnClickListener { selectMotivation("Khả năng thể thao tốt hơn", option4) }
        option5.setOnClickListener { selectMotivation("Giải tỏa căng thẳng", option5) }
    }

    // Hàm hiển thị bước chọn giới tính
    private fun showGenderStep() {
        txtQuestion.text = "Vui lòng chọn giới tính"
        txtDescription.text = "Tạo kế hoạch tập luyện phù hợp với bạn nhất"
        txtDescription.visibility = View.VISIBLE

        showOptions(listOf("Nam", "Nữ"))

        option1.setOnClickListener { selectGender("Nam", option1) }
        option2.setOnClickListener { selectGender("Nữ", option2) }
    }

    // Hàm hiển thị bước chọn năm sinh
    private fun showBirthYearStep() {
        txtQuestion.text = "Bạn sinh năm bao nhiêu?"
        txtDescription.text = "Điều này giúp app điều chỉnh kế hoạch cá nhân"
        txtDescription.visibility = View.VISIBLE

        setupCustomPicker(
            min = 1960,
            max = 2015,
            value = birthYear
        ) { newValue ->
            birthYear = newValue
        }
    }

    // Hàm hiển thị bước chọn chiều cao
    private fun showHeightStep() {
        txtQuestion.text = "Chiều cao của bạn là bao nhiêu?"
        txtDescription.visibility = View.GONE

        txtUnit.visibility = View.VISIBLE
        txtUnit.text = "cm"

        setupCustomPicker(
            min = 120,
            max = 220,
            value = height
        ) { newValue ->
            height = newValue
        }
    }

    // Hàm hiển thị bước chọn cân nặng hiện tại và tính BMI hiện tại
    private fun showCurrentWeightStep() {
        txtQuestion.text = "Cân nặng hiện tại của bạn là bao nhiêu?"
        txtDescription.visibility = View.GONE

        txtUnit.visibility = View.VISIBLE
        layoutBmi.visibility = View.VISIBLE

        txtUnit.text = "kg"
        txtBmiLabel.text = "BMI hiện tại"

        setupCustomPicker(
            min = 300,
            max = 1800,
            value = (currentWeight * 10).toInt(),
            isDecimal = true
        ) { newValue ->
            currentWeight = newValue / 10.0
            updateBmiPanel(calculateBmi(currentWeight))
        }

        updateBmiPanel(calculateBmi(currentWeight))
    }

    // Hàm hiển thị bước chọn cân nặng mục tiêu và tính BMI mục tiêu
    private fun showTargetWeightStep() {
        txtQuestion.text = "Cân nặng mục tiêu của bạn là bao nhiêu?"
        txtDescription.visibility = View.GONE

        txtUnit.visibility = View.VISIBLE
        layoutBmi.visibility = View.VISIBLE

        txtUnit.text = "kg"
        txtBmiLabel.text = "BMI mục tiêu"

        setupCustomPicker(
            min = 300,
            max = 1800,
            value = (targetWeight * 10).toInt(),
            isDecimal = true
        ) { newValue ->
            targetWeight = newValue / 10.0
            updateBmiPanel(calculateBmi(targetWeight))
        }

        updateBmiPanel(calculateBmi(targetWeight))
    }

    // Hàm hiển thị bước tổng kết thông tin
    private fun showSummaryStep() {
        txtQuestion.text = "Bạn muốn đạt được mục tiêu nhanh đến mức nào?"
        txtDescription.visibility = View.GONE

        layoutSummary.visibility = View.VISIBLE

        txtCurrentWeightSummary.text = String.format(Locale.US, "%.1f kg", currentWeight)
        txtTargetWeightSummary.text = String.format(Locale.US, "%.1f kg", targetWeight)

        btnNext.text = "XONG"
        btnNext.setBackgroundResource(R.drawable.bg_green_button)
        btnNext.setTextColor(Color.parseColor("#111111"))
    }

    // Hàm hiển thị bước đang tạo kế hoạch cá nhân
    private fun showLoadingStep() {
        txtQuestion.text = "Đang tạo kế hoạch cá nhân của bạn"

        // Không dùng txtDescription ở trên nữa, vì dòng này sẽ nằm dưới vòng tròn
        txtDescription.visibility = View.GONE

        // Không dùng TextView 40% cũ trong XML nữa
        txtLoadingPercent.visibility = View.GONE

        btnNext.text = "Xong"
        btnNext.isEnabled = false
        btnNext.alpha = 0.6f

        showPlanLoadingLayout()
        startCreatePlanAnimation()
    }

    // Hàm tạo giao diện vòng tròn loading bằng Kotlin
    private fun showPlanLoadingLayout() {
        if (layoutPlanLoading != null) {
            layoutPlanLoading?.visibility = View.VISIBLE
            return
        }

        val loadingLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(0, dp(45), 0, 0)
        }

        val circleBox = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(dp(250), dp(250))
        }

        val circleView = PlanCircleProgressView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER
            )
        }

        val percentText = TextView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
            text = "0%"
            setTextColor(Color.parseColor("#111111"))
            textSize = 48f
            gravity = Gravity.CENTER
            setTypeface(typeface, Typeface.BOLD)
        }

        circleBox.addView(circleView)
        circleBox.addView(percentText)

        val messageText = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(42)
            }

            text = "Đang phân tích hồ sơ của bạn..."
            setTextColor(Color.parseColor("#999999"))
            textSize = 24f
            gravity = Gravity.CENTER
            setTypeface(typeface, Typeface.BOLD)
        }

        loadingLayout.addView(circleBox)
        loadingLayout.addView(messageText)

        layoutContent.addView(loadingLayout)

        layoutPlanLoading = loadingLayout
        circlePlanProgress = circleView
        txtPlanPercent = percentText
        txtPlanMessage = messageText
    }

    // Hàm chạy vòng tròn và số phần trăm từ 0% đến 100%
    private fun startCreatePlanAnimation() {
        createPlanAnimator?.cancel()

        createPlanFinished = false

        circlePlanProgress?.setProgressValue(0)
        txtPlanPercent?.text = "0%"
        txtPlanMessage?.text = "Đang phân tích hồ sơ của bạn..."

        var wasCancelled = false

        createPlanAnimator = ValueAnimator.ofInt(0, 100).apply {
            duration = 4000

            addUpdateListener { animator ->
                val value = animator.animatedValue as Int

                circlePlanProgress?.setProgressValue(value)
                txtPlanPercent?.text = "$value%"
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator) {
                    wasCancelled = true
                }

                override fun onAnimationEnd(animation: Animator) {
                    if (wasCancelled || step != 7) {
                        return
                    }

                    createPlanFinished = true

                    txtPlanPercent?.text = "100%"
                    circlePlanProgress?.setProgressValue(100)
                    txtPlanMessage?.text = "Hoàn tất! Đang mở kế hoạch của bạn..."

                    Handler(Looper.getMainLooper()).postDelayed({
                        finishSetup()
                    }, 500)
                }
            })

            start()
        }
    }

    // Hàm hủy animation loading khi quay lại bước trước
    private fun cancelCreatePlanAnimation() {
        createPlanAnimator?.cancel()
        createPlanAnimator = null
        createPlanFinished = false
    }

    // Hàm hiện danh sách lựa chọn vào các ô option có sẵn trong XML
    private fun showOptions(options: List<String>) {
        val optionViews = listOf(option1, option2, option3, option4, option5)

        optionViews.forEachIndexed { index, textView ->
            if (index < options.size) {
                textView.text = options[index]
                textView.visibility = View.VISIBLE
            } else {
                textView.visibility = View.GONE
            }
        }
    }

    // Hàm lưu động lực đã chọn
    private fun selectMotivation(value: String, selectedView: TextView) {
        motivation = value
        resetOptionStyle()
        selectedView.setBackgroundResource(R.drawable.bg_unit_selected)
        selectedView.setTextColor(Color.parseColor("#111111"))
    }

    // Hàm lưu giới tính đã chọn
    private fun selectGender(value: String, selectedView: TextView) {
        gender = value
        resetOptionStyle()
        selectedView.setBackgroundResource(R.drawable.bg_unit_selected)
        selectedView.setTextColor(Color.parseColor("#111111"))
    }

    // Hàm đưa các ô lựa chọn về trạng thái chưa chọn
    private fun resetOptionStyle() {
        val optionViews = listOf(option1, option2, option3, option4, option5)

        optionViews.forEach {
            it.setBackgroundResource(R.drawable.bg_unit_unselected)
            it.setTextColor(Color.parseColor("#333333"))
        }
    }

    // Hàm cài đặt picker custom cho năm sinh, chiều cao và cân nặng
    private fun setupCustomPicker(
        min: Int,
        max: Int,
        value: Int,
        isDecimal: Boolean = false,
        onChanged: (Int) -> Unit
    ) {
        pickerMin = min
        pickerMax = max
        pickerValue = value
        isDecimalPicker = isDecimal
        onPickerChanged = onChanged

        layoutCustomPicker.visibility = View.VISIBLE
        updateCustomPickerUI()
    }

    // Hàm cập nhật số trên, số đang chọn và số dưới của picker custom
    private fun updateCustomPickerUI() {
        val prevValue = pickerValue - 1
        val nextValue = pickerValue + 1

        txtPickerPrev.text = formatPickerText(prevValue, pickerMin, pickerMax)
        txtPickerCurrent.text = formatPickerText(pickerValue, pickerMin, pickerMax)
        txtPickerNext.text = formatPickerText(nextValue, pickerMin, pickerMax)

        txtPickerCurrent.scaleX = 0.95f
        txtPickerCurrent.scaleY = 0.95f
        txtPickerCurrent.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(80)
            .start()
    }

    // Hàm định dạng số picker
    private fun formatPickerText(value: Int, min: Int, max: Int): String {
        if (value < min || value > max) return ""

        return if (isDecimalPicker) {
            String.format(Locale.US, "%.1f", value / 10.0)
        } else {
            value.toString()
        }
    }

    // Hàm cập nhật số BMI, nhận xét BMI và vị trí vạch chỉ trên thanh BMI
    private fun updateBmiPanel(bmi: Double) {
        txtBmiValue.text = formatBmi(bmi)

        txtBmiStatus.text = when {
            bmi < 18.5 -> "Gầy - bạn nên tăng cân hợp lý."
            bmi < 25.0 -> "Bình thường - hãy duy trì nhé!"
            bmi < 30.0 -> "Thừa cân - nên kiểm soát cân nặng."
            bmi < 35.0 -> "Béo phì - nên giảm cân dần."
            else -> "Béo phì nghiêm trọng - cần ưu tiên cải thiện sức khỏe."
        }

        viewBmiIndicator.post {
            val barWidth = layoutBmiBar.width

            val minBmi = 15.0
            val maxBmi = 40.0

            val percent = (
                    (bmi.coerceIn(minBmi, maxBmi) - minBmi) /
                            (maxBmi - minBmi)
                    ).toFloat()

            viewBmiIndicator.translationX =
                (barWidth * percent) - (viewBmiIndicator.width / 2f)
        }
    }

    // Hàm tính BMI theo công thức cân nặng / chiều cao bình phương
    private fun calculateBmi(weight: Double): Double {
        val heightMeter = height / 100.0
        return weight / (heightMeter * heightMeter)
    }

    // Hàm định dạng BMI còn 1 chữ số thập phân
    private fun formatBmi(bmi: Double): String {
        return String.format(Locale.US, "%.1f", bmi)
    }

    // Hàm cho phép kéo lên/kéo xuống để đổi số liên tục
    @SuppressLint("ClickableViewAccessibility")
    private fun setupPickerSwipe() {
        val stepDistance = 25 * resources.displayMetrics.density

        val swipeListener = View.OnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastTouchY = event.y
                    scrollAccumulator = 0f
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val deltaY = event.y - lastTouchY
                    lastTouchY = event.y
                    scrollAccumulator += deltaY

                    while (scrollAccumulator >= stepDistance) {
                        decreasePickerValue()
                        scrollAccumulator -= stepDistance
                    }

                    while (scrollAccumulator <= -stepDistance) {
                        increasePickerValue()
                        scrollAccumulator += stepDistance
                    }

                    true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    scrollAccumulator = 0f
                    true
                }

                else -> true
            }
        }

        layoutCustomPicker.setOnTouchListener(swipeListener)
        txtPickerPrev.setOnTouchListener(swipeListener)
        txtPickerCurrent.setOnTouchListener(swipeListener)
        txtPickerNext.setOnTouchListener(swipeListener)
    }

    // Hàm tăng giá trị picker khi kéo lên
    private fun increasePickerValue() {
        if (pickerValue < pickerMax) {
            pickerValue++
            onPickerChanged?.invoke(pickerValue)
            updateCustomPickerUI()
        }
    }

    // Hàm giảm giá trị picker khi kéo xuống
    private fun decreasePickerValue() {
        if (pickerValue > pickerMin) {
            pickerValue--
            onPickerChanged?.invoke(pickerValue)
            updateCustomPickerUI()
        }
    }

    // Hàm đổi dp sang pixel
    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    // Hàm lưu thông tin setup vào SharedPreferences và chuyển vào MainActivity
    private fun finishSetup() {
        val loginPrefs = getSharedPreferences("login_data", Context.MODE_PRIVATE)
        val username = loginPrefs.getString("current_user", "guest") ?: "guest"

        getSharedPreferences("user_${username}_profile", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("setup_done", true)
            .putString("motivation", motivation)
            .putString("gender", gender)
            .putInt("birthYear", birthYear)
            .putInt("height", height)
            .putFloat("currentWeight", currentWeight.toFloat())
            .putFloat("targetWeight", targetWeight.toFloat())
            .putFloat("currentBMI", calculateBmi(currentWeight).toFloat())
            .putFloat("targetBMI", calculateBmi(targetWeight).toFloat())
            .apply()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

// View tự vẽ vòng tròn loading 0% -> 100%
private class PlanCircleProgressView(context: Context) : View(context) {

    private var progressValue = 0

    private val strokeWidthPx = 16f * context.resources.displayMetrics.density

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#DDF1DE")
        style = Paint.Style.STROKE
        strokeWidth = strokeWidthPx
        strokeCap = Paint.Cap.ROUND
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#74D97F")
        style = Paint.Style.STROKE
        strokeWidth = strokeWidthPx
        strokeCap = Paint.Cap.ROUND
    }

    private val arcRect = RectF()

    fun setProgressValue(value: Int) {
        progressValue = value.coerceIn(0, 100)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val padding = strokeWidthPx / 2f + 4f

        arcRect.set(
            padding,
            padding,
            width - padding,
            height - padding
        )

        // Vòng nền xanh nhạt
        canvas.drawArc(arcRect, 0f, 360f, false, trackPaint)

        // Vòng xanh đậm chạy theo %
        val sweepAngle = 360f * (progressValue / 100f)
        canvas.drawArc(arcRect, -90f, sweepAngle, false, progressPaint)
    }
}