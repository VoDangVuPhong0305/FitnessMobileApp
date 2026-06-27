package com.example.fitnessmobileapp.ui.plan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessmobileapp.R
import com.example.fitnessmobileapp.data.model.Exercise
import com.example.fitnessmobileapp.data.repository.ExerciseTargetHelper
import com.example.fitnessmobileapp.data.repository.WorkoutDataReader
import java.io.File
import java.io.FileOutputStream

class ExerciseDetailActivity : AppCompatActivity() {

    private lateinit var btnBackExercise: TextView

    private lateinit var tabAnimation: LinearLayout
    private lateinit var tabYoutube: LinearLayout
    private lateinit var txtTabAnimation: TextView
    private lateinit var txtTabYoutube: TextView
    private lateinit var lineAnimation: View
    private lateinit var lineYoutube: View

    private lateinit var videoExercise: VideoView
    private lateinit var txtVideoEmpty: TextView

    private lateinit var txtExerciseName: TextView
    private lateinit var txtTargetLabel: TextView
    private lateinit var btnMinus: TextView
    private lateinit var txtExerciseDuration: TextView
    private lateinit var btnPlus: TextView
    private lateinit var txtExerciseDescription: TextView

    private lateinit var btnPreviousExercise: TextView
    private lateinit var txtExerciseCounter: TextView
    private lateinit var btnNextExercise: TextView
    private lateinit var btnCloseExercise: TextView

    private var exerciseId: String = ""
    private var exerciseType: String = "abs"
    private var exerciseIds: ArrayList<String> = arrayListOf()

    private var exerciseList: List<Exercise> = emptyList()
    private var currentIndex: Int = 0
    private var currentExercise: Exercise? = null

    // Chức năng: lưu ngày hiện tại để tính số lần tăng dần theo lộ trình 30 ngày.
    private var dayNumber = 1

    // Chức năng: lưu mục tiêu hiện tại của bài.
    // Bài time: 30 giây.
    // Bài reps: x12, x14, x16...
    private var currentTarget = ExerciseTargetHelper.ExerciseTarget(
        ExerciseTargetHelper.TYPE_TIME,
        30
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_detail)

