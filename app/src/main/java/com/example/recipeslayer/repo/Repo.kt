package com.example.recipeslayer.repo

import androidx.lifecycle.LiveData
import com.example.recipeslayer.local.ILocalSource
import com.example.recipeslayer.local.LocalSource
import com.example.recipeslayer.models.Favourite
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.models.User
import com.example.recipeslayer.remote.IRemoteSource
import com.example.recipeslayer.remote.RemoteSource

class Repo(
    private val localSource: ILocalSource = LocalSource.getInstance(),
    private val remoteSource: IRemoteSource = RemoteSource.getInstance()
) : IRepo {

    // Local data source (SQLite Room Database)
    override suspend fun getUser(email: String) = localSource.getUser(email)
    override suspend fun getUser(id: Long) = localSource.getUser(id)
    override suspend fun insertUser(user: User) = localSource.insertUser(user)


    override suspend fun getRecipeAr(recipeId: Long) = localSource.getRecipeAr(recipeId)
    override suspend fun insertRecipe(recipe: Recipe) = localSource.insertRecipe(recipe)
    override suspend fun deleteRecipe(recipe: Recipe) = localSource.deleteRecipe(recipe)

    // AR
    override suspend fun getRecipesAr(category: String) = localSource.getRecipesAr(category)


    override fun getRecommendedRecipes() = localSource.getRecommendedRecipes()
    override suspend fun searchRecipesAr(query: String) = localSource.searchByName(query)

    override fun getFavouriteRecipes(userId: Long) = localSource.getFavouriteRecipes(userId)

    override suspend fun insertFavourite(favourite: Favourite) =
        localSource.insertFavourite(favourite)

    override suspend fun deleteFavourite(favourite: Favourite) =
        localSource.deleteFavourite(favourite)

    override suspend fun isFavourite(userId: Long, recipeId: Long) =
        localSource.isFavourite(userId, recipeId)


    // Remote data source (API calls using Retrofit)
    override suspend fun getRecipesOnline(category: String) = remoteSource.getRecipes(category)
    override suspend fun getRecipeOnline(recipeId: Long) = remoteSource.getRecipe(recipeId)
    override suspend fun searchRecipes(name: String) = remoteSource.searchRecipes(name)

}