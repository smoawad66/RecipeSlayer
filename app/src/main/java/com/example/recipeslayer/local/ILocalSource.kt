package com.example.recipeslayer.local

import androidx.lifecycle.LiveData
import com.example.recipeslayer.models.Favourite
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.models.User

interface ILocalSource {

    suspend fun getUser(email: String): User
    suspend fun insertUser(user: User): Long

    suspend fun getRecipe(recipeId: String): Recipe?
    suspend fun insertRecipe(recipe: Recipe)
    suspend fun deleteRecipe(recipe: Recipe)

    fun getFavourites(userId: Long): LiveData<List<Recipe>?>
    suspend fun getFavourite(userId: Long, recipe: Recipe): Favourite?
    suspend fun insertFavourite(favourite: Favourite): Long
    suspend fun deleteFavourite(favourite: Favourite)
    suspend fun getFavouriteId(userId: Long, recipe: Recipe): Long?
}