        bindViews()
        getIntentData()
        loadExerciseList()
        setupButtons()
        showExerciseByIndex()
    }

    private fun bindViews() {
        btnBackExercise = findViewById(R.id.btnBackExercise)

        tabAnimation = findViewById(R.id.tabAnimation)
        tabYoutube = findViewById(R.id.tabYoutube)
        txtTabAnimation = findViewById(R.id.txtTabAnimation)
        txtTabYoutube = findViewById(R.id.txtTabYoutube)
        lineAnimation = findViewById(R.id.lineAnimation)
        lineYoutube = findViewById(R.id.lineYoutube)

        videoExercise = findViewById(R.id.videoExercise)
        txtVideoEmpty = findViewById(R.id.txtVideoEmpty)

        txtExerciseName = findViewById(R.id.txtExerciseName)
        txtTargetLabel = findViewById(R.id.txtTargetLabel)
        btnMinus = findViewById(R.id.btnMinus)
        txtExerciseDuration = findViewById(R.id.txtExerciseDuration)
        btnPlus = findViewById(R.id.btnPlus)
        txtExerciseDescription = findViewById(R.id.txtExerciseDescription)

        btnPreviousExercise = findViewById(R.id.btnPreviousExercise)
        txtExerciseCounter = findViewById(R.id.txtExerciseCounter)
        btnNextExercise = findViewById(R.id.btnNextExercise)
        btnCloseExercise = findViewById(R.id.btnCloseExercise)
    }

    private fun getIntentData() {
        dayNumber = intent.getIntExtra("DAY_NUMBER", 1)
        exerciseId = intent.getStringExtra("EXERCISE_ID") ?: ""
        exerciseType = intent.getStringExtra("EXERCISE_TYPE") ?: "abs"
        exerciseIds = intent.getStringArrayListExtra("EXERCISE_IDS") ?: arrayListOf()
    }

    private fun loadExerciseList() {
        val allExercises = getExercisesByType()

        exerciseList = if (exerciseIds.isNotEmpty()) {
            val exerciseMap = allExercises.associateBy { exercise ->
                exercise.id
            }

            exerciseIds.mapNotNull { id ->
                exerciseMap[id]
            }
        } else {
            allExercises
        }

        currentIndex = exerciseList.indexOfFirst { exercise ->
            exercise.id == exerciseId
        }

        if (currentIndex < 0) {
            currentIndex = 0
        }
    }

    private fun getExercisesByType(): List<Exercise> {
        return when (exerciseType) {
            "abs" -> WorkoutDataReader.getAbsExercises(this)
            "legs" -> WorkoutDataReader.getLegExercises(this)
            "arms_chest" -> WorkoutDataReader.getArmsChestExercises(this)
            "full_body" -> WorkoutDataReader.getFullBodyExercises(this)
            else -> WorkoutDataReader.getAllExercises(this)
        }
    }

    private fun setupButtons() {
        btnBackExercise.setOnClickListener {
            finish()
        }

        btnCloseExercise.setOnClickListener {
            finish()
        }

        tabAnimation.setOnClickListener {
            showAnimationTab()
        }

        tabYoutube.setOnClickListener {
            openYoutubeVideo()
        }

        // Chức năng: giảm thời lượng hoặc số lần.
        // Bài time: giảm 5 giây.
        // Bài reps: giảm 1 lần.
        btnMinus.setOnClickListener {
            currentTarget = ExerciseTargetHelper.decreaseTarget(currentTarget)
            updateTargetUI()
        }

        // Chức năng: tăng thời lượng hoặc số lần.
        // Bài time: tăng 5 giây.
        // Bài reps: tăng 1 lần.
        btnPlus.setOnClickListener {
            currentTarget = ExerciseTargetHelper.increaseTarget(currentTarget)
            updateTargetUI()
        }

        btnPreviousExercise.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                showExerciseByIndex()
            }
        }

        btnNextExercise.setOnClickListener {
            if (currentIndex < exerciseList.size - 1) {
                currentIndex++
                showExerciseByIndex()
            }
        }
    }

    private fun showExerciseByIndex() {
        if (exerciseList.isEmpty()) {
            Toast.makeText(this, "Không có bài tập", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        currentExercise = exerciseList[currentIndex]
        val exercise = currentExercise ?: return

        txtExerciseName.text = exercise.name

        // Chức năng: xác định bài này là bài theo thời gian hay bài theo số lần.
        // Ví dụ:
        // Bật nhảy -> Thời lượng 00:30
        // Gánh đùi -> Lần lặp lại x12
        currentTarget = ExerciseTargetHelper.getTarget(
            exerciseId = exercise.id,
            exerciseName = exercise.name,
            dayNumber = dayNumber
        )

        updateTargetUI()

        txtExerciseDescription.text = if (exercise.description.isEmpty()) {
            "Chưa có mô tả bài tập."
        } else {
            exercise.description.joinToString(separator = "\n\n")
        }

        txtExerciseCounter.text = "${currentIndex + 1} / ${exerciseList.size}"

        updateNavigationButtons()
        showAnimationTab()
    }

    private fun updateTargetUI() {
        txtTargetLabel.text = ExerciseTargetHelper.getTargetLabel(currentTarget)
        txtExerciseDuration.text = ExerciseTargetHelper.getTargetText(currentTarget)
    }

    private fun updateNavigationButtons() {
        btnPreviousExercise.alpha = if (currentIndex == 0) 0.35f else 1f
        btnNextExercise.alpha = if (currentIndex == exerciseList.size - 1) 0.35f else 1f
    }

    private fun showAnimationTab() {
        txtTabAnimation.setTextColor(0xFF222222.toInt())
        txtTabYoutube.setTextColor(0xFF777777.toInt())

        lineAnimation.visibility = View.VISIBLE
        lineYoutube.visibility = View.INVISIBLE

        playAnimationVideo()
    }

    private fun openYoutubeVideo() {
        val exercise = currentExercise ?: return

        txtTabAnimation.setTextColor(0xFF777777.toInt())
        txtTabYoutube.setTextColor(0xFF222222.toInt())

        lineAnimation.visibility = View.INVISIBLE
        lineYoutube.visibility = View.VISIBLE

        if (exercise.youtubeUrl.isBlank()) {
            Toast.makeText(this, "Chưa có video YouTube", Toast.LENGTH_SHORT).show()
            showAnimationTab()
            return
        }

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(exercise.youtubeUrl))
        startActivity(intent)
    }

    private fun playAnimationVideo() {
        val exercise = currentExercise ?: return

        try {
            val cachedFile = copyAssetVideoToCache(exercise.animationFile)

            txtVideoEmpty.visibility = View.GONE
            videoExercise.visibility = View.VISIBLE

            videoExercise.stopPlayback()
            videoExercise.setVideoPath(cachedFile.absolutePath)

            videoExercise.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = true
                mediaPlayer.setVolume(0f, 0f)
                videoExercise.start()
            }

            videoExercise.setOnErrorListener { _, _, _ ->
                videoExercise.visibility = View.GONE
                txtVideoEmpty.visibility = View.VISIBLE
                txtVideoEmpty.text = "Không thể phát hoạt hình"
                true
            }

        } catch (e: Exception) {
            videoExercise.visibility = View.GONE
            txtVideoEmpty.visibility = View.VISIBLE
            txtVideoEmpty.text = "Chưa có hoạt hình"
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

    override fun onPause() {
        super.onPause()
        videoExercise.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoExercise.stopPlayback()
    }
}