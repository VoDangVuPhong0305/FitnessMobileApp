package com.example.fitnessmobileapp.data.repository

import android.content.Context
import com.example.fitnessmobileapp.data.model.PlanDay
import com.example.fitnessmobileapp.data.model.WorkoutPlanCategories

object PlanProgressManager {

    private const val PREF_NAME = "plan_progress_pref"

    // Chức năng: lấy nơi lưu tiến độ riêng theo từng tài khoản.
    private fun getPrefs(context: Context) =
        UserDataPrefs.getUserPrefs(context, PREF_NAME)

    // Chức năng: tạo key lưu tiến độ riêng cho từng loại kế hoạch.
    private fun getCompletedDayKey(planId: String): String {
        return "completed_${planId}_day"
    }

    // Chức năng: lấy ngày cao nhất mà người dùng hiện tại đã hoàn thành.
    fun getCompletedDay(
        context: Context,
        planId: String
    ): Int {
        val sharedPreferences = getPrefs(context)
        val key = getCompletedDayKey(planId)
        return sharedPreferences.getInt(key, 0)
    }

    // Chức năng: phiên bản cũ, mặc định lấy tiến độ cơ bụng.
    fun getCompletedDay(context: Context): Int {
        return getCompletedDay(context, WorkoutPlanCategories.ABS_ID)
    }

    // Chức năng: lưu ngày vừa hoàn thành cho tài khoản hiện tại.
    fun completeDay(
        context: Context,
        planId: String,
        dayNumber: Int
    ) {
        val currentCompletedDay = getCompletedDay(context, planId)

        if (dayNumber > currentCompletedDay) {
            val sharedPreferences = getPrefs(context)
            val key = getCompletedDayKey(planId)

            sharedPreferences.edit()
                .putInt(key, dayNumber)
                .apply()
        }
    }

    // Chức năng: phiên bản cũ, mặc định lưu cho kế hoạch cơ bụng.
    fun completeDay(
        context: Context,
        dayNumber: Int
    ) {
        completeDay(
            context = context,
            planId = WorkoutPlanCategories.ABS_ID,
            dayNumber = dayNumber
        )
    }

    // Chức năng: tìm ngày tập tiếp theo của tài khoản hiện tại.
    fun getCurrentDay(
        context: Context,
        planId: String,
        planDays: List<PlanDay>
    ): Int {
        val completedDay = getCompletedDay(context, planId)

        val nextWorkoutDay = planDays.firstOrNull { planDay ->
            !planDay.isRestDay && planDay.dayNumber > completedDay
        }

        return nextWorkoutDay?.dayNumber ?: 30
    }

    // Chức năng: phiên bản cũ, mặc định lấy ngày hiện tại của cơ bụng.
    fun getCurrentDay(
        context: Context,
        planDays: List<PlanDay>
    ): Int {
        return getCurrentDay(
            context = context,
            planId = WorkoutPlanCategories.ABS_ID,
            planDays = planDays
        )
    }

    // Chức năng: tính phần trăm hoàn thành của tài khoản hiện tại.
    fun getProgressPercent(
        context: Context,
        planId: String
    ): Int {
        val completedDay = getCompletedDay(context, planId)
        return completedDay * 100 / 30
    }

    // Chức năng: phiên bản cũ, mặc định tính tiến độ cơ bụng.
    fun getProgressPercent(context: Context): Int {
        return getProgressPercent(context, WorkoutPlanCategories.ABS_ID)
    }

    // Chức năng: reset tiến độ một kế hoạch của tài khoản hiện tại.
    fun resetProgress(
        context: Context,
        planId: String
    ) {
        val sharedPreferences = getPrefs(context)
        val key = getCompletedDayKey(planId)

        sharedPreferences.edit()
            .putInt(key, 0)
            .apply()
    }

    // Chức năng: phiên bản cũ để reset tiến độ cơ bụng.
    fun resetProgress(context: Context) {
        resetProgress(context, WorkoutPlanCategories.ABS_ID)
    }

    // Chức năng: reset toàn bộ tiến độ của tài khoản hiện tại.
    fun resetAllProgress(context: Context) {
        val sharedPreferences = getPrefs(context)
        val editor = sharedPreferences.edit()

        WorkoutPlanCategories.allPlans.forEach { plan ->
            val key = getCompletedDayKey(plan.id)
            editor.putInt(key, 0)
        }

        editor.apply()
    }
}