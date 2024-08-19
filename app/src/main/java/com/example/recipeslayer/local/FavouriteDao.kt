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

    @Query("SELECT recipe FROM favourites WHERE userId = :userId")
    fun getFavourites(userId: Long): LiveData<List<Recipe>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(favourite: Favourite): Long

    @Delete
    suspend fun deleteFavourite(favourite: Favourite)

    @Query("SELECT * FROM favourites WHERE userId = :userId AND recipe = :recipe")
    suspend fun getFavourite(userId: Long, recipe: Recipe): Favourite?

    @Query("SELECT id FROM favourites WHERE userId = :userId AND recipe = :recipe")
    suspend fun getFavouriteId(userId: Long, recipe: Recipe): Long?

}