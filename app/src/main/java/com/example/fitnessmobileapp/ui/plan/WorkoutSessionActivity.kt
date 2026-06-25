package com.example.fitnessmobileapp.ui.plan

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessmobileapp.R
import com.example.fitnessmobileapp.data.model.Exercise
import com.example.fitnessmobileapp.data.repository.WorkoutDataReader
import java.io.File
import java.io.FileOutputStream

class WorkoutSessionActivity : AppCompatActivity() {

    private lateinit var btnCloseSession: TextView
    private lateinit var txtSessionInfo: TextView
    private lateinit var videoSessionExercise: VideoView
    private lateinit var txtSessionEmpty: TextView
    private lateinit var txtPhaseTitle: TextView
    private lateinit var txtSessionExerciseName: TextView
    private lateinit var txtSessionTimer: TextView
    private lateinit var btnStartNow: TextView
    private lateinit var layoutExerciseControls: LinearLayout
    private lateinit var btnPauseResume: TextView
    private lateinit var btnPreviousExercise: TextView
    private lateinit var btnSkip: TextView

    private var dayNumber: Int = 1
    private var dayTitle: String = "Ngày 1"
    private var exerciseType: String = "abs"
    private var exerciseIds: ArrayList<String> = arrayListOf()

    private var exerciseList: List<Exercise> = emptyList()
    private var currentIndex: Int = 0

    private var countDownTimer: CountDownTimer? = null
    private var secondsLeft: Int = 10
    private var currentPhase: SessionPhase = SessionPhase.PREPARE

    private var pauseDialog: Dialog? = null

    private val prepareSeconds = 10

    enum class SessionPhase {
        PREPARE,
        EXERCISE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_session)

        bindViews()
        getIntentData()
        loadExerciseList()
        setupButtons()
        setupBackHandler()

