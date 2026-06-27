package com.example.fitnessmobileapp.data.repository

// Chức năng: quản lý mục tiêu của từng bài tập.
// Có bài tập theo thời gian: 30 giây.
// Có bài tập theo số lần: x10, x12, x15...
object ExerciseTargetHelper {

    const val TYPE_TIME = "time"
    const val TYPE_REPS = "reps"

    data class ExerciseTarget(
        val type: String,
        val value: Int
    )

    private data class RepRule(
        val startReps: Int,
        val stepReps: Int
    )

    // Danh sách bài tập nên tính theo số lần.
    // Key có thể là id bài tập hoặc tên bài tập.
    // Mình để cả id và tên để nếu bài Toàn thân lấy lại bài cũ nhưng đổi id,
    // app vẫn nhận diện được theo tên.
    private val repRules = mapOf(
        // =========================
        // Tay & ngực
        // =========================
        "arms_chest_002" to RepRule(8, 2),
        "Tập cơ tay sau trên ghế" to RepRule(8, 2),

        "arms_chest_003" to RepRule(6, 2),
        "Chống đẩy" to RepRule(6, 2),

        "arms_chest_004" to RepRule(6, 2),
        "Hít đất chéo" to RepRule(6, 2),

        "arms_chest_005" to RepRule(8, 2),
        "Chống đẩy cao tay" to RepRule(8, 2),

        "arms_chest_012" to RepRule(12, 2),
        "Đưa khuỷu tay về sau" to RepRule(12, 2),

        "arms_chest_013" to RepRule(10, 2),
        "Cuốn tạ chân trái" to RepRule(10, 2),

        "arms_chest_014" to RepRule(10, 2),
        "Cuốn tạ chân phải" to RepRule(10, 2),

        "arms_chest_015" to RepRule(8, 2),
        "Hít đất vỗ vai" to RepRule(8, 2),

        "arms_chest_017" to RepRule(10, 2),
        "Chống đẩy bằng đầu gối" to RepRule(10, 2),

        "arms_chest_019" to RepRule(12, 3),
        "Chống đẩy vào tường" to RepRule(12, 3),

        "arms_chest_020" to RepRule(5, 1),
        "Chống đẩy tay hình kim cương" to RepRule(5, 1),

        "arms_chest_021" to RepRule(6, 2),
        "Chống đẩy để tay rộng" to RepRule(6, 2),

        // =========================
        // Chân
        // =========================
        "legs_002" to RepRule(12, 2),
        "Gánh đùi" to RepRule(12, 2),

        "legs_003" to RepRule(12, 2),
        "Nằm nghiêng người nâng chân trái" to RepRule(12, 2),

        "legs_004" to RepRule(12, 2),
        "Nằm người người nâng chân phải" to RepRule(12, 2),

        "legs_005" to RepRule(8, 2),
        "Tấn sau" to RepRule(8, 2),

        "legs_006" to RepRule(12, 2),
        "Lừa đá chân trái" to RepRule(12, 2),

        "legs_007" to RepRule(12, 2),
        "Lừa đá chân phải" to RepRule(12, 2),

        "legs_012" to RepRule(12, 2),
        "Cây cầu mông" to RepRule(12, 2),

        "legs_013" to RepRule(12, 2),
        "Đứng tấn" to RepRule(12, 2),

        "legs_015" to RepRule(8, 2),
        "Đứng tấn chân trái" to RepRule(8, 2),

        "legs_016" to RepRule(8, 2),
        "Đứng tấn chân phải" to RepRule(8, 2),

        "legs_020" to RepRule(15, 3),
        "Nâng bắp chân dựa tường" to RepRule(15, 3),

        "legs_021" to RepRule(12, 2),
        "Đứng tấn nâng bắp chân dựa tường" to RepRule(12, 2),

        // =========================
        // Cơ bụng
        // =========================
        "abs_001" to RepRule(14, 2),
        "Gập người xe đạp đứng" to RepRule(14, 2),

        "abs_002" to RepRule(16, 2),
        "Gập bụng chéo kiểu nga" to RepRule(16, 2),

        "abs_003" to RepRule(10, 2),
        "Tập cơ bụng" to RepRule(10, 2),

        "abs_004" to RepRule(10, 2),
        "Gập bụng ngược" to RepRule(10, 2),

        "abs_005" to RepRule(16, 2),
        "Chạm gót chân" to RepRule(16, 2),

        "abs_006" to RepRule(12, 2),
        "Con bọ" to RepRule(12, 2),

        "abs_008" to RepRule(10, 2),
        "Hai đầu gối chạm ngực" to RepRule(10, 2),

        "abs_011" to RepRule(14, 2),
        "Gập người gối chạm khuỷu tay" to RepRule(14, 2),

        "abs_012" to RepRule(10, 2),
        "Nâng tay dài" to RepRule(10, 2),

        "abs_013" to RepRule(10, 2),
        "Nghiêng người vặn cơ liên sườn" to RepRule(10, 2),

        "abs_014" to RepRule(8, 2),
        "Nâng chân" to RepRule(8, 2),

        "abs_016" to RepRule(16, 2),
        "Gập bụng đạp xe" to RepRule(16, 2),

        "abs_017" to RepRule(10, 2),
        "Gập bụng ngang thân" to RepRule(10, 2),

        "abs_018" to RepRule(12, 3),
        "Tung chân hít đất" to RepRule(12, 3),

        "abs_019" to RepRule(12, 2),
        "Đứng gập cơ liên sườn trái" to RepRule(12, 2),

        "abs_020" to RepRule(12, 2),
        "Đứng gập cơ liên sườn phải" to RepRule(12, 2),

        "abs_021" to RepRule(8, 2),
        "Gập người sao biển" to RepRule(8, 2)
    )

