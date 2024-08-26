package com.example.recipeslayer.ui.recipe.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipeslayer.models.Favourite
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.repo.Repo
import com.example.recipeslayer.utils.Config
import com.example.recipeslayer.utils.Config.isArabic

class FavouriteViewModel : ViewModel() {

    var preCount: Int = 0
    var recipes: LiveData<List<Recipe>> = MutableLiveData()
    private val repo = Repo()

    fun getFavouriteRecipes(userId: Long) {
        preCount = recipes.value?.size ?: 0
        recipes = repo.getFavouriteRecipes(userId)
    }

    suspend fun isFavourite(userId: Long, recipeId: Long): Boolean {
        return repo.isFavourite(userId, if (isArabic()) recipeId / 10 else recipeId)
    }

    suspend fun insertFavourite(favourite: Favourite) {
        if (isArabic())
            favourite.recipeId /= 10
        repo.insertFavourite(favourite)
    }

    suspend fun deleteFavourite(favourite: Favourite) {
        if (isArabic())
            favourite.recipeId /= 10
        repo.deleteFavourite(favourite)
    }

}