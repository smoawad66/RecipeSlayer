package com.example.recipeslayer.ui.recipe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.repo.Repo
import com.example.recipeslayer.utils.Cache
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class RecipeViewModel: ViewModel() {

    private val _recipes = MutableLiveData<List<Recipe>>(emptyList())
    val recipes: LiveData<List<Recipe>> = _recipes
    private val repo = Repo()


    fun getAllRecipes() = viewModelScope.launch(IO) {

        // Check if data exists in cache
        Cache.recipesCache?.let {
            _recipes.postValue(it)
//            Log.i("cache", "Cache hit")
            return@launch
        }

        val categories = listOf("Vegetarian", "Beef", "Chicken", "Dessert", "Lamb", "Miscellaneous", "Pasta", "Seafood", "Side", "Starter", "Vegan", "Breakfast", "Goat")

        for(category in categories) {
            val fetched = repo.getRecipes(category) ?: continue
            val current = _recipes.value as List<Recipe>
            _recipes.postValue(current.plus(fetched))
        }

//        Log.i("cache", "Cache miss")

        // Caching
        Cache.recipesCache = _recipes.value
    }
}