package com.example.recipeslayer.repo

import com.example.recipeslayer.local.ILocalSource
import com.example.recipeslayer.local.LocalSource
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

    override suspend fun getRecipes(category: String) = remoteSource.getRecipes(category)
    override suspend fun getRecipeById(recipeId: String) = remoteSource.getRecipeById(recipeId)
    override suspend fun searchByName(name: String) = remoteSource.searchByName(name)

}