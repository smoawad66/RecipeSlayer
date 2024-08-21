package com.example.recipeslayer.ui.recipe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeslayer.models.Favourite
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.repo.Repo
import com.example.recipeslayer.utils.Cache
import com.example.recipeslayer.utils.Converters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeViewModel: ViewModel() {

    private val _recipes = MutableLiveData<List<Recipe>>(emptyList())
    val recipes: LiveData<List<Recipe>> = _recipes
    private val repo = Repo()


    ///////////////////////////////////////////////////////////////////
    private val _categories = MutableLiveData<List<String>>(emptyList())

    fun setCategories(categories: List<String>) {
        _categories.value = categories
    }


    fun getCat() = viewModelScope.launch(IO) {

        _recipes.postValue(emptyList())

        val selectedCategories = _categories.value ?: emptyList()

        for (category in selectedCategories) {
            val fetched = repo.getRecipesOnline(category) ?: continue
            val current = _recipes.value ?: emptyList()
            _recipes.postValue(current + fetched)
        }

        // Caching
        Cache.recipesCache = _recipes.value
    }
    ///////////////////////////////////////////////////////////////////




    fun getAllRecipes() = viewModelScope.launch(IO) {
        // Check if data exists in cache
        Cache.recipesCache?.let {
            _recipes.postValue(it)
            return@launch
        }

        val categories = listOf("Vegetarian", "Beef", "Chicken", "Dessert", "Lamb", "Miscellaneous", "Pasta", "Seafood", "Side", "Starter", "Vegan", "Breakfast", "Goat")

        for(category in categories) {
            val fetched = repo.getRecipesOnline(category) ?: continue
            val current = _recipes.value as List<Recipe>
            _recipes.postValue(current.plus(fetched))
        }

        // Caching
        Cache.recipesCache = _recipes.value
    }


    suspend fun insertRecipe(recipe: Recipe) {
        repo.insertRecipe(recipe)
    }

    suspend fun getRecipeOnline(recipeId: Long): Recipe? {
        return repo.getRecipeOnline(recipeId)
    }

    suspend fun getRecipeOffline(recipeId: Long): Recipe? {
        return repo.getRecipeOffline(recipeId)
    }

    fun searchByName(name: String) = viewModelScope.launch(IO) {
        if (name.isEmpty()) {
            _recipes.postValue(listOf())
            return@launch
        }
        val result = repo.searchRecipesOnline(name)
        _recipes.postValue(result ?: listOf())
    }
}