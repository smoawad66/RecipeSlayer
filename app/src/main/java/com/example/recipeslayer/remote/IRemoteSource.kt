package com.example.recipeslayer.remote

import androidx.lifecycle.LiveData
import androidx.room.Query
//import com.example.recipeslayer.models.Favourite
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.models.RecipeDetailResponse
import com.example.recipeslayer.models.User
import retrofit2.Response
import retrofit2.http.GET

interface IRemoteSource {

    suspend fun getRecipes(category: String): List<Recipe>?
    suspend fun getRecipeById(recipeId: String): RecipeDetailResponse
    suspend fun searchByName(name: String): List<Recipe>?
}