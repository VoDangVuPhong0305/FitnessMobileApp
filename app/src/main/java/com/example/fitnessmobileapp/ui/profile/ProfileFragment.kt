package com.example.fitnessmobileapp.ui.profile

import android.app.AlertDialog
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
            Toast.makeText(requireContext(), "Trợ giúp", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.itemMyProfile).setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<View>(R.id.itemReminder).setOnClickListener {
            Toast.makeText(requireContext(), "Nhắc nhở", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.itemSound).setOnClickListener {
            Toast.makeText(requireContext(), "Lựa chọn âm thanh", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.itemResetProgress).setOnClickListener {
            showResetProgressDialog()
        }

        view.findViewById<View>(R.id.itemDeleteAllData).setOnClickListener {
            showDeleteAllDataDialog()
        }

        view.findViewById<View>(R.id.itemFeedback).setOnClickListener {
            Toast.makeText(requireContext(), "Ý kiến phản hồi", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.itemFAQ).setOnClickListener {
            Toast.makeText(requireContext(), "Câu hỏi thường gặp", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showResetProgressDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Đặt lại tiến độ")
            .setMessage("Bạn có chắc muốn đặt lại tiến độ tập luyện không?")
            .setNegativeButton("HỦY", null)
            .setPositiveButton("ĐỒNG Ý") { _, _ ->
                Toast.makeText(requireContext(), "Đã đặt lại tiến độ", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun showDeleteAllDataDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa tất cả dữ liệu")
            .setMessage("Bạn có chắc muốn xóa tất cả dữ liệu không?")
            .setNegativeButton("HỦY", null)
            .setPositiveButton("XÓA") { _, _ ->
                Toast.makeText(requireContext(), "Đã xóa dữ liệu", Toast.LENGTH_SHORT).show()
            }
            .show()
    }
}