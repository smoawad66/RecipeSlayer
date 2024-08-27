package com.example.recipeslayer.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipeslayer.models.Recipe

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes WHERE idMeal = :recipeId")
    suspend fun getRecipe(recipeId: Long): Recipe?

    @Query("SELECT * FROM recipes WHERE idMeal > 99999")
    suspend fun getRecipesAr(): List<Recipe>


    @Query(
        "SELECT r.* FROM recipes r JOIN favourites f ON r.idMeal = f.recipeId " +
                "GROUP BY r.idMeal ORDER BY count(f.userId) DESC, r.idMeal ASC LIMIT 5"
    )
    fun getRecommendedRecipes(): LiveData<List<Recipe>>


    @Query(
        "SELECT r.* FROM recipes r JOIN favourites f ON r.idMeal = f.recipeId * 10 " +
                "GROUP BY r.idMeal ORDER BY count(f.userId) DESC, r.idMeal ASC LIMIT 5"
    )
    fun getRecommendedRecipesAr(): LiveData<List<Recipe>>


    @Query("SELECT * FROM recipes WHERE idMeal > 99999 AND strMeal LIKE :query")
    suspend fun searchRecipesAr(query: String): List<Recipe>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecipe(recipe: Recipe)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)
}