package com.example.fitnessmobileapp.ui.plan

import android.app.Dialog
import android.content.Intent
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
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessmobileapp.R
import com.example.fitnessmobileapp.data.model.Exercise
import com.example.fitnessmobileapp.data.repository.PlanProgressManager
import com.example.fitnessmobileapp.data.repository.WorkoutDataReader
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

    // Chức năng: ánh xạ toàn bộ View trong XML sang biến Kotlin để điều khiển màn tập.
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

    // Chức năng: khởi tạo TextToSpeech để đọc hướng dẫn bằng giọng nói.
    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(this, this)
    }

    // Chức năng: thiết lập tiếng Việt cho TextToSpeech sau khi khởi tạo xong.
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

    // Chức năng: nhận dữ liệu ngày tập được gửi từ PlanDayDetailActivity.
    private fun getIntentData() {
        dayNumber = intent.getIntExtra("DAY_NUMBER", 1)
        dayTitle = intent.getStringExtra("DAY_TITLE") ?: "Ngày $dayNumber"
        exerciseType = intent.getStringExtra("EXERCISE_TYPE") ?: "abs"
        exerciseIds = intent.getStringArrayListExtra("EXERCISE_IDS") ?: arrayListOf()
    }

    // Chức năng: lấy đúng danh sách bài tập trong ngày hiện tại dựa theo exerciseIds.
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

    // Chức năng: xử lý các nút trên màn tập và màn nghỉ.
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
            } else {
                Toast.makeText(this, "Đây là bài đầu tiên", Toast.LENGTH_SHORT).show()
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

    // Chức năng: xử lý nút back vật lý/gesture bằng cách hiện bảng tạm dừng.
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

    // Chức năng: ẩn giao diện nghỉ khi chuyển sang chuẩn bị/tập.
    private fun hideRestScreen() {
        layoutRestScreen.visibility = View.GONE
        videoRestNextExercise.stopPlayback()
    }

    // Chức năng: giai đoạn chuẩn bị 10 giây đầu tiên trước bài đầu tiên.
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

    // Chức năng: giai đoạn giọng đọc 6 giây trước khi tập.
    // Giao diện vẫn là màn tập 00:30 nhưng timer chưa chạy.
    private fun startVoiceGuidePhase() {
        hideRestScreen()

        currentPhase = SessionPhase.VOICE_GUIDE
        secondsLeft = voiceGuideSeconds
        spokenVoiceMarks.clear()

        val exercise = exerciseList[currentIndex]

        txtSessionInfo.text = "${currentIndex + 1}/${exerciseList.size}"

        txtPhaseTitle.visibility = View.GONE
        txtSessionExerciseName.text = exercise.name
        txtSessionTimer.text = formatTime(exercise.duration)

        btnStartNow.visibility = View.GONE
        layoutExerciseControls.visibility = View.VISIBLE
        btnPauseResume.text = "Ⅱ  TẠM DỪNG"

        updatePreviousButton()
        playMainExerciseVideo(exercise)
        startTimer()
    }

    // Chức năng: giai đoạn tập thật, lúc này timer 00:30 mới bắt đầu đếm ngược.
    private fun startExercisePhase() {
        hideRestScreen()

        currentPhase = SessionPhase.EXERCISE
        spokenExerciseEndMarks.clear()

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
        playMainExerciseVideo(exercise)
        startTimer()
    }

    // Chức năng: đọc đếm ngược 3 2 1 ở cuối thời gian tập thật.
// Khi bài tập còn 3 giây, 2 giây, 1 giây thì app sẽ đọc tương ứng.
    private fun handleExerciseEndingCue(second: Int) {
        if (currentPhase != SessionPhase.EXERCISE) return
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

    // Chức năng: chờ ngắn sau khi đọc "Bắt đầu", rồi mới chạy timer tập thật.
    private fun startExercisePhaseAfterShortDelay() {
        mainHandler.removeCallbacksAndMessages(null)

        mainHandler.postDelayed({
            startExercisePhase()
        }, 900)
    }

    // Chức năng: giai đoạn nghỉ 10 giây giữa hai bài.
    // Có giao diện riêng, có +20s, bỏ qua, và gợi ý bài tiếp theo.
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
        txtRestNextName.text = "${nextExercise.name} ${formatTime(nextExercise.duration)}"

        playRestNextExerciseVideo(nextExercise)

        speak("Nghỉ ngơi 10 giây")
        startTimer()
    }

    // Chức năng: cộng thêm 20 giây vào thời gian nghỉ hiện tại.
    private fun addRestTime() {
        if (currentPhase != SessionPhase.REST) return

        countDownTimer?.cancel()
        secondsLeft += 20
        txtRestTimer.text = secondsLeft.toString()
        startTimer()
    }

    // Chức năng: chạy timer cho chuẩn bị, giọng đọc, tập và nghỉ.
// Trong VOICE_GUIDE: đọc tên bài + 3 2 1.
// Trong EXERCISE: đọc 3 2 1 ở 3 giây cuối bài.
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

    // Chức năng: xử lý giọng đọc trong 6 giây trước khi tập.
    private fun handleVoiceGuideCue(second: Int) {
        if (currentPhase != SessionPhase.VOICE_GUIDE) return
        if (spokenVoiceMarks.contains(second)) return

        val exercise = exerciseList[currentIndex]

        when (second) {
            6 -> {
                spokenVoiceMarks.add(second)
                speak("${exercise.name}, ${exercise.duration} giây")
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

    // Chức năng: cập nhật đồng hồ theo từng giai đoạn.
    private fun updateTimerText() {
        when (currentPhase) {
            SessionPhase.PREPARE -> {
                txtSessionTimer.text = secondsLeft.toString()
            }

            SessionPhase.VOICE_GUIDE -> {
                val exercise = exerciseList[currentIndex]
                txtSessionTimer.text = formatTime(exercise.duration)
            }

            SessionPhase.EXERCISE -> {
                txtSessionTimer.text = formatTime(secondsLeft)
            }

            SessionPhase.REST -> {
                txtRestTimer.text = secondsLeft.toString()
            }
        }
    }

    // Chức năng: sau khi tập xong một bài thì chuyển sang nghỉ, nếu là bài cuối thì hoàn thành ngày.
    private fun finishExerciseAndMoveNext() {
        if (currentIndex == exerciseList.size - 1) {
            showWorkoutCompleted()
        } else {
            startRestPhase()
        }
    }

    // Chức năng: sau khi nghỉ xong hoặc bấm bỏ qua, chuyển sang bài tiếp theo.
    private fun goToNextExerciseVoiceGuide() {
        if (currentIndex < exerciseList.size - 1) {
            currentIndex++
            startVoiceGuidePhase()
        } else {
            showWorkoutCompleted()
        }
    }

    // Chức năng: hiện bảng tạm dừng phủ lên màn hình.
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

    // Chức năng: khởi động lại giai đoạn hiện tại.
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

    // Chức năng: tiếp tục buổi tập từ thời gian đang dừng.
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

        startTimer()
    }

    // Chức năng: tạm dừng video đang chạy tùy theo giai đoạn hiện tại.
    private fun pauseCurrentVideo() {
        if (currentPhase == SessionPhase.REST) {
            videoRestNextExercise.pause()
        } else {
            videoSessionExercise.pause()
        }
    }

    // Chức năng: làm mờ nút "Trước đó" nếu đang ở bài đầu tiên.
    private fun updatePreviousButton() {
        btnPreviousExercise.alpha = if (currentIndex == 0) 0.35f else 1f
    }

    // Chức năng: xử lý khi người dùng hoàn thành toàn bộ bài tập trong ngày.
    // Hàm này sẽ dừng timer, dừng video, lưu tiến độ ngày tập,
    // tính tổng số bài, tổng calo, tổng thời gian,
    // sau đó chuyển sang màn hình chúc mừng WorkoutCompletedActivity.
    private fun showWorkoutCompleted() {
        countDownTimer?.cancel()
        mainHandler.removeCallbacksAndMessages(null)

        videoSessionExercise.stopPlayback()
        videoRestNextExercise.stopPlayback()

        stopSpeech()

        PlanProgressManager.completeDay(this, dayNumber)

        val totalExercises = exerciseList.size

        val totalCalories = exerciseList.sumOf { exercise ->
            exercise.calories
        }

        val totalDurationSeconds = exerciseList.sumOf { exercise ->
            exercise.duration
        }

        val intent = Intent(this, WorkoutCompletedActivity::class.java)
        intent.putExtra("DAY_NUMBER", dayNumber)
        intent.putExtra("EXERCISE_COUNT", totalExercises)
        intent.putExtra("TOTAL_CALORIES", totalCalories)
        intent.putExtra("TOTAL_DURATION_SECONDS", totalDurationSeconds)

        startActivity(intent)
        finish()
    }


    // Chức năng: phát video bài tập ở màn chính.
    private fun playMainExerciseVideo(exercise: Exercise) {
        playVideoToView(
            videoView = videoSessionExercise,
            emptyText = txtSessionEmpty,
            exercise = exercise
        )
    }

    // Chức năng: phát video bài tiếp theo trong màn nghỉ ngơi.
    private fun playRestNextExerciseVideo(exercise: Exercise) {
        playVideoToView(
            videoView = videoRestNextExercise,
            emptyText = txtRestNextEmpty,
            exercise = exercise
        )
    }

    // Chức năng: phát video của bài tập vào một VideoView bất kỳ.
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

    // Chức năng: copy video từ assets ra cache để VideoView phát ổn định.
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

    // Chức năng: đổi giây sang định dạng mm:ss.
    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainSeconds = seconds % 60
        return "%02d:%02d".format(minutes, remainSeconds)
    }

    override fun onPause() {
        super.onPause()
        countDownTimer?.cancel()
        mainHandler.removeCallbacksAndMessages(null)
        videoSessionExercise.pause()
        videoRestNextExercise.pause()
        stopSpeech()
    }

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