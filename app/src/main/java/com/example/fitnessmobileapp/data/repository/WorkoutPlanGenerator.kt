package com.example.fitnessmobileapp.data.repository

import com.example.fitnessmobileapp.data.model.Exercise
import com.example.fitnessmobileapp.data.model.PlanDay

object WorkoutPlanGenerator {

    private val restDays = listOf(4, 8, 12, 16, 20, 24, 28)

    // Chức năng: tự tạo lộ trình 30 ngày từ danh sách bài tập có sẵn.
    // Hàm này dùng cho các kế hoạch như toàn thân, tay ngực, chân.
    fun generateThirtyDayPlan(
        planTitle: String,
        exerciseType: String,
        exercises: List<Exercise>
    ): List<PlanDay> {
        val planDays = mutableListOf<PlanDay>()

        for (dayNumber in 1..30) {
            if (restDays.contains(dayNumber)) {
                planDays.add(
                    createRestDay(
                        dayNumber = dayNumber,
                        planTitle = planTitle,
                        exerciseType = exerciseType
                    )
                )
            } else {
                val exerciseCount = getExerciseCountForDay(dayNumber)
                val selectedExercises = pickExercisesForDay(
                    exercises = exercises,
                    dayNumber = dayNumber,
                    exerciseCount = exerciseCount
                )

                planDays.add(
                    createWorkoutDay(
                        dayNumber = dayNumber,
                        planTitle = planTitle,
                        exerciseType = exerciseType,
                        selectedExercises = selectedExercises
                    )
                )
            }
        }

        return planDays
    }

    // Chức năng: tạo dữ liệu cho một ngày nghỉ.
    // Ngày nghỉ không có bài tập, không có thời lượng và không có calo.
    private fun createRestDay(
        dayNumber: Int,
        planTitle: String,
        exerciseType: String
    ): PlanDay {
        return PlanDay(
            dayNumber = dayNumber,
            title = "$planTitle - Ngày $dayNumber",
            exerciseCount = 0,
            durationMinutes = 0,
            isRestDay = true,
            exerciseType = exerciseType,
            exerciseIds = emptyList()
        )
    }

    // Chức năng: tạo dữ liệu cho một ngày tập bình thường.
    // Hàm này tính số bài, tổng phút và danh sách id bài tập trong ngày.
    private fun createWorkoutDay(
        dayNumber: Int,
        planTitle: String,
        exerciseType: String,
        selectedExercises: List<Exercise>
    ): PlanDay {
        val totalSeconds = selectedExercises.sumOf { exercise ->
            exercise.duration
        }

        val durationMinutes = if (totalSeconds == 0) {
            0
        } else {
            (totalSeconds + 59) / 60
        }

        val exerciseIds = selectedExercises.map { exercise ->
            exercise.id
        }

        return PlanDay(
            dayNumber = dayNumber,
            title = "$planTitle - Ngày $dayNumber",
            exerciseCount = selectedExercises.size,
            durationMinutes = durationMinutes,
            isRestDay = false,
            exerciseType = exerciseType,
            exerciseIds = exerciseIds
        )
    }

    // Chức năng: quyết định số bài tập theo từng giai đoạn của lộ trình 30 ngày.
    // Giai đoạn đầu ít bài hơn, càng về sau số bài tăng dần.
    private fun getExerciseCountForDay(dayNumber: Int): Int {
        return when {
            dayNumber <= 7 -> 7
            dayNumber <= 15 -> 8
            dayNumber <= 23 -> 9
            else -> 10
        }
    }

    // Chức năng: chọn bài tập cho một ngày cụ thể.
    // Nếu danh sách bài ít hơn số bài cần chọn thì sẽ xoay vòng lại từ đầu.
    private fun pickExercisesForDay(
        exercises: List<Exercise>,
        dayNumber: Int,
        exerciseCount: Int
    ): List<Exercise> {
        if (exercises.isEmpty()) {
            return emptyList()
        }

        val selectedExercises = mutableListOf<Exercise>()
        val startIndex = ((dayNumber - 1) * 2) % exercises.size

        for (index in 0 until exerciseCount) {
            val exerciseIndex = (startIndex + index) % exercises.size
            selectedExercises.add(exercises[exerciseIndex])
        }

        return selectedExercises
    }
}