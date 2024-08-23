package com.example.recipeslayer.ui.recipe

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.recipeslayer.R
import com.example.recipeslayer.models.Ingredient
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.Locale

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
        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.ARABIC)
            .build()

        val englishArabicTranslator = Translation.getClient(options)

        if(Locale.getDefault().language == "ar") {
            englishArabicTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    englishArabicTranslator.translate(data[position].name)
                        .addOnSuccessListener { translatedText ->
                            holder.tvName.text = translatedText
                        }
                        .addOnFailureListener { exception ->
                            Log.i("lol", "onBindViewHolder: $exception.message")
                        }
                }
                .addOnFailureListener { exception ->
                    Log.i("lol", "onBindViewHolder: $exception.message")
                }

            englishArabicTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    englishArabicTranslator.translate(data[position].measure)
                        .addOnSuccessListener { translatedText ->
                            holder.tvMeasure.text = translatedText
                        }
                        .addOnFailureListener { exception ->
                            Log.i("lol", "onBindViewHolder: $exception.message")
                        }
                }
                .addOnFailureListener { exception ->
                    Log.i("lol", "onBindViewHolder: $exception.message")
                }
        } else {
            holder.tvName.text = data[position].name
            holder.tvMeasure.text = data[position].measure
        }
        Glide.with(holder.itemView)
            .load(data[position].image)
            .error(R.drawable.baseline_error_24)
            .into(holder.img)
    }

    override fun getItemCount(): Int = data.size
}
