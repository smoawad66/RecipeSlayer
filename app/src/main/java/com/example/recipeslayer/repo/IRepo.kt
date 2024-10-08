package com.example.recipeslayer.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.recipeslayer.models.Favourite
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.models.User

interface IRepo {
    suspend fun getUser(email: String) : User?
    suspend fun getUser(id: Long) : User
    suspend fun insertUser(user: User): Long
    suspend fun updateUser(user: User)


    suspend fun getRecipeAr(recipeId: Long): Recipe?
    suspend fun insertRecipe(recipe: Recipe)
    suspend fun deleteRecipe(recipe: Recipe)


    suspend fun getRecipesAr(): List<Recipe>

    fun getRecommendedRecipes(): LiveData<List<Recipe>>


    suspend fun searchRecipesAr(query: String): List<Recipe>

    // Favourites
    fun getFavouriteRecipes(userId: Long): LiveData<List<Recipe>>


    suspend fun insertFavourite(favourite: Favourite)
    suspend fun deleteFavourite(favourite: Favourite)
    suspend fun isFavourite(userId: Long, recipeId: Long): Boolean


    suspend fun getRecipesOnline(category: String): List<Recipe>?
    suspend fun getRecipeOnline(recipeId: Long): Recipe?
    suspend fun searchRecipes(name: String): List<Recipe>?
}