package com.example.fitnessmobileapp.ui.plan

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessmobileapp.R
import java.util.Locale

class WorkoutCompletedActivity : AppCompatActivity() {

    private lateinit var txtCompletedDay: TextView
    private lateinit var txtExerciseCount: TextView
    private lateinit var txtCalories: TextView
    private lateinit var txtDuration: TextView
    private lateinit var btnDoneCompleted: TextView

    private var dayNumber: Int = 1
    private var exerciseCount: Int = 0
    private var totalCalories: Int = 0
    private var totalDurationSeconds: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_completed)

        bindViews()
        getIntentData()
        showCompletedData()
        setupButtons()
    }

    // Chức năng: ánh xạ các View trong file activity_workout_completed.xml sang Kotlin.
    private fun bindViews() {
        txtCompletedDay = findViewById(R.id.txtCompletedDay)
        txtExerciseCount = findViewById(R.id.txtExerciseCount)
        txtCalories = findViewById(R.id.txtCalories)
        txtDuration = findViewById(R.id.txtDuration)
        btnDoneCompleted = findViewById(R.id.btnDoneCompleted)
    }

    // Chức năng: nhận dữ liệu được gửi từ WorkoutSessionActivity sau khi tập xong.
    private fun getIntentData() {
        dayNumber = intent.getIntExtra("DAY_NUMBER", 1)
        exerciseCount = intent.getIntExtra("EXERCISE_COUNT", 0)
        totalCalories = intent.getIntExtra("TOTAL_CALORIES", 0)
        totalDurationSeconds = intent.getIntExtra("TOTAL_DURATION_SECONDS", 0)
    }

    // Chức năng: hiển thị ngày hoàn thành, số bài tập, calo và thời lượng lên giao diện.
    private fun showCompletedData() {
        txtCompletedDay.text = "NGÀY $dayNumber ĐÃ HOÀN THÀNH"
        txtExerciseCount.text = exerciseCount.toString()
        txtCalories.text = totalCalories.toString()
        txtDuration.text = formatDuration(totalDurationSeconds)
    }

    // Chức năng: xử lý nút XONG để đóng màn hình chúc mừng.
    private fun setupButtons() {
        btnDoneCompleted.setOnClickListener {
            finish()
        }
    }

    // Chức năng: đổi tổng số giây sang dạng phút:giây, ví dụ 360 giây thành 06:00.
    private fun formatDuration(seconds: Int): String {
        val minutes = seconds / 60
        val remainSeconds = seconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainSeconds)
    }
}