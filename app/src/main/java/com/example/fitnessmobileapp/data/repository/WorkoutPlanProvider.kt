package com.example.fitnessmobileapp.data.repository

import android.content.Context
import com.example.fitnessmobileapp.data.model.Exercise
import com.example.fitnessmobileapp.data.model.PlanDay
import com.example.fitnessmobileapp.data.model.WorkoutPlanCategories
import com.example.fitnessmobileapp.data.model.WorkoutPlanCategory

object WorkoutPlanProvider {

    // Chức năng: lấy danh sách 30 ngày tập theo id kế hoạch.
    // Tất cả kế hoạch, kể cả Cơ bụng, đều được tự sinh bằng WorkoutPlanGenerator.
    // Sau đó chỉ cá nhân hóa các ngày chưa tập theo BMI/cân nặng.
    fun getPlanDays(
        context: Context,
        planId: String
    ): List<PlanDay> {
        val planLevel = getCurrentPlanLevel(context)

        val completedDay = PlanProgressManager.getCompletedDay(
            context = context,
            planId = planId
        )

        val planCategory = WorkoutPlanCategories.getPlanById(planId)

        return createGeneratedPlan(
            context = context,
            planCategory = planCategory,
            planLevel = planLevel,
            completedDay = completedDay
        )
    }

    // Chức năng: lấy thông tin kế hoạch theo id.
    // Ví dụ truyền "legs" thì trả về thông tin kế hoạch Tập chân.
    fun getPlanCategory(planId: String): WorkoutPlanCategory {
        return WorkoutPlanCategories.getPlanById(planId)
    }

    // Chức năng: tạo lộ trình 30 ngày cho các kế hoạch tự sinh.
    // Sau khi tạo xong chỉ đưa ngày chưa tập qua bước cá nhân hóa theo BMI.
    private fun createGeneratedPlan(
        context: Context,
        planCategory: WorkoutPlanCategory,
        planLevel: String,
        completedDay: Int
    ): List<PlanDay> {
        val exercises = WorkoutDataReader.getExercisesByType(
            context = context,
            exerciseType = planCategory.exerciseType
        )

        val basePlan = WorkoutPlanGenerator.generateThirtyDayPlan(
            planTitle = planCategory.title,
            exerciseType = planCategory.exerciseType,
            exercises = exercises
        )

        return applyPlanLevel(
            context = context,
            planDays = basePlan,
            exerciseType = planCategory.exerciseType,
            planLevel = planLevel,
            completedDay = completedDay
        )
    }

    // Chức năng: đọc cấp độ lộ trình đã lưu trong hồ sơ người dùng.
    // Nếu chưa có planLevel thì tự tính tạm từ BMI.
    private fun getCurrentPlanLevel(context: Context): String {
        val loginPrefs = context.getSharedPreferences("login_data", Context.MODE_PRIVATE)
        val username = loginPrefs.getString("current_user", "guest") ?: "guest"

        val profilePrefs = context.getSharedPreferences(
            "user_${username}_profile",
            Context.MODE_PRIVATE
        )

        val savedPlanLevel = profilePrefs.getString("planLevel", "") ?: ""

        if (
            savedPlanLevel == "underweight" ||
            savedPlanLevel == "normal" ||
            savedPlanLevel == "overweight" ||
            savedPlanLevel == "obese"
        ) {
            return savedPlanLevel
        }

        val currentBMI = getProfileFloat(
            prefs = profilePrefs,
            key = "currentBMI",
            defaultValue = 22f
        ).toDouble()

        return when {
            currentBMI < 18.5 -> "underweight"
            currentBMI < 25.0 -> "normal"
            currentBMI < 30.0 -> "overweight"
            else -> "obese"
        }
    }

    // Chức năng: đọc Float an toàn, tránh lỗi nếu dữ liệu cũ từng lưu dạng Int.
    private fun getProfileFloat(
        prefs: android.content.SharedPreferences,
        key: String,
        defaultValue: Float
    ): Float {
        return try {
            prefs.getFloat(key, defaultValue)
        } catch (e: ClassCastException) {
            prefs.getInt(key, defaultValue.toInt()).toFloat()
        }
    }

