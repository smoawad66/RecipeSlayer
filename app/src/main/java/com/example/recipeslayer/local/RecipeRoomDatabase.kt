package com.example.recipeslayer.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.recipeslayer.models.Favourite
import com.example.recipeslayer.models.Recipe
import com.example.recipeslayer.models.User
import java.util.concurrent.Executors

@Database(
    entities = [User::class, Favourite::class, Recipe::class],
    version = 1,
    exportSchema = false

)
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
                    )
                        .addCallback(RecipeDatabaseCallback(context))
                        .build()
                }
            }
        }

        fun getInstance() = INSTANCE as RecipeRoomDatabase
    }
}