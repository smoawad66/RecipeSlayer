package com.example.recipeslayer.ui.recipe.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.recipeslayer.R
import com.example.recipeslayer.models.Favourite
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.ui.recipe.OnItemClickListener

class FavouriteAdapter(private var data: List<Favourite>) :
    RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {

    fun setData(data: List<Favourite>) {
        this.data = data
    }

    fun getData() = data


    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class ViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        val tv: TextView = itemView.findViewById(R.id.tv)
        val iv: ImageView = itemView.findViewById(R.id.iv)

        init {
            itemView.setOnClickListener { listener?.onItemClick(adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.item_view_recipe, parent, false)
        return ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val recipe = data[position].recipe
        holder.tv.text = recipe.strMeal
        Glide.with(holder.itemView)
            .load(recipe.strMealThumb)
            .error(R.drawable.baseline_error_24)
            .into(holder.iv)
    }

    override fun getItemCount(): Int = data.size
}