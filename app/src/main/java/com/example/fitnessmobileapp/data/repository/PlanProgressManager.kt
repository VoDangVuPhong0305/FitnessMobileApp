package com.example.fitnessmobileapp.data.repository

import android.content.Context
import com.example.fitnessmobileapp.data.model.PlanDay

object PlanProgressManager {

    private const val PREF_NAME = "plan_progress_pref"
    private const val KEY_COMPLETED_ABS_DAY = "completed_abs_day"

    // Chức năng: lấy ngày cơ bụng cao nhất mà người dùng đã hoàn thành.
    // Nếu chưa tập xong ngày nào thì trả về 0.
    fun getCompletedDay(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(KEY_COMPLETED_ABS_DAY, 0)
    }

    // Chức năng: lưu ngày vừa hoàn thành.
    // Nếu người dùng tập lại ngày cũ thì không làm giảm tiến độ hiện tại.
    fun completeDay(context: Context, dayNumber: Int) {
        val currentCompletedDay = getCompletedDay(context)

        if (dayNumber > currentCompletedDay) {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

            sharedPreferences.edit()
                .putInt(KEY_COMPLETED_ABS_DAY, dayNumber)
                .apply()
        }
    }

    // Chức năng: tìm ngày tập tiếp theo cần mở nút Bắt đầu.
    // Nếu ngày kế tiếp là ngày nghỉ thì tự bỏ qua ngày nghỉ để tìm ngày tập tiếp theo.
    fun getCurrentDay(context: Context, planDays: List<PlanDay>): Int {
        val completedDay = getCompletedDay(context)

        val nextWorkoutDay = planDays.firstOrNull { planDay ->
            !planDay.isRestDay && planDay.dayNumber > completedDay
        }

        return nextWorkoutDay?.dayNumber ?: 30
    }

    // Chức năng: tính phần trăm hoàn thành của lộ trình 30 ngày.
    fun getProgressPercent(context: Context): Int {
        val completedDay = getCompletedDay(context)
        return completedDay * 100 / 30
    }

    // Chức năng: reset tiến độ về 0.
    // Hàm này dùng khi test app, sau này có thể gắn vào nút "Làm lại kế hoạch".
    fun resetProgress(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        sharedPreferences.edit()
            .putInt(KEY_COMPLETED_ABS_DAY, 0)
            .apply()
    }
}