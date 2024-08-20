package com.example.recipeslayer.ui.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipeslayer.models.Favourite
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.repo.Repo

class FavouriteViewModel : ViewModel() {

    var favourites: LiveData<List<Favourite>?> = MutableLiveData()
    private val _recipes = MutableLiveData<List<Recipe>>(emptyList())

    private val repo = Repo()


    suspend fun insertFavourite(favourite: Favourite): Long {
        return repo.insertFavourite(favourite)
    }

    suspend fun deleteFavourite(favourite: Favourite) {
        repo.deleteFavourite(favourite)
    }

    suspend fun getFavourite(userId: Long, recipe: Recipe): Favourite? {
        return repo.getFavourite(userId, recipe)
    }

    suspend fun getFavouriteId(userId: Long, recipe: Recipe): Long? {
        return repo.getFavouriteId(userId, recipe)
    }

    fun getFavourites(userId: Long) {
        favourites = repo.getFavourites(userId)
    }

}