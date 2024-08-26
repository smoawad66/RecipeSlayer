package com.example.recipeslayer.local

import android.content.Context
import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.recipeslayer.models.Recipe
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class RecipeDatabaseCallback(private val context: Context) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        RecipeRoomDatabase.getInstance().let { database ->
            val recipeDao = database.getRecipeDao()
            populateDatabaseFromJson(context, recipeDao)
        }
    }

    private fun populateDatabaseFromJson(context: Context, recipeDao: RecipeDao) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val inputStream = context.assets.open("translated_meals.json")
                val reader = BufferedReader(InputStreamReader(inputStream))
                val json = reader.use { it.readText() }

                val gson = Gson()
                val recipeListType = object : TypeToken<List<Recipe>>() {}.type
                val recipes: List<Recipe> = gson.fromJson(json, recipeListType)

                for (recipe in recipes) {
                    recipeDao.insertRecipe(recipe)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.i("test", "populateDatabaseFromJson: error ----------- $e")
            }
        }
    }
}