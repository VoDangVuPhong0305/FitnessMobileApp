package com.example.fitnessmobileapp.data.model

data class WorkoutPlanCategory(
    val id: String,
    val title: String,
    val subtitle: String,
    val exerciseType: String,
    val startColor: String,
    val endColor: String
)

object WorkoutPlanCategories {

    const val FULL_BODY_ID = "full_body"
    const val ABS_ID = "abs"
    const val ARMS_CHEST_ID = "arms_chest"
    const val LEGS_ID = "legs"

    val fullBody = WorkoutPlanCategory(
        id = FULL_BODY_ID,
        title = "TẬP TOÀN THÂN",
        subtitle = "Giảm cân và giữ dáng",
        exerciseType = FULL_BODY_ID,
        startColor = "#6C4DF6",
        endColor = "#91A8FF"
    )

    val abs = WorkoutPlanCategory(
        id = ABS_ID,
        title = "TẬP CƠ BỤNG",
        subtitle = "Cho cơ bụng săn chắc và gợi cảm",
        exerciseType = ABS_ID,
        startColor = "#F89A62",
        endColor = "#F6C46B"
    )

    val armsChest = WorkoutPlanCategory(
        id = ARMS_CHEST_ID,
        title = "TẬP TAY & NGỰC",
        subtitle = "Cải thiện sức mạnh thân trên",
        exerciseType = ARMS_CHEST_ID,
        startColor = "#EA4C89",
        endColor = "#F47C65"
    )

    val legs = WorkoutPlanCategory(
        id = LEGS_ID,
        title = "TẬP CHÂN",
        subtitle = "Giảm mỡ đùi",
        exerciseType = LEGS_ID,
        startColor = "#2E86DE",
        endColor = "#56CCF2"
    )

    val allPlans = listOf(
        fullBody,
        abs,
        armsChest,
        legs
    )

    // Chức năng: lấy thông tin kế hoạch theo id.
    // Ví dụ truyền vào "abs" thì trả về kế hoạch Tập cơ bụng.
    fun getPlanById(planId: String): WorkoutPlanCategory {
        return allPlans.firstOrNull { plan ->
            plan.id == planId
        } ?: abs
    }
}