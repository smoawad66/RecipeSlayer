package com.example.recipeslayer.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipeslayer.models.Favourite
import com.example.recipeslayer.models.Recipe

@Dao
interface FavouriteDao {

    @Query("SELECT r.* FROM recipes r JOIN favourites f ON r.idMeal = f.recipeId WHERE f.userId = :userId ORDER BY f.createdAt DESC")
    fun getFavouriteRecipes(userId: Long): LiveData<List<Recipe>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(favourite: Favourite)

    @Delete
    suspend fun deleteFavourite(favourite: Favourite)

    @Query("SELECT EXISTS(SELECT 1 FROM favourites WHERE userId = :userId AND recipeId = :recipeId)")
    suspend fun isFavourite(userId: Long, recipeId: Long): Boolean

//    @Query("SELECT id FROM favourites WHERE userId = :userId AND recipe = :recipe")
//    suspend fun getFavouriteId(userId: Long, recipe: Recipe): Long?

}