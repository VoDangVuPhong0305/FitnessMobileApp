package com.example.fitnessmobileapp.ui.profile

import android.os.Bundle
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessmobileapp.R

class SoundActivity : AppCompatActivity() {

    private lateinit var radioSoundGroup: RadioGroup
    private var selectedSound = "Chuông nhẹ"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound)

        findViewById<ImageView>(R.id.btnBackSound).setOnClickListener {
            finish()
        }

        radioSoundGroup = findViewById(R.id.radioSoundGroup)

        loadSound()
        checkSelectedRadio()

        radioSoundGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedSound = when (checkedId) {
                R.id.radioSoundBell -> "Chuông nhẹ"
                R.id.radioSoundEnergy -> "Năng lượng"
                R.id.radioSoundBird -> "Tiếng chim"
                R.id.radioSoundSilent -> "Không âm thanh"
                else -> "Chuông nhẹ"
            }
            saveSound()
        }

        findViewById<TextView>(R.id.btnSaveSound).setOnClickListener {
            saveSound()
            Toast.makeText(this, "Đã lưu âm thanh: $selectedSound", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun checkSelectedRadio() {
        val checkedId = when (selectedSound) {
            "Năng lượng" -> R.id.radioSoundEnergy
            "Tiếng chim" -> R.id.radioSoundBird
            "Không âm thanh" -> R.id.radioSoundSilent
            else -> R.id.radioSoundBell
        }

        radioSoundGroup.check(checkedId)
    }

    private fun saveSound() {
        getSharedPreferences("sound_data", MODE_PRIVATE)
            .edit()
            .putString("selectedSound", selectedSound)
            .apply()
    }

    private fun loadSound() {
        selectedSound = getSharedPreferences("sound_data", MODE_PRIVATE)
            .getString("selectedSound", "Chuông nhẹ") ?: "Chuông nhẹ"
    }
}
