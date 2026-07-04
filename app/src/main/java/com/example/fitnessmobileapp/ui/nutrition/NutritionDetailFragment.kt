package com.example.fitnessmobileapp.ui.nutrition

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
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
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan

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

        val tvTitle = header.findViewById<TextView>(R.id.tvTitle)

        val tvNutritionGoal = view.findViewById<TextView>(R.id.tvNutritionGoal)

        val tvBreakfast = view.findViewById<TextView>(R.id.tvBreakfast)
        val tvSnack = view.findViewById<TextView>(R.id.tvSnack)
        val tvLunch = view.findViewById<TextView>(R.id.tvLunch)
        val tvDinner = view.findViewById<TextView>(R.id.tvDinner)

        val btnStandard = view.findViewById<Button>(R.id.btnStandard)
        val btnVegetarian = view.findViewById<Button>(R.id.btnVegetarian)

        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        val btnCheck = view.findViewById<ImageView>(R.id.btnCheck)

        val normalFont = Typeface.create("sans-serif", Typeface.NORMAL)

        tvNutritionGoal.typeface = normalFont
        tvBreakfast.typeface = normalFont
        tvSnack.typeface = normalFont
        tvLunch.typeface = normalFont
        tvDinner.typeface = normalFont

        val data = arguments?.getSerializable("nutrition_data") as? NutritionItem

        data?.let { item ->

            val profileInfo = NutritionGoalHelper.getProfileInfo(requireContext())

            tvNutritionGoal.text =
                "${NutritionGoalHelper.getGoalTitle(profileInfo)}\n" +
                        NutritionGoalHelper.getGoalDescription(profileInfo)

            tvTitle.text = "NGÀY ${item.day}"

            val prefs = requireActivity().getSharedPreferences("user_prefs", 0)
            val isDone = prefs.getBoolean("day_${item.day}_done", false)

            updateCheckButton(btnCheck, isDone)

            fun updateButtons(isStandard: Boolean) {
                if (isStandard) {
                    btnStandard.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#62C97B"))
                    btnStandard.setTextColor(Color.WHITE)

                    btnVegetarian.backgroundTintList =
                        ColorStateList.valueOf(Color.WHITE)
                    btnVegetarian.setTextColor(Color.parseColor("#7D7D7D"))
                } else {
                    btnVegetarian.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#62C97B"))
                    btnVegetarian.setTextColor(Color.WHITE)

                    btnStandard.backgroundTintList =
                        ColorStateList.valueOf(Color.WHITE)
                    btnStandard.setTextColor(Color.parseColor("#7D7D7D"))
                }
            }

            fun cleanLine(line: String): String {
                return line
                    .trim()
                    .removePrefix("•")
                    .removePrefix("●")
                    .trim()
            }

            fun addFoodLines(
                result: MutableList<String>,
                foodText: String
            ) {
                foodText.split("\n")
                    .map { cleanLine(it) }
                    .filter { it.isNotEmpty() }
                    .forEach { line ->
                        result.add("●  $line")
                    }
            }

            fun getMealAdvice(
                mealTitle: String,
                mainFood: String,
                altFood: String
            ): String {
                val fullText = NutritionGoalHelper.applyGoalToMeal(
                    mealTitle = mealTitle,
                    mainFood = mainFood,
                    altFood = altFood,
                    info = profileInfo
                )

                return fullText
                    .split("\n")
                    .map { cleanLine(it) }
                    .lastOrNull { it.startsWith("Gợi ý") }
                    ?: ""
            }

            fun formatMealBlock(
                title: String,
                mainFood: String,
                altFood: String,
                advice: String
            ): SpannableStringBuilder {
                val builder = SpannableStringBuilder()

                val titleText = SpannableString(title)
                titleText.setSpan(
                    RelativeSizeSpan(1.35f),
                    0,
                    titleText.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                titleText.setSpan(
                    StyleSpan(Typeface.BOLD),
                    0,
                    titleText.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                builder.append(titleText)
                builder.append("\n\n")

                val foodLines = mutableListOf<String>()

                addFoodLines(foodLines, mainFood)
                addFoodLines(foodLines, altFood)

                val cleanAdvice = cleanLine(advice)

                builder.append(foodLines.joinToString("\n"))

                if (cleanAdvice.isNotEmpty()) {
                    builder.append("\n\n")
                    builder.append(cleanAdvice)
                }

                return builder
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
                tvBreakfast.text = formatMealBlock(
                    title = "🍳  Bữa ăn sáng",
                    mainFood = b,
                    altFood = bAlt,
                    advice = getMealAdvice("Bữa sáng", b, bAlt)
                )

                tvSnack.text = formatMealBlock(
                    title = "🍎  Bữa ăn nhẹ",
                    mainFood = s,
                    altFood = sAlt,
                    advice = getMealAdvice("Bữa nhẹ", s, sAlt)
                )

                tvLunch.text = formatMealBlock(
                    title = "🥗  Bữa trưa",
                    mainFood = l,
                    altFood = lAlt,
                    advice = getMealAdvice("Bữa trưa", l, lAlt)
                )

                tvDinner.text = formatMealBlock(
                    title = "🍲  Bữa tối",
                    mainFood = d,
                    altFood = dAlt,
                    advice = getMealAdvice("Bữa tối", d, dAlt)
                )
            }

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

            btnCheck.setOnClickListener {
                val currentDone = prefs.getBoolean("day_${item.day}_done", false)
                val newDone = !currentDone

                prefs.edit()
                    .putBoolean("day_${item.day}_done", newDone)
                    .apply()

                updateCheckButton(btnCheck, newDone)

                if (newDone) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Thông báo")
                        .setMessage("Đã kết thúc")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                } else {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Thông báo")
                        .setMessage("Đã bỏ đánh dấu hoàn thành")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }

            btnBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun updateCheckButton(btnCheck: ImageView, isDone: Boolean) {
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
    }
}