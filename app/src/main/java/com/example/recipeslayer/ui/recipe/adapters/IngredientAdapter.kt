package com.example.recipeslayer.ui.recipe.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.recipeslayer.R
import com.example.recipeslayer.models.Ingredient

class IngredientAdapter(private var data: MutableList<Ingredient>) :
    RecyclerView.Adapter<IngredientAdapter.ViewHolder>() {

    fun setData(data: MutableList<Ingredient>) {
        this.data = data
    }

    fun getData() = data

    inner class ViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvMeasure: TextView = itemView.findViewById(R.id.tv_measure)
        val img: ImageView = itemView.findViewById(R.id.iv_ingredient)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.item_view_ingredient, parent, false)
        return ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = data[position].name
        holder.tvMeasure.text = data[position].measure
        Glide.with(holder.itemView)
            .load(data[position].image)
            .error(R.drawable.baseline_error_24)
            .into(holder.img)
    }

    override fun getItemCount(): Int = data.size
}
