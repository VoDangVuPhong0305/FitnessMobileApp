package com.example.fitnessmobileapp.data.repository

import android.content.Context
import com.example.fitnessmobileapp.data.model.PlanDay
import com.example.fitnessmobileapp.data.model.WorkoutPlanCategories
import com.example.fitnessmobileapp.data.model.WorkoutPlanCategory

object WorkoutPlanProvider {

    // Chức năng: lấy danh sách 30 ngày tập theo id kế hoạch.
    // Nếu là cơ bụng thì dùng lộ trình cơ bụng đã làm sẵn.
    // Nếu là toàn thân, tay ngực hoặc chân thì tự sinh lộ trình bằng WorkoutPlanGenerator.
    fun getPlanDays(
        context: Context,
        planId: String
    ): List<PlanDay> {
        return when (planId) {
            WorkoutPlanCategories.ABS_ID -> {
                AbsWorkoutPlan.getThirtyDayPlan()
            }

            WorkoutPlanCategories.FULL_BODY_ID,
            WorkoutPlanCategories.ARMS_CHEST_ID,
            WorkoutPlanCategories.LEGS_ID -> {
                val planCategory = WorkoutPlanCategories.getPlanById(planId)

                createGeneratedPlan(
                    context = context,
                    planCategory = planCategory
                )
            }

            else -> {
                AbsWorkoutPlan.getThirtyDayPlan()
            }
        }
    }

    // Chức năng: lấy thông tin kế hoạch theo id.
    // Ví dụ truyền "legs" thì trả về thông tin kế hoạch Tập chân.
    fun getPlanCategory(planId: String): WorkoutPlanCategory {
        return WorkoutPlanCategories.getPlanById(planId)
    }

    // Chức năng: tạo lộ trình 30 ngày cho các kế hoạch chưa viết tay.
    // Hàm này lấy danh sách bài tập đúng nhóm, rồi đưa vào WorkoutPlanGenerator để chia thành 30 ngày.
    private fun createGeneratedPlan(
        context: Context,
        planCategory: WorkoutPlanCategory
    ): List<PlanDay> {
        val exercises = WorkoutDataReader.getExercisesByType(
            context = context,
            exerciseType = planCategory.exerciseType
        )

        return WorkoutPlanGenerator.generateThirtyDayPlan(
            planTitle = planCategory.title,
            exerciseType = planCategory.exerciseType,
            exercises = exercises
        )
    }
}