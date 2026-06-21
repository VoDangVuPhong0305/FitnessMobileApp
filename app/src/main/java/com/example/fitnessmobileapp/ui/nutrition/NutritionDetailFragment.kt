package com.example.fitnessmobileapp.ui.nutrition

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.fitnessmobileapp.R
import com.example.fitnessmobileapp.model.NutritionItem

class NutritionDetailFragment : Fragment(R.layout.fragment_nutrition_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ánh xạ các view
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvBreakfast = view.findViewById<TextView>(R.id.tvBreakfast)
        val tvSnack = view.findViewById<TextView>(R.id.tvSnack)
        val tvLunch = view.findViewById<TextView>(R.id.tvLunch)
        val tvDinner = view.findViewById<TextView>(R.id.tvDinner)
        val btnStandard = view.findViewById<Button>(R.id.btnStandard)
        val btnVegetarian = view.findViewById<Button>(R.id.btnVegetarian)
        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        val btnCheck = view.findViewById<ImageView>(R.id.btnCheck)

        val data = arguments?.getSerializable("nutrition_data") as? NutritionItem

        data?.let { item ->
            tvTitle.text = "NGÀY ${item.day}"

            fun showMenu(b: String, s: String, l: String, d: String) {
                tvBreakfast.text = "🍳 Bữa sáng: $b"
                tvSnack.text = "🍎 Bữa nhẹ: $s"
                tvLunch.text = "🥗 Bữa trưa: $l"
                tvDinner.text = "🍲 Bữa tối: $d"
            }

            // Hiển thị thực đơn mặc định
            showMenu(item.breakfastStd, item.snackStd, item.lunchStd, item.dinnerStd)

            btnStandard.setOnClickListener { showMenu(item.breakfastStd, item.snackStd, item.lunchStd, item.dinnerStd) }
            btnVegetarian.setOnClickListener { showMenu(item.breakfastVeg, item.snackVeg, item.lunchVeg, item.dinnerVeg) }
        }

        // Logic thoát
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Logic dấu tích
        btnCheck.setOnClickListener {
            // Cập nhật giao diện nút tích
            btnCheck.setImageResource(R.drawable.ic_check_green)
            btnCheck.setColorFilter(Color.GREEN)

            // Hiện thông báo
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Thông báo")
            builder.setMessage("Đã kết thúc")
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()

            // Lưu trạng thái vào bộ nhớ
            data?.let { item ->
                val prefs = requireActivity().getSharedPreferences("user_prefs", 0)
                prefs.edit().putBoolean("day_${item.day}_done", true).apply()
            }
        }
    }
}