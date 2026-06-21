package com.example.fitnessmobileapp.model

import java.io.Serializable

data class NutritionItem(
    val day: Int,
    val breakfastStd: String,
    val snackStd: String,
    val lunchStd: String,
    val dinnerStd: String,
    val breakfastVeg: String,
    val snackVeg: String,
    val lunchVeg: String,
    val dinnerVeg: String
) : Serializable