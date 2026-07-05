package com.example.fitnessmobileapp.data.repository

import android.content.Context
import com.example.fitnessmobileapp.data.model.PlanDay
import com.example.fitnessmobileapp.data.model.WorkoutPlanCategories

object PlanProgressManager {

    private const val PREF_NAME = "plan_progress_pref"

    // Chức năng: lấy SharedPreferences lưu tiến độ riêng theo từng tài khoản.
    // Ví dụ user là "VoPhong" thì file sẽ là:
    // user_VoPhong_plan_progress_pref.xml
    private fun getPrefs(context: Context) =
        UserDataPrefs.getUserPrefs(context, PREF_NAME)

    // Chức năng: tạo key lưu tiến độ riêng cho từng loại kế hoạch.
    // Ví dụ planId = "abs" thì key là completed_abs_day.
    // Cách này giúp mỗi kế hoạch có tiến độ riêng, không bị lẫn với nhau.
    private fun getCompletedDayKey(planId: String): String {
        return "completed_${planId}_day"
    }

    // Chức năng: lấy ngày cao nhất mà người dùng hiện tại đã hoàn thành trong một kế hoạch cụ thể.
    // Bắt buộc truyền planId để tránh mặc định nhầm sang kế hoạch Cơ bụng.
    fun getCompletedDay(
        context: Context,
        planId: String
    ): Int {
        val sharedPreferences = getPrefs(context)
        val key = getCompletedDayKey(planId)
        return sharedPreferences.getInt(key, 0)
    }

    // Chức năng: lưu ngày vừa hoàn thành cho tài khoản hiện tại và kế hoạch hiện tại.
    // Chỉ lưu nếu ngày mới lớn hơn ngày đã hoàn thành trước đó.
    // Ví dụ đã hoàn thành ngày 3 thì không cho ghi lùi về ngày 2.
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

    // Chức năng: tìm ngày tập tiếp theo của tài khoản hiện tại trong một kế hoạch cụ thể.
    // Hàm bỏ qua ngày nghỉ và lấy ngày tập đầu tiên lớn hơn ngày đã hoàn thành.
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

    // Chức năng: tính phần trăm hoàn thành của một kế hoạch cụ thể.
    // Ví dụ hoàn thành 15/30 ngày thì trả về 50.
    fun getProgressPercent(
        context: Context,
        planId: String
    ): Int {
        val completedDay = getCompletedDay(context, planId)
        return completedDay * 100 / 30
    }

    // Chức năng: reset tiến độ của một kế hoạch cụ thể về 0.
    // Hàm này chỉ reset đúng planId được truyền vào.
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

    // Chức năng: reset toàn bộ tiến độ của tất cả kế hoạch thuộc tài khoản hiện tại.
    // Dùng khi người dùng chọn đặt lại tiến độ tập luyện.
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