package com.example.recipeslayer.local

import androidx.lifecycle.LiveData
import com.example.recipeslayer.models.Favourite
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.models.User

interface ILocalSource {

    suspend fun getUser(email: String): User?
    suspend fun getUser(id: Long): User
    suspend fun insertUser(user: User): Long
    suspend fun updateUser(user: User)

    suspend fun getRecipeAr(recipeId: Long): Recipe?
    suspend fun insertRecipe(recipe: Recipe)
    suspend fun deleteRecipe(recipe: Recipe)


    // AR
    suspend fun getRecipesAr(): List<Recipe>

    suspend fun searchRecipesAr(query: String): List<Recipe>

    // Recommended
    fun getRecommendedRecipes(): LiveData<List<Recipe>>


    // Favourites
    fun getFavouriteRecipes(userId: Long): LiveData<List<Recipe>>
    suspend fun insertFavourite(favourite: Favourite)
    suspend fun deleteFavourite(favourite: Favourite)
    suspend fun isFavourite(userId: Long, recipeId: Long): Boolean
}