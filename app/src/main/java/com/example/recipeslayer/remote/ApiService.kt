package com.example.recipeslayer.remote

import com.example.recipeslayer.models.RecipeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("filter.php")
    suspend fun getRecipes(@Query("c") category: String): RecipeResponse


    @GET("lookup.php")
    suspend fun getRecipe(@Query("i") recipeId: Long): RecipeResponse


    @GET("search.php")
    suspend fun searchRecipes(@Query("s") name: String): RecipeResponse
}
