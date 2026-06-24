package com.example.fitnessmobileapp.ui.plan

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fitnessmobileapp.R
import com.example.fitnessmobileapp.data.model.PlanDay
import com.example.fitnessmobileapp.data.repository.AbsWorkoutPlan

class PlanFragment : Fragment(R.layout.fragment_plan) {

    private lateinit var layoutDayList: LinearLayout
    private lateinit var txtRemainingDays: TextView
    private lateinit var txtProgressPercent: TextView

    // Hiện tại đang giả lập:
    // Ngày 1 đã hoàn thành, Ngày 2 đang mở.
    // Sau này mình sẽ thay bằng SharedPreferences.
    private val completedDay = 1
    private val currentDay = 2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutDayList = view.findViewById(R.id.layoutDayList)
        txtRemainingDays = view.findViewById(R.id.txtRemainingDays)
        txtProgressPercent = view.findViewById(R.id.txtProgressPercent)

        showPlanDays()
    }

    private fun showPlanDays() {
        layoutDayList.removeAllViews()

        val planDays = AbsWorkoutPlan.getThirtyDayPlan()

        txtRemainingDays.text = "${30 - completedDay} ngày còn lại"
        txtProgressPercent.text = "${completedDay * 100 / 30}%"

        for (planDay in planDays) {
            val dayCard = createDayCard(planDay)
            layoutDayList.addView(dayCard)
        }
    }

    private fun createDayCard(planDay: PlanDay): View {
        return when {
            planDay.isRestDay -> createRestDayCard(planDay)
            planDay.dayNumber <= completedDay -> createCompletedDayCard(planDay)
            planDay.dayNumber == currentDay -> createActiveDayCard(planDay)
            else -> createPreviewDayCard(planDay)
        }
    }

    // Card ngày đã hoàn thành
    private fun createCompletedDayCard(planDay: PlanDay): View {
        val card = createBaseCard()
        card.setBackgroundResource(R.drawable.bg_plan_day_white)

        val textContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )
        }

        val txtTitle = createTitleText(planDay.title, "#222222")
        val txtSub = createSubText(
            "${planDay.durationMinutes} phút, ${planDay.exerciseCount} bài tập",
            "#888888"
        )

        textContainer.addView(txtTitle)
        textContainer.addView(txtSub)

        val checkCircle = TextView(requireContext()).apply {
            text = "✓"
            textSize = 34f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.parseColor("#6C4CF6"))
            gravity = Gravity.CENTER
            background = resources.getDrawable(R.drawable.bg_plan_check_circle, null)
            layoutParams = LinearLayout.LayoutParams(dp(72), dp(72))
        }

        card.addView(textContainer)
        card.addView(checkCircle)

        // Ngày đã hoàn thành vẫn bấm vào xem lại được
        card.setOnClickListener {
            openPlanDayDetail(planDay)
        }

        return card
    }

    // Card ngày hiện tại có nút Bắt đầu
    private fun createActiveDayCard(planDay: PlanDay): View {
        val card = createBaseCard()
        card.setBackgroundResource(R.drawable.bg_plan_day_active)

        val textContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )
        }

        val txtTitle = createTitleText(planDay.title, "#FFFFFF")
        val txtSub = createSubText(
            "${planDay.durationMinutes} phút, ${planDay.exerciseCount} bài tập",
            "#FFFFFF"
        )

        textContainer.addView(txtTitle)
        textContainer.addView(txtSub)

        val btnStart = TextView(requireContext()).apply {
            text = "Bắt đầu"
            textSize = 22f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.parseColor("#4B2FCB"))
            gravity = Gravity.CENTER
            background = resources.getDrawable(R.drawable.bg_plan_start_button, null)
            layoutParams = LinearLayout.LayoutParams(dp(150), dp(58))
        }

        card.addView(textContainer)
        card.addView(btnStart)

        card.setOnClickListener {
            openPlanDayDetail(planDay)
        }

        btnStart.setOnClickListener {
            openPlanDayDetail(planDay)
        }

        return card
    }

    // Card ngày chưa tới nhưng vẫn cho xem trước
    private fun createPreviewDayCard(planDay: PlanDay): View {
        val card = createBaseCard()
        card.setBackgroundResource(R.drawable.bg_plan_day_white)

        val textContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )
        }

        val txtTitle = createTitleText(planDay.title, "#222222")
        val txtSub = createSubText(
            "${planDay.durationMinutes} phút, ${planDay.exerciseCount} bài tập",
            "#888888"
        )

        textContainer.addView(txtTitle)
        textContainer.addView(txtSub)

        val txtArrow = TextView(requireContext()).apply {
            text = "›"
            textSize = 42f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.parseColor("#B6B6B6"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(dp(46), dp(72))
        }

        card.addView(textContainer)
        card.addView(txtArrow)

        // Ngày chưa tới vẫn bấm vào xem trước được
        card.setOnClickListener {
            openPlanDayDetail(planDay)
        }

        return card
    }

    // Card ngày nghỉ
    private fun createRestDayCard(planDay: PlanDay): View {
        val card = createBaseCard()
        card.setBackgroundResource(R.drawable.bg_plan_day_white)

        val textContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )
        }

        val txtTitle = createTitleText(planDay.title, "#222222")
        val txtSub = createSubText("Ngày nghỉ", "#888888")
        val txtRest = createSubText("Hãy để cơ bụng phục hồi", "#AAAAAA")

        textContainer.addView(txtTitle)
        textContainer.addView(txtSub)
        textContainer.addView(txtRest)

        val txtIcon = TextView(requireContext()).apply {
            text = "☕"
            textSize = 38f
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#B7A7F5"))
            layoutParams = LinearLayout.LayoutParams(dp(72), dp(72))
        }

        card.addView(textContainer)
        card.addView(txtIcon)

        card.setOnClickListener {
            Toast.makeText(requireContext(), "Đây là ngày nghỉ, không có bài tập", Toast.LENGTH_SHORT).show()
        }

        return card
    }

    private fun createBaseCard(): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(24), 0, dp(24), 0)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(118)
            ).apply {
                bottomMargin = dp(16)
            }
        }
    }

    private fun createTitleText(text: String, color: String): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            textSize = 30f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.parseColor(color))
            includeFontPadding = false
        }
    }

    private fun createSubText(text: String, color: String): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            textSize = 17f
            setTextColor(Color.parseColor(color))
            setPadding(0, dp(5), 0, 0)
            includeFontPadding = false
        }
    }

    private fun openPlanDayDetail(planDay: PlanDay) {
        val intent = Intent(requireContext(), PlanDayDetailActivity::class.java)
        intent.putExtra("DAY_NUMBER", planDay.dayNumber)
        intent.putExtra("DAY_TITLE", planDay.title)
        intent.putExtra("DURATION_MINUTES", planDay.durationMinutes)
        intent.putExtra("EXERCISE_COUNT", planDay.exerciseCount)
        intent.putExtra("EXERCISE_TYPE", planDay.exerciseType)
        intent.putStringArrayListExtra("EXERCISE_IDS", ArrayList(planDay.exerciseIds))
        startActivity(intent)
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}