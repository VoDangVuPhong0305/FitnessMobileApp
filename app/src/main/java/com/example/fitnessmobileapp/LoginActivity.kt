package com.example.fitnessmobileapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var chkRememberLogin: CheckBox
    private lateinit var btnLogin: TextView
    private lateinit var btnRegister: TextView

    // Tài khoản mẫu
    private val accounts = mapOf(
        "phong" to "123"
    )

    // Hàm khởi tạo màn hình đăng nhập
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("login_data", Context.MODE_PRIVATE)
        val rememberLogin = prefs.getBoolean("remember_login", false)
        val currentUser = prefs.getString("current_user", "")

        if (rememberLogin && !currentUser.isNullOrEmpty()) {
            openNextScreen(currentUser)
            return
        }

        setContentView(R.layout.activity_login)

        edtUsername = findViewById(R.id.edtUsername)
        edtPassword = findViewById(R.id.edtPassword)
        chkRememberLogin = findViewById(R.id.chkRememberLogin)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        btnLogin.setOnClickListener {
            handleLogin()
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    // Hàm kiểm tra đăng nhập
    private fun handleLogin() {
        val username = edtUsername.text.toString().trim()
        val password = edtPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val fixedPassword = accounts[username]

        val accountPrefs = getSharedPreferences("accounts_data", Context.MODE_PRIVATE)
        val registeredPassword = accountPrefs.getString(username, null)

        val isValid = fixedPassword == password || registeredPassword == password

        if (isValid) {
            saveLoginState(username)
            openNextScreen(username)
        } else {
            Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show()
        }
    }

    // Hàm lưu người dùng hiện tại và trạng thái lưu đăng nhập
    private fun saveLoginState(username: String) {
        getSharedPreferences("login_data", Context.MODE_PRIVATE)
            .edit()
            .putString("current_user", username)
            .putBoolean("remember_login", chkRememberLogin.isChecked)
            .apply()
    }

    // Hàm mở màn hình tiếp theo sau khi đăng nhập thành công
    private fun openNextScreen(username: String) {
        val userPrefs = getSharedPreferences("user_${username}_profile", Context.MODE_PRIVATE)
        val setupDone = userPrefs.getBoolean("setup_done", false)

        val intent = if (setupDone) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, OnboardingActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}