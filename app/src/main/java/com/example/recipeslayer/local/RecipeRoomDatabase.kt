package com.example.recipeslayer.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.recipeslayer.models.Favourite
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.models.User
import com.example.recipeslayer.utils.Converters

@Database(entities = [User::class, Favourite::class, Recipe::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)

abstract class RecipeRoomDatabase : RoomDatabase() {
    abstract fun getUserDao(): UserDao
    abstract fun getFavouriteDao(): FavouriteDao
    abstract fun getRecipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: RecipeRoomDatabase? = null

        fun initRoom(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = synchronized(this) {
                    Room.databaseBuilder(
                        context.applicationContext,
                        RecipeRoomDatabase::class.java,
                        "recipes_db"
                    ).build()
                }
            }
        }

        fun getInstance() = INSTANCE as RecipeRoomDatabase
    }
}