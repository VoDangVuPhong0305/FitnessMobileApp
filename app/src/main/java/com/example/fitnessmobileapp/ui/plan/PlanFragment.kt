package com.example.fitnessmobileapp.ui.plan

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessmobileapp.R
import com.example.fitnessmobileapp.data.model.PlanDay
import com.example.fitnessmobileapp.data.model.WorkoutPlanCategories
import com.example.fitnessmobileapp.data.repository.PlanProgressManager
import com.example.fitnessmobileapp.data.repository.WorkoutPlanProvider

class PlanFragment : Fragment(R.layout.fragment_plan) {

    private lateinit var recyclerPlanHeader: RecyclerView
    private lateinit var layoutPlanDots: LinearLayout
    private lateinit var layoutDayList: LinearLayout

    private lateinit var planHeaderAdapter: PlanHeaderAdapter
    private lateinit var pagerSnapHelper: PagerSnapHelper

    // Chức năng: lưu kế hoạch hiện tại đang được chọn.
    // Mặc định mở app lên chọn Tập cơ bụng.
    private var selectedPlanId: String = WorkoutPlanCategories.ABS_ID

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerPlanHeader = view.findViewById(R.id.recyclerPlanHeader)
        layoutPlanDots = view.findViewById(R.id.layoutPlanDots)
        layoutDayList = view.findViewById(R.id.layoutDayList)

