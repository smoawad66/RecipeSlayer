package com.example.recipeslayer.ui.recipe.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.recipeslayer.R
import com.example.recipeslayer.models.Recipe

class RecipeAdapter(private var data: List<Recipe>) :
    RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    fun setData(data: List<Recipe>) {
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


//        if (Locale.getDefault().language == "ar") {
//            val generativeModel = GenerativeModel(
//                modelName = "gemini-1.5-flash",
//                apiKey = "AIzaSyBJDwCxczi8DQa6LY5ig0SZNOO-dUIyoYM"
//            )
//
//            val prompt = "Translate ${data[position].strMeal} into arabic, answer with the word only."
//            MainScope().launch {
//                val response = generativeModel.generateContent(prompt)
//                holder.tv.text = response.text
//                Log.i("lol", "onCreate: ______${response.text}")
//            }
//
//        } else {
        holder.tv.text = data[position].strMeal
//        }

        Glide.with(holder.itemView)
            .load(data[position].strMealThumb)
            .error(R.drawable.baseline_error_24)
            .into(holder.iv)
    }

    override fun getItemCount(): Int = data.size
}