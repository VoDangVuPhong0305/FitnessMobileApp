package com.example.fitnessmobileapp.ui.plan

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessmobileapp.R
import com.example.fitnessmobileapp.data.model.Exercise
import com.example.fitnessmobileapp.data.repository.ExerciseTargetHelper
import com.example.fitnessmobileapp.data.repository.WorkoutDataReader
import com.example.fitnessmobileapp.data.repository.CustomExerciseTargetManager
import java.io.File
import java.io.FileOutputStream

class PlanDayDetailActivity : AppCompatActivity() {

    private lateinit var btnBack: TextView
    private lateinit var txtDayTitle: TextView
    private lateinit var txtWorkoutInfo: TextView
    private lateinit var txtWorkoutSummary: TextView
    private lateinit var layoutExerciseList: LinearLayout
    private lateinit var btnStartWorkout: TextView

    private var dayNumber: Int = 1
    private var dayTitle: String = "Ngày 1"
    private var durationMinutes: Int = 6
    private var exerciseCount: Int = 7
    private var exerciseType: String = "abs"
    private var exerciseIds: ArrayList<String> = arrayListOf()
    private var exercisesOfDay: List<Exercise> = emptyList()
    private var isCompletedDay: Boolean = false
    private var canStartWorkout: Boolean = true

