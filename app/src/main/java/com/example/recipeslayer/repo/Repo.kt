package com.example.recipeslayer.repo

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
    override suspend fun insertUser(user: User) = localSource.insertUser(user)


    override suspend fun getRecipeOffline(recipeId: Long) = localSource.getRecipe(recipeId)
    override suspend fun insertRecipe(recipe: Recipe) = localSource.insertRecipe(recipe)
    override suspend fun deleteRecipe(recipe: Recipe) = localSource.deleteRecipe(recipe)


    override fun getFavouriteRecipes(userId: Long) = localSource.getFavouriteRecipes(userId)
    override suspend fun insertFavourite(favourite: Favourite) = localSource.insertFavourite(favourite)
    override suspend fun deleteFavourite(favourite: Favourite) = localSource.deleteFavourite(favourite)
    override suspend fun isFavourite(userId: Long, recipeId: Long) = localSource.isFavourite(userId, recipeId)


    // Remote data source (API calls using Retrofit)
    override suspend fun getRecipesOnline(category: String) = remoteSource.getRecipes(category)
    override suspend fun getRecipeOnline(recipeId: Long) = remoteSource.getRecipe(recipeId)
    override suspend fun searchRecipesOnline(name: String) = remoteSource.searchRecipes(name)

}