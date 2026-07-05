package com.example.fitnessmobileapp.ui.nutrition

import android.content.Context
import java.util.Locale
import kotlin.math.abs

enum class NutritionGoal {
    LOSE_WEIGHT,
    MAINTAIN_WEIGHT,
    GAIN_WEIGHT
}

enum class NutritionGoalLevel {
    NONE,
    LIGHT,
    MEDIUM,
    HIGH
}

data class NutritionProfileInfo(
    val currentWeight: Double,
    val targetWeight: Double,
    val goal: NutritionGoal,
    val level: NutritionGoalLevel,
    val weightDifference: Double
)

object NutritionGoalHelper {

    fun getProfileInfo(context: Context): NutritionProfileInfo {
        val loginPrefs = context.getSharedPreferences("login_data", Context.MODE_PRIVATE)
        val username = loginPrefs.getString("current_user", "guest")
            ?.trim()
            ?: "guest"

        val prefs = context.getSharedPreferences(
            "user_${username}_profile",
            Context.MODE_PRIVATE
        )

        val currentWeight = prefs.getFloat("weight", 65f).toDouble()
        val targetWeight = prefs.getFloat("targetWeight", 65f).toDouble()

        val difference = targetWeight - currentWeight
        val absDifference = abs(difference)

        val goal = when {
            absDifference < 1.0 -> NutritionGoal.MAINTAIN_WEIGHT
            difference > 0 -> NutritionGoal.GAIN_WEIGHT
            else -> NutritionGoal.LOSE_WEIGHT
        }

        val level = when {
            absDifference < 1.0 -> NutritionGoalLevel.NONE
            absDifference <= 5.0 -> NutritionGoalLevel.LIGHT
            absDifference <= 10.0 -> NutritionGoalLevel.MEDIUM
            else -> NutritionGoalLevel.HIGH
        }

        return NutritionProfileInfo(
            currentWeight = currentWeight,
            targetWeight = targetWeight,
            goal = goal,
            level = level,
            weightDifference = absDifference
        )
    }

    fun getGoalTitle(info: NutritionProfileInfo): String {
        return when (info.goal) {
            NutritionGoal.LOSE_WEIGHT -> "Mục tiêu: Giảm cân"
            NutritionGoal.GAIN_WEIGHT -> "Mục tiêu: Tăng cân"
            NutritionGoal.MAINTAIN_WEIGHT -> "Mục tiêu: Duy trì cân nặng"
        }
    }

    fun getGoalLevelText(info: NutritionProfileInfo): String {
        return when (info.goal) {
            NutritionGoal.MAINTAIN_WEIGHT -> "Duy trì"

            NutritionGoal.GAIN_WEIGHT -> when (info.level) {
                NutritionGoalLevel.LIGHT -> "Tăng nhẹ"
                NutritionGoalLevel.MEDIUM -> "Tăng vừa"
                NutritionGoalLevel.HIGH -> "Tăng nhiều"
                NutritionGoalLevel.NONE -> "Duy trì"
            }

            NutritionGoal.LOSE_WEIGHT -> when (info.level) {
                NutritionGoalLevel.LIGHT -> "Giảm nhẹ"
                NutritionGoalLevel.MEDIUM -> "Giảm vừa"
                NutritionGoalLevel.HIGH -> "Giảm nhiều"
                NutritionGoalLevel.NONE -> "Duy trì"
            }
        }
    }

