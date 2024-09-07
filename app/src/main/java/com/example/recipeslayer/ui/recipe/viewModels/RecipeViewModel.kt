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
import retrofit2.HttpException
import retrofit2.http.HTTP

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
                return@launch

            val filteredRecipes = RECIPES_CACHE["الكل"]?.filter { it.strCategory == category } ?: listOf()

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
        if (isFound("All")) {
            return _recipes.postValue(RECIPES_CACHE["All"])
        }
        _recipes.value = emptyList()
        val categories = CATEGORIES.shuffled()
        for (cat in categories) {
            val fetched = withContext(IO) { repo.getRecipesOnline(cat) } ?: continue
            if (cat == "Vegetarian") {
                fetched.find { it.idMeal == 53027L }?.modifyKoshari()
            }

            val current = _recipes.value!!
            _recipes.value = current + fetched
        }
        RECIPES_CACHE["All"] = _recipes.value ?: listOf()
    }

    fun filterRecipes(category: String) {

        if (isArabic())
            return filterRecipesAr(category)

        if (isFound(category)) {
            _recipes.postValue(RECIPES_CACHE[category])
            return
        }

        viewModelScope.launch(IO) {

            val filteredRecipes = repo.getRecipesOnline(category) ?: listOf()

            if (category == "Vegetarian") {
                filteredRecipes.find { it.idMeal == 53027L }?.modifyKoshari()
            }

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
        try {
            val result = if (isArabic()) repo.searchRecipesAr(name) else repo.searchRecipes(name)
            if (result != null) {
                result.find { it.idMeal == 53027L }?.modifyKoshari()
            }
            _recipes.postValue(result ?: listOf())
        } catch (_: HttpException) {  }
    }

    private fun Recipe.modifyKoshari() {
        this.apply {
            strYoutube = "https://www.youtube.com/watch?v=RX6k_VjkM1M"
            strMealThumb =
                "https://drive.google.com/uc?export=view&id=1EsOLpXx7Q2F1cUIYP0ENRaH5F_i718He"
            strIngredient9 = "Tomatoes"
            strMeasure9 = "1/2 kilos"
        }
    }
}