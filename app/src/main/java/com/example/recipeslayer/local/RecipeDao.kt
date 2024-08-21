package com.example.recipeslayer.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipeslayer.models.Recipe

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes WHERE idMeal =:recipeId")
    suspend fun getRecipe(recipeId: Long): Recipe?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)
}