    fun getGoalDescription(info: NutritionProfileInfo): String {
        val current = String.format(Locale.US, "%.1f", info.currentWeight)
        val target = String.format(Locale.US, "%.1f", info.targetWeight)
        val diff = String.format(Locale.US, "%.1f", info.weightDifference)

        val advice = when (info.goal) {
            NutritionGoal.MAINTAIN_WEIGHT -> {
                "Cân nặng hiện tại gần với mục tiêu. App sẽ giữ khẩu phần cân bằng giữa đạm, tinh bột tốt, rau xanh và chất béo lành mạnh."
            }

            NutritionGoal.GAIN_WEIGHT -> when (info.level) {
                NutritionGoalLevel.LIGHT -> {
                    "Bạn cần tăng khoảng ${diff}kg. App sẽ gợi ý tăng nhẹ khẩu phần, thêm bữa phụ nhỏ, tăng đạm và tinh bột tốt ở mức vừa phải."
                }

                NutritionGoalLevel.MEDIUM -> {
                    "Bạn cần tăng khoảng ${diff}kg. App sẽ gợi ý tăng khẩu phần rõ hơn, thêm sữa, trứng, chuối, khoai, cơm hoặc các loại hạt."
                }

                NutritionGoalLevel.HIGH -> {
                    "Bạn cần tăng khoảng ${diff}kg. App sẽ gợi ý tăng năng lượng nhiều hơn bằng nhiều bữa nhỏ trong ngày, tăng đạm, tinh bột tốt và bữa phụ, nhưng vẫn hạn chế đồ chiên dầu."
                }

                NutritionGoalLevel.NONE -> {
                    "Cân nặng hiện tại gần với mục tiêu. App sẽ giữ khẩu phần cân bằng để duy trì."
                }
            }

            NutritionGoal.LOSE_WEIGHT -> when (info.level) {
                NutritionGoalLevel.LIGHT -> {
                    "Bạn cần giảm khoảng ${diff}kg. App sẽ gợi ý giảm nhẹ tinh bột, hạn chế đồ ngọt và tăng rau xanh."
                }

                NutritionGoalLevel.MEDIUM -> {
                    "Bạn cần giảm khoảng ${diff}kg. App sẽ gợi ý kiểm soát khẩu phần rõ hơn, giảm tinh bột buổi tối và ưu tiên món luộc/nướng."
                }

                NutritionGoalLevel.HIGH -> {
                    "Bạn cần giảm khoảng ${diff}kg. App sẽ gợi ý chế độ giảm cân thận trọng, ưu tiên rau xanh, đạm nạc, hạn chế món chiên và không cắt ăn quá đột ngột."
                }

                NutritionGoalLevel.NONE -> {
                    "Cân nặng hiện tại gần với mục tiêu. App sẽ giữ khẩu phần cân bằng để duy trì."
                }
            }
        }

        return "Hiện tại: ${current}kg → Mục tiêu: ${target}kg\n" +
                "Mức độ: ${getGoalLevelText(info)}\n" +
                advice
    }

