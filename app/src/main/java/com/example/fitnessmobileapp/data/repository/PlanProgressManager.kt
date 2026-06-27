package com.example.fitnessmobileapp.data.repository

import android.content.Context
import com.example.fitnessmobileapp.data.model.PlanDay
import com.example.fitnessmobileapp.data.model.WorkoutPlanCategories

object PlanProgressManager {

    private const val PREF_NAME = "plan_progress_pref"

    // Chức năng: tạo key lưu tiến độ riêng cho từng loại kế hoạch.
    // Ví dụ:
    // abs -> completed_abs_day
    // legs -> completed_legs_day
    private fun getCompletedDayKey(planId: String): String {
        return "completed_${planId}_day"
    }

    // Chức năng: lấy ngày cao nhất mà người dùng đã hoàn thành của một kế hoạch cụ thể.
    // Ví dụ: lấy tiến độ riêng của cơ bụng, chân, tay ngực hoặc toàn thân.
    fun getCompletedDay(
        context: Context,
        planId: String
    ): Int {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val key = getCompletedDayKey(planId)
        return sharedPreferences.getInt(key, 0)
    }

    // Chức năng: phiên bản cũ để giữ cho phần cơ bụng hiện tại không bị lỗi.
    // Nếu nơi nào trong code cũ gọi getCompletedDay(context) thì vẫn hiểu là lấy tiến độ cơ bụng.
    fun getCompletedDay(context: Context): Int {
        return getCompletedDay(context, WorkoutPlanCategories.ABS_ID)
    }

    // Chức năng: lưu ngày vừa hoàn thành cho một kế hoạch cụ thể.
    // Nếu người dùng tập lại ngày cũ thì không làm giảm tiến độ hiện tại.
    fun completeDay(
        context: Context,
        planId: String,
        dayNumber: Int
    ) {
        val currentCompletedDay = getCompletedDay(context, planId)

        if (dayNumber > currentCompletedDay) {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val key = getCompletedDayKey(planId)

            sharedPreferences.edit()
                .putInt(key, dayNumber)
                .apply()
        }
    }

    // Chức năng: phiên bản cũ để giữ cho phần cơ bụng hiện tại không bị lỗi.
    // Nếu code cũ gọi completeDay(context, dayNumber) thì mặc định lưu cho kế hoạch cơ bụng.
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

    // Chức năng: tìm ngày tập tiếp theo của một kế hoạch cụ thể.
    // Nếu ngày kế tiếp là ngày nghỉ thì tự bỏ qua để tìm ngày tập tiếp theo.
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

    // Chức năng: phiên bản cũ để giữ cho phần cơ bụng hiện tại không bị lỗi.
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

    // Chức năng: tính phần trăm hoàn thành của một kế hoạch cụ thể.
    // Ví dụ hoàn thành 3/30 ngày thì trả về 10.
    fun getProgressPercent(
        context: Context,
        planId: String
    ): Int {
        val completedDay = getCompletedDay(context, planId)
        return completedDay * 100 / 30
    }

    // Chức năng: phiên bản cũ để giữ cho phần cơ bụng hiện tại không bị lỗi.
    fun getProgressPercent(context: Context): Int {
        return getProgressPercent(context, WorkoutPlanCategories.ABS_ID)
    }

    // Chức năng: reset tiến độ của một kế hoạch cụ thể.
    // Ví dụ chỉ reset cơ bụng, không ảnh hưởng tới chân hoặc tay ngực.
    fun resetProgress(
        context: Context,
        planId: String
    ) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val key = getCompletedDayKey(planId)

        sharedPreferences.edit()
            .putInt(key, 0)
            .apply()
    }

    // Chức năng: phiên bản cũ để reset tiến độ cơ bụng.
    fun resetProgress(context: Context) {
        resetProgress(context, WorkoutPlanCategories.ABS_ID)
    }

    // Chức năng: reset toàn bộ tiến độ của tất cả kế hoạch.
    // Sau này có thể dùng cho nút "Xóa tất cả dữ liệu".
    fun resetAllProgress(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        WorkoutPlanCategories.allPlans.forEach { plan ->
            val key = getCompletedDayKey(plan.id)
            editor.putInt(key, 0)
        }

        editor.apply()
    }
}