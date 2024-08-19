package com.example.recipeslayer.ui.recipe

import android.util.Log
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
class RecipeViewModel : ViewModel() {


    fun searchByName(name: String): LiveData<List<Recipe>> = liveData {
        val recipes = repo.searchByName(name)
        if (recipes != null) {
            emit(recipes)
        }
    }

    private val _recipes = MutableLiveData<List<Recipe>>(emptyList())
    val recipes: LiveData<List<Recipe>> = _recipes
    private val repo = Repo()

    private val _categories = MutableLiveData<List<String>>(emptyList())

    init {
        // Set default categories to retrieve all recipes initially
        _categories.value = listOf(
            "Vegetarian", "Beef", "Chicken", "Dessert", "Lamb", "Miscellaneous",
            "Pasta", "Seafood", "Side", "Starter", "Vegan", "Breakfast", "Goat"
        )
        getAllRecipes()  // Fetch all recipes initially
    }

    fun setCategories(categories: List<String>) {
        _categories.value = categories
    }

    fun getAllRecipes() = viewModelScope.launch(IO) {

        // Clear previous recipes
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
}
