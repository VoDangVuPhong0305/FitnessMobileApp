package com.example.fitnessmobileapp.data.repository

import android.content.Context
import com.example.fitnessmobileapp.data.model.Exercise
import com.example.fitnessmobileapp.data.model.PlanDay
import com.example.fitnessmobileapp.data.model.WorkoutPlanCategories
import com.example.fitnessmobileapp.data.model.WorkoutPlanCategory

object WorkoutPlanProvider {

    // Lấy danh sách ngày tập theo loại kế hoạch người dùng chọn.
    // Cả 4 nhóm: Toàn thân, Cơ bụng, Tay & Ngực, Chân
    // đều đi chung qua luồng tạo kế hoạch tự động theo BMI.
    fun getPlanDays(
        context: Context,
        planId: String
    ): List<PlanDay> {
        return when (planId) {
            WorkoutPlanCategories.FULL_BODY_ID,
            WorkoutPlanCategories.ARMS_CHEST_ID,
            WorkoutPlanCategories.LEGS_ID,
            WorkoutPlanCategories.ABS_ID -> {
                val planCategory = WorkoutPlanCategories.getPlanById(planId)

                createGeneratedPlan(
                    context = context,
                    planCategory = planCategory
                )
            }

            else -> {
                val planCategory = WorkoutPlanCategories.getPlanById(
                    WorkoutPlanCategories.FULL_BODY_ID
                )

                createGeneratedPlan(
                    context = context,
                    planCategory = planCategory
                )
            }
        }
    }

    // Chức năng: lấy thông tin kế hoạch theo id.
    // Ví dụ truyền "legs" thì trả về thông tin kế hoạch Tập chân.
    fun getPlanCategory(planId: String): WorkoutPlanCategory {
        return WorkoutPlanCategories.getPlanById(planId)
    }

    // Chức năng: tạo lộ trình 30 ngày cho các kế hoạch tự sinh.
    // Hàm này lấy bài tập theo nhóm, cá nhân hóa theo BMI, rồi đưa vào WorkoutPlanGenerator.
    private fun createGeneratedPlan(
        context: Context,
        planCategory: WorkoutPlanCategory
    ): List<PlanDay> {
        val exercises = WorkoutDataReader.getExercisesByType(
            context = context,
            exerciseType = planCategory.exerciseType
        )

        val personalizedExercises = personalizeExercisesByUserProfile(
            context = context,
            exercises = exercises
        )

        return WorkoutPlanGenerator.generateThirtyDayPlan(
            planTitle = planCategory.title,
            exerciseType = planCategory.exerciseType,
            exercises = personalizedExercises
        )
    }

    // Chức năng: cá nhân hóa thứ tự bài tập dựa trên BMI và khoảng cách tới cân nặng mục tiêu.
    // Nếu còn xa mục tiêu thì ưu tiên bài đốt calo hơn.
    // Nếu gần đạt mục tiêu thì chuyển dần sang bài giữ dáng, siết cơ nhẹ.
    // Các ngày đã tập không bị reset tiến độ, chỉ danh sách bài được gợi ý lại theo thông tin mới.
    private fun personalizeExercisesByUserProfile(
        context: Context,
        exercises: List<Exercise>
    ): List<Exercise> {
        if (exercises.isEmpty()) {
            return exercises
        }

        val userProfile = readUserProfile(context)

        val weightGap = userProfile.currentWeight - userProfile.targetWeight

        return when {
            // Người đang muốn giảm cân
            weightGap > 0.5f -> {
                when {
                    // Còn xa mục tiêu: giảm cân mạnh hơn, ưu tiên bài đốt calo
                    weightGap >= 5f || userProfile.currentBMI >= 27f -> {
                        exercises.sortedByDescending { exercise ->
                            getLoseWeightScore(exercise)
                        }
                    }

                    // Gần đạt mục tiêu: bớt cardio nặng, tăng bài giữ dáng / siết cơ nhẹ
                    weightGap <= 2f -> {
                        exercises.sortedByDescending { exercise ->
                            getNearTargetLoseWeightScore(exercise)
                        }
                    }

                    // Ở giữa: pha trộn giữa giảm cân và giữ dáng
                    else -> {
                        exercises.sortedByDescending { exercise ->
                            getLoseWeightScore(exercise) + getKeepFitScore(exercise)
                        }
                    }
                }
            }

            // Người đang muốn tăng cân / tăng cơ
            weightGap < -0.5f -> {
                val gainGap = -weightGap

                when {
                    // Cần tăng nhiều: ưu tiên bài tăng cơ nhẹ, ít cardio nặng
                    gainGap >= 4f || userProfile.currentBMI < 18.5f -> {
                        exercises.sortedByDescending { exercise ->
                            getGainWeightScore(exercise)
                        }
                    }

                    // Gần đạt mục tiêu tăng cân: chuyển sang giữ dáng, ổn định cơ thể
                    gainGap <= 2f -> {
                        exercises.sortedByDescending { exercise ->
                            getKeepFitScore(exercise) + getGainWeightScore(exercise)
                        }
                    }

                    else -> {
                        exercises.sortedByDescending { exercise ->
                            getGainWeightScore(exercise)
                        }
                    }
                }
            }

            // Cân nặng gần mục tiêu: giữ dáng
            else -> {
                exercises.sortedByDescending { exercise ->
                    getKeepFitScore(exercise)
                }
            }
        }
    }