        setupPlanHeaderRecycler()
        scrollToSelectedPlan()
        showPlanDots()
        showPlanDays()
    }

    override fun onResume() {
        super.onResume()

        if (::layoutDayList.isInitialized) {
            planHeaderAdapter.notifyDataSetChanged()
            showPlanDots()
            showPlanDays()
        }
    }

    // Chức năng: thiết lập danh sách card kế hoạch lớn kéo ngang.
    // PagerSnapHelper giúp card tự khựng vào giữa sau khi kéo.
    private fun setupPlanHeaderRecycler() {
        val planList = WorkoutPlanCategories.allPlans

        planHeaderAdapter = PlanHeaderAdapter(planList) { position ->
            recyclerPlanHeader.smoothScrollToPosition(position)
            selectPlanByPosition(position)
        }

        recyclerPlanHeader.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        recyclerPlanHeader.adapter = planHeaderAdapter

        pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(recyclerPlanHeader)

        recyclerPlanHeader.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(
                recyclerView: RecyclerView,
                newState: Int
            ) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = recyclerView.layoutManager ?: return
                    val snapView = pagerSnapHelper.findSnapView(layoutManager) ?: return
                    val position = layoutManager.getPosition(snapView)

                    if (position != RecyclerView.NO_POSITION) {
                        selectPlanByPosition(position)
                    }
                }
            }
        })
    }

    // Chức năng: khi mở màn hình, tự đưa card Cơ bụng vào vị trí đang chọn.
    private fun scrollToSelectedPlan() {
        recyclerPlanHeader.post {
            val selectedIndex = WorkoutPlanCategories.allPlans.indexOfFirst { plan ->
                plan.id == selectedPlanId
            }

            if (selectedIndex >= 0) {
                recyclerPlanHeader.scrollToPosition(selectedIndex)
            }
        }
    }

    // Chức năng: khi một card plan nằm giữa màn hình thì đổi plan đang chọn.
    // Sau đó vẽ lại dấu chấm và danh sách ngày bên dưới.
    private fun selectPlanByPosition(position: Int) {
        val planList = WorkoutPlanCategories.allPlans

        if (position !in planList.indices) return

        val newPlanId = planList[position].id

        if (selectedPlanId != newPlanId) {
            selectedPlanId = newPlanId
            showPlanDots()
            showPlanDays()
        }
    }

    // Chức năng: hiển thị dấu chấm dưới card.
    // Dấu chấm đang chọn sẽ dài hơn và đổi màu theo plan hiện tại.
    private fun showPlanDots() {
        layoutPlanDots.removeAllViews()

        val planList = WorkoutPlanCategories.allPlans

        planList.forEach { plan ->
            val isSelected = plan.id == selectedPlanId

            val dot = View(requireContext()).apply {
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = dp(6).toFloat()
                    setColor(
                        if (isSelected) {
                            Color.parseColor(plan.startColor)
                        } else {
                            Color.parseColor("#CFCFCF")
                        }
                    )
                }

                layoutParams = LinearLayout.LayoutParams(
                    if (isSelected) dp(26) else dp(9),
                    dp(9)
                ).apply {
                    marginEnd = dp(6)
                }
            }

            layoutPlanDots.addView(dot)
        }
    }

    // Chức năng: đọc kế hoạch hiện tại và vẽ lại danh sách 30 ngày.
    // Header phía trên chỉ dùng để chọn plan, danh sách ngày bên dưới sẽ đổi theo selectedPlanId.
    private fun showPlanDays() {
        layoutDayList.removeAllViews()

        val planDays = WorkoutPlanProvider.getPlanDays(
            context = requireContext(),
            planId = selectedPlanId
        )

        val completedDay = PlanProgressManager.getCompletedDay(
            context = requireContext(),
            planId = selectedPlanId
        )

        val currentDay = PlanProgressManager.getCurrentDay(
            context = requireContext(),
            planId = selectedPlanId,
            planDays = planDays
        )

        for (planDay in planDays) {
            val dayCard = createDayCard(
                planDay = planDay,
                completedDay = completedDay,
                currentDay = currentDay
            )

            layoutDayList.addView(dayCard)
        }
    }

    // Chức năng: quyết định mỗi ngày dùng giao diện nào.
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
    private fun createCompletedDayCard(
        planDay: PlanDay,
        completedDay: Int,
        currentDay: Int
    ): View {
        val selectedPlan = WorkoutPlanProvider.getPlanCategory(selectedPlanId)

        val card = createBaseCard()
        card.setBackgroundResource(R.drawable.bg_plan_day_white)

        val textContainer = createTextContainer()

        val txtTitle = createTitleText("Ngày ${planDay.dayNumber}", "#222222")
        val txtSub = createSubText("Đã kết thúc", "#888888")

        textContainer.addView(txtTitle)
        textContainer.addView(txtSub)

        val checkCircle = TextView(requireContext()).apply {
            text = "✓"
            textSize = 34f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.parseColor(selectedPlan.startColor))
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

    // Chức năng: tạo card cho ngày hiện tại đang được phép tập.
    // Màu card ngày hiện tại sẽ đổi theo plan đang chọn.
    private fun createActiveDayCard(
        planDay: PlanDay,
        completedDay: Int,
        currentDay: Int
    ): View {
        val selectedPlan = WorkoutPlanProvider.getPlanCategory(selectedPlanId)

        val card = createBaseCard()
        card.background = createGradientBackground(
            startColor = selectedPlan.startColor,
            endColor = selectedPlan.endColor,
            radiusDp = 18
        )

        val textContainer = createTextContainer()

        val txtTitle = createTitleText("Ngày ${planDay.dayNumber}", "#FFFFFF")
        val txtSub = createSubText("${planDay.exerciseCount} Bài tập", "#FFFFFF")

        textContainer.addView(txtTitle)
        textContainer.addView(txtSub)

        val btnStart = TextView(requireContext()).apply {
            text = "Bắt đầu"
            textSize = 22f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.parseColor(selectedPlan.startColor))
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
    private fun createPreviewDayCard(
        planDay: PlanDay,
        completedDay: Int,
        currentDay: Int
    ): View {
        val card = createBaseCard()
        card.setBackgroundResource(R.drawable.bg_plan_day_white)

        val textContainer = createTextContainer()

        val txtTitle = createTitleText("Ngày ${planDay.dayNumber}", "#222222")
        val txtSub = createSubText("${planDay.exerciseCount} Bài tập", "#888888")

        textContainer.addView(txtTitle)
        textContainer.addView(txtSub)

        card.addView(textContainer)

        card.setOnClickListener {
            openPlanDayDetail(planDay, completedDay, currentDay)
        }

        return card
    }

    // Chức năng: tạo card ngày nghỉ.
    private fun createRestDayCard(planDay: PlanDay): View {
        val selectedPlan = WorkoutPlanProvider.getPlanCategory(selectedPlanId)

        val card = createBaseCard()
        card.setBackgroundResource(R.drawable.bg_plan_day_white)

        val textContainer = createTextContainer()

        val txtTitle = createTitleText("Ngày ${planDay.dayNumber}", "#222222")
        val txtSub = createSubText("Ngày nghỉ", "#888888")

        textContainer.addView(txtTitle)
        textContainer.addView(txtSub)

        val txtIcon = TextView(requireContext()).apply {
            text = "☕"
            textSize = 38f
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor(selectedPlan.startColor))
            layoutParams = LinearLayout.LayoutParams(dp(72), dp(72))
        }

        card.addView(textContainer)
        card.addView(txtIcon)

        card.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Đây là ngày nghỉ, không có bài tập",
                Toast.LENGTH_SHORT
            ).show()
        }

        return card
    }

    // Chức năng: tạo khung chữ bên trái của mỗi card ngày.
    private fun createTextContainer(): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )
        }
    }

    // Chức năng: tạo khung nền chung cho card ngày.
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

    // Chức năng: tạo chữ tiêu đề ngày.
    private fun createTitleText(text: String, color: String): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            textSize = 30f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.parseColor(color))
            includeFontPadding = false
        }
    }

    // Chức năng: tạo dòng mô tả nhỏ.
    private fun createSubText(text: String, color: String): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            textSize = 17f
            setTextColor(Color.parseColor(color))
            setPadding(0, dp(5), 0, 0)
            includeFontPadding = false
        }
    }

    // Chức năng: mở màn chi tiết ngày tập.
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

    // Chức năng: tạo nền chuyển màu cho ngày hiện tại.
    private fun createGradientBackground(
        startColor: String,
        endColor: String,
        radiusDp: Int
    ): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                Color.parseColor(startColor),
                Color.parseColor(endColor)
            )
        ).apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(radiusDp).toFloat()
        }
    }

    // Chức năng: đổi dp sang pixel.
    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}