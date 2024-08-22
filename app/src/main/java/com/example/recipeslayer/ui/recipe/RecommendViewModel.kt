package com.example.recipeslayer.ui.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.repo.Repo

class RecommendViewModel : ViewModel() {

    var recipes: LiveData<List<Recipe>> = MutableLiveData()
    private val repo = Repo()

    fun recommendRecipes() {
        recipes = repo.getRecommendedRecipes()
    }
}