    // Chức năng: áp dụng cấp độ lộ trình cho danh sách ngày tập.
    // Chỉ cá nhân hóa các ngày chưa tập, không đụng vào ngày đã hoàn thành.
    private fun applyPlanLevel(
        context: Context,
        planDays: List<PlanDay>,
        exerciseType: String,
        planLevel: String,
        completedDay: Int
    ): List<PlanDay> {
        val allExercises = WorkoutDataReader.getExercisesByType(
            context = context,
            exerciseType = exerciseType
        )

        return planDays.map { planDay ->
            if (planDay.dayNumber <= completedDay) {
                planDay
            } else {
                personalizePlanDay(
                    planDay = planDay,
                    allExercises = allExercises,
                    planLevel = planLevel
                )
            }
        }
    }

    // Chức năng: cá nhân hóa một ngày tập theo nhóm BMI.
    private fun personalizePlanDay(
        planDay: PlanDay,
        allExercises: List<Exercise>,
        planLevel: String
    ): PlanDay {
        if (planDay.isRestDay) {
            return planDay
        }

        val targetExerciseCount = getExerciseCountForDay(
            dayNumber = planDay.dayNumber,
            planLevel = planLevel
        )

        val selectedExerciseIds = buildPersonalizedExerciseIds(
            originalIds = planDay.exerciseIds,
            allExercises = allExercises,
            targetCount = targetExerciseCount,
            dayNumber = planDay.dayNumber,
            planLevel = planLevel
        )

        val durationMinutes = calculateDurationMinutes(
            selectedExerciseIds = selectedExerciseIds,
            allExercises = allExercises,
            oldDurationMinutes = planDay.durationMinutes
        )

        return PlanDay(
            dayNumber = planDay.dayNumber,
            title = planDay.title,
            exerciseCount = selectedExerciseIds.size,
            durationMinutes = durationMinutes,
            isRestDay = false,
            exerciseType = planDay.exerciseType,
            exerciseIds = selectedExerciseIds
        )
    }

    // Chức năng: sắp xếp bài tập theo nhóm BMI.
// Béo phì ưu tiên bài Low, Beginner, ít tác động mạnh.
// Thừa cân ưu tiên Beginner/Medium, cường độ vừa để tăng đốt calo.
// Bình thường giữ mức cân bằng.
// Thiếu cân ưu tiên bài nhẹ, không ép cardio quá mạnh.
    private fun sortExercisesByPlanLevel(
        exercises: List<Exercise>,
        planLevel: String,
        dayNumber: Int
    ): List<Exercise> {
        val rotatedExercises = rotateExercisesByDay(
            exercises = exercises,
            dayNumber = dayNumber
        )

        return when (planLevel) {
            "obese" -> {
                rotatedExercises.sortedWith(
                    compareBy<Exercise>(
                        { if (isHighImpactExercise(it.name)) 1 else 0 },
                        { getIntensityScore(it.intensity) },
                        { getLevelScore(it.level) },
                        { it.calories }
                    )
                )
            }

            "overweight" -> {
                rotatedExercises.sortedWith(
                    compareBy<Exercise>(
                        { if (it.intensity == "High") 1 else 0 },
                        { getLevelScore(it.level) },
                        { if (it.intensity == "Medium") 0 else 1 },
                        { -it.calories }
                    )
                )
            }

            "underweight" -> {
                rotatedExercises.sortedWith(
                    compareBy<Exercise>(
                        { if (it.intensity == "High") 1 else 0 },
                        { getLevelScore(it.level) },
                        { getIntensityScore(it.intensity) },
                        { it.calories }
                    )
                )
            }

            else -> {
                rotatedExercises.sortedWith(
                    compareBy<Exercise>(
                        { getLevelScore(it.level) },
                        { getIntensityScore(it.intensity) }
                    )
                )
            }
        }
    }

    // Chức năng: đổi cường độ bài tập thành điểm để sắp xếp.
    private fun getIntensityScore(intensity: String): Int {
        return when (intensity.lowercase()) {
            "low" -> 0
            "medium" -> 1
            "high" -> 2
            else -> 1
        }
    }

    // Chức năng: đổi độ khó bài tập thành điểm để sắp xếp.
    private fun getLevelScore(level: String): Int {
        return when (level.lowercase()) {
            "beginner" -> 0
            "medium" -> 1
            "advanced" -> 2
            else -> 1
        }
    }

