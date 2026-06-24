package com.example.fitnessmobileapp.data.model

data class WorkoutData(
    val exerciseLibrary: ExerciseLibrary
)

data class ExerciseLibrary(
    val fullBody: List<Exercise>,
    val armsChest: List<Exercise>,
    val legs: List<Exercise>,
    val abs: List<Exercise>
)

data class Exercise(
    val id: String,
    val name: String,
    val category: String,
    val duration: Int,
    val calories: Int,
    val level: String,
    val intensity: String,
    val animationFile: String,
    val youtubeUrl: String,
    val description: List<String>
)