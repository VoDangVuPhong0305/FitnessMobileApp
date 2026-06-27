package com.example.fitnessmobileapp.ui.plan

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
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
import com.example.fitnessmobileapp.data.repository.ExerciseTargetHelper
import com.example.fitnessmobileapp.data.repository.PlanProgressManager
import com.example.fitnessmobileapp.data.repository.WorkoutDataReader
import com.example.fitnessmobileapp.data.repository.WorkoutReportManager
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

class WorkoutSessionActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

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

    private lateinit var layoutRestScreen: LinearLayout
    private lateinit var txtRestTimer: TextView
    private lateinit var btnAddRestTime: TextView
    private lateinit var btnSkipRest: TextView
    private lateinit var txtRestNextInfo: TextView
    private lateinit var txtRestNextName: TextView
    private lateinit var videoRestNextExercise: VideoView
    private lateinit var txtRestNextEmpty: TextView

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

    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady: Boolean = false
    private var pendingSpeech: String? = null

    private val spokenVoiceMarks = mutableSetOf<Int>()
    private val spokenExerciseEndMarks = mutableSetOf<Int>()

    private val mainHandler = Handler(Looper.getMainLooper())

    private val prepareSeconds = 10
    private val voiceGuideSeconds = 6
    private val restSeconds = 10

    enum class SessionPhase {
        PREPARE,
        VOICE_GUIDE,
        EXERCISE,
        REST
    }

    // Chức năng: khởi tạo màn tập luyện, nhận dữ liệu bài tập và bắt đầu pha chuẩn bị.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_session)

        bindViews()
        initTextToSpeech()
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

    // Chức năng: ánh xạ các View từ file XML sang biến Kotlin để điều khiển giao diện.
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

        layoutRestScreen = findViewById(R.id.layoutRestScreen)
        txtRestTimer = findViewById(R.id.txtRestTimer)
        btnAddRestTime = findViewById(R.id.btnAddRestTime)
        btnSkipRest = findViewById(R.id.btnSkipRest)
        txtRestNextInfo = findViewById(R.id.txtRestNextInfo)
        txtRestNextName = findViewById(R.id.txtRestNextName)
        videoRestNextExercise = findViewById(R.id.videoRestNextExercise)
        txtRestNextEmpty = findViewById(R.id.txtRestNextEmpty)
    }

    // Chức năng: khởi tạo TextToSpeech để app có thể đọc tên bài và đếm ngược bằng giọng nói.
    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(this, this)
    }

    // Chức năng: xử lý khi TextToSpeech khởi tạo xong.
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(Locale("vi", "VN"))

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                textToSpeech?.language = Locale.getDefault()
            }

            textToSpeech?.setSpeechRate(1.0f)
            textToSpeech?.setPitch(1.0f)

            isTtsReady = true

            pendingSpeech?.let { text ->
                speak(text)
                pendingSpeech = null
            }
        }
    }

    // Chức năng: đọc một câu bằng TextToSpeech.
    private fun speak(text: String) {
        if (!isTtsReady) {
            pendingSpeech = text
            return
        }

        textToSpeech?.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "speech_${System.currentTimeMillis()}"
        )
    }

    // Chức năng: dừng giọng đọc hiện tại.
    private fun stopSpeech() {
        textToSpeech?.stop()
    }

    // Chức năng: lấy dữ liệu được truyền từ màn chi tiết ngày sang.
    private fun getIntentData() {
        dayNumber = intent.getIntExtra("DAY_NUMBER", 1)
        dayTitle = intent.getStringExtra("DAY_TITLE") ?: "Ngày $dayNumber"
        exerciseType = intent.getStringExtra("EXERCISE_TYPE") ?: "abs"
        exerciseIds = intent.getStringArrayListExtra("EXERCISE_IDS") ?: arrayListOf()
    }

    // Chức năng: đọc danh sách bài tập theo nhóm và lọc đúng các bài của ngày hiện tại.
    private fun loadExerciseList() {
        val allExercises = when (exerciseType) {
            "abs" -> WorkoutDataReader.getAbsExercises(this)
            "legs" -> WorkoutDataReader.getLegExercises(this)
            "arms_chest" -> WorkoutDataReader.getArmsChestExercises(this)
            "full_body" -> WorkoutDataReader.getFullBodyExercises(this)
            else -> WorkoutDataReader.getAllExercises(this)
        }

        val exerciseMap = allExercises.associateBy { exercise ->
            exercise.id
        }

        exerciseList = exerciseIds.mapNotNull { id ->
            exerciseMap[id]
        }
    }

    // Chức năng: gắn sự kiện cho các nút trong màn tập luyện.
    private fun setupButtons() {
        btnCloseSession.setOnClickListener {
            showPauseDialog()
        }

        btnStartNow.setOnClickListener {
            countDownTimer?.cancel()
            mainHandler.removeCallbacksAndMessages(null)
            stopSpeech()

            when (currentPhase) {
                SessionPhase.PREPARE -> startVoiceGuidePhase()
                SessionPhase.REST -> goToNextExerciseVoiceGuide()

                SessionPhase.VOICE_GUIDE -> {
                    speak("Bắt đầu")
                    startExercisePhaseAfterShortDelay()
                }

                SessionPhase.EXERCISE -> {}
            }
        }

        btnPauseResume.setOnClickListener {
            showPauseDialog()
        }

        btnPreviousExercise.setOnClickListener {
            if (currentIndex > 0) {
                countDownTimer?.cancel()
                mainHandler.removeCallbacksAndMessages(null)
                stopSpeech()
                currentIndex--
                startVoiceGuidePhase()
            }
        }

        btnSkip.setOnClickListener {
            countDownTimer?.cancel()
            mainHandler.removeCallbacksAndMessages(null)
            stopSpeech()

            when (currentPhase) {
                SessionPhase.PREPARE -> startVoiceGuidePhase()
                SessionPhase.VOICE_GUIDE -> startExercisePhase()
                SessionPhase.EXERCISE -> finishExerciseAndMoveNext()
                SessionPhase.REST -> goToNextExerciseVoiceGuide()
            }
        }

        btnAddRestTime.setOnClickListener {
            addRestTime()
        }

        btnSkipRest.setOnClickListener {
            countDownTimer?.cancel()
            mainHandler.removeCallbacksAndMessages(null)
            stopSpeech()
            goToNextExerciseVoiceGuide()
        }
    }

    // Chức năng: khi người dùng bấm nút Back của điện thoại thì mở dialog tạm dừng thay vì thoát ngay.
    private fun setupBackHandler() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showPauseDialog()
                }
            }
        )
    }

    // Chức năng: ẩn màn nghỉ và dừng video xem trước bài tiếp theo.
    private fun hideRestScreen() {
        layoutRestScreen.visibility = View.GONE
        videoRestNextExercise.stopPlayback()
    }

    // Chức năng: bắt đầu pha chuẩn bị trước khi vào bài đầu tiên.
    private fun startPreparePhase() {
        hideRestScreen()

        currentPhase = SessionPhase.PREPARE
        secondsLeft = prepareSeconds
        spokenVoiceMarks.clear()

        val exercise = exerciseList[currentIndex]

        txtSessionInfo.text = ""
        txtPhaseTitle.visibility = View.VISIBLE
        txtPhaseTitle.text = "ĐÃ SẴN SÀNG TẬP"

        txtSessionExerciseName.text = exercise.name.uppercase()
        txtSessionTimer.text = secondsLeft.toString()

        btnStartNow.visibility = View.VISIBLE
        btnStartNow.text = "BẮT ĐẦU NGAY"
        layoutExerciseControls.visibility = View.GONE

        playMainExerciseVideo(exercise)
        speak("Đã sẵn sàng tập")
        startTimer()
    }

    // Chức năng: pha hướng dẫn ngắn trước mỗi bài, đọc tên bài và mục tiêu bài tập.
    private fun startVoiceGuidePhase() {
        hideRestScreen()

        currentPhase = SessionPhase.VOICE_GUIDE
        secondsLeft = voiceGuideSeconds
        spokenVoiceMarks.clear()

        val exercise = exerciseList[currentIndex]

        txtSessionInfo.text = "${currentIndex + 1}/${exerciseList.size}"

        txtPhaseTitle.visibility = View.GONE
        txtSessionExerciseName.text = exercise.name
        txtSessionTimer.text = getExerciseTargetText(exercise)

        btnStartNow.visibility = View.GONE
        layoutExerciseControls.visibility = View.VISIBLE
        btnPauseResume.text = "Ⅱ  TẠM DỪNG"
        btnSkip.text = "Bỏ qua  →"

        updatePreviousButton()
        playMainExerciseVideo(exercise)
        startTimer()
    }

    // Chức năng: bắt đầu bài tập chính.
    // Nếu bài theo thời gian thì đếm ngược.
    // Nếu bài theo số lần thì hiện x lần và chờ người dùng bấm TIẾP TỤC.
    private fun startExercisePhase() {
        hideRestScreen()

        currentPhase = SessionPhase.EXERCISE
        spokenExerciseEndMarks.clear()

        val exercise = exerciseList[currentIndex]
        val target = getExerciseTarget(exercise)

        txtSessionInfo.text = "${currentIndex + 1}/${exerciseList.size}"

        txtPhaseTitle.visibility = View.GONE
        txtSessionExerciseName.text = exercise.name

        btnStartNow.visibility = View.GONE
        layoutExerciseControls.visibility = View.VISIBLE
        btnPauseResume.text = "Ⅱ  TẠM DỪNG"

        updatePreviousButton()
        playMainExerciseVideo(exercise)

        if (target.type == ExerciseTargetHelper.TYPE_REPS) {
            countDownTimer?.cancel()
            secondsLeft = 0
            txtSessionTimer.text = "x${target.value}"
            btnSkip.text = "Bỏ qua  →"

            speak("Thực hiện ${target.value} lần")
        } else {
            secondsLeft = target.value
            txtSessionTimer.text = formatTime(secondsLeft)
            btnSkip.text = "Bỏ qua  →"

            startTimer()
        }
    }

    // Chức năng: đọc 3, 2, 1 ở cuối bài tập theo thời gian.
    // Bài theo số lần thì không cần đọc đếm ngược cuối bài.
    private fun handleExerciseEndingCue(second: Int) {
        if (currentPhase != SessionPhase.EXERCISE) return
        if (isCurrentExerciseReps()) return
        if (spokenExerciseEndMarks.contains(second)) return

        when (second) {
            3 -> {
                spokenExerciseEndMarks.add(second)
                speak("3")
            }

            2 -> {
                spokenExerciseEndMarks.add(second)
                speak("2")
            }

            1 -> {
                spokenExerciseEndMarks.add(second)
                speak("1")
            }
        }
    }

    // Chức năng: sau khi đọc “Bắt đầu”, chờ một chút rồi chuyển sang pha tập thật.
    private fun startExercisePhaseAfterShortDelay() {
        mainHandler.removeCallbacksAndMessages(null)

        mainHandler.postDelayed({
            startExercisePhase()
        }, 900)
    }

    // Chức năng: hiển thị màn nghỉ giữa 2 bài và xem trước bài tiếp theo.
    private fun startRestPhase() {
        currentPhase = SessionPhase.REST
        secondsLeft = restSeconds
        spokenVoiceMarks.clear()

        layoutRestScreen.visibility = View.VISIBLE

        videoSessionExercise.pause()
        txtRestTimer.text = secondsLeft.toString()

        val nextExercise = exerciseList[currentIndex + 1]
        val nextNumber = currentIndex + 2

        txtRestNextInfo.text = "Tiếp theo $nextNumber/${exerciseList.size}"
        txtRestNextName.text = "${nextExercise.name} ${getExerciseTargetText(nextExercise)}"

        playRestNextExerciseVideo(nextExercise)

        speak("Nghỉ ngơi 10 giây")
        startTimer()
    }

    // Chức năng: cộng thêm 20 giây nghỉ khi người dùng bấm nút +20s.
    private fun addRestTime() {
        if (currentPhase != SessionPhase.REST) return

        countDownTimer?.cancel()
        secondsLeft += 20
        txtRestTimer.text = secondsLeft.toString()
        startTimer()
    }

    // Chức năng: chạy bộ đếm thời gian cho pha chuẩn bị, hướng dẫn, bài time và nghỉ.
    private fun startTimer() {
        countDownTimer?.cancel()
        updateTimerText()
        handleVoiceGuideCue(secondsLeft)
        handleExerciseEndingCue(secondsLeft)

        countDownTimer = object : CountDownTimer(secondsLeft * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                secondsLeft = ((millisUntilFinished + 999L) / 1000L).toInt()
                updateTimerText()
                handleVoiceGuideCue(secondsLeft)
                handleExerciseEndingCue(secondsLeft)
            }

            override fun onFinish() {
                secondsLeft = 0
                updateTimerText()

                when (currentPhase) {
                    SessionPhase.PREPARE -> startVoiceGuidePhase()

                    SessionPhase.VOICE_GUIDE -> {
                        speak("Bắt đầu")
                        startExercisePhaseAfterShortDelay()
                    }

                    SessionPhase.EXERCISE -> finishExerciseAndMoveNext()

                    SessionPhase.REST -> goToNextExerciseVoiceGuide()
                }
            }
        }.start()
    }

    // Chức năng: đọc tên bài, mục tiêu và đếm 3 2 1 trong pha hướng dẫn.
    private fun handleVoiceGuideCue(second: Int) {
        if (currentPhase != SessionPhase.VOICE_GUIDE) return
        if (spokenVoiceMarks.contains(second)) return

        val exercise = exerciseList[currentIndex]
        val target = getExerciseTarget(exercise)

        when (second) {
            6 -> {
                spokenVoiceMarks.add(second)

                if (target.type == ExerciseTargetHelper.TYPE_REPS) {
                    speak("${exercise.name}, ${target.value} lần")
                } else {
                    speak("${exercise.name}, ${target.value} giây")
                }
            }

            3 -> {
                spokenVoiceMarks.add(second)
                speak("3")
            }

            2 -> {
                spokenVoiceMarks.add(second)
                speak("2")
            }

            1 -> {
                spokenVoiceMarks.add(second)
                speak("1")
            }
        }
    }

    // Chức năng: cập nhật chữ hiển thị bộ đếm hoặc số lần trên màn hình.
    private fun updateTimerText() {
        when (currentPhase) {
            SessionPhase.PREPARE -> {
                txtSessionTimer.text = secondsLeft.toString()
            }

            SessionPhase.VOICE_GUIDE -> {
                val exercise = exerciseList[currentIndex]
                txtSessionTimer.text = getExerciseTargetText(exercise)
            }

            SessionPhase.EXERCISE -> {
                val exercise = exerciseList[currentIndex]
                val target = getExerciseTarget(exercise)

                txtSessionTimer.text = if (target.type == ExerciseTargetHelper.TYPE_REPS) {
                    "x${target.value}"
                } else {
                    formatTime(secondsLeft)
                }
            }

            SessionPhase.REST -> {
                txtRestTimer.text = secondsLeft.toString()
            }
        }
    }

    // Chức năng: kết thúc bài hiện tại.
    // Nếu là bài cuối thì hoàn thành buổi tập, nếu chưa thì chuyển sang màn nghỉ.
    private fun finishExerciseAndMoveNext() {
        if (currentIndex == exerciseList.size - 1) {
            showWorkoutCompleted()
        } else {
            startRestPhase()
        }
    }

    // Chức năng: sau khi nghỉ xong, chuyển sang bài tiếp theo.
    private fun goToNextExerciseVoiceGuide() {
        if (currentIndex < exerciseList.size - 1) {
            currentIndex++
            startVoiceGuidePhase()
        } else {
            showWorkoutCompleted()
        }
    }

    // Chức năng: hiển thị dialog tạm dừng khi người dùng muốn thoát, tập lại hoặc tiếp tục.
    private fun showPauseDialog() {
        countDownTimer?.cancel()
        mainHandler.removeCallbacksAndMessages(null)
        pauseCurrentVideo()
        stopSpeech()

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

    // Chức năng: tập lại từ đầu pha hiện tại.
    private fun restartCurrentExercise() {
        countDownTimer?.cancel()
        mainHandler.removeCallbacksAndMessages(null)
        stopSpeech()

        when (currentPhase) {
            SessionPhase.PREPARE -> startPreparePhase()
            SessionPhase.VOICE_GUIDE -> startVoiceGuidePhase()
            SessionPhase.EXERCISE -> startExercisePhase()
            SessionPhase.REST -> startRestPhase()
        }
    }

    // Chức năng: tiếp tục buổi tập sau khi tạm dừng.
    // Nếu là bài theo số lần thì không chạy timer lại.
    private fun continueCurrentWorkout() {
        when (currentPhase) {
            SessionPhase.EXERCISE,
            SessionPhase.VOICE_GUIDE -> videoSessionExercise.start()

            SessionPhase.REST -> videoRestNextExercise.start()

            SessionPhase.PREPARE -> {
                val exercise = exerciseList[currentIndex]
                playMainExerciseVideo(exercise)
            }
        }

        if (currentPhase == SessionPhase.EXERCISE && isCurrentExerciseReps()) {
            txtSessionTimer.text = getExerciseTargetText(exerciseList[currentIndex])
            return
        }

        startTimer()
    }

    // Chức năng: tạm dừng video đang phát.
    private fun pauseCurrentVideo() {
        if (currentPhase == SessionPhase.REST) {
            videoRestNextExercise.pause()
        } else {
            videoSessionExercise.pause()
        }
    }

    // Chức năng: cập nhật trạng thái nút Trước đó.
    // Nếu đang ở bài đầu tiên thì vẫn hiển thị đủ chữ nhưng làm nhạt màu.
    // Nếu không phải bài đầu tiên thì nút hiện rõ để quay về bài trước.
    private fun updatePreviousButton() {
        btnPreviousExercise.text = "←  Trước đó"

        if (currentIndex == 0) {
            btnPreviousExercise.alpha = 1f
            btnPreviousExercise.isEnabled = false
            btnPreviousExercise.setTextColor(0xFFD6D6D6.toInt())
        } else {
            btnPreviousExercise.alpha = 1f
            btnPreviousExercise.isEnabled = true
            btnPreviousExercise.setTextColor(0xFF777777.toInt())
        }
    }

    // Chức năng: xử lý khi người dùng hoàn thành toàn bộ bài trong ngày.
    private fun showWorkoutCompleted() {
        countDownTimer?.cancel()
        mainHandler.removeCallbacksAndMessages(null)
        videoSessionExercise.stopPlayback()
        videoRestNextExercise.stopPlayback()
        stopSpeech()

        PlanProgressManager.completeDay(
            context = this,
            planId = exerciseType,
            dayNumber = dayNumber
        )

        saveWorkoutReport()

        AlertDialog.Builder(this)
            .setTitle("Hoàn thành")
            .setMessage("Bạn đã hoàn thành $dayTitle.")
            .setPositiveButton("OK") { _, _ ->
                finish()
            }
            .show()
    }

    // Chức năng: lưu dữ liệu buổi tập để màn Báo cáo có thể thống kê.
    private fun saveWorkoutReport() {
        val totalDurationSeconds = calculateTotalDurationSeconds()
        val totalCalories = calculateTotalCalories()

        WorkoutReportManager.saveCompletedWorkout(
            context = this,
            dayNumber = dayNumber,
            exerciseType = exerciseType,
            exerciseCount = exerciseList.size,
            durationSeconds = totalDurationSeconds,
            calories = totalCalories
        )
    }

    // Chức năng: phát video bài tập chính.
    private fun playMainExerciseVideo(exercise: Exercise) {
        playVideoToView(
            videoView = videoSessionExercise,
            emptyText = txtSessionEmpty,
            exercise = exercise
        )
    }

    // Chức năng: phát video xem trước bài tiếp theo ở màn nghỉ.
    private fun playRestNextExerciseVideo(exercise: Exercise) {
        playVideoToView(
            videoView = videoRestNextExercise,
            emptyText = txtRestNextEmpty,
            exercise = exercise
        )
    }

    // Chức năng: phát video từ assets lên VideoView.
    private fun playVideoToView(
        videoView: VideoView,
        emptyText: TextView,
        exercise: Exercise
    ) {
        try {
            val cachedFile = copyAssetVideoToCache(exercise.animationFile)

            emptyText.visibility = View.GONE
            videoView.visibility = View.VISIBLE

            videoView.stopPlayback()
            videoView.setVideoPath(cachedFile.absolutePath)

            videoView.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = true
                mediaPlayer.setVolume(0f, 0f)
                videoView.start()
            }

            videoView.setOnErrorListener { _, _, _ ->
                videoView.visibility = View.GONE
                emptyText.visibility = View.VISIBLE
                emptyText.text = "Không thể phát hoạt hình"
                true
            }

        } catch (e: Exception) {
            videoView.visibility = View.GONE
            emptyText.visibility = View.VISIBLE
            emptyText.text = "Chưa có hoạt hình"
        }
    }

    // Chức năng: copy video từ assets sang cache để VideoView có thể phát được.
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

    // Chức năng: lấy mục tiêu của bài hiện tại.
    // Bài time trả về 30 giây, bài reps trả về x số lần.
    private fun getExerciseTarget(exercise: Exercise): ExerciseTargetHelper.ExerciseTarget {
        return ExerciseTargetHelper.getTarget(
            exerciseId = exercise.id,
            exerciseName = exercise.name,
            dayNumber = dayNumber
        )
    }

    // Chức năng: đổi mục tiêu bài tập thành chữ để hiển thị.
    // Ví dụ: 00:30 hoặc x12.
    private fun getExerciseTargetText(exercise: Exercise): String {
        val target = getExerciseTarget(exercise)

        return if (target.type == ExerciseTargetHelper.TYPE_REPS) {
            "x${target.value}"
        } else {
            formatTime(target.value)
        }
    }

    // Chức năng: kiểm tra bài hiện tại có phải bài đếm số lần không.
    private fun isCurrentExerciseReps(): Boolean {
        if (exerciseList.isEmpty()) return false

        val exercise = exerciseList[currentIndex]
        val target = getExerciseTarget(exercise)

        return target.type == ExerciseTargetHelper.TYPE_REPS
    }

    // Chức năng: tính tổng thời gian tập để lưu sang Báo cáo.
    // Bài theo thời gian lấy đúng số giây, bài theo số lần tạm quy đổi theo duration gốc.
    private fun calculateTotalDurationSeconds(): Int {
        return exerciseList.sumOf { exercise ->
            val target = getExerciseTarget(exercise)

            if (target.type == ExerciseTargetHelper.TYPE_TIME) {
                target.value
            } else {
                exercise.duration
            }
        }
    }

    // Chức năng: tính tổng calories của các bài trong ngày.
    private fun calculateTotalCalories(): Int {
        return exerciseList.sumOf { exercise ->
            exercise.calories
        }
    }

    // Chức năng: định dạng số giây thành dạng 00:30.
    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainSeconds = seconds % 60
        return "%02d:%02d".format(minutes, remainSeconds)
    }

    // Chức năng: khi app tạm dừng thì dừng timer, video và giọng đọc để tránh chạy ngầm.
    override fun onPause() {
        super.onPause()
        countDownTimer?.cancel()
        mainHandler.removeCallbacksAndMessages(null)
        videoSessionExercise.pause()
        videoRestNextExercise.pause()
        stopSpeech()
    }

    // Chức năng: giải phóng tài nguyên khi thoát màn tập luyện.
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        mainHandler.removeCallbacksAndMessages(null)
        videoSessionExercise.stopPlayback()
        videoRestNextExercise.stopPlayback()
        stopSpeech()
        textToSpeech?.shutdown()
        pauseDialog?.dismiss()
    }
}