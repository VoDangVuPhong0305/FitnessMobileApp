package com.example.fitnessmobileapp.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.fitnessmobileapp.R

class ProfileFragment : Fragment() {

    private lateinit var txtHeight: TextView
    private lateinit var txtWeight: TextView
    private lateinit var txtTargetWeight: TextView
    private lateinit var txtGender: TextView
    private lateinit var txtBirthday: TextView
    private lateinit var txtWorkoutLevel: TextView
    private lateinit var txtWorkoutDays: TextView
    private lateinit var txtProfileBMI: TextView
    private lateinit var txtProfileGoal: TextView
    private lateinit var txtProfileAdvice: TextView

    private val heightCm = 165
    private val weightKg = 65.0
    private val targetWeightKg = 60.0
    private val gender = "Nữ"
    private val birthday = "2004-01-01"
    private val workoutLevel = "Trung bình"
    private val workoutDays = "3/7"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        txtHeight = view.findViewById(R.id.txtHeight)
        txtWeight = view.findViewById(R.id.txtWeight)
        txtTargetWeight = view.findViewById(R.id.txtTargetWeight)
        txtGender = view.findViewById(R.id.txtGender)
        txtBirthday = view.findViewById(R.id.txtBirthday)
        txtWorkoutLevel = view.findViewById(R.id.txtWorkoutLevel)
        txtWorkoutDays = view.findViewById(R.id.txtWorkoutDays)
        txtProfileBMI = view.findViewById(R.id.txtProfileBMI)
        txtProfileGoal = view.findViewById(R.id.txtProfileGoal)
        txtProfileAdvice = view.findViewById(R.id.txtProfileAdvice)

        showProfileData()

        return view
    }

    private fun showProfileData() {
        txtHeight.text = "$heightCm cm"
        txtWeight.text = "%.1f kg".format(weightKg)
        txtTargetWeight.text = "%.1f kg".format(targetWeightKg)
        txtGender.text = gender
        txtBirthday.text = birthday
        txtWorkoutLevel.text = workoutLevel
        txtWorkoutDays.text = workoutDays

        val heightM = heightCm / 100.0
        val bmi = weightKg / (heightM * heightM)

        val bmiStatus = when {
            bmi < 18.5 -> "Gầy"
            bmi < 25 -> "Bình thường"
            bmi < 30 -> "Thừa cân"
            else -> "Béo phì"
        }

        val goal = weightKg - targetWeightKg

        txtProfileBMI.text = "BMI hiện tại: %.1f - %s".format(bmi, bmiStatus)
        txtProfileGoal.text = "Mục tiêu: Giảm %.1f kg".format(goal)
        txtProfileAdvice.text = "Gợi ý: Duy trì tập luyện 3 - 4 buổi mỗi tuần."
    }
}