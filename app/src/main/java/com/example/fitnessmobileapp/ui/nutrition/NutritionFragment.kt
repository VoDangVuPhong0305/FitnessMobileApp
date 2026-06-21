package com.example.fitnessmobileapp.ui.nutrition

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.fitnessmobileapp.R
import com.example.fitnessmobileapp.model.NutritionItem

class NutritionFragment : Fragment(R.layout.fragment_nutrition) {

    // Khai báo danh sách ngoài này để dùng chung
    private val menuList = List(30) { i ->
        val breakfasts = listOf("Bánh mì trứng", "Phở bò", "Xôi gà", "Bún thang", "Yến mạch trái cây", "Sandwich bơ", "Bánh cuốn")
        val snacks = listOf("Táo", "Sữa chua", "Hạt hạnh nhân", "Chuối", "Sinh tố bơ", "Việt quất")
        val lunches = listOf("Cơm gà nướng", "Cá kho tộ", "Bò xào thiên lý", "Cơm tấm", "Mì xào hải sản", "Thịt kho tàu")
        val dinners = listOf("Salad ức gà", "Canh rong biển", "Đậu phụ sốt", "Cá hấp xì dầu", "Súp bí đỏ", "Bông cải luộc")
        val vegLunches = listOf("Đậu phụ sốt cà", "Nấm kho gừng", "Cơm gạo lứt rau củ", "Canh chua chay", "Đậu hũ chiên sả")
        val vegDinners = listOf("Canh bí đỏ", "Salad bơ", "Rau cải luộc", "Đậu phụ hấp", "Mướp xào")

        NutritionItem(
            day = i + 1,
            breakfastStd = breakfasts[i % breakfasts.size],
            snackStd = snacks[i % snacks.size],
            lunchStd = lunches[i % lunches.size],
            dinnerStd = dinners[i % dinners.size],
            breakfastVeg = "Ngũ cốc hạt",
            snackVeg = snacks[i % snacks.size],
            lunchVeg = vegLunches[i % vegLunches.size],
            dinnerVeg = vegDinners[i % vegDinners.size]
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDays(view)
    }

    override fun onResume() {
        super.onResume()
        // Mỗi khi quay lại màn hình này, kiểm tra lại trạng thái để cập nhật màu sắc
        view?.let { setupDays(it) }
    }

    private fun setupDays(view: View) {
        val prefs = requireActivity().getSharedPreferences("user_prefs", 0)

        for (i in 1..30) {
            val resId = resources.getIdentifier("day$i", "id", requireContext().packageName)
            val dayView = view.findViewById<TextView>(resId)

            // Kiểm tra trạng thái đã xong chưa từ SharedPreferences
            val isDone = prefs.getBoolean("day_${i}_done", false)
            if (isDone) {
                // Nếu xong, đổi màu nền thành xanh nhạt hoặc đổi màu chữ
                dayView?.setBackgroundColor(Color.parseColor("#C8E6C9")) // Xanh nhạt
            }

            dayView?.setOnClickListener {
                val selectedItem = menuList[i - 1]
                val bundle = Bundle()
                bundle.putSerializable("nutrition_data", selectedItem)

                val detailFragment = NutritionDetailFragment()
                detailFragment.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, detailFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }
}