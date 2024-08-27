package com.example.recipeslayer.ui.recipe.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.repo.Repo
import com.example.recipeslayer.utils.Cache.RECIPES_CACHE
import com.example.recipeslayer.utils.Cache.RECIPES_CACHE_AR
import com.example.recipeslayer.utils.Config.isArabic
import com.example.recipeslayer.utils.Constants.CATEGORIES
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeViewModel : ViewModel() {

    private var _recipes = MutableLiveData<List<Recipe>>(listOf())
    var recipes: LiveData<List<Recipe>> = _recipes

    private val repo = Repo()

    // AR
    private suspend fun getAllRecipesAr() {
        if (!RECIPES_CACHE["الكل"].isNullOrEmpty()) {
            return _recipes.postValue(RECIPES_CACHE["الكل"])
        }

        val allRecipes = repo.getRecipesAr().shuffled()
        _recipes.postValue(allRecipes)

        RECIPES_CACHE_AR["الكل"] = allRecipes
    }

    private fun filterRecipesAr(category: String) {

        if (!RECIPES_CACHE_AR[category].isNullOrEmpty())
            return _recipes.postValue(RECIPES_CACHE_AR[category])

        val filteredRecipes = RECIPES_CACHE_AR["الكل"]?.filter { it.strCategory == category } ?: listOf()

        _recipes.value = filteredRecipes

        RECIPES_CACHE_AR[category] = filteredRecipes
    }

    // EN
    suspend fun getAllRecipes() {
        if (isArabic())
            return getAllRecipesAr()

        if (!RECIPES_CACHE["All"].isNullOrEmpty()) {
            return _recipes.postValue(RECIPES_CACHE["All"])
        }

        val categories = CATEGORIES.shuffled()
        for (cat in categories) {
            val fetched = repo.getRecipesOnline(cat) ?: continue
            val current = _recipes.value ?: listOf()
            _recipes.postValue(current.plus(fetched))
        }

        RECIPES_CACHE["All"] = _recipes.value ?: listOf()
    }

    fun filterRecipes(category: String) {

        if (isArabic())
            return filterRecipesAr(category)

        if (!RECIPES_CACHE[category].isNullOrEmpty()) {
            _recipes.postValue(RECIPES_CACHE[category])
            return
        }

        viewModelScope.launch {
            val filteredRecipes = withContext(IO) { repo.getRecipesOnline(category) ?: listOf() }
            _recipes.postValue(filteredRecipes)
            RECIPES_CACHE[category] = filteredRecipes
        }

//        Log.i("ts", "${filteredRecipes.size}")
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