    // Chức năng: lưu màu riêng của kế hoạch hiện tại.
    private var planStartColor: String = "#7B61FF"
    private var planEndColor: String = "#91A8FF"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_day_detail)

        btnBack = findViewById(R.id.btnBack)
        txtDayTitle = findViewById(R.id.txtDayTitle)
        txtWorkoutInfo = findViewById(R.id.txtWorkoutInfo)
        txtWorkoutSummary = findViewById(R.id.txtWorkoutSummary)
        layoutExerciseList = findViewById(R.id.layoutExerciseList)
        btnStartWorkout = findViewById(R.id.btnStartWorkout)

        getIntentData()
        showExerciseList()
        showHeaderInfo()
        setupButtons()
    }

    // Chức năng: khi quay lại màn chi tiết ngày tập từ màn chi tiết bài tập,
    // tự load lại danh sách bài để hiển thị số lần/thời gian mới nếu người dùng vừa bấm LƯU.
    override fun onResume() {
        super.onResume()

        if (::layoutExerciseList.isInitialized) {
            showExerciseList()
            showHeaderInfo()
        }
    }

    // Chức năng: nhận dữ liệu ngày tập và màu kế hoạch từ PlanFragment.
    private fun getIntentData() {
        dayNumber = intent.getIntExtra("DAY_NUMBER", 1)
        dayTitle = intent.getStringExtra("DAY_TITLE") ?: "Ngày $dayNumber"
        durationMinutes = intent.getIntExtra("DURATION_MINUTES", 6)
        exerciseCount = intent.getIntExtra("EXERCISE_COUNT", 7)
        exerciseType = intent.getStringExtra("EXERCISE_TYPE") ?: "abs"
        exerciseIds = intent.getStringArrayListExtra("EXERCISE_IDS") ?: arrayListOf()

        // Chức năng: nhận màu riêng của từng kế hoạch.
        planStartColor = intent.getStringExtra("PLAN_START_COLOR") ?: "#7B61FF"
        planEndColor = intent.getStringExtra("PLAN_END_COLOR") ?: "#91A8FF"

        isCompletedDay = intent.getBooleanExtra("IS_COMPLETED_DAY", false)
        canStartWorkout = intent.getBooleanExtra("CAN_START_WORKOUT", true)
    }

    // Chức năng: hiển thị thông tin đầu màn hình chi tiết ngày tập.
    // Bao gồm tên ngày tập, loại kế hoạch, tổng phút và tổng số bài tập.
    // Tổng phút sẽ được tính lại nếu người dùng đã chỉnh số lần/thời gian của bài tập.
    private fun showHeaderInfo() {
        txtDayTitle.text = dayTitle.uppercase()
        txtWorkoutInfo.text = getWorkoutTitle()

        val realExerciseCount = if (exercisesOfDay.isNotEmpty()) {
            exercisesOfDay.size
        } else {
            exerciseCount
        }

        val realDurationMinutes = if (exercisesOfDay.isNotEmpty()) {
            calculateCustomDurationMinutes()
        } else {
            durationMinutes
        }

        txtWorkoutSummary.text = "$realDurationMinutes phút, $realExerciseCount bài tập"
    }

    // Chức năng: đổi exerciseType thành tên dễ đọc trên giao diện.
    private fun getWorkoutTitle(): String {
        return when (exerciseType) {
            "full_body" -> "Tập Toàn Thân"
            "abs" -> "Tập Cơ Bụng"
            "arms_chest" -> "Tập Tay & Ngực"
            "legs" -> "Tập Chân"
            else -> "Tập Luyện"
        }
    }

    // Chức năng: xử lý nút quay lại và nút bắt đầu/tập lại.
    // Nếu ngày chưa mở thì ẩn nút để người dùng chỉ xem trước danh sách bài.
    private fun setupButtons() {
        btnBack.setOnClickListener {
            finish()
        }

        if (!canStartWorkout) {
            btnStartWorkout.visibility = View.GONE
            return
        }

        btnStartWorkout.visibility = View.VISIBLE

        // Chức năng: đổi màu nút BẮT ĐẦU/TẬP LẠI theo màu của kế hoạch.
        btnStartWorkout.background = createPlanButtonBackground()

        btnStartWorkout.text = if (isCompletedDay) {
            "TẬP LẠI"
        } else {
            "BẮT ĐẦU"
        }

        btnStartWorkout.setOnClickListener {
            if (exercisesOfDay.isNotEmpty()) {
                openWorkoutSession()
            } else {
                Toast.makeText(this, "Không có bài tập trong ngày này", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Chức năng: hiển thị danh sách bài tập của ngày hiện tại.
    private fun showExerciseList() {
        layoutExerciseList.removeAllViews()

        val allExercises = getExercisesByType()

        val exerciseMap = allExercises.associateBy { exercise ->
            exercise.id
        }

        exercisesOfDay = exerciseIds.mapNotNull { id ->
            exerciseMap[id]
        }

        for (exercise in exercisesOfDay) {
            val exerciseItem = createExerciseItem(exercise)
            layoutExerciseList.addView(exerciseItem)
        }
    }

    // Chức năng: lấy danh sách bài tập theo nhóm kế hoạch.
    private fun getExercisesByType(): List<Exercise> {
        return when (exerciseType) {
            "abs" -> WorkoutDataReader.getAbsExercises(this)
            "legs" -> WorkoutDataReader.getLegExercises(this)
            "arms_chest" -> WorkoutDataReader.getArmsChestExercises(this)
            "full_body" -> WorkoutDataReader.getFullBodyExercises(this)
            else -> WorkoutDataReader.getAllExercises(this)
        }
    }

    // Chức năng: tạo từng dòng bài tập gồm ảnh, tên bài và thời lượng/số lần.
    private fun createExerciseItem(exercise: Exercise): LinearLayout {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(12), 0, dp(12))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(104)
            )
        }

        val imgExercise = ImageView(this).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            setBackgroundResource(R.drawable.bg_exercise_media_rounded)
            clipToOutline = true
            outlineProvider = android.view.ViewOutlineProvider.BACKGROUND
            layoutParams = LinearLayout.LayoutParams(dp(86), dp(86))
        }

        val thumbnail = getVideoThumbnail(exercise.animationFile)
        if (thumbnail != null) {
            imgExercise.setImageBitmap(thumbnail)
        }

        val textContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(18), 0, 0, 0)
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )
        }

        val txtName = TextView(this).apply {
            text = exercise.name
            textSize = 22f
            setTypeface(null, Typeface.BOLD)
            setTextColor(0xFF222222.toInt())
            maxLines = 2
        }

        val txtDuration = TextView(this).apply {
            text = getExerciseTargetText(exercise)
            textSize = 16f
            setTextColor(0xFF777777.toInt())
            setPadding(0, dp(6), 0, 0)
        }

        textContainer.addView(txtName)
        textContainer.addView(txtDuration)

        root.addView(imgExercise)
        root.addView(textContainer)

        root.setOnClickListener {
            openExerciseDetail(exercise)
        }

        return root
    }

    // Chức năng: lấy nội dung hiển thị số lần hoặc thời gian của từng bài tập.
    // Nếu người dùng đã chỉnh và lưu thì hiển thị giá trị mới.
    // Ví dụ: Chống đẩy mặc định x6, sau khi lưu x8 thì danh sách hiển thị x8.
    private fun getExerciseTargetText(exercise: Exercise): String {
        val target = CustomExerciseTargetManager.getTarget(
            context = this,
            exerciseType = exerciseType,
            dayNumber = dayNumber,
            exercise = exercise
        )

        return if (target.type == ExerciseTargetHelper.TYPE_REPS) {
            "x${target.value}"
        } else {
            formatDuration(target.value)
        }
    }

    // Chức năng: tính lại tổng thời lượng của ngày tập dựa trên số lần/thời gian đã chỉnh.
    // Bài theo thời gian thì lấy số giây hiện tại.
    // Bài theo số lần thì quy đổi thời lượng theo tỉ lệ số lần mới so với số lần mặc định.
    private fun calculateCustomDurationMinutes(): Int {
        val totalSeconds = exercisesOfDay.sumOf { exercise ->
            val defaultTarget = CustomExerciseTargetManager.getDefaultTarget(
                exercise = exercise,
                dayNumber = dayNumber
            )

            val currentTarget = CustomExerciseTargetManager.getTarget(
                context = this,
                exerciseType = exerciseType,
                dayNumber = dayNumber,
                exercise = exercise
            )

            if (currentTarget.type == ExerciseTargetHelper.TYPE_TIME) {
                currentTarget.value
            } else {
                CustomExerciseTargetManager.calculateDurationForTarget(
                    baseDurationSeconds = exercise.duration,
                    defaultTarget = defaultTarget,
                    currentTarget = currentTarget
                )
            }
        }

        return (totalSeconds + 59) / 60
    }

    // Chức năng: bấm từng bài trong danh sách thì mở màn chi tiết bài tập.
    // Đồng thời truyền màu kế hoạch sang ExerciseDetailActivity.
    private fun openExerciseDetail(exercise: Exercise) {
        val intent = Intent(this, ExerciseDetailActivity::class.java)
        intent.putExtra("DAY_NUMBER", dayNumber)
        intent.putExtra("EXERCISE_ID", exercise.id)
        intent.putExtra("EXERCISE_TYPE", exerciseType)
        intent.putStringArrayListExtra("EXERCISE_IDS", exerciseIds)

        // Chức năng: truyền màu kế hoạch sang màn chi tiết bài tập.
        intent.putExtra("PLAN_START_COLOR", planStartColor)
        intent.putExtra("PLAN_END_COLOR", planEndColor)

        startActivity(intent)
    }

    // Chức năng: bấm nút BẮT ĐẦU/TẬP LẠI thì mở màn tập thật.
    // Đồng thời truyền màu kế hoạch sang WorkoutSessionActivity.
    private fun openWorkoutSession() {
        val intent = Intent(this, WorkoutSessionActivity::class.java)
        intent.putExtra("DAY_NUMBER", dayNumber)
        intent.putExtra("DAY_TITLE", dayTitle)
        intent.putExtra("EXERCISE_TYPE", exerciseType)
        intent.putStringArrayListExtra("EXERCISE_IDS", exerciseIds)

        // Chức năng: truyền màu kế hoạch sang màn tập thật.
        intent.putExtra("PLAN_START_COLOR", planStartColor)
        intent.putExtra("PLAN_END_COLOR", planEndColor)

        startActivity(intent)
    }

    // Chức năng: lấy ảnh thumbnail từ video trong assets.
    private fun getVideoThumbnail(assetPath: String): Bitmap? {
        return try {
            val cachedFile = copyAssetVideoToCache(assetPath)

            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(cachedFile.absolutePath)

            val bitmap = retriever.getFrameAtTime(1_000_000)
            retriever.release()

            bitmap
        } catch (e: Exception) {
            null
        }
    }

    // Chức năng: copy video từ assets sang cache để hệ thống đọc được video.
    private fun copyAssetVideoToCache(assetPath: String): File {
        val fileName = assetPath.replace("/", "_")
        val cachedFile = File(cacheDir, fileName)

        if (!cachedFile.exists()) {
            assets.open(assetPath).use { input ->
                FileOutputStream(cachedFile).use { output ->
                    input.copyTo(output)
                }
            }
        }

        return cachedFile
    }

    // Chức năng: tạo nền gradient theo màu của từng kế hoạch.
    private fun createPlanButtonBackground(radiusDp: Int = 28): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                Color.parseColor(planStartColor),
                Color.parseColor(planEndColor)
            )
        ).apply {
            cornerRadius = dp(radiusDp).toFloat()
        }
    }

    // Chức năng: định dạng số giây thành chữ hiển thị.
    private fun formatDuration(seconds: Int): String {
        return if (seconds < 60) {
            "$seconds s"
        } else {
            val minutes = seconds / 60
            val remainSeconds = seconds % 60

            if (remainSeconds == 0) {
                "$minutes phút"
            } else {
                "$minutes phút $remainSeconds s"
            }
        }
    }

    // Chức năng: đổi dp sang pixel.
    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}