    fun applyGoalToMeal(
        mealTitle: String,
        mainFood: String,
        altFood: String,
        info: NutritionProfileInfo
    ): String {
        val rule = when (info.goal) {
            NutritionGoal.MAINTAIN_WEIGHT -> when (mealTitle) {
                "Bữa sáng" -> "Gợi ý duy trì: giữ khẩu phần vừa đủ, không quá nhiều đường hoặc dầu mỡ."
                "Bữa nhẹ" -> "Gợi ý duy trì: ăn nhẹ vừa phải, tránh ăn vặt quá nhiều."
                "Bữa trưa" -> "Gợi ý duy trì: cân bằng giữa cơm, đạm và rau."
                else -> "Gợi ý duy trì: ăn nhẹ, đủ chất, không ăn quá no trước khi ngủ."
            }

            NutritionGoal.GAIN_WEIGHT -> when (info.level) {
                NutritionGoalLevel.LIGHT -> when (mealTitle) {
                    "Bữa sáng" -> "Gợi ý tăng nhẹ: thêm 1 ly sữa hoặc 1 quả trứng nếu còn đói."
                    "Bữa nhẹ" -> "Gợi ý tăng nhẹ: thêm chuối, sữa chua hoặc một ít hạt."
                    "Bữa trưa" -> "Gợi ý tăng nhẹ: tăng thêm một ít cơm/khoai và giữ đủ đạm."
                    else -> "Gợi ý tăng nhẹ: ăn đủ bữa tối, có thể thêm sữa hoặc trái cây sau bữa."
                }

                NutritionGoalLevel.MEDIUM -> when (mealTitle) {
                    "Bữa sáng" -> "Gợi ý tăng vừa: thêm sữa, trứng, bánh mì hoặc yến mạch để tăng năng lượng."
                    "Bữa nhẹ" -> "Gợi ý tăng vừa: thêm chuối, hạt, sữa chua hoặc phô mai."
                    "Bữa trưa" -> "Gợi ý tăng vừa: tăng thêm 1 phần cơm/khoai và 1 phần đạm."
                    else -> "Gợi ý tăng vừa: bổ sung thêm tinh bột tốt và đạm, không bỏ bữa tối."
                }

                NutritionGoalLevel.HIGH -> when (mealTitle) {
                    "Bữa sáng" -> "Gợi ý tăng nhiều: ăn đủ món chính, thêm sữa, trứng hoặc bơ đậu phộng."
                    "Bữa nhẹ" -> "Gợi ý tăng nhiều: nên có bữa phụ rõ ràng như sữa, chuối, hạt hoặc bánh mì."
                    "Bữa trưa" -> "Gợi ý tăng nhiều: tăng khẩu phần cơm/khoai, thêm đạm và chất béo tốt."
                    else -> "Gợi ý tăng nhiều: ăn đủ bữa tối, có thể thêm bữa phụ sau đó nếu cần."
                }

                NutritionGoalLevel.NONE -> "Gợi ý duy trì: giữ khẩu phần cân bằng."
            }

            NutritionGoal.LOSE_WEIGHT -> when (info.level) {
                NutritionGoalLevel.LIGHT -> when (mealTitle) {
                    "Bữa sáng" -> "Gợi ý giảm nhẹ: ăn vừa đủ, hạn chế đường và đồ chiên."
                    "Bữa nhẹ" -> "Gợi ý giảm nhẹ: chọn trái cây ít ngọt hoặc sữa chua ít đường."
                    "Bữa trưa" -> "Gợi ý giảm nhẹ: giảm nhẹ tinh bột, tăng rau xanh."
                    else -> "Gợi ý giảm nhẹ: ăn tối vừa đủ, hạn chế ăn vặt sau bữa tối."
                }

                NutritionGoalLevel.MEDIUM -> when (mealTitle) {
                    "Bữa sáng" -> "Gợi ý giảm vừa: ưu tiên trứng, yến mạch, sữa ít đường hoặc món ít dầu."
                    "Bữa nhẹ" -> "Gợi ý giảm vừa: chọn trái cây ít ngọt, sữa chua ít đường hoặc một ít hạt."
                    "Bữa trưa" -> "Gợi ý giảm vừa: giảm khoảng 1/3 lượng cơm/bánh mì, tăng rau và đạm nạc."
                    else -> "Gợi ý giảm vừa: ăn nhẹ hơn bữa trưa, hạn chế tinh bột và món chiên."
                }

                NutritionGoalLevel.HIGH -> when (mealTitle) {
                    "Bữa sáng" -> "Gợi ý giảm nhiều: ăn đủ nhưng chọn món ít đường, ít dầu, tránh bỏ bữa."
                    "Bữa nhẹ" -> "Gợi ý giảm nhiều: ưu tiên trái cây ít ngọt hoặc sữa chua ít đường, không ăn vặt nhiều."
                    "Bữa trưa" -> "Gợi ý giảm nhiều: kiểm soát tinh bột rõ hơn, tăng rau xanh và đạm nạc."
                    else -> "Gợi ý giảm nhiều: ăn tối nhẹ, hạn chế tinh bột, không cắt ăn quá đột ngột."
                }

                NutritionGoalLevel.NONE -> "Gợi ý duy trì: giữ khẩu phần cân bằng."
            }
        }

        return "• $mainFood\n• $altFood\n\n$rule"
    }

