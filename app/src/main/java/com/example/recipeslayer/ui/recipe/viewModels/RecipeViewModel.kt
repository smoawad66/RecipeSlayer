package com.example.recipeslayer.ui.recipe.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.repo.Repo
import com.example.recipeslayer.utils.Cache.RECIPES_CACHE
import com.example.recipeslayer.utils.Cache.isFound
import com.example.recipeslayer.utils.Config.isArabic
import com.example.recipeslayer.utils.Constants.CATEGORIES
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.wait

class RecipeViewModel : ViewModel() {

    private var _recipes = MutableLiveData<List<Recipe>>(listOf())
    var recipes: LiveData<List<Recipe>> = _recipes

    private val repo = Repo()

    // AR
    private suspend fun getAllRecipesAr() {
        if (isFound("الكل")) {
            return _recipes.postValue(RECIPES_CACHE["الكل"])
        }

        val allRecipes = repo.getRecipesAr().shuffled()
        _recipes.postValue(allRecipes)

        RECIPES_CACHE["الكل"] = allRecipes
    }

    private fun filterRecipesAr(category: String) {

        if (isFound(category))
            return _recipes.postValue(RECIPES_CACHE[category])


        viewModelScope.launch(IO) {
            if (!isFound("الكل"))
                getAllRecipesAr()
            if (category == "الكل")
                return@launch _recipes.postValue(RECIPES_CACHE["الكل"])

            val filteredRecipes =
                RECIPES_CACHE["الكل"]?.filter { it.strCategory == category } ?: listOf()

            withContext(Main) {
                _recipes.value = filteredRecipes
                RECIPES_CACHE[category] = filteredRecipes
            }
        }
    }

    // EN
    suspend fun getAllRecipes() {
        if (isArabic())
            return getAllRecipesAr()

        if (!RECIPES_CACHE["All"].isNullOrEmpty()) {
            return _recipes.postValue(RECIPES_CACHE["All"])
        }

        _recipes.value = emptyList()
        val categories = CATEGORIES.shuffled()

        for (cat in categories) {
            val fetched = withContext(IO) { repo.getRecipesOnline(cat) } ?: continue
            val current = _recipes.value!!
            _recipes.value = current + fetched
        }

        RECIPES_CACHE["All"] = _recipes.value ?: listOf()
    }

    fun filterRecipes(category: String) {

        Log.i("test", "from filterRecipes")

        if (isArabic())
            return filterRecipesAr(category)

        if (isFound(category)) {
            _recipes.postValue(RECIPES_CACHE[category])
            return
        }

        viewModelScope.launch(IO) {
            if (category == "All")
                return@launch getAllRecipes()

            val filteredRecipes = repo.getRecipesOnline(category) ?: listOf()

            withContext(Main) {
                _recipes.postValue(filteredRecipes)
                RECIPES_CACHE[category] = filteredRecipes
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