package com.example.recipeslayer.local

import com.example.recipeslayer.models.Favourite
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.models.User
import com.example.recipeslayer.utils.Config.isArabic

class LocalSource private constructor() : ILocalSource {

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
    override suspend fun updateUser(user: User) = userDao.updateUser(user)

    // Recipe
    override suspend fun getRecipeAr(recipeId: Long) = recipeDao.getRecipe(recipeId)
    override suspend fun insertRecipe(recipe: Recipe) = recipeDao.insertRecipe(recipe)
    override suspend fun deleteRecipe(recipe: Recipe) = recipeDao.deleteRecipe(recipe)

    // AR
    override suspend fun getRecipesAr() = recipeDao.getRecipesAr()

    override suspend fun searchRecipesAr(query: String) = recipeDao.searchRecipesAr("%$query%")

    override fun getRecommendedRecipes() =
        if (isArabic()) recipeDao.getRecommendedRecipesAr()
        else recipeDao.getRecommendedRecipes()

    // Favourite
    override fun getFavouriteRecipes(userId: Long) =
        if (isArabic()) favouriteDao.getFavouriteRecipesAr(userId)
        else favouriteDao.getFavouriteRecipes(userId)


    override suspend fun insertFavourite(favourite: Favourite) =
        favouriteDao.insertFavourite(favourite)

    override suspend fun deleteFavourite(favourite: Favourite) =
        favouriteDao.deleteFavourite(favourite)

    override suspend fun isFavourite(userId: Long, recipeId: Long) =
        favouriteDao.isFavourite(userId, recipeId)
}