    // Chức năng: đọc BMI và cân nặng đã lưu từ onboarding.
    private fun readUserProfile(context: Context): UserWorkoutProfile {
        val loginPrefs = context.getSharedPreferences("login_data", Context.MODE_PRIVATE)
        val username = loginPrefs.getString("current_user", "guest") ?: "guest"

        val profilePrefs = context.getSharedPreferences(
            "user_${username}_profile",
            Context.MODE_PRIVATE
        )

        return UserWorkoutProfile(
            currentBMI = profilePrefs.getFloat("currentBMI", 22f),
            currentWeight = profilePrefs.getFloat("currentWeight", 65f),
            targetWeight = profilePrefs.getFloat("targetWeight", 65f)
        )
    }

    // Chức năng: chấm điểm bài tập cho người đang giảm cân nhưng đã gần đạt mục tiêu.
    // Ưu tiên bài giữ dáng, siết cơ, ổn định cơ thể.
    // Vẫn giữ một ít bài đốt calo nhưng không quá nặng như giai đoạn đầu.
    private fun getNearTargetLoseWeightScore(exercise: Exercise): Int {
        var score = 0

        when (exercise.level) {
            "Beginner" -> score += 12
            "Medium" -> score += 8
        }

        when (exercise.intensity) {
            "Medium" -> score += 12
            "Low" -> score += 10
            "High" -> score += 3
        }

        score += exercise.calories

        val name = exercise.name.lowercase()

        if (name.contains("đo sàn")) score += 14
        if (name.contains("plank")) score += 14
        if (name.contains("gánh đùi")) score += 12
        if (name.contains("squat")) score += 12
        if (name.contains("cây cầu")) score += 12
        if (name.contains("chống đẩy")) score += 10
        if (name.contains("gập bụng")) score += 10
        if (name.contains("tấn")) score += 8

        // Vẫn giữ cardio, nhưng không ưu tiên quá mạnh khi đã gần đạt mục tiêu
        if (name.contains("đấm")) score += 5
        if (name.contains("bật nhảy")) score += 2
        if (name.contains("leo núi")) score += 2

        return score
    }

    // Chức năng: chấm điểm bài tập cho người muốn giảm cân.
    // Ưu tiên bài đốt calo cao, cường độ vừa, vận động nhiều.
    private fun getLoseWeightScore(exercise: Exercise): Int {
        var score = 0

        score += exercise.calories * 3

        when (exercise.intensity) {
            "High" -> score += 8
            "Medium" -> score += 12
            "Low" -> score += 3
        }

        when (exercise.level) {
            "Beginner" -> score += 8
            "Medium" -> score += 6
        }

        val name = exercise.name.lowercase()

        if (name.contains("bật nhảy")) score += 10
        if (name.contains("leo núi")) score += 10
        if (name.contains("đấm")) score += 8
        if (name.contains("đạp xe")) score += 8
        if (name.contains("gập người")) score += 6
        if (name.contains("tấn")) score += 5

        return score
    }

    // Chức năng: chấm điểm bài tập cho người muốn tăng cân / tăng cơ nhẹ.
    // Ưu tiên bài ổn định, ít cardio nặng, phù hợp người mới.
    private fun getGainWeightScore(exercise: Exercise): Int {
        var score = 0

        when (exercise.level) {
            "Beginner" -> score += 12
            "Medium" -> score += 5
        }

        when (exercise.intensity) {
            "Low" -> score += 10
            "Medium" -> score += 8
            "High" -> score -= 5
        }

        val name = exercise.name.lowercase()

        if (name.contains("chống đẩy")) score += 12
        if (name.contains("gánh đùi")) score += 12
        if (name.contains("đứng tấn")) score += 10
        if (name.contains("cây cầu")) score += 10
        if (name.contains("đo sàn")) score += 8
        if (name.contains("tấn sau")) score += 8

        if (name.contains("bật nhảy")) score -= 8
        if (name.contains("leo núi")) score -= 8
        if (name.contains("tung chân")) score -= 8

        return score
    }

    // Chức năng: chấm điểm bài tập cho người BMI bình thường / giữ dáng.
    // Ưu tiên bài cân bằng giữa toàn thân, cơ bụng, tay ngực và chân.
    private fun getKeepFitScore(exercise: Exercise): Int {
        var score = 0

        score += exercise.calories * 2

        when (exercise.intensity) {
            "Medium" -> score += 12
            "Low" -> score += 7
            "High" -> score += 4
        }

        when (exercise.level) {
            "Beginner" -> score += 10
            "Medium" -> score += 6
        }

        val name = exercise.name.lowercase()

        if (name.contains("bật nhảy")) score += 7
        if (name.contains("gánh đùi")) score += 7
        if (name.contains("chống đẩy")) score += 7
        if (name.contains("gập")) score += 7
        if (name.contains("đo sàn")) score += 7
        if (name.contains("cây cầu")) score += 5

        return score
    }

    // Dữ liệu tạm để chứa thông tin người dùng đọc từ SharedPreferences.
    private data class UserWorkoutProfile(
        val currentBMI: Float,
        val currentWeight: Float,
        val targetWeight: Float
    )
}