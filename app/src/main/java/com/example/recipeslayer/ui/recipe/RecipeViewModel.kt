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

        Log.i("cat", category)

        viewModelScope.launch(IO) {
            val categories = when (category) {
                "All" -> CATEGORIES.shuffled()
                else -> listOf(category)
            }
            _recipes.postValue(listOf())
            for (cat in categories) {
                val fetched = repo.getRecipesOnline(cat) ?: listOf()
                val current = _recipes.value ?: listOf()
                _recipes.postValue(current.plus(fetched))
            }

            withContext(Main) {
                RECIPES_CACHE[category] = _recipes.value ?: listOf()
            }
        }
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
        val result = repo.searchRecipes(name)
        _recipes.postValue(result ?: listOf())
    }
}