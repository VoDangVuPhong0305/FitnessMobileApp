package com.example.fitnessmobileapp.data.repository

import android.content.Context
import android.content.SharedPreferences

object UserDataPrefs {

    // Chức năng: lấy tài khoản đang đăng nhập hiện tại.
    fun getCurrentUsername(context: Context): String {
        val loginPrefs = context.getSharedPreferences(
            "login_data",
            Context.MODE_PRIVATE
        )

        return loginPrefs.getString("current_user", "guest") ?: "guest"
    }

    // Chức năng: làm sạch tên tài khoản để dùng làm tên file SharedPreferences.
    private fun safeUsername(username: String): String {
        return username
            .trim()
            .lowercase()
            .replace("@", "_at_")
            .replace(".", "_")
            .replace(" ", "_")
    }

    // Chức năng: tạo SharedPreferences riêng theo từng tài khoản.
    fun getUserPrefs(
        context: Context,
        baseName: String
    ): SharedPreferences {
        val username = safeUsername(getCurrentUsername(context))

        return context.getSharedPreferences(
            "user_${username}_$baseName",
            Context.MODE_PRIVATE
        )
    }
}