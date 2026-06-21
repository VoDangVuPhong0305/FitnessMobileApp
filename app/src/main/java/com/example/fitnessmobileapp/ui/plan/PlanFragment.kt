package com.example.fitnessmobileapp.ui.plan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.fitnessmobileapp.R

class PlanFragment : Fragment() {

    private lateinit var cardDay1: LinearLayout
    private lateinit var btnStartDay1: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_plan, container, false)

        cardDay1 = view.findViewById(R.id.cardDay1)
        btnStartDay1 = view.findViewById(R.id.btnStartDay1)

        cardDay1.setOnClickListener {
            openPlanDayDetail(1)
        }

        btnStartDay1.setOnClickListener {
            openPlanDayDetail(1)
        }

        return view
    }

    private fun openPlanDayDetail(dayNumber: Int) {
        val intent = Intent(requireContext(), PlanDayDetailActivity::class.java)
        intent.putExtra("DAY_NUMBER", dayNumber)
        startActivity(intent)
    }
}