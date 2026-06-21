package com.example.fitnessmobileapp.ui.plan

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessmobileapp.R

class PlanDayDetailActivity : AppCompatActivity() {

    private lateinit var btnBack: TextView
    private lateinit var txtDayTitle: TextView
    private lateinit var txtWorkoutInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_day_detail)

        btnBack = findViewById(R.id.btnBack)
        txtDayTitle = findViewById(R.id.txtDayTitle)
        txtWorkoutInfo = findViewById(R.id.txtWorkoutInfo)

        val dayNumber = intent.getIntExtra("DAY_NUMBER", 1)

        txtDayTitle.text = "NGÀY $dayNumber"
        txtWorkoutInfo.text = "Tập Toàn Thân"

        btnBack.setOnClickListener {
            finish()
        }
    }
}