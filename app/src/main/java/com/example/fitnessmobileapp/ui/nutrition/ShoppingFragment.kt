package com.example.fitnessmobileapp.ui.nutrition

import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessmobileapp.R
import com.google.android.material.tabs.TabLayout

class ShoppingFragment : Fragment(R.layout.fragment_shopping) {

    private var isVegetarian = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val rv = view.findViewById<RecyclerView>(R.id.shoppingRecyclerView)
        rv.layoutManager = LinearLayoutManager(requireContext())

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        listOf("TUẦN 1", "TUẦN 2", "TUẦN 3", "TUẦN 4", "TUẦN 5").forEach {
            tabLayout.addTab(tabLayout.newTab().setText(it))
        }

        // Load data ban đầu
        rv.adapter = ShoppingAdapter(getData(week = 1, vegetarian = false))

        // Xử lý đổi tab tuần
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                rv.adapter =
                    ShoppingAdapter(getData(week = tab.position + 1, vegetarian = isVegetarian))
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // Xử lý đổi chế độ ăn
        val rgDietMode = view.findViewById<RadioGroup>(R.id.rgDietMode)
        rgDietMode.setOnCheckedChangeListener { _, checkedId ->
            isVegetarian = checkedId == R.id.rbVegetarian
            val currentWeek = tabLayout.selectedTabPosition + 1
            rv.adapter = ShoppingAdapter(getData(week = currentWeek, vegetarian = isVegetarian))
        }
    }

    private fun getData(week: Int, vegetarian: Boolean): List<ShoppingItem> {
        return if (vegetarian) getVegetarianData(week) else getStandardData(week)
    }

    private fun getStandardData(week: Int): List<ShoppingItem> {
        val week1 = listOf(
            ShoppingItem("BÁNH QUY", isHeader = true),
            ShoppingItem("Bánh quy", "(ít đường hoặc không đường thì tốt hơn)\n- như món tráng miệng *2 lần"),

            ShoppingItem("SẢN PHẨM BƠ SỮA, PHÔ MAI VÀ TRỨNG", isHeader = true),
            ShoppingItem("Trứng", "- *7"),
            ShoppingItem("Sữa không béo", "- ăn với bột yến mạch *2 lần"),
            ShoppingItem("Sữa chua (ít béo hoặc ít đường)", "- như đồ ăn nhẹ *7 lần"),
            ShoppingItem("Phô mai (mặn hoặc ít béo)", "- *1 miếng"),

            ShoppingItem("TRÁI CÂY TƯƠI", isHeader = true),
            ShoppingItem("Bưởi hoặc cam", "- như đồ ăn nhẹ *1 lần"),

            ShoppingItem("RAU", isHeader = true),
            ShoppingItem("Rau các loại", "- rau luộc *2 lần\n- dùng cho salad *5 lần\n- dùng cho bánh sandwich *3 lần"),
            ShoppingItem("Đậu các loại", "- đậu nướng làm món chính *1 lần"),
            ShoppingItem("Ngô", "- dùng cho salad *2 lần"),
            ShoppingItem("Hummus hoặc khoai tây nghiền", "- như đồ ăn nhẹ *2 lần"),
            ShoppingItem("Khoai tây", "- *3"),

            ShoppingItem("THỊT VÀ HẢI SẢN", isHeader = true),
            ShoppingItem("Ức gà", "- dùng cho salad *2 lần"),
            ShoppingItem("Cá hoặc hải sản", "- như món chính *3 lần"),
            ShoppingItem("Bất kỳ thịt nạc nào", "- như món chính *4 lần"),

            ShoppingItem("HẠT, NGŨ CỐC VÀ GẠO", isHeader = true),
            ShoppingItem("Bánh mì ngũ cốc nguyên cám", "- *7 lát"),
            ShoppingItem("Các loại hạt", "- như đồ ăn nhẹ *2 lần"),
            ShoppingItem("Bột yến mạch", "- dùng cho cháo *2 lần"),
            ShoppingItem("Gạo lứt hoặc mì", "- như món chính *1 lần")
        )

        val week2 = listOf(
            ShoppingItem("BÁNH QUY", isHeader = true),
            ShoppingItem("Bánh quy", "(ít đường hoặc không đường thì tốt hơn)\n- như món tráng miệng *3 lần"),

            ShoppingItem("SẢN PHẨM BƠ SỮA, PHÔ MAI VÀ TRỨNG", isHeader = true),
            ShoppingItem("Trứng", "- *10"),
            ShoppingItem("Sữa không béo", "- ăn với bột yến mạch *2 lần"),
            ShoppingItem("Sữa chua (ít béo hoặc ít đường)", "- như đồ ăn nhẹ *5 lần"),
            ShoppingItem("Phô mai (mặn hoặc ít béo)", "- *1 miếng"),

            ShoppingItem("TRÁI CÂY TƯƠI", isHeader = true),
            ShoppingItem("Bưởi hoặc cam", "- như đồ ăn nhẹ *1 lần"),
            ShoppingItem("Lựa chọn trong các loại quả mọng\n(quả việt quất, dâu tây, quả mâm xôi đỏ, quả mâm xôi đen)", "- như đồ ăn nhẹ *2 lần"),
            ShoppingItem("Trái cây (táo, lê, chuối, cam, bưởi, bơ, dưa, v.v.)", "- như đồ ăn nhẹ *1 lần"),

            ShoppingItem("RAU", isHeader = true),
            ShoppingItem("Rau các loại", "- rau luộc *2 lần\n- dùng cho salad *5 lần\n- dùng cho bánh sandwich *2 lần\n- sử dụng cho món trứng tráng *2 lần\n- dùng cho súp *3 lần"),
            ShoppingItem("Đậu các loại", "- đậu nướng làm món chính *2 lần"),
            ShoppingItem("Ngô", "- dùng cho salad *1 lần"),
            ShoppingItem("Hummus hoặc khoai tây nghiền", "- như đồ ăn nhẹ *1 lần"),
            ShoppingItem("Khoai tây", "- *6"),

            ShoppingItem("THỊT VÀ HẢI SẢN", isHeader = true),
            ShoppingItem("Ức gà", "- dùng cho salad *3 lần"),
            ShoppingItem("Cá hoặc hải sản", "- như món chính *2 lần"),
            ShoppingItem("Bất kỳ thịt nạc nào", "- như món chính *2 lần"),

            ShoppingItem("HẠT, NGŨ CỐC VÀ GẠO", isHeader = true),
            ShoppingItem("Bánh mì ngũ cốc nguyên cám hoặc bánh mì nướng", "- *4 lát"),
            ShoppingItem("Các loại hạt (bất kỳ loại hạt nào không có muối)", "- như đồ ăn nhẹ *2 lần"),
            ShoppingItem("Bột yến mạch", "- dùng cho cháo bột yến mạch *1 lần"),
            ShoppingItem("Ngũ cốc nguyên hạt\n(bột yến mạch, lúa mì nứt, lúa mạch, cháo ngô Ý)", "- như món chính *2 lần"),
            ShoppingItem("Gạo lứt hoặc mì", "- như món chính *2 lần")
        )

        val week3 = listOf(
            ShoppingItem("BÁNH QUY", isHeader = true),
            ShoppingItem("Bánh quy", "(ít đường hoặc không đường thì tốt hơn)\n- như món tráng miệng *4 lần"),

            ShoppingItem("SẢN PHẨM BƠ SỮA, PHÔ MAI VÀ TRỨNG", isHeader = true),
            ShoppingItem("Trứng", "- *8"),
            ShoppingItem("Sữa không béo", "- ăn với bột yến mạch *4 lần"),
            ShoppingItem("Sữa chua (ít béo hoặc ít đường)", "- như đồ ăn nhẹ *6 lần"),
            ShoppingItem("Phô mai (mặn hoặc ít béo)", "- *1 miếng"),

            ShoppingItem("TRÁI CÂY TƯƠI", isHeader = true),
            ShoppingItem("Bưởi hoặc cam", "- như đồ ăn nhẹ *1 lần"),
            ShoppingItem("Lựa chọn trong các loại quả mọng\n(quả việt quất, dâu tây, quả mâm xôi đỏ, quả mâm xôi đen)", "- như đồ ăn nhẹ *4 lần"),
            ShoppingItem("Trái cây (táo, lê, chuối, cam, bưởi, bơ, dưa, v.v.)", "- dùng cho salad *2 lần"),

            ShoppingItem("RAU", isHeader = true),
            ShoppingItem("Rau các loại", "- rau luộc *2 lần\n- dùng cho salad *5 lần\n- dùng cho bánh sandwich *1 lần\n- sử dụng cho món trứng tráng *2 lần\n- dùng cho súp *1 lần"),
            ShoppingItem("Đậu các loại", "- đậu nướng làm món chính *1 lần"),
            ShoppingItem("Ngô", "- dùng cho salad *3 lần"),
            ShoppingItem("Hummus hoặc khoai tây nghiền", "- như đồ ăn nhẹ *3 lần"),
            ShoppingItem("Khoai tây", "- *3"),

            ShoppingItem("THỊT VÀ HẢI SẢN", isHeader = true),
            ShoppingItem("Ức gà", "- dùng cho salad *2 lần"),
            ShoppingItem("Cá hoặc hải sản", "- như món chính *2 lần"),
            ShoppingItem("Bất kỳ thịt nạc nào", "- như món chính *3 lần"),

            ShoppingItem("HẠT, NGŨ CỐC VÀ GẠO", isHeader = true),
            ShoppingItem("Bánh mì ngũ cốc nguyên cám hoặc bánh mì nướng", "- *3 lát"),
            ShoppingItem("Các loại hạt (bất kỳ loại hạt nào không có muối)", "- như đồ ăn nhẹ *1 lần"),
            ShoppingItem("Bột yến mạch", "- dùng cho cháo bột yến mạch *3 lần"),
            ShoppingItem("Ngũ cốc nguyên hạt\n(bột yến mạch, lúa mì nứt, lúa mạch, cháo ngô Ý)", "- như món chính *4 lần"),
            ShoppingItem("Gạo lứt hoặc mì", "- như món chính *1 lần")
        )

        val week4 = listOf(
            ShoppingItem("BÁNH QUY", isHeader = true),
            ShoppingItem("Bánh quy", "(ít đường hoặc không đường thì tốt hơn)\n- như món tráng miệng *3 lần"),

            ShoppingItem("SẢN PHẨM BƠ SỮA, PHÔ MAI VÀ TRỨNG", isHeader = true),
            ShoppingItem("Trứng", "- *5"),
            ShoppingItem("Sữa không béo", "- ăn với bột yến mạch *4 lần"),
            ShoppingItem("Sữa chua (ít béo hoặc ít đường)", "- như đồ ăn nhẹ *4 lần"),
            ShoppingItem("Phô mai (mặn hoặc ít béo)", "- *1 miếng"),

            ShoppingItem("TRÁI CÂY TƯƠI", isHeader = true),
            ShoppingItem("Bưởi hoặc cam", "- như đồ ăn nhẹ *3 lần"),
            ShoppingItem("Lựa chọn trong các loại quả mọng\n(quả việt quất, dâu tây, quả mâm xôi đỏ, quả mâm xôi đen)", "- như đồ ăn nhẹ *3 lần"),
            ShoppingItem("Trái cây (táo, lê, chuối, cam, bưởi, bơ, dưa, v.v.)", "- dùng cho salad *3 lần"),

            ShoppingItem("RAU", isHeader = true),
            ShoppingItem("Rau các loại", "- rau luộc *2 lần\n- dùng cho salad *4 lần\n- dùng cho bánh sandwich *1 lần\n- sử dụng cho món trứng tráng *1 lần\n- dùng cho súp *1 lần"),
            ShoppingItem("Đậu các loại", "- đậu nướng làm món chính *1 lần"),
            ShoppingItem("Ngô", "- dùng cho salad *2 lần"),
            ShoppingItem("Hummus hoặc khoai tây nghiền", "- như đồ ăn nhẹ *1 lần"),
            ShoppingItem("Khoai tây", "- *3"),

            ShoppingItem("THỊT VÀ HẢI SẢN", isHeader = true),
            ShoppingItem("Ức gà", "- dùng cho salad *2 lần"),
            ShoppingItem("Cá hoặc hải sản", "- như món chính *2 lần"),
            ShoppingItem("Bất kỳ thịt nạc nào", "- như món chính *2 lần"),

            ShoppingItem("HẠT, NGŨ CỐC VÀ GẠO", isHeader = true),
            ShoppingItem("Bánh mì ngũ cốc nguyên cám hoặc bánh mì nướng", "- *5 lát"),
            ShoppingItem("Các loại hạt (bất kỳ loại hạt nào không có muối)", "- như đồ ăn nhẹ *2 lần"),
            ShoppingItem("Bột yến mạch", "- dùng cho cháo bột yến mạch *2 lần"),
            ShoppingItem("Ngũ cốc nguyên hạt\n(bột yến mạch, lúa mì nứt, lúa mạch, cháo ngô Ý)", "- như món chính *4 lần"),
            ShoppingItem("Gạo lứt hoặc mì", "- như món chính *2 lần")
        )

        val week5 = listOf(
            ShoppingItem("SẢN PHẨM BƠ SỮA, PHÔ MAI VÀ TRỨNG", isHeader = true),
            ShoppingItem("Trứng", "- *5"),
            ShoppingItem("Sữa không béo", "- ăn với bột yến mạch *1 lần"),

            ShoppingItem("TRÁI CÂY TƯƠI", isHeader = true),
            ShoppingItem("Trái cây (táo, lê, chuối, cam, bưởi, bơ, dưa, v.v.)", "- như đồ ăn nhẹ *1 lần"),
            ShoppingItem("Lựa chọn trong các loại quả mọng\n(quả việt quất, dâu tây, quả mâm xôi đỏ, quả mâm xôi đen)", "- như đồ ăn nhẹ *1 lần"),

            ShoppingItem("RAU", isHeader = true),
            ShoppingItem("Rau các loại", "- rau luộc *1 lần\n- sử dụng cho món trứng tráng *1 lần\n- dùng cho súp *1 lần"),
            ShoppingItem("Ngô", "- dùng cho salad *1 lần"),
            ShoppingItem("Hummus hoặc khoai tây nghiền", "- như đồ ăn nhẹ *1 lần"),

            ShoppingItem("THỊT VÀ HẢI SẢN", isHeader = true),
            ShoppingItem("Bất kỳ thịt nạc nào", "- như món chính *1 lần"),
            ShoppingItem("Cá hoặc hải sản", "- như món chính *1 lần"),

            ShoppingItem("HẠT, NGŨ CỐC VÀ GẠO", isHeader = true),
            ShoppingItem("Các loại hạt (bất kỳ loại hạt nào không có muối)", "- như đồ ăn nhẹ *1 lần"),
            ShoppingItem("Bột yến mạch", "- dùng cho cháo bột yến mạch *1 lần"),
            ShoppingItem("Ngũ cốc nguyên hạt\n(bột yến mạch, lúa mì nứt, lúa mạch, cháo ngô Ý)", "- như món chính *1 lần"),
            ShoppingItem("Gạo lứt hoặc mì", "- như món chính *1 lần")
        )

        return when (week) {
            1 -> week1
            2 -> week2
            3 -> week3
            4 -> week4
            5 -> week5
            else -> week1
        }
    }

    private fun getVegetarianData(week: Int): List<ShoppingItem> {
        val week1 = listOf(
            ShoppingItem("BÁNH QUY", isHeader = true),
            ShoppingItem(
                "Bánh quy",
                "(ít đường hoặc không đường thì tốt hơn)\n- như món tráng miệng *2 lần"
            ),

            ShoppingItem("SẢN PHẨM BƠ SỮA, PHÔ MAI VÀ TRỨNG", isHeader = true),
            ShoppingItem("Trứng", "- *7"),
            ShoppingItem("Sữa hạnh nhân hoặc đậu nành", "- ăn với bột yến mạch *2 lần"),
            ShoppingItem("Sữa chua không đường (ít béo)", "- như đồ ăn nhẹ *7 lần"),
            ShoppingItem("Phô mai chay (ít béo)", "- *1 miếng"),

            ShoppingItem("TRÁI CÂY TƯƠI", isHeader = true),
            ShoppingItem("Bưởi hoặc cam", "- như đồ ăn nhẹ *1 lần"),

            ShoppingItem("RAU VÀ ĐẬU", isHeader = true),
            ShoppingItem(
                "Rau các loại",
                "- rau luộc *2 lần\n- dùng cho salad *5 lần\n- dùng cho bánh sandwich *3 lần"
            ),
            ShoppingItem("Đậu hũ", "- như món chính *3 lần"),
            ShoppingItem("Đậu lăng", "- đậu nướng làm món chính *1 lần"),
            ShoppingItem("Ngô", "- dùng cho salad *2 lần"),
            ShoppingItem("Hummus hoặc khoai tây nghiền", "- như đồ ăn nhẹ *2 lần"),
            ShoppingItem("Khoai tây", "- *3"),

            ShoppingItem("HẠT, NGŨ CỐC VÀ GẠO", isHeader = true),
            ShoppingItem("Bánh mì ngũ cốc nguyên cám", "- *7 lát"),
            ShoppingItem("Các loại hạt", "- như đồ ăn nhẹ *2 lần"),
            ShoppingItem("Bột yến mạch", "- dùng cho cháo *2 lần"),
            ShoppingItem("Gạo lứt hoặc mì", "- như món chính *1 lần")
        )

        val week2 = listOf(
            ShoppingItem("BÁNH QUY", isHeader = true),
            ShoppingItem(
                "Bánh quy",
                "(ít đường hoặc không đường thì tốt hơn)\n- như món tráng miệng *3 lần"
            ),

            ShoppingItem("SẢN PHẨM BƠ SỮA, PHÔ MAI VÀ TRỨNG", isHeader = true),
            ShoppingItem("Trứng", "- *10"),
            ShoppingItem("Sữa hạnh nhân hoặc đậu nành", "- ăn với bột yến mạch *2 lần"),
            ShoppingItem("Sữa chua không đường (ít béo)", "- như đồ ăn nhẹ *5 lần"),
            ShoppingItem("Phô mai chay (ít béo)", "- *1 miếng"),

            ShoppingItem("TRÁI CÂY TƯƠI", isHeader = true),
            ShoppingItem("Bưởi hoặc cam", "- như đồ ăn nhẹ *1 lần"),
            ShoppingItem(
                "Lựa chọn trong các loại quả mọng\n(quả việt quất, dâu tây, quả mâm xôi đỏ, quả mâm xôi đen)",
                "- như đồ ăn nhẹ *2 lần"
            ),
            ShoppingItem(
                "Trái cây (táo, lê, chuối, cam, bưởi, bơ, dưa, v.v.)",
                "- như đồ ăn nhẹ *1 lần"
            ),

            ShoppingItem("RAU VÀ ĐẬU", isHeader = true),
            ShoppingItem(
                "Rau các loại",
                "- rau luộc *2 lần\n- dùng cho salad *5 lần\n- dùng cho bánh sandwich *2 lần\n- sử dụng cho món trứng tráng *2 lần\n- dùng cho súp *3 lần"
            ),
            ShoppingItem("Đậu hũ", "- như món chính *2 lần"),
            ShoppingItem("Đậu lăng", "- đậu nướng làm món chính *2 lần"),
            ShoppingItem("Ngô", "- dùng cho salad *1 lần"),
            ShoppingItem("Hummus hoặc khoai tây nghiền", "- như đồ ăn nhẹ *1 lần"),
            ShoppingItem("Khoai tây", "- *6"),

            ShoppingItem("HẠT, NGŨ CỐC VÀ GẠO", isHeader = true),
            ShoppingItem("Bánh mì ngũ cốc nguyên cám hoặc bánh mì nướng", "- *4 lát"),
            ShoppingItem(
                "Các loại hạt (bất kỳ loại hạt nào không có muối)",
                "- như đồ ăn nhẹ *2 lần"
            ),
            ShoppingItem("Bột yến mạch", "- dùng cho cháo bột yến mạch *1 lần"),
            ShoppingItem(
                "Ngũ cốc nguyên hạt\n(bột yến mạch, lúa mì nứt, lúa mạch, cháo ngô Ý)",
                "- như món chính *2 lần"
            ),
            ShoppingItem("Gạo lứt hoặc mì", "- như món chính *2 lần")
        )

        val week3 = listOf(
            ShoppingItem("BÁNH QUY", isHeader = true),
            ShoppingItem(
                "Bánh quy",
                "(ít đường hoặc không đường thì tốt hơn)\n- như món tráng miệng *4 lần"
            ),

            ShoppingItem("SẢN PHẨM BƠ SỮA, PHÔ MAI VÀ TRỨNG", isHeader = true),
            ShoppingItem("Trứng", "- *8"),
            ShoppingItem("Sữa hạnh nhân hoặc đậu nành", "- ăn với bột yến mạch *4 lần"),
            ShoppingItem("Sữa chua không đường (ít béo)", "- như đồ ăn nhẹ *6 lần"),
            ShoppingItem("Phô mai chay (ít béo)", "- *1 miếng"),

            ShoppingItem("TRÁI CÂY TƯƠI", isHeader = true),
            ShoppingItem("Bưởi hoặc cam", "- như đồ ăn nhẹ *1 lần"),
            ShoppingItem(
                "Lựa chọn trong các loại quả mọng\n(quả việt quất, dâu tây, quả mâm xôi đỏ, quả mâm xôi đen)",
                "- như đồ ăn nhẹ *4 lần"
            ),
            ShoppingItem(
                "Trái cây (táo, lê, chuối, cam, bưởi, bơ, dưa, v.v.)",
                "- dùng cho salad *2 lần"
            ),

            ShoppingItem("RAU VÀ ĐẬU", isHeader = true),
            ShoppingItem(
                "Rau các loại",
                "- rau luộc *2 lần\n- dùng cho salad *5 lần\n- dùng cho bánh sandwich *1 lần\n- sử dụng cho món trứng tráng *2 lần\n- dùng cho súp *1 lần"
            ),
            ShoppingItem("Đậu hũ", "- như món chính *2 lần"),
            ShoppingItem("Đậu lăng", "- đậu nướng làm món chính *1 lần"),
            ShoppingItem("Ngô", "- dùng cho salad *3 lần"),
            ShoppingItem("Hummus hoặc khoai tây nghiền", "- như đồ ăn nhẹ *3 lần"),
            ShoppingItem("Khoai tây", "- *3"),

            ShoppingItem("HẠT, NGŨ CỐC VÀ GẠO", isHeader = true),
            ShoppingItem("Bánh mì ngũ cốc nguyên cám hoặc bánh mì nướng", "- *3 lát"),
            ShoppingItem(
                "Các loại hạt (bất kỳ loại hạt nào không có muối)",
                "- như đồ ăn nhẹ *1 lần"
            ),
            ShoppingItem("Bột yến mạch", "- dùng cho cháo bột yến mạch *3 lần"),
            ShoppingItem(
                "Ngũ cốc nguyên hạt\n(bột yến mạch, lúa mì nứt, lúa mạch, cháo ngô Ý)",
                "- như món chính *4 lần"
            ),
            ShoppingItem("Gạo lứt hoặc mì", "- như món chính *1 lần")
        )

        val week4 = listOf(
            ShoppingItem("BÁNH QUY", isHeader = true),
            ShoppingItem(
                "Bánh quy",
                "(ít đường hoặc không đường thì tốt hơn)\n- như món tráng miệng *3 lần"
            ),

            ShoppingItem("SẢN PHẨM BƠ SỮA, PHÔ MAI VÀ TRỨNG", isHeader = true),
            ShoppingItem("Trứng", "- *5"),
            ShoppingItem("Sữa hạnh nhân hoặc đậu nành", "- ăn với bột yến mạch *4 lần"),
            ShoppingItem("Sữa chua không đường (ít béo)", "- như đồ ăn nhẹ *4 lần"),
            ShoppingItem("Phô mai chay (ít béo)", "- *1 miếng"),

            ShoppingItem("TRÁI CÂY TƯƠI", isHeader = true),
            ShoppingItem("Bưởi hoặc cam", "- như đồ ăn nhẹ *3 lần"),
            ShoppingItem(
                "Lựa chọn trong các loại quả mọng\n(quả việt quất, dâu tây, quả mâm xôi đỏ, quả mâm xôi đen)",
                "- như đồ ăn nhẹ *3 lần"
            ),
            ShoppingItem(
                "Trái cây (táo, lê, chuối, cam, bưởi, bơ, dưa, v.v.)",
                "- dùng cho salad *3 lần"
            ),

            ShoppingItem("RAU VÀ ĐẬU", isHeader = true),
            ShoppingItem(
                "Rau các loại",
                "- rau luộc *2 lần\n- dùng cho salad *4 lần\n- dùng cho bánh sandwich *1 lần\n- sử dụng cho món trứng tráng *1 lần\n- dùng cho súp *1 lần"
            ),
            ShoppingItem("Đậu hũ", "- như món chính *2 lần"),
            ShoppingItem("Đậu lăng", "- đậu nướng làm món chính *1 lần"),
            ShoppingItem("Ngô", "- dùng cho salad *2 lần"),
            ShoppingItem("Hummus hoặc khoai tây nghiền", "- như đồ ăn nhẹ *1 lần"),
            ShoppingItem("Khoai tây", "- *3"),

            ShoppingItem("HẠT, NGŨ CỐC VÀ GẠO", isHeader = true),
            ShoppingItem("Bánh mì ngũ cốc nguyên cám hoặc bánh mì nướng", "- *5 lát"),
            ShoppingItem(
                "Các loại hạt (bất kỳ loại hạt nào không có muối)",
                "- như đồ ăn nhẹ *2 lần"
            ),
            ShoppingItem("Bột yến mạch", "- dùng cho cháo bột yến mạch *2 lần"),
            ShoppingItem(
                "Ngũ cốc nguyên hạt\n(bột yến mạch, lúa mì nứt, lúa mạch, cháo ngô Ý)",
                "- như món chính *4 lần"
            ),
            ShoppingItem("Gạo lứt hoặc mì", "- như món chính *2 lần")
        )

        val week5 = listOf(
            ShoppingItem("SẢN PHẨM BƠ SỮA, PHÔ MAI VÀ TRỨNG", isHeader = true),
            ShoppingItem("Trứng", "- *5"),
            ShoppingItem("Sữa hạnh nhân hoặc đậu nành", "- ăn với bột yến mạch *1 lần"),

            ShoppingItem("TRÁI CÂY TƯƠI", isHeader = true),
            ShoppingItem(
                "Trái cây (táo, lê, chuối, cam, bưởi, bơ, dưa, v.v.)",
                "- như đồ ăn nhẹ *1 lần"
            ),
            ShoppingItem(
                "Lựa chọn trong các loại quả mọng\n(quả việt quất, dâu tây, quả mâm xôi đỏ, quả mâm xôi đen)",
                "- như đồ ăn nhẹ *1 lần"
            ),

            ShoppingItem("RAU VÀ ĐẬU", isHeader = true),
            ShoppingItem(
                "Rau các loại",
                "- rau luộc *1 lần\n- sử dụng cho món trứng tráng *1 lần\n- dùng cho súp *1 lần"
            ),
            ShoppingItem("Đậu hũ", "- như món chính *1 lần"),
            ShoppingItem("Ngô", "- dùng cho salad *1 lần"),
            ShoppingItem("Hummus hoặc khoai tây nghiền", "- như đồ ăn nhẹ *1 lần"),

            ShoppingItem("HẠT, NGŨ CỐC VÀ GẠO", isHeader = true),
            ShoppingItem(
                "Các loại hạt (bất kỳ loại hạt nào không có muối)",
                "- như đồ ăn nhẹ *1 lần"
            ),
            ShoppingItem("Bột yến mạch", "- dùng cho cháo bột yến mạch *1 lần"),
            ShoppingItem(
                "Ngũ cốc nguyên hạt\n(bột yến mạch, lúa mì nứt, lúa mạch, cháo ngô Ý)",
                "- như món chính *1 lần"
            ),
            ShoppingItem("Gạo lứt hoặc mì", "- như món chính *1 lần")
        )

        return when (week) {
            1 -> week1
            2 -> week2
            3 -> week3
            4 -> week4
            5 -> week5
            else -> week1
        }
    }
}