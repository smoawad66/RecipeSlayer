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
    override suspend fun getUserById(id: Long) = localSource.getUserById(id)
    override suspend fun updateUser(user: User) = localSource.updateUser(user)



    override suspend fun getRecipe(recipeId: String) = localSource.getRecipe(recipeId)
    override suspend fun insertRecipe(recipe: Recipe) = localSource.insertRecipe(recipe)
    override suspend fun deleteRecipe(recipe: Recipe) = localSource.deleteRecipe(recipe)


    override fun getFavourites(userId: Long) = localSource.getFavourites(userId)
    override suspend fun insertFavourite(favourite: Favourite) = localSource.insertFavourite(favourite)
    override suspend fun getFavourite(userId: Long, recipe: Recipe) = localSource.getFavourite(userId, recipe)
    override suspend fun deleteFavourite(favourite: Favourite) = localSource.deleteFavourite(favourite)
    override suspend fun getFavouriteId(userId: Long, recipe: Recipe) = localSource.getFavouriteId(userId, recipe)


    // Remote data source (API calls using Retrofit)
    override suspend fun getRecipes(category: String) = remoteSource.getRecipes(category)
    override suspend fun getRecipeById(recipeId: String) = remoteSource.getRecipeById(recipeId)
    override suspend fun searchByName(name: String) = remoteSource.searchByName(name)

}