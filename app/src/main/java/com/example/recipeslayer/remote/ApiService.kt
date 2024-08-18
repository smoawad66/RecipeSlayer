package com.example.recipeslayer.remote

import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.models.RecipeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("filter.php")
    suspend fun getRecipes(@Query("c") category: String): RecipeResponse


    @GET("lookup.php")
    suspend fun getRecipeById(@Query("i") recipeId: String): Recipe?


    @GET("search.php")
    suspend fun searchByName(@Query("s") name: String): RecipeResponse
}
