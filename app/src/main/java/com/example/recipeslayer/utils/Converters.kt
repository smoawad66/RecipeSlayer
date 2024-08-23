package com.example.recipeslayer.utils

import androidx.room.TypeConverter
import com.example.recipeslayer.models.Recipe
import com.google.gson.Gson
import com.google.gson.GsonBuilder

object Converters {

    private val gson: Gson = GsonBuilder().serializeNulls().create()

    @TypeConverter
    fun fromRecipeToString(recipe: Recipe): String {
        return gson.toJson(recipe)
    }

    @TypeConverter
    fun fromStringToRecipe(str: String): Recipe {
        return gson.fromJson(str, Recipe::class.java)
    }
}