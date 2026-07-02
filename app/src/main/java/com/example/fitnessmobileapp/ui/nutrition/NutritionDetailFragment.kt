package com.example.fitnessmobileapp.ui.nutrition

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.fitnessmobileapp.R

class NutritionDetailFragment : Fragment(R.layout.fragment_nutrition_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val header = view.findViewById<View>(R.id.header)
        ViewCompat.setOnApplyWindowInsetsListener(header) { v, insets ->
            val statusBarHeight =
                insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            v.setPadding(
                v.paddingLeft,
                statusBarHeight,
                v.paddingRight,
                v.paddingBottom
            )
            insets
        }

        val tvTitle = view.findViewById<View>(R.id.header)
            .findViewById<TextView>(R.id.tvTitle)

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

            val prefs = requireActivity().getSharedPreferences("user_prefs", 0)
            val isDone = prefs.getBoolean("day_${item.day}_done", false)

            if (isDone) {
                btnCheck.setImageResource(R.drawable.ic_check_black)
                btnCheck.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.bg_check_checked
                )
            } else {
                btnCheck.setImageResource(R.drawable.ic_check_white)
                btnCheck.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.bg_check_unchecked
                )
            }

            fun updateButtons(isStandard: Boolean) {
                if (isStandard) {
                    btnStandard.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#20C76F"))
                    btnStandard.setTextColor(Color.WHITE)

                    btnVegetarian.backgroundTintList =
                        ColorStateList.valueOf(Color.WHITE)
                    btnVegetarian.setTextColor(Color.parseColor("#666666"))
                } else {
                    btnVegetarian.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#20C76F"))
                    btnVegetarian.setTextColor(Color.WHITE)

                    btnStandard.backgroundTintList =
                        ColorStateList.valueOf(Color.WHITE)
                    btnStandard.setTextColor(Color.parseColor("#666666"))
                }
            }

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
                tvBreakfast.text = "🍳 Bữa sáng\n\n• $b\n• $bAlt"
                tvSnack.text = "🍎 Bữa nhẹ\n\n• $s\n• $sAlt"
                tvLunch.text = "🥗 Bữa trưa\n\n• $l\n• $lAlt"
                tvDinner.text = "🍲 Bữa tối\n\n• $d\n• $dAlt"
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
            updateButtons(true)

            // Nút Tiêu chuẩn
            btnStandard.setOnClickListener {
                updateButtons(true)

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
            }

            // Nút Ăn chay
            btnVegetarian.setOnClickListener {
                updateButtons(false)

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

            // Nút hoàn thành
            btnCheck.setOnClickListener {
                data?.let { item ->

                    val currentDone = prefs.getBoolean("day_${item.day}_done", false)

                    if (!currentDone) {
                        // Chưa hoàn thành -> chuyển sang hoàn thành
                        btnCheck.setImageResource(R.drawable.ic_check_black)
                        btnCheck.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.bg_check_checked
                        )

                        prefs.edit()
                            .putBoolean("day_${item.day}_done", true)
                            .apply()

                        AlertDialog.Builder(requireContext())
                            .setTitle("Thông báo")
                            .setMessage("Đã kết thúc")
                            .setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()

                    } else {
                        // Đã hoàn thành -> bỏ hoàn thành, quay về trạng thái ban đầu
                        btnCheck.setImageResource(R.drawable.ic_check_white)
                        btnCheck.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.bg_check_unchecked
                        )

                        prefs.edit()
                            .putBoolean("day_${item.day}_done", false)
                            .apply()

                        AlertDialog.Builder(requireContext())
                            .setTitle("Thông báo")
                            .setMessage("Đã bỏ đánh dấu hoàn thành")
                            .setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                }
            }
            // Nút quay lại
            btnBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
    }
}