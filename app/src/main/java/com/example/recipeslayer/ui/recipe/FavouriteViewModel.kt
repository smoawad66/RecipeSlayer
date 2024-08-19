package com.example.recipeslayer.ui.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeslayer.models.Favourite
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.repo.Repo
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavouriteViewModel: ViewModel() {

    private val _favouriteRecipes = MutableLiveData<List<Recipe>>(emptyList())
    val favouriteRecipes: LiveData<List<Recipe>> = _favouriteRecipes
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

    fun getFavourites(userId: Long) = viewModelScope.launch {
        val fetched = withContext(IO) { repo.getFavourites(userId) }
        _favouriteRecipes.postValue(fetched.value)
    }

}