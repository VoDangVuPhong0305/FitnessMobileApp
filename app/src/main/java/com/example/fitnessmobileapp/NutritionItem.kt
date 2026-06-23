package com.example.fitnessmobileapp.model

import java.io.Serializable

data class NutritionItem(
    val day: Int,

    // Thực đơn tiêu chuẩn (có thịt/cá/trứng)
    val breakfastStd: String,
    val snackStd: String,
    val lunchStd: String,
    val dinnerStd: String,

    // Thực đơn ăn chay
    val breakfastVeg: String,
    val snackVeg: String,
    val lunchVeg: String,
    val dinnerVeg: String,

    // Món thay thế cho Tiêu chuẩn (đa dạng)
    val breakfastAlt: String,
    val snackAlt: String,
    val lunchAlt: String,
    val dinnerAlt: String,

    // Món thay thế cho Ăn chay (thuần chay)
    val breakfastVegAlt: String,
    val snackVegAlt: String,
    val lunchVegAlt: String,
    val dinnerVegAlt: String
) : Serializable