        if (exerciseList.isEmpty()) {
            Toast.makeText(this, "Không có bài tập trong ngày này", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        startPreparePhase()
    }

    private fun bindViews() {
        btnCloseSession = findViewById(R.id.btnCloseSession)
        txtSessionInfo = findViewById(R.id.txtSessionInfo)
        videoSessionExercise = findViewById(R.id.videoSessionExercise)
        txtSessionEmpty = findViewById(R.id.txtSessionEmpty)
        txtPhaseTitle = findViewById(R.id.txtPhaseTitle)
        txtSessionExerciseName = findViewById(R.id.txtSessionExerciseName)
        txtSessionTimer = findViewById(R.id.txtSessionTimer)
        btnStartNow = findViewById(R.id.btnStartNow)
        layoutExerciseControls = findViewById(R.id.layoutExerciseControls)
        btnPauseResume = findViewById(R.id.btnPauseResume)
        btnPreviousExercise = findViewById(R.id.btnPreviousExercise)
        btnSkip = findViewById(R.id.btnSkip)
    }

    private fun getIntentData() {
        dayNumber = intent.getIntExtra("DAY_NUMBER", 1)
        dayTitle = intent.getStringExtra("DAY_TITLE") ?: "Ngày $dayNumber"
        exerciseType = intent.getStringExtra("EXERCISE_TYPE") ?: "abs"
        exerciseIds = intent.getStringArrayListExtra("EXERCISE_IDS") ?: arrayListOf()
    }

    private fun loadExerciseList() {
        val allExercises = when (exerciseType) {
            "abs" -> WorkoutDataReader.getAbsExercises(this)
            "legs" -> WorkoutDataReader.getLegExercises(this)
            "arms_chest" -> WorkoutDataReader.getArmsChestExercises(this)
            else -> WorkoutDataReader.getAllExercises(this)
        }

        val exerciseMap = allExercises.associateBy { exercise ->
            exercise.id
        }

        exerciseList = exerciseIds.mapNotNull { id ->
            exerciseMap[id]
        }
    }

    private fun setupButtons() {
        btnCloseSession.setOnClickListener {
            showPauseDialog()
        }

        btnStartNow.setOnClickListener {
            countDownTimer?.cancel()
            startExercisePhase()
        }

        btnPauseResume.setOnClickListener {
            showPauseDialog()
        }

        btnPreviousExercise.setOnClickListener {
            if (currentIndex > 0) {
                countDownTimer?.cancel()
                currentIndex--
                startPreparePhase()
            } else {
                Toast.makeText(this, "Đây là bài đầu tiên", Toast.LENGTH_SHORT).show()
            }
        }

        btnSkip.setOnClickListener {
            countDownTimer?.cancel()
            moveToNextExerciseOrFinish()
        }
    }

    private fun setupBackHandler() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExitConfirmDialog()
                }
            }
        )
    }

    private fun startPreparePhase() {
        currentPhase = SessionPhase.PREPARE
        secondsLeft = prepareSeconds

        val exercise = exerciseList[currentIndex]

        txtSessionInfo.text = ""
        txtPhaseTitle.visibility = View.VISIBLE
        txtPhaseTitle.text = "ĐÃ SẴN SÀNG TẬP"

        txtSessionExerciseName.text = exercise.name.uppercase()
        txtSessionTimer.text = secondsLeft.toString()

        btnStartNow.visibility = View.VISIBLE
        layoutExerciseControls.visibility = View.GONE

        playExerciseVideo(exercise)
        startTimer()
    }

    private fun startExercisePhase() {
        currentPhase = SessionPhase.EXERCISE

        val exercise = exerciseList[currentIndex]

        secondsLeft = exercise.duration

        txtSessionInfo.text = "${currentIndex + 1}/${exerciseList.size}"

        txtPhaseTitle.visibility = View.GONE
        txtSessionExerciseName.text = exercise.name
        txtSessionTimer.text = formatTime(secondsLeft)

        btnStartNow.visibility = View.GONE
        layoutExerciseControls.visibility = View.VISIBLE
        btnPauseResume.text = "Ⅱ  TẠM DỪNG"

        updatePreviousButton()
        playExerciseVideo(exercise)
        startTimer()
    }

    private fun startTimer() {
        countDownTimer?.cancel()
        updateTimerText()

        countDownTimer = object : CountDownTimer(secondsLeft * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                secondsLeft = ((millisUntilFinished + 999L) / 1000L).toInt()
                updateTimerText()
            }

            override fun onFinish() {
                secondsLeft = 0
                updateTimerText()

                if (currentPhase == SessionPhase.PREPARE) {
                    startExercisePhase()
                } else {
                    moveToNextExerciseOrFinish()
                }
            }
        }.start()
    }

    private fun updateTimerText() {
        if (currentPhase == SessionPhase.PREPARE) {
            txtSessionTimer.text = secondsLeft.toString()
        } else {
            txtSessionTimer.text = formatTime(secondsLeft)
        }
    }

    private fun showPauseDialog() {
        countDownTimer?.cancel()
        videoSessionExercise.pause()

        pauseDialog = Dialog(this)
        pauseDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pauseDialog?.setContentView(R.layout.dialog_pause_workout)
        pauseDialog?.setCancelable(false)

        val btnRestartCurrentExercise =
            pauseDialog?.findViewById<TextView>(R.id.btnRestartCurrentExercise)
        val btnExitWorkout =
            pauseDialog?.findViewById<TextView>(R.id.btnExitWorkout)
        val btnContinueWorkout =
            pauseDialog?.findViewById<TextView>(R.id.btnContinueWorkout)

        btnRestartCurrentExercise?.setOnClickListener {
            pauseDialog?.dismiss()
            restartCurrentExercise()
        }

        btnExitWorkout?.setOnClickListener {
            pauseDialog?.dismiss()
            finish()
        }

        btnContinueWorkout?.setOnClickListener {
            pauseDialog?.dismiss()
            continueCurrentWorkout()
        }

        pauseDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pauseDialog?.show()

        pauseDialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    private fun restartCurrentExercise() {
        countDownTimer?.cancel()

        if (currentPhase == SessionPhase.PREPARE) {
            startPreparePhase()
        } else {
            startExercisePhase()
        }
    }

    private fun continueCurrentWorkout() {
        if (currentPhase == SessionPhase.EXERCISE) {
            videoSessionExercise.start()
        } else {
            val exercise = exerciseList[currentIndex]
            playExerciseVideo(exercise)
        }

        startTimer()
    }

    private fun moveToNextExerciseOrFinish() {
        if (currentIndex == exerciseList.size - 1) {
            showWorkoutCompleted()
        } else {
            currentIndex++
            startPreparePhase()
        }
    }

    private fun updatePreviousButton() {
        if (currentIndex == 0) {
            btnPreviousExercise.alpha = 0.35f
        } else {
            btnPreviousExercise.alpha = 1f
        }
    }

    private fun showWorkoutCompleted() {
        countDownTimer?.cancel()
        videoSessionExercise.stopPlayback()

        AlertDialog.Builder(this)
            .setTitle("Hoàn thành")
            .setMessage("Bạn đã hoàn thành $dayTitle.")
            .setPositiveButton("OK") { _, _ ->
                finish()
            }
            .show()
    }

    private fun showExitConfirmDialog() {
        countDownTimer?.cancel()
        videoSessionExercise.pause()

        AlertDialog.Builder(this)
            .setTitle("Thoát buổi tập?")
            .setMessage("Tiến trình buổi tập hiện tại sẽ chưa được lưu.")
            .setNegativeButton("Ở lại") { _, _ ->
                continueCurrentWorkout()
            }
            .setPositiveButton("Thoát") { _, _ ->
                finish()
            }
            .show()
    }

    private fun playExerciseVideo(exercise: Exercise) {
        try {
            val cachedFile = copyAssetVideoToCache(exercise.animationFile)

            txtSessionEmpty.visibility = View.GONE
            videoSessionExercise.visibility = View.VISIBLE

            videoSessionExercise.stopPlayback()
            videoSessionExercise.setVideoPath(cachedFile.absolutePath)

            videoSessionExercise.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = true
                mediaPlayer.setVolume(0f, 0f)
                videoSessionExercise.start()
            }

            videoSessionExercise.setOnErrorListener { _, _, _ ->
                videoSessionExercise.visibility = View.GONE
                txtSessionEmpty.visibility = View.VISIBLE
                txtSessionEmpty.text = "Không thể phát hoạt hình"
                true
            }

        } catch (e: Exception) {
            videoSessionExercise.visibility = View.GONE
            txtSessionEmpty.visibility = View.VISIBLE
            txtSessionEmpty.text = "Chưa có hoạt hình"
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

    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainSeconds = seconds % 60
        return "%02d:%02d".format(minutes, remainSeconds)
    }

    override fun onPause() {
        super.onPause()
        countDownTimer?.cancel()
        videoSessionExercise.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        videoSessionExercise.stopPlayback()
        pauseDialog?.dismiss()
    }
}