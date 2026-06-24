package com.example.fitnessmobileapp.ui.plan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessmobileapp.R
import com.example.fitnessmobileapp.data.model.Exercise
import com.example.fitnessmobileapp.data.repository.WorkoutDataReader
import java.io.File
import java.io.FileOutputStream

class ExerciseDetailActivity : AppCompatActivity() {

    private lateinit var btnBackExercise: TextView
    private lateinit var btnCloseExercise: TextView

    private lateinit var tabAnimation: LinearLayout
    private lateinit var tabYoutube: LinearLayout
    private lateinit var lineAnimation: android.view.View
    private lateinit var lineYoutube: android.view.View

    private lateinit var videoContainer: FrameLayout
    private lateinit var videoExercise: VideoView
    private lateinit var txtVideoEmpty: TextView

    private lateinit var txtExerciseName: TextView
    private lateinit var txtExerciseDuration: TextView
    private lateinit var txtExerciseDescription: TextView
    private lateinit var btnMinus: TextView
    private lateinit var btnPlus: TextView

    private lateinit var btnPreviousExercise: TextView
    private lateinit var btnNextExercise: TextView
    private lateinit var txtExerciseCounter: TextView

    private var exerciseList: List<Exercise> = emptyList()
    private var currentIndex = 0
    private var currentExercise: Exercise? = null
    private var currentDuration = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_detail)

        bindViews()
        setupButtons()
        loadExerciseList()
    }

    override fun onPause() {
        super.onPause()
        if (::videoExercise.isInitialized) {
            videoExercise.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::videoExercise.isInitialized) {
            videoExercise.stopPlayback()
        }
    }

    private fun bindViews() {
        btnBackExercise = findViewById(R.id.btnBackExercise)
        btnCloseExercise = findViewById(R.id.btnCloseExercise)

        tabAnimation = findViewById(R.id.tabAnimation)
        tabYoutube = findViewById(R.id.tabYoutube)
        lineAnimation = findViewById(R.id.lineAnimation)
        lineYoutube = findViewById(R.id.lineYoutube)

        videoContainer = findViewById(R.id.videoContainer)
        videoExercise = findViewById(R.id.videoExercise)
        txtVideoEmpty = findViewById(R.id.txtVideoEmpty)

        txtExerciseName = findViewById(R.id.txtExerciseName)
        txtExerciseDuration = findViewById(R.id.txtExerciseDuration)
        txtExerciseDescription = findViewById(R.id.txtExerciseDescription)
        btnMinus = findViewById(R.id.btnMinus)
        btnPlus = findViewById(R.id.btnPlus)

        btnPreviousExercise = findViewById(R.id.btnPreviousExercise)
        btnNextExercise = findViewById(R.id.btnNextExercise)
        txtExerciseCounter = findViewById(R.id.txtExerciseCounter)
    }

    private fun setupButtons() {
        btnBackExercise.setOnClickListener {
            finish()
        }

        btnCloseExercise.setOnClickListener {
            finish()
        }

        tabAnimation.setOnClickListener {
            setActiveTab(isAnimation = true)
            currentExercise?.let { playAnimationVideo(it) }
        }

        tabYoutube.setOnClickListener {
            val youtubeUrl = currentExercise?.youtubeUrl.orEmpty()

            if (youtubeUrl.isBlank()) {
                Toast.makeText(this, "Chưa có video YouTube", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            setActiveTab(isAnimation = false)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl))
            startActivity(intent)
        }

        btnMinus.setOnClickListener {
            if (currentDuration > 1) {
                currentDuration -= 1
                updateDurationText()
            }
        }

        btnPlus.setOnClickListener {
            currentDuration += 1
            updateDurationText()
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

    private fun loadExerciseList() {
        val exerciseId = intent.getStringExtra("EXERCISE_ID")
        val exerciseType = intent.getStringExtra("EXERCISE_TYPE") ?: "abs"

        exerciseList = when (exerciseType) {
            "arms_chest" -> WorkoutDataReader.getArmsChestExercises(this)
            "legs" -> WorkoutDataReader.getLegExercises(this)
            "abs" -> WorkoutDataReader.getAbsExercises(this)
            else -> WorkoutDataReader.getAllExercises(this)
        }

        if (exerciseList.isEmpty()) {
            Toast.makeText(this, "Không có danh sách bài tập", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        currentIndex = exerciseList.indexOfFirst { it.id == exerciseId }

        if (currentIndex == -1) {
            currentIndex = 0
        }

        showExerciseByIndex()
    }

    private fun showExerciseByIndex() {
        val exercise = exerciseList[currentIndex]

        currentExercise = exercise
        currentDuration = exercise.duration

        txtExerciseName.text = exercise.name
        updateDurationText()

        txtExerciseDescription.text = exercise.description
            .filter { it.isNotBlank() }
            .joinToString("\n\n")

        txtExerciseCounter.text = "${currentIndex + 1} / ${exerciseList.size}"

        updateNavigationButtons()

        setActiveTab(isAnimation = true)
        playAnimationVideo(exercise)
    }

    private fun updateNavigationButtons() {
        btnPreviousExercise.alpha = if (currentIndex == 0) 0.35f else 1f
        btnNextExercise.alpha = if (currentIndex == exerciseList.size - 1) 0.35f else 1f
    }

    private fun playAnimationVideo(exercise: Exercise) {
        val animationPath = exercise.animationFile

        if (animationPath.isBlank()) {
            videoExercise.visibility = android.view.View.GONE
            txtVideoEmpty.visibility = android.view.View.VISIBLE
            txtVideoEmpty.text = "Chưa có hoạt hình"
            return
        }

        try {
            videoExercise.stopPlayback()

            val cachedFile = copyAssetVideoToCache(animationPath)

            txtVideoEmpty.visibility = android.view.View.GONE
            videoExercise.visibility = android.view.View.VISIBLE

            videoExercise.setVideoPath(cachedFile.absolutePath)

            videoExercise.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = true
                mediaPlayer.setVolume(0f, 0f)
                videoExercise.start()
            }

            videoExercise.setOnErrorListener { _, _, _ ->
                videoExercise.visibility = android.view.View.GONE
                txtVideoEmpty.visibility = android.view.View.VISIBLE
                txtVideoEmpty.text = "Không thể phát hoạt hình"
                true
            }

        } catch (e: Exception) {
            videoExercise.visibility = android.view.View.GONE
            txtVideoEmpty.visibility = android.view.View.VISIBLE
            txtVideoEmpty.text = "Không thể mở file hoạt hình"
        }
    }

    private fun copyAssetVideoToCache(assetPath: String): File {
        val safeFileName = assetPath
            .replace("/", "_")
            .replace(" ", "_")
            .replace(Regex("[^A-Za-z0-9._-]"), "_")

        val outputFile = File(cacheDir, safeFileName)

        if (outputFile.exists() && outputFile.length() > 0) {
            return outputFile
        }

        assets.open(assetPath).use { inputStream ->
            FileOutputStream(outputFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return outputFile
    }

    private fun setActiveTab(isAnimation: Boolean) {
        if (isAnimation) {
            lineAnimation.visibility = android.view.View.VISIBLE
            lineYoutube.visibility = android.view.View.INVISIBLE
        } else {
            lineAnimation.visibility = android.view.View.INVISIBLE
            lineYoutube.visibility = android.view.View.VISIBLE
        }
    }

    private fun updateDurationText() {
        txtExerciseDuration.text = formatDuration(currentDuration)
    }

    private fun formatDuration(seconds: Int): String {
        val minute = seconds / 60
        val second = seconds % 60
        return String.format("%02d:%02d", minute, second)
    }
}