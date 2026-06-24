package com.example.fitnessmobileapp.data.repository

import android.content.Context
import com.example.fitnessmobileapp.data.model.Exercise
import com.example.fitnessmobileapp.data.model.ExerciseLibrary
import com.example.fitnessmobileapp.data.model.WorkoutData
import org.json.JSONArray
import org.json.JSONObject

object WorkoutDataReader {

    fun loadWorkoutData(context: Context): WorkoutData {
        val jsonString = context.assets
            .open("data/workout_data.json")
            .bufferedReader()
            .use { it.readText() }

        val jsonObject = JSONObject(jsonString)

        val exerciseLibraryObject = jsonObject.getJSONObject("exerciseLibrary")

        val exerciseLibrary = ExerciseLibrary(
            fullBody = parseExerciseArray(exerciseLibraryObject.optJSONArray("fullBody")),
            armsChest = parseExerciseArray(exerciseLibraryObject.optJSONArray("armsChest")),
            legs = parseExerciseArray(exerciseLibraryObject.optJSONArray("legs")),
            abs = parseExerciseArray(exerciseLibraryObject.optJSONArray("abs"))
        )

        return WorkoutData(
            exerciseLibrary = exerciseLibrary
        )
    }

    //Hàm lấy toàn bộ bài tập
    fun getAllExercises(context: Context): List<Exercise> {
        val workoutData = loadWorkoutData(context)

        return workoutData.exerciseLibrary.fullBody +
                workoutData.exerciseLibrary.armsChest +
                workoutData.exerciseLibrary.legs +
                workoutData.exerciseLibrary.abs
    }

    //Hàm chỉ lấy bài tập Tay & Ngực
    fun getArmsChestExercises(context: Context): List<Exercise> {
        return loadWorkoutData(context).exerciseLibrary.armsChest
    }

    //Hàm chỉ lấy bài tập Chân
    fun getLegExercises(context: Context): List<Exercise> {
        return loadWorkoutData(context).exerciseLibrary.legs
    }

    //Hàm chỉ lấy bài tập Cơ bụng
    fun getAbsExercises(context: Context): List<Exercise> {
        return loadWorkoutData(context).exerciseLibrary.abs
    }

    //Hàm chuyển mảng JSONArray thành danh sách bài tập
    private fun parseExerciseArray(jsonArray: JSONArray?): List<Exercise> {
        val exercises = mutableListOf<Exercise>()

        if (jsonArray == null) {
            return exercises
        }

        for (i in 0 until jsonArray.length()) {
            val exerciseObject = jsonArray.getJSONObject(i)
            exercises.add(parseExercise(exerciseObject))
        }

        return exercises
    }

    //Hàm chuyển object JSON => object Exercise
    private fun parseExercise(jsonObject: JSONObject): Exercise {
        return Exercise(
            id = jsonObject.getString("id"),
            name = jsonObject.getString("name"),
            category = jsonObject.getString("category"),
            duration = jsonObject.getInt("duration"),
            calories = jsonObject.getInt("calories"),
            level = jsonObject.getString("level"),
            intensity = jsonObject.getString("intensity"),
            animationFile = jsonObject.optString("animationFile", ""),
            youtubeUrl = jsonObject.optString("youtubeUrl", ""),
            description = parseDescription(jsonObject.optJSONArray("description"))
        )
    }

    //Hàm đọc danh sách mô tả và loại bỏ dòng trống
    private fun parseDescription(jsonArray: JSONArray?): List<String> {
        val descriptions = mutableListOf<String>()

        if (jsonArray == null) {
            return descriptions
        }

        for (i in 0 until jsonArray.length()) {
            val text = jsonArray.optString(i, "")
            if (text.isNotBlank()) {
                descriptions.add(text)
            }
        }

        return descriptions
    }
}