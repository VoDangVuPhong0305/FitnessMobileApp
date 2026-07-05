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

    // Chức năng: xóa toàn bộ mục tiêu tùy chỉnh của bài tập thuộc tài khoản hiện tại.
    // Hàm này dùng khi người dùng đặt lại tiến độ hoặc xóa toàn bộ dữ liệu.
    // Lưu ý: chỉ clear SharedPreferences đang dùng, không mở danh sách file cũ để tránh tạo file XML rỗng.
    fun clearAllTargets(context: Context) {
        getPrefs(context)
            .edit()
            .clear()
            .apply()
    }
}