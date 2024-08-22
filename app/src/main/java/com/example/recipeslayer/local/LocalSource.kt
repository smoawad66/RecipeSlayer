package com.example.recipeslayer.local

import androidx.lifecycle.LiveData
import com.example.recipeslayer.models.Favourite
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.models.User

class LocalSource private constructor(): ILocalSource {

    private val userDao = RecipeRoomDatabase.getInstance().getUserDao()
    private val favouriteDao = RecipeRoomDatabase.getInstance().getFavouriteDao()
    private val recipeDao = RecipeRoomDatabase.getInstance().getRecipeDao()



    companion object {
        @Volatile
        private var INSTANCE: LocalSource? = null

        fun getInstance(): LocalSource {
            if (INSTANCE == null) {
                INSTANCE = synchronized(this) { LocalSource() }
            }
            return INSTANCE as LocalSource
        }
    }

    // User
    override suspend fun getUser(email: String) = userDao.getUser(email)
    override suspend fun getUser(id: Long) = userDao.getUser(id)
    override suspend fun insertUser(user: User) = userDao.insertUser(user)

    // Recipe
    override suspend fun getRecipe(recipeId: Long) = recipeDao.getRecipe(recipeId)
    override suspend fun insertRecipe(recipe: Recipe) = recipeDao.insertRecipe(recipe)
    override suspend fun deleteRecipe(recipe: Recipe) = recipeDao.deleteRecipe(recipe)
    override fun getRecommendedRecipes() = recipeDao.getRecommendedRecipes()

    // Favourite
    override fun getFavouriteRecipes(userId: Long) = favouriteDao.getFavouriteRecipes(userId)
    override suspend fun insertFavourite(favourite: Favourite) = favouriteDao.insertFavourite(favourite)
    override suspend fun deleteFavourite(favourite: Favourite) = favouriteDao.deleteFavourite(favourite)
    override suspend fun isFavourite(userId: Long, recipeId: Long) = favouriteDao.isFavourite(userId, recipeId)
//    override suspend fun getFavourite(userId: Long, recipe: Recipe) = favouriteDao.getFavourite(userId, recipe)
//    override suspend fun getFavouriteId(userId: Long, recipe: Recipe) = favouriteDao.getFavouriteId(userId, recipe)
}