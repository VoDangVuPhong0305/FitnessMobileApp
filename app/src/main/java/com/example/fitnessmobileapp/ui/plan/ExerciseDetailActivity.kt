package com.example.fitnessmobileapp.ui.plan

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
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
import com.example.fitnessmobileapp.data.repository.ExerciseYoutubeLinkHelper
import com.example.fitnessmobileapp.data.repository.WorkoutDataReader
import com.example.fitnessmobileapp.data.repository.CustomExerciseTargetManager
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
    private lateinit var layoutYoutubePreview: LinearLayout
    private lateinit var txtYoutubeTitle: TextView
    private lateinit var txtYoutubeHint: TextView
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

    // Chức năng: lưu màu riêng của kế hoạch hiện tại.
    private var planStartColor: String = "#7B61FF"
    private var planEndColor: String = "#91A8FF"

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

    // Chức năng: lưu lại số lần/thời gian ban đầu khi mở bài tập.
    // Dùng để so sánh xem người dùng có thay đổi bằng nút + hoặc - hay không.
    private var originalTarget = ExerciseTargetHelper.ExerciseTarget(
        ExerciseTargetHelper.TYPE_TIME,
        30
    )

    // Chức năng: khởi tạo màn chi tiết bài tập.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_detail)

        bindViews()
        getIntentData()
        applyPlanColors()
        loadExerciseList()
        setupButtons()
        showExerciseByIndex()
    }

    // Chức năng: ánh xạ View từ XML sang Kotlin.
    private fun bindViews() {
        btnBackExercise = findViewById(R.id.btnBackExercise)

        tabAnimation = findViewById(R.id.tabAnimation)
        tabYoutube = findViewById(R.id.tabYoutube)
        txtTabAnimation = findViewById(R.id.txtTabAnimation)
        txtTabYoutube = findViewById(R.id.txtTabYoutube)
        lineAnimation = findViewById(R.id.lineAnimation)
        lineYoutube = findViewById(R.id.lineYoutube)

        videoExercise = findViewById(R.id.videoExercise)
        layoutYoutubePreview = findViewById(R.id.layoutYoutubePreview)
        txtYoutubeTitle = findViewById(R.id.txtYoutubeTitle)
        txtYoutubeHint = findViewById(R.id.txtYoutubeHint)
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

    // Chức năng: lấy dữ liệu được truyền từ màn chi tiết ngày.
    private fun getIntentData() {
        dayNumber = intent.getIntExtra("DAY_NUMBER", 1)
        exerciseId = intent.getStringExtra("EXERCISE_ID") ?: ""
        exerciseType = intent.getStringExtra("EXERCISE_TYPE") ?: "abs"
        exerciseIds = intent.getStringArrayListExtra("EXERCISE_IDS") ?: arrayListOf()

        // Chức năng: nhận màu riêng của kế hoạch từ PlanDayDetailActivity.
        planStartColor = intent.getStringExtra("PLAN_START_COLOR") ?: "#7B61FF"
        planEndColor = intent.getStringExtra("PLAN_END_COLOR") ?: "#91A8FF"
    }

    // Chức năng: lấy danh sách bài tập đúng theo nhóm và đúng các bài trong ngày.
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

    // Chức năng: lấy danh sách bài tập theo loại kế hoạch.
    private fun getExercisesByType(): List<Exercise> {
        return when (exerciseType) {
            "abs" -> WorkoutDataReader.getAbsExercises(this)
            "legs" -> WorkoutDataReader.getLegExercises(this)
            "arms_chest" -> WorkoutDataReader.getArmsChestExercises(this)
            "full_body" -> WorkoutDataReader.getFullBodyExercises(this)
            else -> WorkoutDataReader.getAllExercises(this)
        }
    }

    // Chức năng: xử lý các nút bấm trong màn chi tiết bài tập.
    private fun setupButtons() {
        btnBackExercise.setOnClickListener {
            finish()
        }

        btnCloseExercise.setOnClickListener {
            if (isTargetChanged()) {
                saveCurrentTargetAndClose()
            } else {
                finish()
            }
        }

        tabAnimation.setOnClickListener {
            showAnimationTab()
        }

        tabYoutube.setOnClickListener {
            showYoutubeTab()
        }

        layoutYoutubePreview.setOnClickListener {
            openYoutubeExternal()
        }

        // Chức năng: giảm thời lượng hoặc số lần.
        // Bài time: giảm 5 giây.
        // Bài reps: giảm 1 lần.
        btnMinus.setOnClickListener {
            currentTarget = ExerciseTargetHelper.decreaseTarget(currentTarget)
            updateTargetUI()
            updateCloseButtonState()
        }

        // Chức năng: tăng thời lượng hoặc số lần.
        // Bài time: tăng 5 giây.
        // Bài reps: tăng 1 lần.
        btnPlus.setOnClickListener {
            currentTarget = ExerciseTargetHelper.increaseTarget(currentTarget)
            updateTargetUI()
            updateCloseButtonState()
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

    // Chức năng: hiển thị bài tập theo vị trí hiện tại.
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
        // Gánh đùi -> Lần lặp lại x12.
        currentTarget = CustomExerciseTargetManager.getTarget(
            context = this,
            exerciseType = exerciseType,
            dayNumber = dayNumber,
            exercise = exercise
        )

        // Chức năng: lưu lại giá trị ban đầu khi vừa mở bài.
        // Dùng để biết người dùng có chỉnh khác ban đầu hay không.
        originalTarget = ExerciseTargetHelper.ExerciseTarget(
            currentTarget.type,
            currentTarget.value
        )

        updateTargetUI()
        updateCloseButtonState()

        txtExerciseDescription.text = if (exercise.description.isEmpty()) {
            "Chưa có mô tả bài tập."
        } else {
            exercise.description.joinToString(separator = "\n\n")
        }

        txtExerciseCounter.text = "${currentIndex + 1} / ${exerciseList.size}"

        updateNavigationButtons()
        showAnimationTab()
    }

    // Chức năng: cập nhật chữ “Thời lượng/Lần lặp lại” và giá trị 00:30/x12.
    private fun updateTargetUI() {
        txtTargetLabel.text = ExerciseTargetHelper.getTargetLabel(currentTarget)
        txtExerciseDuration.text = ExerciseTargetHelper.getTargetText(currentTarget)
    }

    // Chức năng: làm mờ nút trước/sau nếu đang ở bài đầu hoặc bài cuối.
    private fun updateNavigationButtons() {
        btnPreviousExercise.alpha = if (currentIndex == 0) 0.35f else 1f
        btnNextExercise.alpha = if (currentIndex == exerciseList.size - 1) 0.35f else 1f
    }

    // Chức năng: hiển thị tab Hoạt hình và phát video offline trong assets.
    private fun showAnimationTab() {
        txtTabAnimation.setTextColor(0xFF222222.toInt())
        txtTabYoutube.setTextColor(0xFF777777.toInt())

        lineAnimation.visibility = View.VISIBLE
        lineYoutube.visibility = View.INVISIBLE

        layoutYoutubePreview.visibility = View.GONE
        txtVideoEmpty.visibility = View.GONE
        videoExercise.visibility = View.VISIBLE

        playAnimationVideo()
    }

    // Chức năng: hiển thị tab Video bằng khung xem YouTube ổn định.
    // Không nhúng YouTube trong WebView để tránh lỗi 152-4 / 153.
    private fun showYoutubeTab() {
        val exercise = currentExercise ?: return

        txtTabAnimation.setTextColor(0xFF777777.toInt())
        txtTabYoutube.setTextColor(0xFF222222.toInt())

        lineAnimation.visibility = View.INVISIBLE
        lineYoutube.visibility = View.VISIBLE

        videoExercise.stopPlayback()
        videoExercise.visibility = View.GONE
        txtVideoEmpty.visibility = View.GONE
        layoutYoutubePreview.visibility = View.VISIBLE

        val youtubeUrl = getCurrentYoutubeUrl()

        if (youtubeUrl.isBlank()) {
            txtYoutubeTitle.text = "Chưa có video hướng dẫn"
            txtYoutubeHint.text = "Bài tập này chưa được thêm link YouTube"
            layoutYoutubePreview.isEnabled = false
            layoutYoutubePreview.alpha = 0.7f
        } else {
            txtYoutubeTitle.text = "Video hướng dẫn ${exercise.name}"
            txtYoutubeHint.text = "Nhấn để xem trên YouTube"
            layoutYoutubePreview.isEnabled = true
            layoutYoutubePreview.alpha = 1f
        }
    }

    // Chức năng: mở video YouTube bằng app YouTube hoặc trình duyệt.
    private fun openYoutubeExternal() {
        val youtubeUrl = getCurrentYoutubeUrl()

        if (youtubeUrl.isBlank()) {
            Toast.makeText(this, "Chưa có video YouTube", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Không thể mở YouTube", Toast.LENGTH_SHORT).show()
        }
    }

    // Chức năng: lấy link YouTube của bài hiện tại.
    // Ưu tiên link trong JSON, nếu chưa có thì lấy từ ExerciseYoutubeLinkHelper.
    private fun getCurrentYoutubeUrl(): String {
        val exercise = currentExercise ?: return ""

        return ExerciseYoutubeLinkHelper.getYoutubeUrl(
            exerciseId = exercise.id,
            exerciseName = exercise.name,
            defaultUrl = exercise.youtubeUrl
        )
    }

    // Chức năng: phát video hoạt hình offline của bài tập.
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

    // Chức năng: copy video từ assets sang cache để VideoView có thể phát.
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

    // Chức năng: kiểm tra người dùng có chỉnh số lần/thời gian khác với giá trị ban đầu hay không.
    // Nếu có thay đổi thì nút ĐÓNG sẽ đổi thành LƯU.
    private fun isTargetChanged(): Boolean {
        return currentTarget.type != originalTarget.type ||
                currentTarget.value != originalTarget.value
    }

    // Chức năng: cập nhật trạng thái nút cuối màn hình.
    // Nếu người dùng chưa chỉnh gì thì hiển thị ĐÓNG.
    // Nếu người dùng đã bấm + hoặc - làm thay đổi số lần/thời gian thì hiển thị LƯU.
    private fun updateCloseButtonState() {
        if (isTargetChanged()) {
            btnCloseExercise.text = "LƯU"
        } else {
            btnCloseExercise.text = "ĐÓNG"
        }

        btnCloseExercise.background = createPlanButtonBackground()
    }

    // Chức năng: lưu số lần/thời gian mới của bài tập sau khi người dùng bấm LƯU.
    // Sau khi lưu xong sẽ quay lại màn danh sách bài tập của ngày đó.
    private fun saveCurrentTargetAndClose() {
        val exercise = currentExercise ?: return

        CustomExerciseTargetManager.saveTarget(
            context = this,
            exerciseType = exerciseType,
            dayNumber = dayNumber,
            exercise = exercise,
            target = currentTarget
        )

        Toast.makeText(
            this,
            "Đã lưu thay đổi",
            Toast.LENGTH_SHORT
        ).show()

        setResult(RESULT_OK)
        finish()
    }

    // Chức năng: áp dụng màu riêng của kế hoạch cho màn chi tiết bài tập.
    private fun applyPlanColors() {
        val startColor = Color.parseColor(planStartColor)

        // Đổi màu gạch dưới tab Hoạt hình / Video.
        lineAnimation.setBackgroundColor(startColor)
        lineYoutube.setBackgroundColor(startColor)

        // Đổi màu nút tăng/giảm thời lượng hoặc số lần.
        btnMinus.setTextColor(startColor)
        btnPlus.setTextColor(startColor)

        // Đổi màu nút chuyển bài.
        btnPreviousExercise.setTextColor(startColor)
        btnNextExercise.setTextColor(startColor)

        // Đổi màu nút ĐÓNG.
        btnCloseExercise.background = createPlanButtonBackground()
    }

    // Chức năng: tạo nền gradient theo màu kế hoạch.
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

    // Chức năng: đổi dp sang pixel.
    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    // Chức năng: khi tạm dừng màn hình thì tạm dừng video offline.
    override fun onPause() {
        super.onPause()
        videoExercise.pause()
    }

    // Chức năng: giải phóng tài nguyên khi thoát màn chi tiết bài tập.
    override fun onDestroy() {
        super.onDestroy()
        videoExercise.stopPlayback()
    }
}