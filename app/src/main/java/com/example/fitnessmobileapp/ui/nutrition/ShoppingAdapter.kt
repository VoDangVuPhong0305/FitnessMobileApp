package com.example.fitnessmobileapp.ui.nutrition

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessmobileapp.R

class ShoppingAdapter(
    private val items: List<ShoppingItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtHeader: TextView = view.findViewById(R.id.txtHeader)
        val imgIcon: ImageView = view.findViewById(R.id.imgIcon)
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cbItem: CheckBox = view.findViewById(R.id.cbItem)
        val txtItemName: TextView = view.findViewById(R.id.txtItemName)
        val txtDescription: TextView = view.findViewById(R.id.txtDescription)
    }

    override fun getItemViewType(position: Int) = if (items[position].isHeader) 0 else 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 0) {
            HeaderViewHolder(inflater.inflate(R.layout.item_header, parent, false))
        } else {
            ItemViewHolder(inflater.inflate(R.layout.item_row, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        if (holder is HeaderViewHolder) {
            holder.txtHeader.text = item.text
            if (item.iconRes != 0) holder.imgIcon.setImageResource(item.iconRes)
        } else if (holder is ItemViewHolder) {
            holder.txtItemName.text = item.text
            holder.txtDescription.text = item.description
            holder.cbItem.isChecked = item.isChecked ?: false
            holder.cbItem.setOnCheckedChangeListener(null)
            holder.cbItem.setOnCheckedChangeListener { _, checked ->
                // lưu trạng thái nếu cần
            }
        }
    }

    override fun getItemCount() = items.size
}