    // Chức năng: xoay danh sách theo ngày để các ngày không bị lấy y chang cùng một nhóm bài.
    private fun rotateExercisesByDay(
        exercises: List<Exercise>,
        dayNumber: Int
    ): List<Exercise> {
        if (exercises.isEmpty()) {
            return exercises
        }

        val startIndex = ((dayNumber - 1) * 2) % exercises.size

        return exercises.drop(startIndex) + exercises.take(startIndex)
    }

    // Chức năng: nhận diện tạm các bài có tác động mạnh, cần hạn chế cho nhóm béo phì.
    private fun isHighImpactExercise(exerciseName: String): Boolean {
        val name = exerciseName.lowercase()

        val highImpactKeywords = listOf(
            "bật",
            "nhảy",
            "leo núi",
            "tung chân",
            "burpee",
            "hít đất",
            "chống đẩy",
            "tấn sau"
        )

        return highImpactKeywords.any { keyword ->
            name.contains(keyword)
        }
    }

    // Chức năng: tạo danh sách bài tập mới cho một ngày.
    // Không chỉ đổi số lượng bài, mà còn chọn bài phù hợp với nhóm BMI.
    private fun buildPersonalizedExerciseIds(
        originalIds: List<String>,
        allExercises: List<Exercise>,
        targetCount: Int,
        dayNumber: Int,
        planLevel: String
    ): List<String> {
        if (targetCount <= 0) {
            return emptyList()
        }

        if (allExercises.isEmpty()) {
            return originalIds.take(targetCount)
        }

        val sortedExercises = sortExercisesByPlanLevel(
            exercises = allExercises,
            planLevel = planLevel,
            dayNumber = dayNumber
        )

        val selectedIds = mutableListOf<String>()

        val originalIdSet = originalIds.toSet()

        // Ưu tiên lấy các bài phù hợp mà vẫn nằm trong lộ trình gốc của ngày đó.
        sortedExercises.forEach { exercise ->
            if (
                selectedIds.size < targetCount &&
                originalIdSet.contains(exercise.id) &&
                !selectedIds.contains(exercise.id)
            ) {
                selectedIds.add(exercise.id)
            }
        }

        // Nếu chưa đủ số bài thì lấy thêm từ danh sách bài đã được sắp xếp theo BMI.
        sortedExercises.forEach { exercise ->
            if (
                selectedIds.size < targetCount &&
                !selectedIds.contains(exercise.id)
            ) {
                selectedIds.add(exercise.id)
            }
        }

        return selectedIds
    }

    // Chức năng: tính lại tổng phút dựa trên danh sách bài tập sau khi cá nhân hóa.
    private fun calculateDurationMinutes(
        selectedExerciseIds: List<String>,
        allExercises: List<Exercise>,
        oldDurationMinutes: Int
    ): Int {
        if (selectedExerciseIds.isEmpty()) {
            return 0
        }

        val exerciseMap = allExercises.associateBy { exercise ->
            exercise.id
        }

        val totalSeconds = selectedExerciseIds.sumOf { exerciseId ->
            exerciseMap[exerciseId]?.duration ?: 60
        }

        if (totalSeconds <= 0) {
            return oldDurationMinutes
        }

        return (totalSeconds + 59) / 60
    }

    // Chức năng: quyết định số bài tập theo từng giai đoạn và nhóm BMI.
// Béo phì tập ít bài hơn thừa cân để giảm áp lực lên khớp và tim mạch.
    private fun getExerciseCountForDay(
        dayNumber: Int,
        planLevel: String
    ): Int {
        return when (planLevel) {
            "underweight" -> {
                when {
                    dayNumber <= 7 -> 4
                    dayNumber <= 15 -> 5
                    dayNumber <= 23 -> 5
                    else -> 6
                }
            }

            "normal" -> {
                when {
                    dayNumber <= 7 -> 6
                    dayNumber <= 15 -> 7
                    dayNumber <= 23 -> 8
                    else -> 8
                }
            }

            "overweight" -> {
                when {
                    dayNumber <= 7 -> 5
                    dayNumber <= 15 -> 6
                    dayNumber <= 23 -> 6
                    else -> 7
                }
            }

            "obese" -> {
                when {
                    dayNumber <= 7 -> 3
                    dayNumber <= 15 -> 4
                    dayNumber <= 23 -> 4
                    else -> 5
                }
            }

            else -> {
                when {
                    dayNumber <= 7 -> 6
                    dayNumber <= 15 -> 7
                    dayNumber <= 23 -> 8
                    else -> 8
                }
            }
        }
    }
}