package com.example.fitnessmobileapp.data.repository

import android.content.Context
import com.example.fitnessmobileapp.data.model.Exercise
import kotlin.math.roundToInt

object CustomExerciseTargetManager {

    private const val PREF_NAME = "custom_exercise_target"

    // Chức năng: lấy SharedPreferences lưu số lần/thời gian riêng cho từng tài khoản.
    private fun getPrefs(context: Context) =
        UserDataPrefs.getUserPrefs(context, PREF_NAME)

    // Chức năng: tạo key riêng cho từng bài tập theo loại bài, ngày tập và id bài tập.
    private fun getKeyPrefix(
        exerciseType: String,
        dayNumber: Int,
        exerciseId: String
    ): String {
        return "${exerciseType}_${dayNumber}_${exerciseId}"
    }

    // Chức năng: lấy số lần/thời gian mặc định của bài tập theo dữ liệu ban đầu.
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

    // Chức năng: xóa custom số lần/thời gian bài tập của tài khoản hiện tại.
    // Dùng khi Đặt lại tiến độ hoặc Xóa tất cả dữ liệu.
    fun clearAllTargets(context: Context) {
        val username = UserDataPrefs.getCurrentUsername(context)
            .trim()
            .lowercase()
            .replace("@", "_at_")
            .replace(".", "_")
            .replace(" ", "_")

        // Chức năng: xóa đúng file đang dùng hiện tại.
        getPrefs(context)
            .edit()
            .clear()
            .apply()

        // Chức năng: xóa thêm các tên cũ nếu trước đó app từng lưu bằng tên khác.
        val possiblePrefNames = listOf<String>(
            "user_${username}_custom_exercise_target",
            "user_${username}_custom_exercise_targets",
            "user_${username}_custom_exercise_target_data",
            "user_${username}_exercise_target_data",
            "user_${username}_exercise_targets",
            "user_${username}_custom_targets",
            "user_${username}_workout_custom_targets",
            "user_${username}_custom_exercise_target_pref",

            // Các tên cũ dạng lưu chung toàn app, xóa để tránh app đọc nhầm dữ liệu cũ.
            "custom_exercise_target",
            "custom_exercise_targets",
            "custom_exercise_target_data",
            "exercise_target_data",
            "exercise_targets",
            "custom_targets",
            "workout_custom_targets",
            "custom_exercise_target_pref"
        )

        possiblePrefNames.forEach { prefName ->
            context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()
        }
    }
}