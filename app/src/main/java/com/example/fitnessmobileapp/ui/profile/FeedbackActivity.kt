package com.example.fitnessmobileapp.ui.profile

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessmobileapp.R

class FeedbackActivity : AppCompatActivity() {

    private lateinit var edtFeedback: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        findViewById<ImageView>(R.id.btnBackFeedback).setOnClickListener {
            finish()
        }

        edtFeedback = findViewById(R.id.edtFeedback)

        findViewById<TextView>(R.id.btnSendFeedback).setOnClickListener {
            val content = edtFeedback.text.toString().trim()

            if (content.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập ý kiến phản hồi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            getSharedPreferences("feedback_data", MODE_PRIVATE)
                .edit()
                .putString("lastFeedback", content)
                .apply()

            Toast.makeText(this, "Đã gửi phản hồi. Cảm ơn bạn!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
