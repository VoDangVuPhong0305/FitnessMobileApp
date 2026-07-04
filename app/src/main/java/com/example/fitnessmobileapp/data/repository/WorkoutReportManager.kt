package com.example.fitnessmobileapp.data.repository

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class WorkoutReportRecord(
    val id: Long,
    val date: String,
    val dayNumber: Int,
    val exerciseType: String,
    val exerciseCount: Int,
    val durationSeconds: Int,
    val calories: Int,
    val completedAtMillis: Long
)

data class WorkoutReportSummary(
    val totalWorkouts: Int,
    val totalExercises: Int,
    val totalDurationSeconds: Int,
    val totalCalories: Int
)

object WorkoutReportManager {

    private const val PREF_NAME = "workout_report_pref"
    private const val KEY_WORKOUT_HISTORY = "workout_history"

    // Chức năng: lấy nơi lưu báo cáo riêng theo từng tài khoản.
    private fun getPrefs(context: Context) =
        UserDataPrefs.getUserPrefs(context, PREF_NAME)

    // Chức năng: lưu một lần tập đã hoàn thành vào lịch sử của tài khoản hiện tại.
    fun saveCompletedWorkout(
        context: Context,
        dayNumber: Int,
        exerciseType: String,
        exerciseCount: Int,
        durationSeconds: Int,
        calories: Int
    ) {
        val completedAtMillis = System.currentTimeMillis()
        val date = createDateString(completedAtMillis)

        val historyArray = loadHistoryJsonArray(context)

        val recordObject = JSONObject().apply {
            put("id", completedAtMillis)
            put("date", date)
            put("dayNumber", dayNumber)
            put("exerciseType", exerciseType)
            put("exerciseCount", exerciseCount)
            put("durationSeconds", durationSeconds)
            put("calories", calories)
            put("completedAtMillis", completedAtMillis)
        }

        historyArray.put(recordObject)
        saveHistoryJsonArray(context, historyArray)
    }

    // Chức năng: lấy toàn bộ lịch sử tập luyện của tài khoản hiện tại.
    fun getAllRecords(context: Context): List<WorkoutReportRecord> {
        val historyArray = loadHistoryJsonArray(context)
        val records = mutableListOf<WorkoutReportRecord>()

        for (index in 0 until historyArray.length()) {
            val recordObject = historyArray.optJSONObject(index)
            if (recordObject != null) {
                records.add(jsonToRecord(recordObject))
            }
        }

        return records
    }

    // Chức năng: lấy danh sách các lần tập trong hôm nay của tài khoản hiện tại.
    fun getTodayRecords(context: Context): List<WorkoutReportRecord> {
        val today = createDateString(System.currentTimeMillis())

        return getAllRecords(context).filter { record ->
            record.date == today
        }
    }

    // Chức năng: tính thống kê hôm nay của tài khoản hiện tại.
    fun getTodaySummary(context: Context): WorkoutReportSummary {
        val todayRecords = getTodayRecords(context)
        return summarizeRecords(todayRecords)
    }

    // Chức năng: tính tổng thống kê của tài khoản hiện tại.
    fun getTotalSummary(context: Context): WorkoutReportSummary {
        val allRecords = getAllRecords(context)
        return summarizeRecords(allRecords)
    }

    // Chức năng: lấy lịch sử tập theo một ngày cụ thể của tài khoản hiện tại.
    fun getRecordsByDate(
        context: Context,
        date: String
    ): List<WorkoutReportRecord> {
        return getAllRecords(context).filter { record ->
            record.date == date
        }
    }

    // Chức năng: xóa toàn bộ dữ liệu báo cáo của tài khoản hiện tại.
    fun clearReportData(context: Context) {
        val sharedPreferences = getPrefs(context)

        sharedPreferences.edit()
            .remove(KEY_WORKOUT_HISTORY)
            .apply()
    }

    // Chức năng: cộng dồn danh sách bản ghi thành một bản thống kê tổng.
    private fun summarizeRecords(records: List<WorkoutReportRecord>): WorkoutReportSummary {
        val totalWorkouts = records.size

        val totalExercises = records.sumOf { record ->
            record.exerciseCount
        }

        val totalDurationSeconds = records.sumOf { record ->
            record.durationSeconds
        }

        val totalCalories = records.sumOf { record ->
            record.calories
        }

        return WorkoutReportSummary(
            totalWorkouts = totalWorkouts,
            totalExercises = totalExercises,
            totalDurationSeconds = totalDurationSeconds,
            totalCalories = totalCalories
        )
    }

    // Chức năng: đọc chuỗi JSON lịch sử từ SharedPreferences theo tài khoản hiện tại.
    private fun loadHistoryJsonArray(context: Context): JSONArray {
        val sharedPreferences = getPrefs(context)
        val jsonString = sharedPreferences.getString(KEY_WORKOUT_HISTORY, "[]") ?: "[]"

        return try {
            JSONArray(jsonString)
        } catch (exception: Exception) {
            JSONArray()
        }
    }

    // Chức năng: lưu JSONArray lịch sử vào SharedPreferences theo tài khoản hiện tại.
    private fun saveHistoryJsonArray(
        context: Context,
        historyArray: JSONArray
    ) {
        val sharedPreferences = getPrefs(context)

        sharedPreferences.edit()
            .putString(KEY_WORKOUT_HISTORY, historyArray.toString())
            .apply()
    }

    // Chức năng: tạo chuỗi ngày từ thời gian millis.
    private fun createDateString(timeMillis: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date(timeMillis))
    }

    // Chức năng: chuyển một JSONObject thành WorkoutReportRecord.
    private fun jsonToRecord(recordObject: JSONObject): WorkoutReportRecord {
        return WorkoutReportRecord(
            id = recordObject.optLong("id", 0L),
            date = recordObject.optString("date", ""),
            dayNumber = recordObject.optInt("dayNumber", 0),
            exerciseType = recordObject.optString("exerciseType", ""),
            exerciseCount = recordObject.optInt("exerciseCount", 0),
            durationSeconds = recordObject.optInt("durationSeconds", 0),
            calories = recordObject.optInt("calories", 0),
            completedAtMillis = recordObject.optLong("completedAtMillis", 0L)
        )
    }
}