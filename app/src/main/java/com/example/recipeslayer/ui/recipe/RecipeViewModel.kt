package com.example.recipeslayer.ui.recipe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.repo.Repo
import com.example.recipeslayer.utils.Cache.RECIPES_CACHE
import com.example.recipeslayer.utils.Constants.CATEGORIES
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeViewModel : ViewModel() {

    private val _recipes = MutableLiveData<List<Recipe>>(listOf())
    val recipes: LiveData<List<Recipe>> = _recipes
    private val repo = Repo()

    fun getRecipes(category: String = "All") {

        if (!RECIPES_CACHE[category].isNullOrEmpty()) {
            _recipes.postValue(RECIPES_CACHE[category])
            return
        }

        viewModelScope.launch(IO) {
            val categories = when (category) {
                "All" -> CATEGORIES.shuffled()
                else -> listOf(category)
            }
            _recipes.postValue(listOf())
            for (cat in categories) {
                val fetched = repo.getRecipes(cat) ?: listOf()
                val current = _recipes.value ?: listOf()
                _recipes.postValue(current.plus(fetched))
            }

            withContext(Main) {
                RECIPES_CACHE[category] = _recipes.value ?: listOf()
            }
        }
    }

    suspend fun getRecipeDetails(recipeId: String): Recipe? {
        return repo.getRecipeById(recipeId).meals?.first()
    }

    fun searchByName(name: String) {
        if (name.isEmpty()) {
            _recipes.value = listOf()
        }
        viewModelScope.launch(IO) {
            val result = repo.searchByName(name)
            _recipes.postValue(result ?: listOf())
        }
    }
}