package com.example.fitnessmobileapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.fitnessmobileapp.ui.nutrition.NutritionFragment
import com.example.fitnessmobileapp.ui.plan.PlanFragment
import com.example.fitnessmobileapp.ui.profile.ProfileFragment
import com.example.fitnessmobileapp.ui.report.ReportFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.graphics.Color
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = Color.TRANSPARENT
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottomNavigation)
        val originalBottomPadding = bottomNavigation.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigation) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                originalBottomPadding + systemBars.bottom
            )

            insets
        }

        // Mở app lên sẽ hiển thị màn hình Kế hoạch trước
        replaceFragment(PlanFragment())
        bottomNavigation.selectedItemId = R.id.nav_plan

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_nutrition -> {
                    replaceFragment(NutritionFragment())
                    true
                }

                R.id.nav_plan -> {
                    replaceFragment(PlanFragment())
                    true
                }

                R.id.nav_report -> {
                    replaceFragment(ReportFragment())
                    true
                }

                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}