package com.example.fitnessmobileapp.ui.report

object ReportData {

    data class WorkoutHistory(
        val exerciseName: String,
        val time: String,
        val duration: String,
        val calories: Double,
        val minutes: Int
    )

    data class WeightRecord(
        val date: String,
        val weight: Double
    )

    // Dữ liệu mẫu lịch sử tập luyện theo ngày
    private val workoutHistoryMap = mapOf(
        "10/6/2026" to listOf(
            WorkoutHistory("Tập Toàn Thân Ngày 1", "20:44", "00:41", 12.6, 41),
            WorkoutHistory("Gập bụng", "21:00", "00:15", 30.0, 15)
        ),

        "12/6/2026" to listOf(
            WorkoutHistory("Plank", "19:30", "00:10", 20.0, 10),
            WorkoutHistory("Squat", "19:45", "00:20", 45.0, 20)
        ),

        "16/6/2026" to listOf(
            WorkoutHistory("Nhảy tại chỗ", "18:20", "00:08", 25.0, 8)
        )
    )

    // Dữ liệu mẫu cân nặng
    private val weightList = mutableListOf(
        WeightRecord("10/6", 77.5),
        WeightRecord("12/6", 76.8),
        WeightRecord("16/6", 76.0),
        WeightRecord("19/6", 75.5)
    )

    fun getTotalWorkoutDays(): Int {
        return workoutHistoryMap.size
    }

    fun getTotalCalories(): Double {
        return workoutHistoryMap.values
            .flatten()
            .sumOf { it.calories }
    }

    fun getWorkoutMinutes(): Int {
        return workoutHistoryMap.values
            .flatten()
            .sumOf { it.minutes }
    }

    fun getWorkoutHistoryByDate(date: String): List<WorkoutHistory> {
        return workoutHistoryMap[date] ?: emptyList()
    }

    fun getWeightRecords(): MutableList<WeightRecord> {
        return weightList
    }

    fun addNewWeight(weight: Double) {
        weightList.add(WeightRecord("Hôm nay", weight))

        // Chỉ giữ 4 mốc gần nhất để biểu đồ không quá dài
        while (weightList.size > 4) {
            weightList.removeAt(0)
        }
    }

    fun getCurrentWeight(): Double {
        return weightList.lastOrNull()?.weight ?: 0.0
    }

    fun getOldWeight(): Double {
        return weightList.firstOrNull()?.weight ?: 0.0
    }

    fun getWeightChange(): Double {
        return getCurrentWeight() - getOldWeight()
    }
}