    // Chức năng: lấy mục tiêu bài tập theo ngày.
    // Nếu bài thuộc nhóm reps thì trả về x lần.
    // Nếu không thuộc nhóm reps thì mặc định là bài theo thời gian 30 giây.
    fun getTarget(
        exerciseId: String,
        exerciseName: String,
        dayNumber: Int
    ): ExerciseTarget {
        val rule = repRules[exerciseId] ?: repRules[exerciseName]

        return if (rule != null) {
            val stage = getStageByDay(dayNumber)
            val reps = rule.startReps + rule.stepReps * stage
            ExerciseTarget(TYPE_REPS, reps)
        } else {
            ExerciseTarget(TYPE_TIME, 30)
        }
    }

    // Chức năng: kiểm tra bài này có phải bài đếm số lần không.
    fun isRepExercise(
        exerciseId: String,
        exerciseName: String
    ): Boolean {
        return repRules.containsKey(exerciseId) || repRules.containsKey(exerciseName)
    }

    // Chức năng: lấy tiêu đề hiển thị.
    // Bài time: Thời lượng.
    // Bài reps: Lần lặp lại.
    fun getTargetLabel(target: ExerciseTarget): String {
        return if (target.type == TYPE_REPS) {
            "Lần lặp lại"
        } else {
            "Thời lượng"
        }
    }

    // Chức năng: lấy nội dung hiển thị.
    // Bài time: 00:30.
    // Bài reps: x12.
    fun getTargetText(target: ExerciseTarget): String {
        return if (target.type == TYPE_REPS) {
            "x${target.value}"
        } else {
            formatSeconds(target.value)
        }
    }

    // Chức năng: tăng mục tiêu khi người dùng bấm nút cộng.
    // Bài time tăng 5 giây.
    // Bài reps tăng 1 lần.
    fun increaseTarget(target: ExerciseTarget): ExerciseTarget {
        return if (target.type == TYPE_REPS) {
            target.copy(value = target.value + 1)
        } else {
            target.copy(value = target.value + 5)
        }
    }

    // Chức năng: giảm mục tiêu khi người dùng bấm nút trừ.
    // Bài time giảm 5 giây, thấp nhất 5 giây.
    // Bài reps giảm 1 lần, thấp nhất 1 lần.
    fun decreaseTarget(target: ExerciseTarget): ExerciseTarget {
        return if (target.type == TYPE_REPS) {
            target.copy(value = (target.value - 1).coerceAtLeast(1))
        } else {
            target.copy(value = (target.value - 5).coerceAtLeast(5))
        }
    }

    // Chức năng: chia lộ trình 30 ngày thành 4 giai đoạn tăng dần.
    private fun getStageByDay(dayNumber: Int): Int {
        return when {
            dayNumber <= 7 -> 0
            dayNumber <= 15 -> 1
            dayNumber <= 23 -> 2
            else -> 3
        }
    }

    private fun formatSeconds(seconds: Int): String {
        val minute = seconds / 60
        val second = seconds % 60
        return "%02d:%02d".format(minute, second)
    }
}