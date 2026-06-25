package com.example.fitnessmobileapp.ui.plan

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessmobileapp.R
import com.example.fitnessmobileapp.data.model.Exercise
import com.example.fitnessmobileapp.data.repository.WorkoutDataReader
import java.io.File
import java.io.FileOutputStream
import android.view.View

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
        showHeaderInfo()
        showExerciseList()
        setupButtons()
    }

    private fun getIntentData() {
        dayNumber = intent.getIntExtra("DAY_NUMBER", 1)
        dayTitle = intent.getStringExtra("DAY_TITLE") ?: "Ngày $dayNumber"
        durationMinutes = intent.getIntExtra("DURATION_MINUTES", 6)
        exerciseCount = intent.getIntExtra("EXERCISE_COUNT", 7)
        exerciseType = intent.getStringExtra("EXERCISE_TYPE") ?: "abs"
        exerciseIds = intent.getStringArrayListExtra("EXERCISE_IDS") ?: arrayListOf()

        isCompletedDay = intent.getBooleanExtra("IS_COMPLETED_DAY", false)
        canStartWorkout = intent.getBooleanExtra("CAN_START_WORKOUT", true)
    }

    private fun showHeaderInfo() {
        txtDayTitle.text = dayTitle.uppercase()
        txtWorkoutInfo.text = "Tập Cơ Bụng"
        txtWorkoutSummary.text = "$durationMinutes phút, $exerciseCount bài tập"
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

    private fun getExercisesByType(): List<Exercise> {
        return when (exerciseType) {
            "abs" -> WorkoutDataReader.getAbsExercises(this)
            "legs" -> WorkoutDataReader.getLegExercises(this)
            "arms_chest" -> WorkoutDataReader.getArmsChestExercises(this)
            else -> WorkoutDataReader.getAllExercises(this)
        }
    }

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
            text = formatDuration(exercise.duration)
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

    // Bấm từng bài trong danh sách thì vẫn mở màn chi tiết bài tập
    private fun openExerciseDetail(exercise: Exercise) {
        val intent = Intent(this, ExerciseDetailActivity::class.java)
        intent.putExtra("EXERCISE_ID", exercise.id)
        intent.putExtra("EXERCISE_TYPE", exerciseType)
        intent.putStringArrayListExtra("EXERCISE_IDS", exerciseIds)
        startActivity(intent)
    }

    // Bấm nút BẮT ĐẦU thì mở màn tập thật
    private fun openWorkoutSession() {
        val intent = Intent(this, WorkoutSessionActivity::class.java)
        intent.putExtra("DAY_NUMBER", dayNumber)
        intent.putExtra("DAY_TITLE", dayTitle)
        intent.putExtra("EXERCISE_TYPE", exerciseType)
        intent.putStringArrayListExtra("EXERCISE_IDS", exerciseIds)
        startActivity(intent)
    }

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

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}