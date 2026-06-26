package com.example.fitnessmobileapp.ui.plan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessmobileapp.R
import com.example.fitnessmobileapp.data.model.WorkoutPlanCategory
import com.example.fitnessmobileapp.data.repository.PlanProgressManager

class PlanHeaderAdapter(
    private val planList: List<WorkoutPlanCategory>,
    private val onPlanClick: (Int) -> Unit
) : RecyclerView.Adapter<PlanHeaderAdapter.PlanHeaderViewHolder>() {

    inner class PlanHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtHeaderTitle: TextView = itemView.findViewById(R.id.txtHeaderTitle)
        val txtHeaderSubtitle: TextView = itemView.findViewById(R.id.txtHeaderSubtitle)
        val txtHeaderRemaining: TextView = itemView.findViewById(R.id.txtHeaderRemaining)
        val txtHeaderPercent: TextView = itemView.findViewById(R.id.txtHeaderPercent)
        val viewHeaderProgress: View = itemView.findViewById(R.id.viewHeaderProgress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanHeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plan_header, parent, false)

        // Chức năng: tạo khoảng cách giữa các card nhưng vẫn giữ tổng vùng item bằng chiều rộng màn hình.
        // totalItemWidth là vùng canh giữa của mỗi item.
        // cardWidth nhỏ hơn một chút để tạo khoảng hở trái/phải giữa các card.
        val totalItemWidth = parent.resources.displayMetrics.widthPixels - dp(parent, 36)
        val cardSpacing = dp(parent, 12)
        val cardWidth = totalItemWidth - cardSpacing

        view.layoutParams = RecyclerView.LayoutParams(
            cardWidth,
            RecyclerView.LayoutParams.MATCH_PARENT
        ).apply {
            marginStart = cardSpacing / 2
            marginEnd = cardSpacing / 2
        }

        return PlanHeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlanHeaderViewHolder, position: Int) {
        val plan = planList[position]
        val context = holder.itemView.context

        holder.txtHeaderTitle.text = plan.title
        holder.txtHeaderSubtitle.text = plan.subtitle

        val completedDay = PlanProgressManager.getCompletedDay(
            context = context,
            planId = plan.id
        )

        val progressPercent = PlanProgressManager.getProgressPercent(
            context = context,
            planId = plan.id
        )

        holder.txtHeaderRemaining.text = "${30 - completedDay} ngày còn lại"
        holder.txtHeaderPercent.text = "$progressPercent%"

        holder.viewHeaderProgress.post {
            val parentView = holder.viewHeaderProgress.parent as View
            val parentWidth = parentView.width

            val newWidth = if (progressPercent <= 0) {
                0
            } else {
                maxOf(dp(holder.itemView, 16), parentWidth * progressPercent / 100)
            }

            holder.viewHeaderProgress.layoutParams =
                holder.viewHeaderProgress.layoutParams.apply {
                    width = newWidth
                }
        }

        holder.itemView.setOnClickListener {
            onPlanClick(position)
        }
    }

    override fun getItemCount(): Int {
        return planList.size
    }

    // Chức năng: đổi đơn vị dp sang pixel trong Adapter.
    private fun dp(view: View, value: Int): Int {
        return (value * view.resources.displayMetrics.density).toInt()
    }

    // Chức năng: đổi đơn vị dp sang pixel khi đang có ViewGroup.
    private fun dp(parent: ViewGroup, value: Int): Int {
        return (value * parent.resources.displayMetrics.density).toInt()
    }
}