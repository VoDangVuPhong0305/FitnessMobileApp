package com.example.fitnessmobileapp.ui.nutrition

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessmobileapp.R

class ShoppingAdapter(
    items: List<ShoppingItem>,
    private val listKey: String
) : RecyclerView.Adapter<ShoppingAdapter.GroupViewHolder>() {

    private val groups: List<ShoppingGroup> = buildGroups(items)

    data class ShoppingGroup(
        val title: String,
        val iconRes: Int,
        val items: List<ShoppingItem>
    )

    inner class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtGroupTitle: TextView = view.findViewById(R.id.txtGroupTitle)
        val imgGroupIcon: ImageView = view.findViewById(R.id.imgGroupIcon)
        val layoutItems: LinearLayout = view.findViewById(R.id.layoutItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shopping_group, parent, false)

        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groups[position]
        val context = holder.itemView.context

        holder.txtGroupTitle.text = group.title
        holder.txtGroupTitle.typeface = ResourcesCompat.getFont(context, R.font.anton_regular)
        holder.txtGroupTitle.textSize = 20f
        holder.txtGroupTitle.setTextColor(Color.parseColor("#333333"))
        holder.txtGroupTitle.includeFontPadding = false

        if (group.iconRes != 0) {
            holder.imgGroupIcon.visibility = View.VISIBLE
            holder.imgGroupIcon.setImageResource(group.iconRes)
        } else {
            holder.imgGroupIcon.visibility = View.GONE
        }

        holder.layoutItems.removeAllViews()

        group.items.forEach { item ->
            holder.layoutItems.addView(
                createShoppingRow(
                    parent = holder.layoutItems,
                    groupTitle = group.title,
                    item = item
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    private fun buildGroups(items: List<ShoppingItem>): List<ShoppingGroup> {
        val result = mutableListOf<ShoppingGroup>()

        var currentTitle = ""
        var currentIcon = 0
        var currentItems = mutableListOf<ShoppingItem>()

        items.forEach { item ->
            if (item.isHeader) {
                if (currentTitle.isNotEmpty()) {
                    result.add(
                        ShoppingGroup(
                            title = currentTitle,
                            iconRes = currentIcon,
                            items = currentItems
                        )
                    )
                }

                currentTitle = item.text
                currentIcon = item.iconRes
                currentItems = mutableListOf()
            } else {
                currentItems.add(item)
            }
        }

        if (currentTitle.isNotEmpty()) {
            result.add(
                ShoppingGroup(
                    title = currentTitle,
                    iconRes = currentIcon,
                    items = currentItems
                )
            )
        }

        return result
    }

    private fun createShoppingRow(
        parent: LinearLayout,
        groupTitle: String,
        item: ShoppingItem
    ): View {
        val context = parent.context
        val prefs = context.getSharedPreferences("shopping_checked_state", Context.MODE_PRIVATE)

        val checkedKey = buildCheckedKey(groupTitle, item)

        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.TOP
            setPadding(0, dp(10), 0, dp(8))

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val checkBox = CheckBox(context).apply {
            buttonTintList = ColorStateList.valueOf(Color.parseColor("#C8C8C8"))

            setOnCheckedChangeListener(null)
            isChecked = prefs.getBoolean(checkedKey, false)

            minWidth = dp(40)
            minHeight = dp(40)

            layoutParams = LinearLayout.LayoutParams(
                dp(40),
                dp(40)
            )

            setOnCheckedChangeListener { _, isChecked ->
                prefs.edit()
                    .putBoolean(checkedKey, isChecked)
                    .apply()
            }
        }

        val textContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL

            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                marginStart = dp(8)
            }
        }

        val txtName = TextView(context).apply {
            text = item.text
            setTextColor(Color.parseColor("#222222"))
            textSize = 17f
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            includeFontPadding = true
        }

        textContainer.addView(txtName)

        if (item.description.isNotBlank()) {
            val txtDescription = TextView(context).apply {
                text = item.description
                setTextColor(Color.parseColor("#8A8A8A"))
                textSize = 14f
                typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                includeFontPadding = true
                setLineSpacing(dp(3).toFloat(), 1.0f)

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(3)
                }
            }

            textContainer.addView(txtDescription)
        }

        row.addView(checkBox)
        row.addView(textContainer)

        return row
    }

    private fun buildCheckedKey(groupTitle: String, item: ShoppingItem): String {
        val rawKey = "$listKey|$groupTitle|${item.text}|${item.description}"
        return rawKey.hashCode().toString()
    }

    private fun dp(value: Int): Int {
        return (value * android.content.res.Resources.getSystem().displayMetrics.density).toInt()
    }
}