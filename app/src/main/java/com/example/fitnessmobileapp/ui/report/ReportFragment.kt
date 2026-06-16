package com.example.fitnessmobileapp.ui.report

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.example.fitnessmobileapp.R

class ReportFragment : Fragment(R.layout.fragment_report) {

    private lateinit var scrollReport: NestedScrollView
    private lateinit var tabCalendar: LinearLayout
    private lateinit var tabWeight: LinearLayout
    private lateinit var sectionCalendar: LinearLayout
    private lateinit var sectionWeight: LinearLayout
    private lateinit var lineCalendar: View
    private lateinit var lineWeight: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scrollReport = view.findViewById(R.id.scrollReport)
        tabCalendar = view.findViewById(R.id.tabCalendar)
        tabWeight = view.findViewById(R.id.tabWeight)
        sectionCalendar = view.findViewById(R.id.sectionCalendar)
        sectionWeight = view.findViewById(R.id.sectionWeight)
        lineCalendar = view.findViewById(R.id.lineCalendar)
        lineWeight = view.findViewById(R.id.lineWeight)

        setActiveTab(true)

        tabCalendar.setOnClickListener {
            scrollReport.post {
                scrollReport.smoothScrollTo(0, 0)
            }
            setActiveTab(true)
        }

        tabWeight.setOnClickListener {
            scrollReport.post {
                scrollReport.smoothScrollTo(0, sectionWeight.top)
            }
            setActiveTab(false)
        }
    }

    private fun setActiveTab(isCalendarSelected: Boolean) {
        if (isCalendarSelected) {
            lineCalendar.visibility = View.VISIBLE
            lineWeight.visibility = View.INVISIBLE
        } else {
            lineCalendar.visibility = View.INVISIBLE
            lineWeight.visibility = View.VISIBLE
        }
    }
}