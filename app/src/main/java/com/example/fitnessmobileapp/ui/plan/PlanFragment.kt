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
import com.example.fitnessmobileapp.data.repository.PlanProgressManager

class PlanFragment : Fragment(R.layout.fragment_plan) {

    private lateinit var layoutDayList: LinearLayout
    private lateinit var txtRemainingDays: TextView
    private lateinit var txtProgressPercent: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutDayList = view.findViewById(R.id.layoutDayList)
        txtRemainingDays = view.findViewById(R.id.txtRemainingDays)
        txtProgressPercent = view.findViewById(R.id.txtProgressPercent)

        showPlanDays()
    }

    override fun onResume() {
        super.onResume()

        if (::layoutDayList.isInitialized) {
            showPlanDays()
        }
    }

    // Chức năng: đọc tiến độ hiện tại và vẽ lại toàn bộ 30 ngày.
    // Hàm này chạy khi mở tab Plan và khi quay lại từ màn tập.
    private fun showPlanDays() {
        layoutDayList.removeAllViews()

        val planDays = AbsWorkoutPlan.getThirtyDayPlan()

        val completedDay = PlanProgressManager.getCompletedDay(requireContext())
        val currentDay = PlanProgressManager.getCurrentDay(requireContext(), planDays)
        val progressPercent = PlanProgressManager.getProgressPercent(requireContext())

        txtRemainingDays.text = "${30 - completedDay} ngày còn lại"
        txtProgressPercent.text = "$progressPercent%"

        for (planDay in planDays) {
            val dayCard = createDayCard(
                planDay = planDay,
                completedDay = completedDay,
                currentDay = currentDay
            )

            layoutDayList.addView(dayCard)
        }
    }

    // Chức năng: quyết định mỗi ngày sẽ dùng giao diện nào:
    // ngày nghỉ, ngày đã xong, ngày hiện tại, hoặc ngày xem trước.
    private fun createDayCard(
        planDay: PlanDay,
        completedDay: Int,
        currentDay: Int
    ): View {
        return when {
            planDay.isRestDay -> createRestDayCard(planDay)
            planDay.dayNumber <= completedDay -> createCompletedDayCard(planDay, completedDay, currentDay)
            planDay.dayNumber == currentDay -> createActiveDayCard(planDay, completedDay, currentDay)
            else -> createPreviewDayCard(planDay, completedDay, currentDay)
        }
    }

    // Chức năng: tạo card cho ngày đã hoàn thành.
    // Card này có dấu tick và vẫn cho bấm vào xem lại danh sách bài.
    private fun createCompletedDayCard(
        planDay: PlanDay,
        completedDay: Int,
        currentDay: Int
    ): View {
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

        card.setOnClickListener {
            openPlanDayDetail(planDay, completedDay, currentDay)
        }

        return card
    }

    // Chức năng: tạo card cho ngày hiện tại.
    // Đây là ngày đang được mở để người dùng bấm Bắt đầu.
    private fun createActiveDayCard(
        planDay: PlanDay,
        completedDay: Int,
        currentDay: Int
    ): View {
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
            openPlanDayDetail(planDay, completedDay, currentDay)
        }

        btnStart.setOnClickListener {
            openPlanDayDetail(planDay, completedDay, currentDay)
        }

        return card
    }

    // Chức năng: tạo card cho ngày chưa mở.
    // Người dùng vẫn được bấm vào xem trước danh sách bài, nhưng chưa được bắt đầu tập.
    private fun createPreviewDayCard(
        planDay: PlanDay,
        completedDay: Int,
        currentDay: Int
    ): View {
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

        card.setOnClickListener {
            openPlanDayDetail(planDay, completedDay, currentDay)
        }

        return card
    }

    // Chức năng: tạo card ngày nghỉ.
    // Ngày nghỉ không mở màn tập, chỉ báo đây là ngày nghỉ.
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

        textContainer.addView(txtTitle)
        textContainer.addView(txtSub)

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

    // Chức năng: tạo khung nền chung cho tất cả card ngày.
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

    // Chức năng: tạo chữ tiêu đề lớn, ví dụ "Ngày 1", "Ngày 2".
    private fun createTitleText(text: String, color: String): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            textSize = 30f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.parseColor(color))
            includeFontPadding = false
        }
    }

    // Chức năng: tạo dòng mô tả nhỏ, ví dụ "6 phút, 7 bài tập".
    private fun createSubText(text: String, color: String): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            textSize = 17f
            setTextColor(Color.parseColor(color))
            setPadding(0, dp(5), 0, 0)
            includeFontPadding = false
        }
    }

    // Chức năng: mở màn chi tiết ngày tập và gửi theo trạng thái ngày đó.
    // Nếu là ngày hiện tại hoặc ngày đã xong thì cho bấm Bắt đầu/Tập lại.
    // Nếu là ngày tương lai thì chỉ cho xem trước danh sách bài.
    private fun openPlanDayDetail(
        planDay: PlanDay,
        completedDay: Int,
        currentDay: Int
    ) {
        val isCompletedDay = !planDay.isRestDay && planDay.dayNumber <= completedDay
        val canStartWorkout = !planDay.isRestDay && planDay.dayNumber <= currentDay

        val intent = Intent(requireContext(), PlanDayDetailActivity::class.java)
        intent.putExtra("DAY_NUMBER", planDay.dayNumber)
        intent.putExtra("DAY_TITLE", planDay.title)
        intent.putExtra("DURATION_MINUTES", planDay.durationMinutes)
        intent.putExtra("EXERCISE_COUNT", planDay.exerciseCount)
        intent.putExtra("EXERCISE_TYPE", planDay.exerciseType)
        intent.putExtra("IS_COMPLETED_DAY", isCompletedDay)
        intent.putExtra("CAN_START_WORKOUT", canStartWorkout)
        intent.putStringArrayListExtra("EXERCISE_IDS", ArrayList(planDay.exerciseIds))
        startActivity(intent)
    }

    // Chức năng: đổi đơn vị dp sang pixel để tạo giao diện bằng Kotlin.
    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}