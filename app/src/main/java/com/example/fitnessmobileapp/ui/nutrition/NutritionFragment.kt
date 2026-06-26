package com.example.fitnessmobileapp.ui.nutrition

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.fitnessmobileapp.R
import com.example.fitnessmobileapp.ui.nutrition.NutritionItem

private val menuList = List(30) { i ->

    val breakfasts = listOf(
        "Bánh mì trứng", "Phở bò", "Xôi gà",
        "Bún thang", "Yến mạch trái cây",
        "Sandwich bơ", "Bánh cuốn"
    )

    val snacks = listOf(
        "Táo", "Sữa chua", "Hạt hạnh nhân",
        "Chuối", "Sinh tố bơ", "Việt quất"
    )

    val lunches = listOf(
        "Cơm gà nướng", "Cá kho tộ",
        "Bò xào thiên lý", "Cơm tấm",
        "Mì xào hải sản", "Thịt kho tàu"
    )

    val dinners = listOf(
        "Salad ức gà", "Canh rong biển",
        "Đậu phụ sốt", "Cá hấp xì dầu",
        "Súp bí đỏ", "Bông cải luộc"
    )

    val vegLunches = listOf(
        "Đậu phụ sốt cà", "Nấm kho gừng",
        "Cơm gạo lứt rau củ", "Canh chua chay",
        "Đậu hũ chiên sả"
    )

    val vegDinners = listOf(
        "Canh bí đỏ", "Salad bơ",
        "Rau cải luộc", "Đậu phụ hấp",
        "Mướp xào"
    )

    val altStd = listOf(
        "Thịt gà", "Trứng", "Cá hồi", "Bò nướng",
        "Tôm hấp", "Ức gà áp chảo", "Cá thu",
        "Thịt heo nạc", "Cá ngừ", "Thịt vịt",
        "Bò xào", "Cá basa", "Gà luộc",
        "Cá rô phi", "Thịt thăn heo"
    )

    val altVeg = listOf(
        "Đậu hũ", "Nấm kho", "Hạt điều", "Rau chân vịt",
        "Đậu đỏ", "Đậu đen", "Đậu lăng",
        "Hạt chia", "Hạt óc chó", "Bông cải xanh",
        "Măng tây", "Đậu Hà Lan", "Nấm đông cô",
        "Sữa đậu nành", "Hạt hướng dương"

    )

    NutritionItem(
        day = i + 1,

        breakfastStd = breakfasts[i % breakfasts.size],
        snackStd = snacks[i % snacks.size],
        lunchStd = lunches[i % lunches.size],
        dinnerStd = dinners[i % dinners.size],

        breakfastVeg = "Ngũ cốc hạt",
        snackVeg = snacks[i % snacks.size],
        lunchVeg = vegLunches[i % vegLunches.size],
        dinnerVeg = vegDinners[i % vegDinners.size],

        breakfastAlt = altStd[(i * 4) % altStd.size],
        snackAlt = altStd[(i * 4 + 1) % altStd.size],
        lunchAlt = altStd[(i * 4 + 2) % altStd.size],
        dinnerAlt = altStd[(i * 4 + 3) % altStd.size],

        breakfastVegAlt = altVeg[(i * 4) % altVeg.size],
        snackVegAlt = altVeg[(i * 4 + 1) % altVeg.size],
        lunchVegAlt = altVeg[(i * 4 + 2) % altVeg.size],
        dinnerVegAlt = altVeg[(i * 4 + 3) % altVeg.size]
    )
}

class NutritionFragment : Fragment(R.layout.fragment_nutrition) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        for (i in 1..30) {

            val viewId = resources.getIdentifier(
                "day$i",
                "id",
                requireContext().packageName
            )

            val dayView = view.findViewById<TextView>(viewId)
            val prefs = requireActivity().getSharedPreferences("user_prefs", 0)

            if (prefs.getBoolean("day_${i}_done", false)) {
                dayView?.setBackgroundColor(
                    android.graphics.Color.parseColor("#C8E6C9")
                )
            }

            dayView?.setOnClickListener {

                val detailFragment = NutritionDetailFragment()

                val bundle = Bundle()
                bundle.putSerializable(
                    "nutrition_data",
                    menuList[i - 1]
                )

                detailFragment.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, detailFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }
}

