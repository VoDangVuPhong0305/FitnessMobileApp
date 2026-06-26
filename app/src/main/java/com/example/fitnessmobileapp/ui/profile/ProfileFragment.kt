package com.example.fitnessmobileapp.ui.profile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fitnessmobileapp.R

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.btnHelp).setOnClickListener {
            startActivity(Intent(requireContext(), FAQActivity::class.java))
        }

        view.findViewById<View>(R.id.itemMyProfile).setOnClickListener {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }

        view.findViewById<View>(R.id.itemReminder).setOnClickListener {
            startActivity(Intent(requireContext(), ReminderActivity::class.java))
        }

        view.findViewById<View>(R.id.itemSound).setOnClickListener {
            startActivity(Intent(requireContext(), SoundActivity::class.java))
        }

        view.findViewById<View>(R.id.itemResetProgress).setOnClickListener {
            showResetProgressDialog()
        }

        view.findViewById<View>(R.id.itemDeleteAllData).setOnClickListener {
            showDeleteAllDataDialog()
        }

        view.findViewById<View>(R.id.itemFeedback).setOnClickListener {
            startActivity(Intent(requireContext(), FeedbackActivity::class.java))
        }

        view.findViewById<View>(R.id.itemFAQ).setOnClickListener {
            startActivity(Intent(requireContext(), FAQActivity::class.java))
        }
    }

    private fun showResetProgressDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Đặt lại tiến độ")
            .setMessage("Bạn có chắc muốn đặt lại tiến độ tập luyện không?")
            .setNegativeButton("HỦY", null)
            .setPositiveButton("ĐỒNG Ý") { _, _ ->
                requireActivity()
                    .getSharedPreferences("progress_data", Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply()

                Toast.makeText(requireContext(), "Đã đặt lại tiến độ", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun showDeleteAllDataDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa tất cả dữ liệu")
            .setMessage("Bạn có chắc muốn xóa tất cả dữ liệu không? Thao tác này sẽ xóa hồ sơ, nhắc nhở, âm thanh và tiến độ.")
            .setNegativeButton("HỦY", null)
            .setPositiveButton("XÓA") { _, _ ->
                val context = requireContext()

                context.getSharedPreferences("profile_data", Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply()

                context.getSharedPreferences("reminder_data", Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply()

                context.getSharedPreferences("sound_data", Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply()

                context.getSharedPreferences("progress_data", Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply()

                Toast.makeText(context, "Đã xóa tất cả dữ liệu", Toast.LENGTH_SHORT).show()
            }
            .show()
    }
}
