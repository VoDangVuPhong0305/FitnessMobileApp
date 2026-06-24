package com.example.fitnessmobileapp.data.model

data class PlanDay(
    val dayNumber: Int,
    val title: String,
    val exerciseCount: Int,
    val durationMinutes: Int,
    val isRestDay: Boolean,
    val exerciseType: String,
    val exerciseIds: List<String>
)