    fun adjustShoppingDescription(
        itemName: String,
        oldDescription: String,
        info: NutritionProfileInfo
    ): String {
        val lowerName = itemName.lowercase()

        val extraNote = when (info.goal) {
            NutritionGoal.MAINTAIN_WEIGHT -> when {
                lowerName.contains("rau") ||
                        lowerName.contains("thịt") ||
                        lowerName.contains("cá") ||
                        lowerName.contains("trứng") ||
                        lowerName.contains("gạo") ->
                    "Mục tiêu duy trì: mua theo khẩu phần bình thường, cân bằng các nhóm chất."

                else -> ""
            }

            NutritionGoal.GAIN_WEIGHT -> when {
                lowerName.contains("gạo") ||
                        lowerName.contains("bánh mì") ||
                        lowerName.contains("khoai") ||
                        lowerName.contains("mì") ->
                    when (info.level) {
                        NutritionGoalLevel.LIGHT -> "Mục tiêu tăng nhẹ: có thể mua thêm một ít tinh bột tốt."
                        NutritionGoalLevel.MEDIUM -> "Mục tiêu tăng vừa: nên mua thêm tinh bột tốt như gạo, khoai, bánh mì nguyên cám."
                        NutritionGoalLevel.HIGH -> "Mục tiêu tăng nhiều: nên chuẩn bị nhiều hơn tinh bột tốt cho các bữa chính và bữa phụ."
                        NutritionGoalLevel.NONE -> ""
                    }

                lowerName.contains("thịt") ||
                        lowerName.contains("ức gà") ||
                        lowerName.contains("cá") ||
                        lowerName.contains("trứng") ||
                        lowerName.contains("đậu") ||
                        lowerName.contains("sữa") ||
                        lowerName.contains("hạt") ->
                    when (info.level) {
                        NutritionGoalLevel.LIGHT -> "Mục tiêu tăng nhẹ: bổ sung thêm đạm ở mức vừa phải."
                        NutritionGoalLevel.MEDIUM -> "Mục tiêu tăng vừa: nên mua thêm đạm và thực phẩm giàu năng lượng."
                        NutritionGoalLevel.HIGH -> "Mục tiêu tăng nhiều: cần chuẩn bị đủ đạm, sữa, hạt hoặc bữa phụ để tăng năng lượng."
                        NutritionGoalLevel.NONE -> ""
                    }

                else -> ""
            }

            NutritionGoal.LOSE_WEIGHT -> when {
                lowerName.contains("gạo") ||
                        lowerName.contains("bánh mì") ||
                        lowerName.contains("khoai") ||
                        lowerName.contains("mì") ->
                    when (info.level) {
                        NutritionGoalLevel.LIGHT -> "Mục tiêu giảm nhẹ: mua vừa đủ tinh bột, không cần cắt hoàn toàn."
                        NutritionGoalLevel.MEDIUM -> "Mục tiêu giảm vừa: mua tinh bột vừa phải, ưu tiên gạo lứt/khoai/yến mạch."
                        NutritionGoalLevel.HIGH -> "Mục tiêu giảm nhiều: kiểm soát lượng tinh bột kỹ hơn, không mua quá nhiều đồ tinh bột nhanh."
                        NutritionGoalLevel.NONE -> ""
                    }

                lowerName.contains("rau") ->
                    when (info.level) {
                        NutritionGoalLevel.LIGHT -> "Mục tiêu giảm nhẹ: nên tăng rau xanh trong bữa chính."
                        NutritionGoalLevel.MEDIUM -> "Mục tiêu giảm vừa: nên mua nhiều rau hơn để tăng cảm giác no."
                        NutritionGoalLevel.HIGH -> "Mục tiêu giảm nhiều: ưu tiên rau xanh, nấm, dưa leo, salad để hỗ trợ kiểm soát khẩu phần."
                        NutritionGoalLevel.NONE -> ""
                    }

                lowerName.contains("thịt") ||
                        lowerName.contains("ức gà") ||
                        lowerName.contains("cá") ||
                        lowerName.contains("trứng") ||
                        lowerName.contains("đậu") ->
                    when (info.level) {
                        NutritionGoalLevel.LIGHT -> "Mục tiêu giảm nhẹ: ưu tiên đạm nạc, hạn chế chiên."
                        NutritionGoalLevel.MEDIUM -> "Mục tiêu giảm vừa: ưu tiên đạm nạc, luộc/nướng, hạn chế dầu mỡ."
                        NutritionGoalLevel.HIGH -> "Mục tiêu giảm nhiều: vẫn cần đủ đạm nạc để không bị mất cơ khi giảm cân."
                        NutritionGoalLevel.NONE -> ""
                    }

                else -> ""
            }
        }

        return if (extraNote.isBlank()) {
            oldDescription
        } else {
            if (oldDescription.isBlank()) {
                extraNote
            } else {
                "$oldDescription\n\n$extraNote"
            }
        }
    }
}