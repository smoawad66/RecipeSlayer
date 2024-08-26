package com.example.recipeslayer.ui.recipe.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeslayer.R
import com.example.recipeslayer.utils.Constants.CATEGORIES
import com.example.recipeslayer.utils.Constants.CATEGORIES_AR
import com.example.recipeslayer.utils.Config

class FilterAdapter(private var data: List<String> = listOf("All").plus(CATEGORIES)) :
    RecyclerView.Adapter<FilterAdapter.ViewHolder>() {

    var currentPosition = 0
    var previousPosition = 0


    init {
        if (Config.isArabic()) {
            data = listOf("الكل").plus(CATEGORIES_AR)
        }
    }
    fun setData(data: List<String>) {
        this.data = data
    }

    fun getData() = data

    fun setSelectedPosition(position: Int) {
        currentPosition = position
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class ViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        val tv: TextView = itemView.findViewById(R.id.tv_category)

        init {
            itemView.setOnClickListener { listener?.onItemClick(adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.item_view_filter, parent, false)
        return ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tv.text = data[position]

        if (position == currentPosition) {
            holder.tv.setBackgroundResource(R.color.button_selected_background)
            holder.tv.setTextColor(holder.itemView.context.getColor(R.color.button_selected_text))
        } else {
            holder.tv.setBackgroundResource(R.color.button_unselected_background)
            holder.tv.setTextColor(holder.itemView.context.getColor(R.color.button_unselected_text))
        }

        holder.itemView.setOnClickListener {
            previousPosition = currentPosition
            currentPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(currentPosition)
            listener?.onItemClick(currentPosition)
        }
    }

    override fun getItemCount(): Int = data.size
}