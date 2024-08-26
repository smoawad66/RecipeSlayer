package com.example.recipeslayer.ui.recipe.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.repo.Repo
import com.example.recipeslayer.utils.Cache.RECIPES_CACHE
import com.example.recipeslayer.utils.Cache.RECIPES_CACHE_AR
import com.example.recipeslayer.utils.Config
import com.example.recipeslayer.utils.Config.isArabic
import com.example.recipeslayer.utils.Constants.CATEGORIES
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeViewModel : ViewModel() {

    private var _recipes = MutableLiveData<List<Recipe>>(listOf())
    var recipes: LiveData<List<Recipe>> = _recipes

    private val repo = Repo()


    // AR
    private fun getRecipesAr(category: String) {

        if (!RECIPES_CACHE_AR[category].isNullOrEmpty()) {
            _recipes.postValue(RECIPES_CACHE_AR[category])
            return
        }

        viewModelScope.launch(IO) {
            _recipes.postValue(repo.getRecipesAr(category).shuffled())
            withContext(Main) {
                RECIPES_CACHE_AR[category] = _recipes.value ?: listOf()
            }
        }
    }


    // EN
    fun getRecipes(category: String) {

        if (isArabic()) {
            getRecipesAr(category)
            return
        }

        if (!RECIPES_CACHE[category].isNullOrEmpty()) {
            _recipes.postValue(RECIPES_CACHE[category])
            return
        }

        viewModelScope.launch(IO) {
            val categories = when (category) {
                "All" -> CATEGORIES
                else -> listOf(category)
            }
            _recipes.postValue(listOf())
            val all = mutableListOf<Recipe>()
            for (cat in categories) {
                val fetched = repo.getRecipesOnline(cat) ?: listOf()
                all.addAll(fetched)
            }
            _recipes.postValue(all.shuffled())

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
        return repo.getRecipeAr(recipeId)
    }

    fun searchByName(name: String) = viewModelScope.launch(IO) {
        if (name.isEmpty()) {
            _recipes.postValue(listOf())
            return@launch
        }
        val result = if (isArabic()) repo.searchRecipesAr(name) else repo.searchRecipes(name)
        _recipes.postValue(result ?: listOf())
    }
}
