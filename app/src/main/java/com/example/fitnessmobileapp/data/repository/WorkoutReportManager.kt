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

    // Chức năng: lưu một lần tập đã hoàn thành vào lịch sử.
    // Mỗi lần người dùng tập xong một vòng thì sẽ tạo một bản ghi mới.
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

    // Chức năng: lấy toàn bộ lịch sử tập luyện đã lưu.
    // Hàm này sẽ được màn Báo cáo dùng để thống kê tổng dữ liệu.
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

    // Chức năng: lấy danh sách các lần tập trong hôm nay.
    // Dùng để tính hôm nay tập bao nhiêu bài, bao nhiêu phút, bao nhiêu calo.
    fun getTodayRecords(context: Context): List<WorkoutReportRecord> {
        val today = createDateString(System.currentTimeMillis())

        return getAllRecords(context).filter { record ->
            record.date == today
        }
    }

    // Chức năng: tính tổng thống kê trong hôm nay.
    // Ví dụ: hôm nay tập 2 vòng, tổng 14 bài, 12 phút, 90 calo.
    fun getTodaySummary(context: Context): WorkoutReportSummary {
        val todayRecords = getTodayRecords(context)
        return summarizeRecords(todayRecords)
    }

    // Chức năng: tính tổng thống kê từ trước đến nay.
    // Dùng cho màn Báo cáo tổng quan toàn bộ quá trình tập luyện.
    fun getTotalSummary(context: Context): WorkoutReportSummary {
        val allRecords = getAllRecords(context)
        return summarizeRecords(allRecords)
    }

    // Chức năng: lấy lịch sử tập theo một ngày cụ thể.
    // date truyền vào có dạng yyyy-MM-dd, ví dụ 2026-06-25.
    fun getRecordsByDate(
        context: Context,
        date: String
    ): List<WorkoutReportRecord> {
        return getAllRecords(context).filter { record ->
            record.date == date
        }
    }

    // Chức năng: xóa toàn bộ dữ liệu báo cáo.
    // Hàm này dùng khi người dùng chọn "Xóa tất cả dữ liệu" hoặc khi cần test lại app.
    fun clearReportData(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

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

    // Chức năng: đọc chuỗi JSON lịch sử từ SharedPreferences và chuyển thành JSONArray.
    // Nếu chưa có dữ liệu thì trả về mảng rỗng.
    private fun loadHistoryJsonArray(context: Context): JSONArray {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString(KEY_WORKOUT_HISTORY, "[]") ?: "[]"

        return try {
            JSONArray(jsonString)
        } catch (exception: Exception) {
            JSONArray()
        }
    }

    // Chức năng: lưu JSONArray lịch sử vào SharedPreferences.
    private fun saveHistoryJsonArray(
        context: Context,
        historyArray: JSONArray
    ) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        sharedPreferences.edit()
            .putString(KEY_WORKOUT_HISTORY, historyArray.toString())
            .apply()
    }

    // Chức năng: tạo chuỗi ngày từ thời gian millis.
    // Kết quả có dạng yyyy-MM-dd để dễ lọc dữ liệu theo ngày.
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