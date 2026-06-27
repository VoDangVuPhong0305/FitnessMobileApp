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
import com.example.fitnessmobileapp.ui.nutrition.NutritionItem

class NutritionDetailFragment : Fragment(R.layout.fragment_nutrition_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

            fun showMenu(
                b: String,
                s: String,
                l: String,
                d: String,
                bAlt: String,
                sAlt: String,
                lAlt: String,
                dAlt: String
            ) {
                tvBreakfast.text =
                    "🍳 Bữa sáng\n\n• $b\n• $bAlt"

                tvSnack.text =
                    "🍎 Bữa nhẹ\n\n• $s\n• $sAlt"

                tvLunch.text =
                    "🥗 Bữa trưa\n\n• $l\n• $lAlt"

                tvDinner.text =
                    "🍲 Bữa tối\n\n• $d\n• $dAlt"
            }

            // Hiển thị mặc định (Tiêu chuẩn)
            showMenu(
                item.breakfastStd,
                item.snackStd,
                item.lunchStd,
                item.dinnerStd,
                item.breakfastAlt,
                item.snackAlt,
                item.lunchAlt,
                item.dinnerAlt
            )

            // Nút Tiêu chuẩn
            btnStandard.setOnClickListener {

                btnStandard.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50"))
                btnVegetarian.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(Color.WHITE)

                btnStandard.setTextColor(Color.WHITE)
                btnVegetarian.setTextColor(Color.parseColor("#4CAF50"))

                showMenu(
                    item.breakfastStd,
                    item.snackStd,
                    item.lunchStd,
                    item.dinnerStd,
                    item.breakfastAlt,
                    item.snackAlt,
                    item.lunchAlt,
                    item.dinnerAlt
                )
                btnStandard.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50"))
                btnStandard.setTextColor(Color.WHITE)

                btnVegetarian.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(Color.WHITE)
                btnVegetarian.setTextColor(Color.parseColor("#4CAF50"))
            }

// Nút Ăn chay
            btnVegetarian.setOnClickListener {

                btnVegetarian.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50"))
                btnStandard.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(Color.WHITE)

                btnVegetarian.setTextColor(Color.WHITE)
                btnStandard.setTextColor(Color.parseColor("#4CAF50"))

                showMenu(
                    item.breakfastVeg,
                    item.snackVeg,
                    item.lunchVeg,
                    item.dinnerVeg,
                    item.breakfastVegAlt,
                    item.snackVegAlt,
                    item.lunchVegAlt,
                    item.dinnerVegAlt
                )
            }
        }

        // Nút quay lại
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Nút hoàn thành
        btnCheck.setOnClickListener {


                btnCheck.setImageResource(android.R.drawable.checkbox_on_background)

                btnCheck.setColorFilter(Color.parseColor("#4CAF50"))

                btnCheck.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#E8F5E9")
                    )
                AlertDialog.Builder(requireContext())
                    .setTitle("Thông báo")
                    .setMessage("Đã kết thúc")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()

                data?.let { item ->
                    val prefs =
                        requireActivity().getSharedPreferences("user_prefs", 0)

                    prefs.edit()
                        .putBoolean("day_${item.day}_done", true)
                        .apply()
                }
            }
        }
    }