package com.example.fitnessmobileapp.data.repository

import android.content.Context
import android.content.SharedPreferences

object UserDataPrefs {

    // Chức năng: lấy tài khoản đang đăng nhập hiện tại từ login_data.
    // Hàm này giữ nguyên chữ hoa/thường đúng theo username người dùng đã nhập.
    fun getCurrentUsername(context: Context): String {
        val loginPrefs = context.getSharedPreferences(
            "login_data",
            Context.MODE_PRIVATE
        )

        return loginPrefs.getString("current_user", "guest")
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: "guest"
    }

    // Chức năng: chuẩn hóa username để dùng trong tên file SharedPreferences.
    // Chỉ thay các ký tự dễ gây lỗi trong tên file như @, dấu chấm, khoảng trắng.
    fun getSafeUsername(context: Context): String {
        return getCurrentUsername(context)
            .trim()
            .replace("@", "_at_")
            .replace(".", "_")
            .replace(" ", "_")
    }

    // Chức năng: tạo SharedPreferences riêng theo từng tài khoản.
    fun getUserPrefs(
        context: Context,
        baseName: String
    ): SharedPreferences {
        val username = getSafeUsername(context)

        return context.getSharedPreferences(
            "user_${username}_$baseName",
            Context.MODE_PRIVATE
        )
    }
}