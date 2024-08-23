package com.example.recipeslayer.ui.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipeslayer.models.Favourite
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.repo.Repo

class FavouriteViewModel : ViewModel() {

    var preCount: Int = 0
    var recipes: LiveData<List<Recipe>> = MutableLiveData()
    private val repo = Repo()

    fun getFavouriteRecipes(userId: Long) {
        preCount = recipes.value?.size ?: 0
        recipes = repo.getFavouriteRecipes(userId)
    }

    suspend fun isFavourite(userId: Long, recipeId: Long): Boolean {
        return repo.isFavourite(userId, recipeId)
    }

    suspend fun insertFavourite(favourite: Favourite) {
        repo.insertFavourite(favourite)
    }

    suspend fun deleteFavourite(favourite: Favourite) {
        repo.deleteFavourite(favourite)
    }

}