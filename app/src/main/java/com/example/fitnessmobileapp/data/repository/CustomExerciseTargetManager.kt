package com.example.fitnessmobileapp.data.repository

import android.content.Context
import com.example.fitnessmobileapp.data.model.Exercise
import kotlin.math.roundToInt

object CustomExerciseTargetManager {

    // Chức năng: lấy tên tài khoản hiện tại để lưu số lần/thời gian chỉnh riêng cho từng người dùng.
    private fun getCurrentUsername(context: Context): String {
        val loginPrefs = context.getSharedPreferences("login_data", Context.MODE_PRIVATE)
        return loginPrefs.getString("current_user", "guest") ?: "guest"
    }

    // Chức năng: lấy SharedPreferences dùng để lưu số lần/thời gian đã chỉnh của từng bài tập.
    private fun getPrefs(context: Context) =
        context.getSharedPreferences(
            "user_${getCurrentUsername(context)}_custom_exercise_target",
            Context.MODE_PRIVATE
        )

    // Chức năng: tạo key riêng cho từng bài tập theo loại bài, ngày tập và id bài tập.
    // Ví dụ: arms_chest_1_push_up
    private fun getKeyPrefix(
        exerciseType: String,
        dayNumber: Int,
        exerciseId: String
    ): String {
        return "${exerciseType}_${dayNumber}_${exerciseId}"
    }

    // Chức năng: lấy số lần/thời gian mặc định của bài tập theo dữ liệu ban đầu.
    // Ví dụ: Chống đẩy ngày 1 mặc định là x6.
    fun getDefaultTarget(
        exercise: Exercise,
        dayNumber: Int
    ): ExerciseTargetHelper.ExerciseTarget {
        return ExerciseTargetHelper.getTarget(
            exerciseId = exercise.id,
            exerciseName = exercise.name,
            dayNumber = dayNumber
        )
    }

    // Chức năng: lấy số lần/thời gian hiện tại của bài tập.
    // Nếu người dùng chưa chỉnh thì trả về giá trị mặc định.
    // Nếu người dùng đã bấm Lưu thì trả về giá trị đã lưu.
    fun getTarget(
        context: Context,
        exerciseType: String,
        dayNumber: Int,
        exercise: Exercise
    ): ExerciseTargetHelper.ExerciseTarget {
        val defaultTarget = getDefaultTarget(
            exercise = exercise,
            dayNumber = dayNumber
        )

        val keyPrefix = getKeyPrefix(
            exerciseType = exerciseType,
            dayNumber = dayNumber,
            exerciseId = exercise.id
        )

        val prefs = getPrefs(context)

        val savedType = prefs.getString("${keyPrefix}_type", defaultTarget.type)
            ?: defaultTarget.type

        val savedValue = prefs.getInt("${keyPrefix}_value", defaultTarget.value)

        return ExerciseTargetHelper.ExerciseTarget(
            type = savedType,
            value = savedValue
        )
    }

    // Chức năng: lưu số lần/thời gian mới sau khi người dùng chỉnh trong màn chi tiết bài tập.
    // Nếu người dùng chỉnh về đúng giá trị mặc định thì xóa dữ liệu lưu để tránh lưu dư.
    fun saveTarget(
        context: Context,
        exerciseType: String,
        dayNumber: Int,
        exercise: Exercise,
        target: ExerciseTargetHelper.ExerciseTarget
    ) {
        val defaultTarget = getDefaultTarget(
            exercise = exercise,
            dayNumber = dayNumber
        )

        val keyPrefix = getKeyPrefix(
            exerciseType = exerciseType,
            dayNumber = dayNumber,
            exerciseId = exercise.id
        )

        val editor = getPrefs(context).edit()

        if (
            target.type == defaultTarget.type &&
            target.value == defaultTarget.value
        ) {
            editor
                .remove("${keyPrefix}_type")
                .remove("${keyPrefix}_value")
                .apply()
        } else {
            editor
                .putString("${keyPrefix}_type", target.type)
                .putInt("${keyPrefix}_value", target.value)
                .apply()
        }
    }

    // Chức năng: tính lại thời lượng dự kiến của bài tập khi người dùng đổi số lần.
    // Ví dụ: mặc định x6 = 30 giây, người dùng đổi thành x8 thì thời lượng tăng theo tỉ lệ 8/6.
    fun calculateDurationForTarget(
        baseDurationSeconds: Int,
        defaultTarget: ExerciseTargetHelper.ExerciseTarget,
        currentTarget: ExerciseTargetHelper.ExerciseTarget
    ): Int {
        if (defaultTarget.value <= 0) {
            return baseDurationSeconds
        }

        val ratio = currentTarget.value.toDouble() / defaultTarget.value.toDouble()

        return (baseDurationSeconds * ratio)
            .roundToInt()
            .coerceAtLeast(1)
    }

    // Chức năng: tính lại kcal của bài tập khi người dùng đổi số lần.
    // Ví dụ: mặc định x6 = 8 kcal, người dùng đổi thành x8 thì kcal = 8 * 8 / 6.
    fun calculateCaloriesForTarget(
        baseCalories: Int,
        defaultTarget: ExerciseTargetHelper.ExerciseTarget,
        currentTarget: ExerciseTargetHelper.ExerciseTarget
    ): Double {
        if (defaultTarget.value <= 0) {
            return baseCalories.toDouble()
        }

        val ratio = currentTarget.value.toDouble() / defaultTarget.value.toDouble()

        return baseCalories * ratio
    }

    // Chức năng: tính kcal thực tế cho bài tập theo thời gian.
    // Nếu người dùng chỉ tập một phần thời gian rồi bỏ qua thì kcal chỉ tính theo số giây đã tập.
    fun calculateCaloriesForActualSeconds(
        baseCalories: Int,
        baseDurationSeconds: Int,
        actualSeconds: Int
    ): Double {
        if (baseDurationSeconds <= 0) {
            return baseCalories.toDouble()
        }

        return baseCalories * (actualSeconds.toDouble() / baseDurationSeconds.toDouble())
    }
}