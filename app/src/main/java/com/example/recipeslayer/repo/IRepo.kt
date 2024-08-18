package com.example.recipeslayer.repo

import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.models.User

interface IRepo {
    suspend fun getUser(email: String) : User
    suspend fun insertUser(user: User): Long


    suspend fun getRecipes(category: String): List<Recipe>?
    suspend fun getRecipeById(recipeId: String): Recipe?
    suspend fun searchByName(name: String): List<Recipe>?
}