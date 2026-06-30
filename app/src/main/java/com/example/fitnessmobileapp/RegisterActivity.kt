package com.example.fitnessmobileapp

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.ImageView

class RegisterActivity : AppCompatActivity() {

    private lateinit var edtRegisterUsername: EditText
    private lateinit var edtRegisterPassword: EditText
    private lateinit var btnCreateAccount: TextView

    // Hàm khởi tạo màn hình đăng ký
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnBackLogin = findViewById<ImageView>(R.id.btnBackLogin)

        btnBackLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        edtRegisterUsername = findViewById(R.id.edtRegisterUsername)
        edtRegisterPassword = findViewById(R.id.edtRegisterPassword)
        btnCreateAccount = findViewById(R.id.btnCreateAccount)

        btnCreateAccount.setOnClickListener {
            registerAccount()
        }
    }

    // Hàm tạo tài khoản cục bộ và lưu vào SharedPreferences
    private fun registerAccount() {
        val username = edtRegisterUsername.text.toString().trim()
        val password = edtRegisterPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs = getSharedPreferences("accounts_data", Context.MODE_PRIVATE)

        if (prefs.contains(username)) {
            Toast.makeText(this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show()
            return
        }

        prefs.edit()
            .putString(username, password)
            .apply()

        Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
        finish()
    }
}