package com.example.fitnessmobileapp

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.example.fitnessmobileapp.ui.nutrition.NutritionFragment
import com.example.fitnessmobileapp.ui.plan.PlanFragment
import com.example.fitnessmobileapp.ui.profile.ProfileFragment
import com.example.fitnessmobileapp.ui.report.ReportFragment

class MainActivity : AppCompatActivity() {

    private lateinit var navNutrition: LinearLayout
    private lateinit var navPlan: LinearLayout
    private lateinit var navReport: LinearLayout
    private lateinit var navProfile: LinearLayout

    private lateinit var imgNavNutrition: ImageView
    private lateinit var imgNavPlan: ImageView
    private lateinit var imgNavReport: ImageView
    private lateinit var imgNavProfile: ImageView

    private lateinit var txtNavNutrition: TextView
    private lateinit var txtNavPlan: TextView
    private lateinit var txtNavReport: TextView
    private lateinit var txtNavProfile: TextView

    private var currentTabId: Int = 0

    private val activeColor = Color.parseColor("#222222")
    private val inactiveColor = Color.parseColor("#A8A8A8")

    // Chức năng: khởi tạo màn chính của app và thiết lập thanh điều hướng custom.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Chức năng: không dùng edge-to-edge cho MainActivity.
        // Mục đích là để thanh navigation của hệ thống chỉ còn một thanh đen nhỏ bên dưới,
        // không làm nền trắng của app kéo dài xuống quá nhiều.
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = Color.parseColor("#EEEEEE")
        window.navigationBarColor = Color.BLACK

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = false
        }

        setContentView(R.layout.activity_main)

        bindViews()
        setupNavigationClicks()

        // Chức năng: mở app lên sẽ vào tab Kế hoạch trước.
        openTab(R.id.nav_plan)
    }

    // Chức năng: ánh xạ các View của thanh điều hướng custom.
    private fun bindViews() {
        navNutrition = findViewById(R.id.nav_nutrition)
        navPlan = findViewById(R.id.nav_plan)
        navReport = findViewById(R.id.nav_report)
        navProfile = findViewById(R.id.nav_profile)

        imgNavNutrition = findViewById(R.id.imgNavNutrition)
        imgNavPlan = findViewById(R.id.imgNavPlan)
        imgNavReport = findViewById(R.id.imgNavReport)
        imgNavProfile = findViewById(R.id.imgNavProfile)

        txtNavNutrition = findViewById(R.id.txtNavNutrition)
        txtNavPlan = findViewById(R.id.txtNavPlan)
        txtNavReport = findViewById(R.id.txtNavReport)
        txtNavProfile = findViewById(R.id.txtNavProfile)
    }

    // Chức năng: gắn sự kiện bấm cho từng tab ở thanh điều hướng dưới.
    private fun setupNavigationClicks() {
        navNutrition.setOnClickListener {
            openTab(R.id.nav_nutrition)
        }

        navPlan.setOnClickListener {
            openTab(R.id.nav_plan)
        }

        navReport.setOnClickListener {
            openTab(R.id.nav_report)
        }

        navProfile.setOnClickListener {
            openTab(R.id.nav_profile)
        }
    }

    // Chức năng: mở tab được chọn và đổi Fragment tương ứng.
    private fun openTab(tabId: Int) {
        if (currentTabId == tabId) return

        currentTabId = tabId

        when (tabId) {
            R.id.nav_nutrition -> {
                replaceFragment(NutritionFragment())
            }

            R.id.nav_plan -> {
                replaceFragment(PlanFragment())
            }

            R.id.nav_report -> {
                replaceFragment(ReportFragment())
            }

            R.id.nav_profile -> {
                replaceFragment(ProfileFragment())
            }
        }

        updateNavigationState(tabId)
    }

    // Chức năng: cập nhật màu icon và chữ theo tab đang chọn.
    private fun updateNavigationState(selectedTabId: Int) {
        setTabState(
            icon = imgNavNutrition,
            text = txtNavNutrition,
            isSelected = selectedTabId == R.id.nav_nutrition
        )

        setTabState(
            icon = imgNavPlan,
            text = txtNavPlan,
            isSelected = selectedTabId == R.id.nav_plan
        )

        setTabState(
            icon = imgNavReport,
            text = txtNavReport,
            isSelected = selectedTabId == R.id.nav_report
        )

        setTabState(
            icon = imgNavProfile,
            text = txtNavProfile,
            isSelected = selectedTabId == R.id.nav_profile
        )
    }

    // Chức năng: đổi màu và độ nổi bật cho từng tab.
// Không setTypeface(null, ...) nữa để tránh ghi đè font Anton trong XML.
    private fun setTabState(
        icon: ImageView,
        text: TextView,
        isSelected: Boolean
    ) {
        val color = if (isSelected) activeColor else inactiveColor

        icon.setColorFilter(color)
        text.setTextColor(color)

        // Chức năng: giữ chữ đậm hơn nhưng không phá font Anton đã khai báo trong XML.
        text.paint.isFakeBoldText = true

        icon.alpha = if (isSelected) 1f else 0.72f
        text.alpha = if (isSelected) 1f else 0.86f

        // Chức năng: tab đang chọn to hơn nhẹ để dễ nhận biết.
        icon.scaleX = if (isSelected) 1.12f else 1f
        icon.scaleY = if (isSelected) 1.12f else 1f

        text.scaleX = if (isSelected) 1.04f else 1f
        text.scaleY = if (isSelected) 1.04f else 1f
    }

    // Chức năng: thay Fragment đang hiển thị trong vùng nội dung chính.
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}