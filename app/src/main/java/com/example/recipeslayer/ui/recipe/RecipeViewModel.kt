package com.example.recipeslayer.ui.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.repo.Repo
import com.example.recipeslayer.utils.Cache
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeViewModel: ViewModel() {


    fun searchByName(name: String): LiveData<List<Recipe>> = liveData {
        val recipes = repo.searchByName(name)
        if (recipes != null) {
            emit(recipes)
        }
        else emit(emptyList())
    }

    private val _recipes = MutableLiveData<List<Recipe>>(emptyList())
    val recipes: LiveData<List<Recipe>> = _recipes
    private val repo = Repo()

    private val _categories = MutableLiveData<List<String>>(emptyList())

    fun setCategories(categories: List<String>) {
        _categories.value = categories
    }

    fun getCat() = viewModelScope.launch {

        _recipes.postValue(emptyList())

        val selectedCategories = _categories.value ?: emptyList()

        for (category in selectedCategories) {
            val fetched = repo.getRecipes(category) ?: continue
            val current = _recipes.value ?: emptyList()
            _recipes.postValue(current + fetched)
        }

        // Caching
        Cache.recipesCache = _recipes.value
    }

    fun getAllRecipes() = viewModelScope.launch {

        // Check if data exists in cache
        Cache.recipesCache?.let {
            _recipes.postValue(it)
            return@launch
        }

        val categories = listOf("Vegetarian", "Beef", "Chicken", "Dessert", "Lamb", "Miscellaneous", "Pasta", "Seafood", "Side", "Starter", "Vegan", "Breakfast", "Goat")

        for(category in categories) {
            val fetched = withContext(IO) { repo.getRecipes(category) }
            if (fetched == null) continue
            val current = _recipes.value as List<Recipe>
            _recipes.postValue(current.plus(fetched))
        }

        // Caching
        Cache.recipesCache = _recipes.value
    }


    suspend fun getRecipeDetails(recipeId: String): Recipe? {
        return repo.getRecipeById(recipeId).meals?.first()
    }

}