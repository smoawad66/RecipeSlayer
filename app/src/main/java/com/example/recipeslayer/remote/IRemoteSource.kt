package com.example.recipeslayer.remote


import com.example.recipeslayer.models.Recipe


interface IRemoteSource {

    suspend fun getRecipes(category: String): List<Recipe>?
    suspend fun getRecipe(recipeId: Long): Recipe?
    suspend fun searchRecipes(name: String): List<Recipe>?
}