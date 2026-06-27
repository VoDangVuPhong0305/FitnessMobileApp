package com.example.fitnessmobileapp.ui.nutrition

data class ShoppingItem(
    val text: String,
    val description: String = "",
    val isHeader: Boolean = false,
    val iconRes: Int = 0,
    val isChecked: Boolean = false
)